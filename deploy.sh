#!/usr/bin/env bash
# Copyright (C) 2023-2026 Saidone
# SPDX-License-Identifier: GPL-3.0-or-later

set -euo pipefail
cd "$(dirname "$0")"

IFS= read -r -p "Clojars username: " clojars_username
IFS= read -r -s -p "Clojars deploy token: " clojars_token
printf '\n'

if [[ -z "$clojars_username" || -z "$clojars_token" ]]; then
  printf 'Username and deploy token are required.\n' >&2
  exit 1
fi

CLOJARS_USERNAME="$clojars_username" CLOJARS_PASSWORD="$clojars_token" \
  clojure -T:build deploy
