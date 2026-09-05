#!/usr/bin/env bash
# Starts the TaskTrek Spring Boot API and Vite frontend on macOS or Linux.

set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
FRONTEND_ROOT="$REPOSITORY_ROOT/frontend"
RUNTIME_DIRECTORY="$REPOSITORY_ROOT/.runtime"
STATE_FILE="$RUNTIME_DIRECTORY/running-unix.env"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
LAUNCHER_ERROR_LOG="$RUNTIME_DIRECTORY/launcher-$TIMESTAMP.err.log"

API_PORT=8080
FRONTEND_PORT=5173
DATABASE_NAME="tasktrek_db"
API_PORT_SPECIFIED=false
FRONTEND_PORT_SPECIFIED=false
SKIP_FRONTEND_INSTALL=false
SKIP_DATABASE_PASSWORD_PROMPT=false

mkdir -p "$RUNTIME_DIRECTORY"

on_error() {
  local exit_code=$?
  {
    printf '[%s] TaskTrek launcher failed (exit code %s).\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$exit_code"
    printf 'Command: %s\n' "$BASH_COMMAND"
    printf 'Line: %s\n' "$LINENO"
  } > "$LAUNCHER_ERROR_LOG"
  printf 'TaskTrek launcher failed. Details were written to %s\n' "$LAUNCHER_ERROR_LOG" >&2
  exit "$exit_code"
}
trap on_error ERR

usage() {
  cat <<'EOF'
Usage: ./scripts/start-app.sh [options]

Options:
  --api-port PORT                 Preferred API port (default: 8080)
  --frontend-port PORT            Preferred frontend port (default: 5173)
  --database-name NAME            PostgreSQL database (default: tasktrek_db)
  --skip-frontend-install         Do not run npm install when packages are missing
  --skip-database-password-prompt Do not prompt for database credentials
  -h, --help                      Show this help
EOF
}

while (($#)); do
  case "$1" in
    --api-port) API_PORT="$2"; API_PORT_SPECIFIED=true; shift 2 ;;
    --frontend-port) FRONTEND_PORT="$2"; FRONTEND_PORT_SPECIFIED=true; shift 2 ;;
    --database-name) DATABASE_NAME="$2"; shift 2 ;;
    --skip-frontend-install) SKIP_FRONTEND_INSTALL=true; shift ;;
    --skip-database-password-prompt) SKIP_DATABASE_PASSWORD_PROMPT=true; shift ;;
    -h|--help) usage; exit 0 ;;
    *) printf 'Unknown option: %s\n' "$1" >&2; usage >&2; exit 2 ;;
  esac
done

[[ "$API_PORT" =~ ^[0-9]+$ && "$API_PORT" -ge 1024 && "$API_PORT" -le 65535 ]] || { echo "Invalid API port." >&2; exit 2; }
[[ "$FRONTEND_PORT" =~ ^[0-9]+$ && "$FRONTEND_PORT" -ge 1024 && "$FRONTEND_PORT" -le 65535 ]] || { echo "Invalid frontend port." >&2; exit 2; }
[[ "$DATABASE_NAME" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]] || { echo "Database names may contain only letters, numbers, and underscores." >&2; exit 2; }
[[ -x "$REPOSITORY_ROOT/mvnw" ]] || { echo "Could not find executable Maven Wrapper at $REPOSITORY_ROOT/mvnw" >&2; exit 1; }
[[ -d "$FRONTEND_ROOT" ]] || { echo "Could not find frontend workspace at $FRONTEND_ROOT" >&2; exit 1; }
command -v java >/dev/null || { echo "Java 21+ is required." >&2; exit 1; }
command -v node >/dev/null || { echo "Node.js 20+ is required." >&2; exit 1; }
command -v npm >/dev/null || { echo "npm is required." >&2; exit 1; }
command -v psql >/dev/null || { echo "PostgreSQL's psql command is required." >&2; exit 1; }

port_in_use() {
  local port="$1"
  if command -v lsof >/dev/null; then
    lsof -nP -iTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
  elif command -v ss >/dev/null; then
    ss -ltn "sport = :$port" | grep -q LISTEN
  else
    netstat -an 2>/dev/null | grep -E "[.:]$port[[:space:]].*LISTEN" >/dev/null
  fi
}

available_port() {
  local preferred="$1" candidate
  for ((candidate=preferred; candidate<preferred+100; candidate++)); do
    if ! port_in_use "$candidate"; then printf '%s' "$candidate"; return 0; fi
  done
  return 1
}

wait_for_port() {
  local port="$1" name="$2" pid="$3" timeout="${4:-45}" deadline=$((SECONDS + timeout))
  while ((SECONDS < deadline)); do
    if port_in_use "$port"; then
      printf '%s is available on port %s.\n' "$name" "$port"
      return 0
    fi
    if ! kill -0 "$pid" 2>/dev/null; then
      echo "$name stopped before opening port $port. Check .runtime logs." >&2
      return 1
    fi
    sleep 0.5
  done
  echo "$name did not open port $port within $timeout seconds. Check .runtime logs." >&2
  return 1
}

save_state() {
  local api_pid="$1" frontend_pid="$2"
  cat > "$STATE_FILE" <<EOF
API_PID=$api_pid
FRONTEND_PID=$frontend_pid
API_PORT=$API_PORT
FRONTEND_PORT=$FRONTEND_PORT
EOF
}

if [[ "$SKIP_DATABASE_PASSWORD_PROMPT" == false && -z "${TASKTREK_DB_USERNAME:-}" ]]; then
  read -r -p "PostgreSQL username: " TASKTREK_DB_USERNAME
  export TASKTREK_DB_USERNAME
fi
if [[ "$SKIP_DATABASE_PASSWORD_PROMPT" == false && -z "${TASKTREK_DB_PASSWORD:-}" ]]; then
  read -r -s -p "PostgreSQL password for '$TASKTREK_DB_USERNAME': " TASKTREK_DB_PASSWORD
  printf '\n'
  export TASKTREK_DB_PASSWORD
fi
[[ -n "${TASKTREK_DB_USERNAME:-}" && -n "${TASKTREK_DB_PASSWORD:-}" ]] || { echo "PostgreSQL username and password are required." >&2; exit 1; }

set +e
database_exists="$(PGPASSWORD="$TASKTREK_DB_PASSWORD" psql --no-password --host localhost --port 5432 --username "$TASKTREK_DB_USERNAME" --dbname postgres --tuples-only --no-align --command "SELECT 1 FROM pg_database WHERE datname = '$DATABASE_NAME';")"
database_check_status=$?
set -e
if ((database_check_status != 0)); then
  echo "Could not connect to PostgreSQL as '$TASKTREK_DB_USERNAME'. Verify the username, password, and PostgreSQL service." >&2
  exit 1
fi
if [[ ! "$database_exists" =~ ^[[:space:]]*1[[:space:]]*$ ]]; then
  printf "Creating PostgreSQL database '%s'...\n" "$DATABASE_NAME"
  PGPASSWORD="$TASKTREK_DB_PASSWORD" psql --no-password --host localhost --port 5432 --username "$TASKTREK_DB_USERNAME" --dbname postgres --command "CREATE DATABASE $DATABASE_NAME;"
fi
export TASKTREK_DB_NAME="$DATABASE_NAME"

if port_in_use "$API_PORT"; then
  if [[ "$API_PORT_SPECIFIED" == true ]]; then echo "API port $API_PORT is already in use. Use ./scripts/stop-app.sh or select another port." >&2; exit 1; fi
  API_PORT="$(available_port "$API_PORT")"
  printf 'Port 8080 is occupied; using API port %s instead.\n' "$API_PORT"
fi
if port_in_use "$FRONTEND_PORT"; then
  if [[ "$FRONTEND_PORT_SPECIFIED" == true ]]; then echo "Frontend port $FRONTEND_PORT is already in use. Use ./scripts/stop-app.sh or select another port." >&2; exit 1; fi
  FRONTEND_PORT="$(available_port "$FRONTEND_PORT")"
  printf 'Port 5173 is occupied; using frontend port %s instead.\n' "$FRONTEND_PORT"
fi
[[ "$API_PORT" != "$FRONTEND_PORT" ]] || { echo "API and frontend ports must differ." >&2; exit 1; }

ENVIRONMENT_FILE="$FRONTEND_ROOT/.env"
if [[ ! -f "$ENVIRONMENT_FILE" ]]; then cp "$FRONTEND_ROOT/.env.example" "$ENVIRONMENT_FILE"; fi
grep -v '^[[:space:]]*VITE_API_BASE_URL[[:space:]]*=' "$ENVIRONMENT_FILE" > "$ENVIRONMENT_FILE.tmp" || true
printf 'VITE_API_BASE_URL=http://localhost:%s/api\n' "$API_PORT" >> "$ENVIRONMENT_FILE.tmp"
mv "$ENVIRONMENT_FILE.tmp" "$ENVIRONMENT_FILE"

if [[ "$SKIP_FRONTEND_INSTALL" == false && ! -x "$FRONTEND_ROOT/node_modules/.bin/vite" ]]; then
  (cd "$FRONTEND_ROOT" && npm install)
fi

API_OUT="$RUNTIME_DIRECTORY/api-$TIMESTAMP.out.log"
API_ERR="$RUNTIME_DIRECTORY/api-$TIMESTAMP.err.log"
FRONTEND_OUT="$RUNTIME_DIRECTORY/frontend-$TIMESTAMP.out.log"
FRONTEND_ERR="$RUNTIME_DIRECTORY/frontend-$TIMESTAMP.err.log"

(cd "$REPOSITORY_ROOT" && ./mvnw spring-boot:run "-Dspring-boot.run.arguments=--server.port=$API_PORT --app.cors.allowed-origins=http://localhost:$FRONTEND_PORT") >"$API_OUT" 2>"$API_ERR" &
API_PID=$!
save_state "$API_PID" ""
printf 'Started API (PID %s). Logs: %s\n' "$API_PID" "$API_OUT"
wait_for_port "$API_PORT" "TaskTrek API" "$API_PID"

(cd "$FRONTEND_ROOT" && npm run dev -- --host 127.0.0.1 --port "$FRONTEND_PORT" --strictPort) >"$FRONTEND_OUT" 2>"$FRONTEND_ERR" &
FRONTEND_PID=$!
save_state "$API_PID" "$FRONTEND_PID"
printf 'Started frontend (PID %s). Logs: %s\n' "$FRONTEND_PID" "$FRONTEND_OUT"
wait_for_port "$FRONTEND_PORT" "TaskTrek frontend" "$FRONTEND_PID" 20

printf '\nTaskTrek is ready:\n  Frontend: http://localhost:%s\n  API:      http://localhost:%s/api\n' "$FRONTEND_PORT" "$API_PORT"
