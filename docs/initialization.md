# Initialization decisions

This file records the decisions for issue #2. Later issues changed the tooling: issue #3 added ktlint, Detekt, and Kotlin warnings-as-errors, issue #5 added `./eng/verify.sh` and CI, and issue #7 moved the version to `cistula.version`. The Lint configuration moved from `app/lint.xml` to `config/lint/lint.xml`. Current tooling is in `docs/development/` and `docs/decisions/`. It does not define product behavior. Issue #8 records product scope and architecture.

## License

The license is Apache-2.0. The terms are in `LICENSE`. The copyright holder is Daniel Kindl. The copyright year is 2026.

Apache-2.0 is the license of the Android SDK Build-Tools, the Android Gradle Plugin, Kotlin, and AndroidX. The license includes a patent grant. `LICENSE` is the unmodified Apache license text. The copyright notice is in this file and in `README.md`.

## Module

The project has one Android application module, `:app`. The project has no library module.

## Application id

The application id and the namespace are `dev.danielkindl.cistula`. The id uses the domain from the owner git identity, `danielkindl.dev`.

## Android SDK

| Item | Value | Reason |
| --- | --- | --- |
| compileSdk | 37.0 | Installed platform `android-37.0`. This is the maximum API that AGP 9.4 supports. |
| targetSdk | 37.0 | Same value as compileSdk. The build file sets it explicitly. |
| minSdk | 26 | Oldest API that this repository supports. |

minSdk is 26 so that every supported device can use an adaptive icon. The repository does not ship PNG launcher icons. A later product decision can raise minSdk. Issue #8 can record that decision.

## JDK

The Gradle build runs on JDK 17 or later. The checked build used JDK 25.0.4.1.

Kotlin `jvmTarget` is 17. Java source compatibility and target compatibility are 17. Android source in this repository must stay compatible with that target.

## Build tools and libraries

| Item | Version | Reason |
| --- | --- | --- |
| Android Gradle Plugin | 9.4.1 | Latest stable 9.4 patch on 2026-10-13. It supports API 37. |
| Gradle | 9.7.1 | AGP 9.4 requires Gradle 9.6.0 or later. Kotlin 2.4.20 records Gradle 9.7.0 support. |
| Kotlin | 2.4.21 | Latest stable Kotlin release on 2026-10-08. |
| Compose BOM | 2026.09.00 | Latest stable Compose BOM recorded by Android Developers on 2026-10-13. |
| activity-compose | 1.13.0 | Latest stable Activity Compose release. |
| Build-Tools | 36.0.0 | Default Build-Tools version for AGP 9.4. |

AGP 9 compiles Kotlin with built-in Kotlin. The application module does not apply `org.jetbrains.kotlin.android`. The root build file puts the Kotlin Gradle plugin on the buildscript classpath. The version comes from `kotlin` in the version catalog. That pin selects the Kotlin version for built-in Kotlin. The Compose compiler plugin uses the same catalog entry.

The version catalog is `gradle/libs.versions.toml`. Dependency versions in the application module come from that catalog.

The application dependencies are Activity Compose, the Compose BOM, Compose UI, the Compose UI preview, Material 3, and the debug Compose UI tooling. The project has no other libraries.

Compose brings `libandroidx.graphics.path.so`. The application module keeps the debug symbols in that library. The build does not need the NDK strip tool. This repository has no native source.

Kotlin 2.4.21 records Gradle 7.6.3 through 9.7.0 as the fully supported Gradle range. Gradle 9.7.1 is the newest release on the 9.7 line. `app/lint.xml` ignores `AndroidGradlePluginVersion` in `gradle/wrapper/gradle-wrapper.properties` for that reason. The check reports Gradle 9.8.1. That release is outside the fully supported line. The check stays active for other files.

Kotlin 2.4.21 records AGP 9.3.1 as the newest fully supported AGP. This repository uses AGP 9.4.1 because that stable plugin supports API 37. The checked build reported no deprecation warning from that pair.

## Release build

`isMinifyEnabled` is false. The release build type has no signing config. `assembleRelease` writes an unsigned APK. Signing is not part of issue #2.

## Tests

The unit test task exists. This issue adds no tests and no domain behavior. Issue #4 added the test infrastructure and the first behavior test. See `docs/development/testing.md` and ADR 0005.

## Static analysis

This issue does not add ktlint, Detekt, or compiler `-Werror`. Issue #3 owns those tools. Android Lint for this commit must finish with no errors and no warnings.

## Verification command

This issue does not add `./eng/verify.sh` or GitHub Actions. Issue #5 owns that command. Until that issue, run the commands in `README.md`.

## Branches

Issue #2 is the bootstrap exception from the parent issue. A pull request needs a base branch. This repository has no commit before this one. This commit goes on `main`. The initialization creates `dev` from this commit. Later implementation issues use a short-lived branch and a pull request to `dev`.

## Emulator

The initialization host had Android SDK platform 37.0 and Build-Tools 36.0.0 and 37.0.0. The host had no emulator package and no system image. `README.md` records the local emulator procedure. Issue #5 added the manual Instrumentation workflow. Issue #4 added the tests that the workflow runs. See ADR 0006 and `docs/development/testing.md`.

## Placeholder UI

The launcher icon is a geometric placeholder. The adaptive icon includes a monochrome drawable for themed icons. The icon files are in `mipmap-anydpi` because minSdk is 26. The theme uses the Material 3 default light scheme and the default dark scheme. These choices are not a brand decision. The activity calls `enableEdgeToEdge()` so the Material 3 scaffold can pad system bars. The manifest sets `android:enableOnBackInvokedCallback` and `tools:targetApi="33"` because that attribute applies from API 33.

## Version fields

`versionCode` is 1. `versionName` is `0.0.0`. Issue #7 owns the SemVer policy and the `versionCode` policy.

## Gradle wrapper checksums

`gradle/wrapper/gradle-wrapper.properties` sets `distributionSha256Sum` to the SHA-256 that Gradle publishes for `gradle-9.7.1-bin.zip`.

Published distribution SHA-256:

`acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a`

The initialization check downloaded `https://services.gradle.org/distributions/gradle-9.7.1-bin.zip` and computed the same SHA-256.

Published wrapper JAR SHA-256:

`7a9ce74cff467ca1bf60a4fcd9f05185acceda4d0f382434d393e17864262c5d`

Gradle publishes that hash as `gradle-9.7.1-wrapper.jar.sha256`. The committed `gradle/wrapper/gradle-wrapper.jar` must have that SHA-256.

A later reader can repeat the distribution check.

1. Download `https://services.gradle.org/distributions/gradle-9.7.1-bin.zip`.
2. Compute the SHA-256.
3. Compare it with `distributionSha256Sum` in `gradle/wrapper/gradle-wrapper.properties`.
4. Compute the SHA-256 of `gradle/wrapper/gradle-wrapper.jar`.
5. Compare it with the published wrapper JAR SHA-256 in this file.
