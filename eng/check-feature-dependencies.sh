#!/usr/bin/env bash
# Issue #31 and ADR 0014 approve only the exact storage dependencies in the approval file.
set -euo pipefail
catalog=${1:-gradle/libs.versions.toml}
approvals="$(dirname "$0")/../config/approved-feature-dependencies.txt"
[[ -r "$catalog" && -r "$approvals" ]] || { echo 'FAIL: Dependency policy input is not readable.' >&2; exit 1; }
unapproved=$(
  grep -Ei 'ksp|hilt|dagger|room|retrofit|okhttp|work-runtime|workmanager|firebase|analytics|crashlytics' "$catalog" \
    | grep -vE '^(room|ksp) = "[0-9]+\.[0-9]+\.[0-9]+"$' \
    | grep -Fvx -f "$approvals" || true
)
if [[ -n "$unapproved" ]]; then
  printf '%s\nFAIL: Unapproved feature or tracking dependency found. An issue and ADR are required.\n' "$unapproved" >&2
  exit 1
fi
