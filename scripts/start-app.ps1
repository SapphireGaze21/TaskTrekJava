<#
.SYNOPSIS
Starts the TaskTrek Spring Boot API and Vite frontend.

.DESCRIPTION
The two processes are started in the background with their output recorded in
.runtime. Without explicit port arguments, the launcher selects available ports.
Database credentials come from
TASKTREK_DB_USERNAME and TASKTREK_DB_PASSWORD; the script prompts for a
username or password when either was not provided.
#>

[CmdletBinding()]
param(
    [switch]$SkipFrontendInstall,
    [ValidateRange(1024, 65535)]
    [int]$ApiPort = 8080,
    [ValidateRange(1024, 65535)]
    [int]$FrontendPort = 5173,
    [ValidatePattern('^[A-Za-z_][A-Za-z0-9_]*$')]
    [string]$DatabaseName = "tasktrek_db",
    [switch]$SkipDatabasePasswordPrompt
)

$ErrorActionPreference = "Stop"

$repositoryRoot = Split-Path -Parent $PSScriptRoot
$frontendRoot = Join-Path $repositoryRoot "frontend"
$mavenWrapper = Join-Path $repositoryRoot "mvnw.cmd"
$runtimeDirectory = Join-Path $repositoryRoot ".runtime"
$stateFile = Join-Path $runtimeDirectory "running.json"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"

New-Item -ItemType Directory -Force -Path $runtimeDirectory | Out-Null
$launcherErrorLog = Join-Path $runtimeDirectory "launcher-$timestamp.err.log"

trap {
    $errorDetails = @(
        "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] TaskTrek launcher failed.",
        ($_ | Out-String).Trim(),
        "Stack trace:",
        $_.ScriptStackTrace
    ) -join [Environment]::NewLine
    Set-Content -LiteralPath $launcherErrorLog -Value $errorDetails
    Write-Error "TaskTrek launcher failed. Details were written to $launcherErrorLog"
    exit 1
}

function Get-ListenerDescription {
    param([int]$Port)

    $listener = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if (-not $listener) { return $null }

    $processName = "unknown process"
    try { $processName = (Get-Process -Id $listener.OwningProcess -ErrorAction Stop).ProcessName } catch { }
    return "$processName (PID $($listener.OwningProcess))"
}

function Get-AvailablePort {
    param([Parameter(Mandatory)] [int]$PreferredPort)

    for ($candidate = $PreferredPort; $candidate -lt ($PreferredPort + 100); $candidate++) {
        if (-not (Get-ListenerDescription -Port $candidate)) { return $candidate }
    }
    throw "Could not find an available port between $PreferredPort and $($PreferredPort + 99)."
}

function Save-RunningState {
    param(
        [Nullable[int]]$ApiProcessId,
        [Nullable[int]]$FrontendProcessId,
        [Nullable[int]]$SelectedApiPort,
        [Nullable[int]]$SelectedFrontendPort
    )

    [PSCustomObject]@{
        repositoryRoot = $repositoryRoot
        startedAt = (Get-Date).ToString("o")
        apiProcessId = $ApiProcessId
        frontendProcessId = $FrontendProcessId
        apiPort = $SelectedApiPort
        frontendPort = $SelectedFrontendPort
    } | ConvertTo-Json | Set-Content -LiteralPath $stateFile
}

function Start-LoggedProcess {
    param(
        [Parameter(Mandatory)] [string]$Name,
        [Parameter(Mandatory)] [string]$FilePath,
        [Parameter(Mandatory)] [string[]]$Arguments,
        [Parameter(Mandatory)] [string]$WorkingDirectory
    )

    $standardOutput = Join-Path $runtimeDirectory "$Name-$timestamp.out.log"
    $standardError = Join-Path $runtimeDirectory "$Name-$timestamp.err.log"
    $process = Start-Process -FilePath $FilePath -ArgumentList $Arguments -WorkingDirectory $WorkingDirectory `
        -WindowStyle Hidden -RedirectStandardOutput $standardOutput -RedirectStandardError $standardError -PassThru

    Write-Host "Started $Name (PID $($process.Id))."
    Write-Host "  Logs: $standardOutput"
    return $process
}

function ConvertTo-PlainText {
    param([Parameter(Mandatory)] [Security.SecureString]$Value)

    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Value)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    }
}

function Ensure-Database {
    param(
        [Parameter(Mandatory)] [string]$Name,
        [Parameter(Mandatory)] [string]$Username
    )

    $psqlCommand = Get-Command "psql.exe" -ErrorAction SilentlyContinue
    if (-not $psqlCommand) {
        throw "PostgreSQL's psql.exe is required to create missing database '$Name'. Add PostgreSQL's bin folder to PATH, create the database manually, then run the launcher again."
    }

    $originalPgPassword = $env:PGPASSWORD
    $env:PGPASSWORD = $env:TASKTREK_DB_PASSWORD
    try {
        $exists = & $psqlCommand.Source --no-password --host localhost --port 5432 --username $Username --dbname postgres --tuples-only --no-align --command "SELECT 1 FROM pg_database WHERE datname = '$Name';"
        if ($LASTEXITCODE -ne 0) {
            throw "Could not connect to PostgreSQL as '$Username'. Verify the username and password."
        }
        if ($exists -match '^1\s*$') { return }

        Write-Host "Creating PostgreSQL database '$Name'..."
        & $psqlCommand.Source --no-password --host localhost --port 5432 --username $Username --dbname postgres --command "CREATE DATABASE $Name;"
        if ($LASTEXITCODE -ne 0) {
            throw "PostgreSQL could not create '$Name'. Ensure '$Username' has permission to create databases, or create it manually."
        }
    } finally {
        if ($null -eq $originalPgPassword) { Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue } else { $env:PGPASSWORD = $originalPgPassword }
    }
}

function Wait-ForPort {
    param(
        [Parameter(Mandatory)] [int]$Port,
        [Parameter(Mandatory)] [string]$ServiceName,
        [Parameter(Mandatory)] [System.Diagnostics.Process]$Process,
        [int]$TimeoutSeconds = 45
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Get-ListenerDescription -Port $Port) {
            Write-Host "$ServiceName is available on port $Port."
            return
        }
        if ($Process.HasExited) {
            throw "$ServiceName stopped before opening port $Port. Check .runtime logs."
        }
        Start-Sleep -Milliseconds 500
    }

    throw "$ServiceName did not open port $Port within $TimeoutSeconds seconds. Check .runtime logs."
}

if (-not (Test-Path -LiteralPath $mavenWrapper)) {
    throw "Could not find Maven Wrapper at $mavenWrapper"
}
if (-not (Test-Path -LiteralPath $frontendRoot)) {
    throw "Could not find frontend workspace at $frontendRoot"
}
if (-not (Get-Command "node.exe" -ErrorAction SilentlyContinue)) {
    throw "Node.js is required. Install Node.js 20+ and run this script again."
}
if (-not (Get-Command "npm.cmd" -ErrorAction SilentlyContinue)) {
    throw "npm is required. Reinstall Node.js and run this script again."
}
if (-not (Get-Command "java.exe" -ErrorAction SilentlyContinue)) {
    throw "Java 21+ is required. Install a JDK and run this script again."
}

if (-not $SkipDatabasePasswordPrompt -and [string]::IsNullOrWhiteSpace($env:TASKTREK_DB_USERNAME)) {
    $env:TASKTREK_DB_USERNAME = Read-Host "PostgreSQL username"
}
if (-not $SkipDatabasePasswordPrompt -and [string]::IsNullOrWhiteSpace($env:TASKTREK_DB_PASSWORD)) {
    $secureDatabasePassword = Read-Host "PostgreSQL password for '$($env:TASKTREK_DB_USERNAME)'" -AsSecureString
    $env:TASKTREK_DB_PASSWORD = ConvertTo-PlainText -Value $secureDatabasePassword
}
$env:TASKTREK_DB_NAME = $DatabaseName
Ensure-Database -Name $DatabaseName -Username $env:TASKTREK_DB_USERNAME

$environmentFile = Join-Path $frontendRoot ".env"
$exampleEnvironmentFile = Join-Path $frontendRoot ".env.example"
if (-not (Test-Path -LiteralPath $environmentFile) -and (Test-Path -LiteralPath $exampleEnvironmentFile)) {
    Copy-Item -LiteralPath $exampleEnvironmentFile -Destination $environmentFile
    Write-Host "Created frontend/.env from .env.example."
}

$apiPortWasSpecified = $PSBoundParameters.ContainsKey("ApiPort")
$frontendPortWasSpecified = $PSBoundParameters.ContainsKey("FrontendPort")

$apiListener = Get-ListenerDescription -Port $ApiPort
if ($apiListener) {
    if ($apiPortWasSpecified) {
        throw "Port $ApiPort is already in use by $apiListener. Run scripts\stop_app.cmd to stop the previously launched app, or choose a different API port."
    }
    $ApiPort = Get-AvailablePort -PreferredPort $ApiPort
    Write-Host "Port 8080 is occupied by $apiListener; using API port $ApiPort instead."
}

$frontendListener = Get-ListenerDescription -Port $FrontendPort
if ($frontendListener) {
    if ($frontendPortWasSpecified) {
        throw "Port $FrontendPort is already in use by $frontendListener. Run scripts\stop_app.cmd to stop the previously launched app, or choose a different frontend port."
    }
    $FrontendPort = Get-AvailablePort -PreferredPort $FrontendPort
    Write-Host "Port 5173 is occupied by $frontendListener; using frontend port $FrontendPort instead."
}
if ($ApiPort -eq $FrontendPort) {
    throw "The API and frontend must use different ports."
}

if (Test-Path -LiteralPath $environmentFile) {
    $environmentContent = @(Get-Content -LiteralPath $environmentFile |
        Where-Object { $_ -notmatch '^\s*VITE_API_BASE_URL\s*=' })
    $environmentContent += "VITE_API_BASE_URL=http://localhost:$ApiPort/api"
    Set-Content -LiteralPath $environmentFile -Value $environmentContent
}

$viteExecutable = Join-Path $frontendRoot "node_modules/.bin/vite.cmd"
if (-not $SkipFrontendInstall -and -not (Test-Path -LiteralPath $viteExecutable)) {
    Write-Host "Installing frontend dependencies..."
    Push-Location $frontendRoot
    try {
        & npm.cmd install
        if ($LASTEXITCODE -ne 0) { throw "npm install exited with code $LASTEXITCODE." }
    } finally {
        Pop-Location
    }
}

$apiCommand = "call `"$mavenWrapper`" spring-boot:run `"-Dspring-boot.run.arguments=--server.port=$ApiPort --app.cors.allowed-origins=http://localhost:$FrontendPort`""
$apiProcess = Start-LoggedProcess -Name "api" -FilePath $env:ComSpec -Arguments @("/d", "/s", "/c", $apiCommand) -WorkingDirectory $repositoryRoot
Save-RunningState -ApiProcessId $apiProcess.Id -FrontendProcessId $null -SelectedApiPort $ApiPort -SelectedFrontendPort $FrontendPort
Wait-ForPort -Port $ApiPort -ServiceName "TaskTrek API" -Process $apiProcess

$frontendCommand = "call npm.cmd run dev -- --host 127.0.0.1 --port $FrontendPort --strictPort"
$frontendProcess = Start-LoggedProcess -Name "frontend" -FilePath $env:ComSpec -Arguments @("/d", "/s", "/c", $frontendCommand) -WorkingDirectory $frontendRoot
Save-RunningState -ApiProcessId $apiProcess.Id -FrontendProcessId $frontendProcess.Id -SelectedApiPort $ApiPort -SelectedFrontendPort $FrontendPort
Wait-ForPort -Port $FrontendPort -ServiceName "TaskTrek frontend" -Process $frontendProcess -TimeoutSeconds 20

Write-Host ""
Write-Host "TaskTrek is starting:"
Write-Host "  Frontend: http://localhost:$FrontendPort"
Write-Host "  API:      http://localhost:$ApiPort/api"
Write-Host ""
Write-Host "If a service does not become available, inspect the newest matching .runtime log files."
