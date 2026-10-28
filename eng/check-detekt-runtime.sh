#!/usr/bin/env bash
# Run with each documented Gradle JDK to check the Detekt runtime compatibility.
set -euo pipefail

cd "$(dirname "$0")/.."
output=$(mktemp)
trap 'rm -f "$output"' EXIT

if ! ./gradlew --console=plain --warning-mode=fail --max-workers=2 \
    detekt --rerun-tasks --no-build-cache >"$output" 2>&1; then
  cat "$output"
  echo "FAIL: Detekt did not complete." >&2
  exit 1
fi

cat "$output"
if grep -q 'WARNING:.*sun\.misc\.Unsafe' "$output"; then
  echo "FAIL: Detekt emitted the Unsafe JVM warning." >&2
  exit 1
fi

echo "PASS: Detekt completed without the Unsafe JVM warning."
