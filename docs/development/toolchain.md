# Toolchain

Versions checked on 2026-10-14 against official sources (Google Maven, Maven Central, Gradle
Plugin Portal, services.gradle.org, kotlinlang.org, developer.android.com). All are stable releases.

| Tool | Version | Source of truth | Notes |
| --- | --- | --- | --- |
| JDK | 21 in CI (Temurin 21.0.12.1 used locally) | CI `setup-java` | AGP 9.4 needs JDK 17+. Bytecode target 17. |
| Gradle | 9.7.1 | `gradle/wrapper/gradle-wrapper.properties` | Distribution SHA-256 pinned. 9.8.1 not used: AGP 9.4.1 triggers its deprecations. |
| Android Gradle Plugin | 9.4.1 | `libs.versions.toml` | Needs Gradle 9.6+, supports up to API 37. Built-in Kotlin. |
| Kotlin | 2.4.21 | `libs.versions.toml` | Also the Compose compiler plugin version. |
| compileSdk / targetSdk | 37 | `libs.versions.toml` | Platform `android-37.0`. |
| minSdk | 26 | `libs.versions.toml` | See ADR 0002. |
| Build-Tools | 36.0.0 | AGP default | Downloaded by AGP. |
| Compose BOM | 2026.09.00 | `libs.versions.toml` | UI (ui, ui-tooling, material3). |
| Activity Compose | 1.13.0 | `libs.versions.toml` | |
| Detekt (CLI) | 1.23.8 | `libs.versions.toml` | Detekt 2 is alpha. |
| ktlint | 1.8.0 | `libs.versions.toml` | Run by Spotless. |
| Spotless | 8.10.4 | `libs.versions.toml` | |
| Dependency Analysis Gradle Plugin | 3.19.2 | `libs.versions.toml` | `buildHealth`. |
| Test libraries | none | — | Issue #4 adds them. |

## Vulnerability and dependency tooling

- Dependabot version updates (Gradle, GitHub Actions), weekly, to `dev`.
- Dependabot alerts and security updates (repository setting).
- Dependency Analysis Gradle Plugin for unused or misdeclared dependencies.
- `dependency-review` CI job on PRs.
- See `repository-settings.md`.

## Update a version

1. Change the version only in `gradle/libs.versions.toml` (or the wrapper, with
   `./gradlew wrapper --gradle-version X --gradle-distribution-sha256-sum <official sum>`).
2. Read the release notes and the AGP/Kotlin/Gradle compatibility tables.
3. Run `./eng/verify.sh`. Update this table in the same PR.
