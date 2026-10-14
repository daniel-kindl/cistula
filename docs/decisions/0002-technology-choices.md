# 0002. Technology choices

- Status: Accepted
- Date: 2026-10-14
- Issue: #8

## Context

The project needs a native Android app with minimal dependencies.
The versions must be current, stable, and compatible.

## Decision

- Kotlin 2.4.21 with the Android Gradle Plugin 9.4.1 built-in Kotlin support (no separate
  `org.jetbrains.kotlin.android` plugin). The Kotlin Gradle plugin on the classpath sets the
  compiler version.
- Gradle 9.7.1, Gradle Kotlin DSL, one version catalog (`gradle/libs.versions.toml`).
- JDK 17 or later for Gradle (CI uses JDK 21). The Java/Kotlin bytecode target stays 17, as
  recorded in `docs/initialization.md`.
- compileSdk and targetSdk 37. minSdk 26 (adaptive icons on every
  supported device; see `docs/initialization.md`).
- Jetpack Compose (BOM 2026.09.00, Material 3).
- One included build, `build-logic`, with one convention plugin. It keeps module build files small and
  holds the quality gates in one place.
- No DI framework, KSP, Room, Retrofit, OkHttp, or WorkManager in M0.

Full version table and compatibility notes: `docs/development/toolchain.md`.

## Consequences

- AGP 9 built-in Kotlin needs no extra plugin, but the Kotlin version must be compatible with AGP.
  Dependabot groups Kotlin and AGP updates for this reason.
- minSdk 26 needs API checks for newer platform APIs. Android Lint enforces them (`NewApi`).

## Alternatives considered

- Gradle 9.8.1 (newest): AGP 9.4.1 calls Gradle APIs that 9.8 deprecates, and the build fails on
  deprecations. Gradle 9.7.1 is the newest version without warnings.
- XML views instead of Compose: Compose is the current recommended UI toolkit.
- Convention logic in the root `build.gradle.kts` (`subprojects {}`): Gradle discourages
  cross-project configuration; it breaks isolated projects.
