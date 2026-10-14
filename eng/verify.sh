#!/usr/bin/env bash
# Single verification entry point. Used locally and in CI. It never changes files.
# Usage: ./eng/verify.sh
set -euo pipefail

cd "$(dirname "$0")/.."
GRADLE=(./gradlew --console=plain --warning-mode=fail)

step() { printf '\n==> [%s] %s\n' "$1" "$2"; }
fail() { printf '\nFAIL: %s\n' "$1" >&2; exit 1; }

step 1 "Gradle wrapper validation"
props=gradle/wrapper/gradle-wrapper.properties
grep -q '^distributionSha256Sum=[0-9a-f]\{64\}$' "$props" || fail "$props must pin distributionSha256Sum."
version=$(sed -n 's#^distributionUrl=.*/gradle-\([0-9.]*\)-bin\.zip$#\1#p' "$props")
[[ -n "$version" ]] || fail "$props must use an official gradle-<version>-bin.zip distributionUrl."
expected=$(curl -fsSL --retry 3 "https://services.gradle.org/distributions/gradle-${version}-wrapper.jar.sha256") \
  || fail "Cannot download the official wrapper checksum for Gradle ${version} (network required)."
actual=$(sha256sum gradle/wrapper/gradle-wrapper.jar | cut -d' ' -f1)
[[ "$actual" == "$expected" ]] || fail "gradle-wrapper.jar does not match the official Gradle ${version} checksum."
echo "gradle-wrapper.jar matches official Gradle ${version} checksum."

step 2 "Repository configuration checks"
semver=$(sed -n 's/^cistula\.version=//p' gradle.properties)
[[ "$semver" =~ ^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$ ]] || fail "cistula.version '$semver' is not MAJOR.MINOR.PATCH."
# Feature dependencies need an approved issue and an ADR before they enter the build.
if grep -nEi 'ksp|hilt|dagger|room|retrofit|okhttp|work-runtime|workmanager|firebase|analytics|crashlytics' gradle/libs.versions.toml; then
  fail "Feature or tracking dependency found in gradle/libs.versions.toml. Add it only with an approved issue and ADR."
fi
# No silent suppressions or lint baselines. Exceptions need a reason in config/ and review.
if grep -rnE '@Suppress(Warnings|Lint)?\(|tools:ignore=|//\s*(ktlint-disable|noinspection)|detekt:disable' \
     --exclude-dir=build --exclude-dir=.gradle --include='*.kt' --include='*.kts' --include='*.xml' app build-logic/convention/src; then
  fail "Inline suppression found. Fix the finding instead."
fi
if find . \( -name build -o -name .gradle -o -name .git \) -prune -o -name 'lint-baseline*.xml' -print -o -name 'detekt-baseline*.xml' -print | grep -q .; then
  fail "Lint or Detekt baseline found. Baselines are not allowed."
fi
if git ls-files --eol | grep -E '^i/crlf' | grep -vE '\.(bat|cmd)$'; then
  fail "Files with CRLF line endings found. Use LF."
fi
echo "Configuration checks passed."

step 3 "Formatting verification (ktlint via Spotless; run ./eng/format.sh to fix)"
"${GRADLE[@]}" spotlessCheck

step 4 "Kotlin static analysis (Detekt)"
"${GRADLE[@]}" detekt

step 5 "Android Lint (app)"
"${GRADLE[@]}" :app:lintDebug

step 6 "Compile Android app (debug, release, instrumentation tests)"
"${GRADLE[@]}" :app:assembleDebug :app:assembleRelease :app:assembleDebugAndroidTest

step 7 "Unit tests"
"${GRADLE[@]}" :app:testDebugUnitTest

step 8 "Dependency analysis (unused and misconfigured dependencies)"
"${GRADLE[@]}" buildHealth

step 9 "Working tree unchanged"
if [[ -n "$(git status --porcelain --untracked-files=no)" ]]; then
  git status --short --untracked-files=no >&2
  fail "Verification changed tracked files."
fi

printf '\nPASS: all verification steps succeeded.\n'
printf 'NOT RUN: instrumentation tests (need a device or emulator; see docs/development/testing.md).\n'
