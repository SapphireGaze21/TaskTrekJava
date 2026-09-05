<# Stops only the API and frontend processes started by start_app.cmd. #>

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
$repositoryRoot = Split-Path -Parent $PSScriptRoot
$stateFile = Join-Path $repositoryRoot ".runtime\running.json"

if (-not (Test-Path -LiteralPath $stateFile)) {
    Write-Host "No TaskTrek launcher state was found. Nothing to stop."
    exit 0
}

$state = Get-Content -Raw -LiteralPath $stateFile | ConvertFrom-Json
if ($state.repositoryRoot -ne $repositoryRoot) {
    throw "The launcher state belongs to a different project. It was not used."
}

foreach ($service in @(
    @{ Name = "frontend"; ProcessId = $state.frontendProcessId },
    @{ Name = "API"; ProcessId = $state.apiProcessId }
)) {
    if (-not $service.ProcessId) { continue }
    $process = Get-Process -Id $service.ProcessId -ErrorAction SilentlyContinue
    if ($process) {
        & taskkill.exe /PID $service.ProcessId /T /F | Out-Null
        Write-Host "Stopped TaskTrek $($service.Name) (PID $($service.ProcessId))."
    } else {
        Write-Host "TaskTrek $($service.Name) was already stopped."
    }
}

Remove-Item -LiteralPath $stateFile -Force
Write-Host "TaskTrek has stopped. Its ports are now available."
