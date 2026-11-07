#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "${BASH_SOURCE[0]}")"
command -v docker >/dev/null
export COMPOSE_PROJECT_NAME="cral-integration-$(date +%s)-$$"
result_dir="$PWD/integration-results/$COMPOSE_PROJECT_NAME"
mkdir -p "$result_dir"
compose=(docker compose --env-file docker/.env -f docker/docker-compose.yml -f docker/integration/compose.yml)
cleanup() {
    status=$?
    trap - EXIT
    "${compose[@]}" logs --no-color >"$result_dir/services.log" 2>&1 || true
    "${compose[@]}" down --volumes --remove-orphans >"$result_dir/cleanup.log" 2>&1 || {
        echo "Cleanup failed; see $result_dir/cleanup.log" >&2
        if (( status == 0 )); then status=1; fi
    }
    echo "Integration test exit code: $status. Logs: $result_dir"
    exit "$status"
}
"${compose[@]}" config --quiet
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM
"${compose[@]}" up --build -d alfresco postgres solr6 activemq transform-core-aio
"${compose[@]}" build integration
"${compose[@]}" run --rm -T --no-deps integration 2>&1 | tee "$result_dir/tests.log"
