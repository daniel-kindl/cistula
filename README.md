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

The SDK on the initialization host had no emulator and no system image. Use the Android SDK command-line tools to create a device.

1. Accept the SDK licenses. Run `sdkmanager --licenses`.
2. On an x86_64 host, install the emulator and the API 37.0 image. Run `sdkmanager "emulator" "system-images;android-37.0;google_apis;x86_64"`.
3. On an arm64 host, use `system-images;android-37.0;google_apis;arm64-v8a` in that command.
4. Create a virtual device. Run `avdmanager create avd -n cistula-api37 -k "system-images;android-37.0;google_apis;x86_64"`.
5. On an arm64 host, put the arm64 image id in the `-k` value.
6. Start the virtual device. Run `emulator -avd cistula-api37`.
7. Install the debug build. Run `./gradlew :app:installDebug`.

`sdkmanager --list` shows the image ids when a host needs a different CPU type. Use an API 26 image when you need the minimum supported API. Issue #4 and issue #5 own CI device tests.

## License

Copyright 2026 Daniel Kindl. The license is Apache-2.0. The terms are in `LICENSE`. The reason for this choice is in `docs/initialization.md`.

## Decisions

Toolchain, SDK, and branch decisions are in `docs/initialization.md`.
