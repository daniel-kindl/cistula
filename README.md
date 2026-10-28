# Cistula

Cistula is a native Android application for a personal book collection. The application is offline-first. The repository contains the M0 engineering foundation and a [book domain contract](docs/architecture/books.md). The screen shows the application name. Book screens, storage, and online metadata are not implemented.

## Requirements

You need all of the following items.

- JDK 21 for Detekt. Gradle can run on JDK 21 or 25. Both were verified locally with Temurin 21.0.13+9 and OpenJDK 25.0.4.1. CI uses Temurin 21. See the [JDK policy](docs/development/toolchain.md#jdk-policy).
- Android SDK platform `android-37.0`.
- Android SDK Build-Tools 36.0.0.
- An SDK location. Set `ANDROID_HOME` to the SDK directory, or create `local.properties` with `sdk.dir`. Do not commit `local.properties`.

## Verify

Run `./eng/verify.sh` from the repository root. It is the same command as CI and never changes
files. It checks the Gradle wrapper, repository policies, ktlint formatting (Spotless), Detekt,
Android Lint (warnings are errors), debug, release, and instrumentation-test builds, unit tests, and
dependency health. Run `./eng/format.sh` to apply formatting.

Single tasks: `./gradlew :app:assembleDebug`, `./gradlew :app:assembleRelease`,
`./gradlew :app:lintDebug`, `./gradlew :app:testDebugUnitTest`.

`assembleRelease` writes an unsigned APK. This repository has no signing config.

`./eng/verify.sh` runs the unit tests and writes the coverage report to
`app/build/reports/coverage/test/debug/index.html`. There is no coverage percentage gate.
See [docs/development/testing.md](docs/development/testing.md).

## Contributing

Read `AGENTS.md` and `CONTRIBUTING.md`. The agent task order is in
`docs/development/agent-workflow.md`. Branch from `dev`. Security reports: `SECURITY.md`.

## Emulator

The `android-dev` Distrobox has the Android Emulator and an Android 16
(API 36) virtual device named `android-dev-36`. Start it from a desktop
session that provides `DISPLAY` and `XAUTHORITY`.

1. Enter the container. Run `distrobox enter android-dev` on the host.
2. Start the device. Run `emulator -avd android-dev-36 -gpu host` in the container.
3. Install the debug build. Run `./gradlew :app:installDebug` from this repository.

If the emulator's side toolbar does not respond, run
`android-emulator-controls` to open the Back, Home, Overview, Power, volume,
rotation and screenshot buttons. The controls send commands through ADB.

The app supports API 36. Its minimum SDK is 26, and its target SDK is 37.
On this workstation, Emulator 37.2.12.0 crashes with software graphics.
The windowed device boots with `-gpu host`.

On another machine, use `sdkmanager --list` to find a system image for its CPU.
Install `emulator` and that image with `sdkmanager`, then create an AVD with
`avdmanager create avd`. Use an API 26 image to test the minimum supported API.
CI device tests run in the manual Instrumentation workflow. See
[docs/development/testing.md](docs/development/testing.md) and ADR 0006.

## License

Copyright 2026 Daniel Kindl. The license is Apache-2.0. The terms are in `LICENSE`. The reason for this choice is in `docs/initialization.md`.

## Decisions

Initial toolchain, SDK, and branch decisions are in `docs/initialization.md`. ADRs are in
`docs/decisions/`. Architecture: `docs/architecture/README.md`. Development guides: `docs/development/`.
