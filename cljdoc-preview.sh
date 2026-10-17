#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

read -r LIB VERSION < <(clojure -T:build info)
PROJECT="${LIB##*/}"
CLJDOC_DATA=$(mktemp -d /tmp/cral-cljdoc.XXXXXX)

echo "Generating cljdoc for $LIB $VERSION (data: $CLJDOC_DATA)"
clojure -T:build install

docker run --rm \
  -v "$PWD:/$PROJECT:ro" \
  -v "$HOME/.m2:/root/.m2" \
  -v "$CLJDOC_DATA:/app/data" \
  --entrypoint clojure \
  cljdoc/cljdoc -Sforce -M:cli ingest \
    --project "$LIB" \
    --version "$VERSION" \
    --git "/$PROJECT"

docker run --rm -p 9000:8000 \
  -v "$CLJDOC_DATA:/app/data" \
  -v "$HOME/.m2:/root/.m2" \
  cljdoc/cljdoc
