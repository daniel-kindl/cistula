#!/usr/bin/env bash
# Applies ktlint formatting to Kotlin sources and Gradle scripts. Verification never formats.
set -euo pipefail
cd "$(dirname "$0")/.."
./gradlew --console=plain --warning-mode=fail spotlessApply
