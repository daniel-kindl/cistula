#!/usr/bin/env bash
# Verify that the issue #31 dependency approval does not permit unrelated feature dependencies.
set -euo pipefail
cd "$(dirname "$0")/.."
fixture=$(mktemp)
trap 'rm -f "$fixture"' EXIT
checker=eng/check-feature-dependencies.sh

"$checker" gradle/libs.versions.toml
for entry in \
  'extra = { module = "androidx.room:room-paging", version.ref = "room" }' \
  'extra = { module = "com.squareup.retrofit2:retrofit", version = "1.0.0" }' \
  'extra = { module = "com.google.firebase:firebase-analytics", version = "1.0.0" }' \
  'ksp = { id = "other.ksp.plugin", version.ref = "ksp" }' \
  'room = "unapproved"'; do
  cp gradle/libs.versions.toml "$fixture"
  printf '\n%s\n' "$entry" >> "$fixture"
  if "$checker" "$fixture" >/dev/null 2>&1; then
    printf 'FAIL: Unapproved dependency passed: %s\n' "$entry" >&2
    exit 1
  fi
done
printf 'PASS: Approved storage dependencies pass. Unapproved feature dependencies fail.\n'
