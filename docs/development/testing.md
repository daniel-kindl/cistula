# Testing

Issue #4 owns the test infrastructure (framework, conventions, first behavior test, testing ADR).

## Current state (M0)

- No unit or instrumentation tests exist. No test library is in the version catalog.
- `./eng/verify.sh` runs `:app:testDebugUnitTest` (no tests yet) and compiles the instrumentation
  APK (`:app:assembleDebugAndroidTest`). It does not run instrumentation tests; it prints them as NOT RUN.
- The manual `Instrumentation` workflow (`gh workflow run Instrumentation --ref <branch>`) runs
  `:app:connectedDebugAndroidTest` on an API 26 emulator. It is useful only after issue #4 adds tests.
- Fixtures must use synthetic data only.
