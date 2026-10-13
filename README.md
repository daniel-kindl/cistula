# Cistula

Cistula is a native Android application for a personal book collection. The application is offline-first. This repository is at milestone M0. M0 is the engineering foundation. It does not contain book features.

## Requirements

You need all of the following items.

- JDK 17 or later. The tested JDK is 25.0.4.1. The JDK policy is in `docs/initialization.md`.
- Android SDK platform `android-37.0`.
- Android SDK Build-Tools 36.0.0.
- An SDK location. Set `ANDROID_HOME` to the SDK directory, or create `local.properties` with `sdk.dir`. Do not commit `local.properties`.

## Build

Run these commands from the repository root.

1. Run `./gradlew :app:assembleDebug`.
2. Run `./gradlew :app:assembleRelease`.
3. Run `./gradlew :app:lint`.
4. Run `./gradlew :app:testDebugUnitTest`.

`assembleRelease` writes an unsigned APK. This repository has no signing config.

The unit test task has no tests. Issue #4 adds the first behavior test.

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
Issue #4 and issue #5 own CI device tests.

## License

Copyright 2026 Daniel Kindl. The license is Apache-2.0. The terms are in `LICENSE`. The reason for this choice is in `docs/initialization.md`.

## Decisions

Toolchain, SDK, and branch decisions are in `docs/initialization.md`.
