#!/usr/bin/env bash
# Stops only processes recorded by scripts/start-app.sh.

set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPOSITORY_ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd)"
STATE_FILE="$REPOSITORY_ROOT/.runtime/running-unix.env"

if [[ ! -f "$STATE_FILE" ]]; then
  echo "No TaskTrek launcher state was found. Nothing to stop."
  exit 0
fi

api_pid="$(grep '^API_PID=' "$STATE_FILE" | cut -d= -f2-)"
frontend_pid="$(grep '^FRONTEND_PID=' "$STATE_FILE" | cut -d= -f2-)"

stop_tree() {
  local name="$1" pid="$2" child
  [[ -n "$pid" ]] || return 0
  if ! kill -0 "$pid" 2>/dev/null; then echo "TaskTrek $name was already stopped."; return 0; fi
  if command -v pgrep >/dev/null; then
    while IFS= read -r child; do stop_tree "$name child" "$child"; done < <(pgrep -P "$pid" || true)
  fi
  kill "$pid" 2>/dev/null || true
  sleep 0.5
  kill -9 "$pid" 2>/dev/null || true
  echo "Stopped TaskTrek $name (PID $pid)."
}

stop_tree "frontend" "$frontend_pid"
stop_tree "API" "$api_pid"
rm -f "$STATE_FILE"
echo "TaskTrek has stopped. Its ports are now available."
