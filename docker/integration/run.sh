#!/usr/bin/env bash
set -euo pipefail
for value in "$STARTUP_TIMEOUT" "$TEST_TIMEOUT"; do
    [[ "$value" =~ ^[1-9][0-9]*$ ]] || { echo 'Timeouts must be positive seconds' >&2; exit 2; }
done
# Work on a private copy: build/clean and fixture configuration never change the host.
tar -C /source --exclude=./target --exclude=./.cpcache \
    --exclude=./.idea --exclude=./.aws --exclude=./.codex --exclude=./.agents \
    --exclude=./smoke-results --exclude=./integration-results -cf - . | tar -C /work -xf -
git config --global --add safe.directory /work
ready() {
    curl -fsS --max-time 10 -u admin:admin \
        http://alfresco:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/-root- >/dev/null &&
    curl -fsS --max-time 10 -H 'X-Alfresco-Search-Secret: secret' \
        'http://solr6:8983/solr/alfresco/admin/ping?wt=json' >/dev/null &&
    curl -fsS --max-time 10 http://transform-core-aio:8090/ready >/dev/null
}
echo "Waiting for Alfresco, Solr and ATS (max ${STARTUP_TIMEOUT}s)..."
deadline=$((SECONDS + STARTUP_TIMEOUT))
until ready 2>/dev/null; do
    if (( SECONDS >= deadline )); then echo 'Service startup timed out' >&2; exit 124; fi
    sleep 5
done
# One deadline covers dependency downloads, build and the complete test suite.
timeout --signal=TERM --kill-after=30s "${TEST_TIMEOUT}s" bash -euo pipefail -c '
    echo "Building library JAR..."
    clojure -T:build jar
    # Fixtures load this file for every namespace. Change only the private copy,
    # after building, so the JAR retains the normal configuration.
    sed -i '\''s/:host[[:space:]]*"localhost"/:host "alfresco"/'\'' resources/config.edn
    echo "Running all tests..."
    clojure -M:test
'
