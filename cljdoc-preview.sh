#!/usr/bin/env bash
#
#  CRAL
#  Copyright (C) 2023-2026 Saidone
#  SPDX-License-Identifier: GPL-3.0-or-later
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

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
