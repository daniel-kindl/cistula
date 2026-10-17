# Toolchain

Versions checked on 2026-10-14 against official sources (Google Maven, Maven Central, Gradle
Plugin Portal, services.gradle.org, kotlinlang.org, developer.android.com). All are stable releases.

The convention build's Kotlin plugin resolution was rechecked on 2026-10-17 for issue #12.

| Tool | Version | Source of truth | Notes |
| --- | --- | --- | --- |
| JDK | 21 in CI (Temurin 21.0.12.1 used locally) | CI `setup-java` | AGP 9.4 needs JDK 17+. Bytecode target 17. |
| Gradle | 9.7.1 | `gradle/wrapper/gradle-wrapper.properties` | Distribution SHA-256 pinned. 9.8.1 not used: AGP 9.4.1 triggers its deprecations. |
| Android Gradle Plugin | 9.4.1 | `libs.versions.toml` | Needs Gradle 9.6+, supports up to API 37. Built-in Kotlin. |
| Kotlin | 2.4.21 | `libs.versions.toml` | Application, Compose compiler, and convention build. See ADR 0010. |
| compileSdk / targetSdk | 37 | `libs.versions.toml` | Platform `android-37.0`. |
| minSdk | 26 | `libs.versions.toml` | See ADR 0002. |
| Build-Tools | 36.0.0 | AGP default | Downloaded by AGP. |
| Compose BOM | 2026.09.00 | `libs.versions.toml` | UI (ui, ui-tooling, material3). |
| Activity Compose | 1.13.0 | `libs.versions.toml` | |
| Detekt (CLI) | 1.23.8 | `libs.versions.toml` | Detekt 2 is alpha. |
| ktlint | 1.8.0 | `libs.versions.toml` | Run by Spotless. |
| Spotless | 8.10.4 | `libs.versions.toml` | |
| Dependency Analysis Gradle Plugin | 3.19.2 | `libs.versions.toml` | `buildHealth`. Latest stable on 2026-10-14. It officially supports AGP 8.10.0 to 9.3.1 and logs a compatibility warning for AGP 9.4.1. The warning is a log line, not a Gradle deprecation; `buildHealth` passes. Tracked in issue #11. |
| JUnit | 4.13.2 | `libs.versions.toml` | Unit tests and instrumentation tests. |
| AndroidX Test JUnit | 1.3.0 | `libs.versions.toml` | `androidx.test.ext:junit`. It brings `AndroidJUnitRunner`. |
| Compose UI test (`ui-test-junit4`) | Compose BOM | `libs.versions.toml` | Resolved to 1.12.1 on 2026-10-14. Tests call the `junit4.v2` rule. |
| JaCoCo | AGP default | Not pinned | 0.8.14 when checked on 2026-10-14. Report only. No percentage gate. |

## Vulnerability and dependency tooling

- Dependabot version updates (Gradle, GitHub Actions), weekly, to `dev`.
- Dependabot alerts and security updates (repository setting).
- Dependency Analysis Gradle Plugin for unused or misdeclared dependencies.
- `dependency-review` CI job on PRs.
- Build-tool security constraints: `build-tool-security-constraints` in `libs.versions.toml` raises
  transitive build-time libraries (Bouncy Castle, jose4j, jdom2, httpclient, commons-lang3) that AGP,
  Kotlin, or Android Lint bring with known advisories. They apply to the root build classpath and
  the `androidLintTool` configuration. They do not enter the APK. Remove an entry when the tools
  bring a fixed version.
- The convention build uses `java-gradle-plugin` and the catalog's Kotlin JVM plugin instead of
  `kotlin-dsl`. Its resolved Kotlin Gradle plugin is 2.4.21. CI has no advisory allowance for
  GHSA-r937-wjx7-w2jp. ADR 0010 records the fix for issue #12. The existing `.gradle.kts` scripts
  still use Gradle's embedded script compiler.
- See `repository-settings.md`.

## Update a version

1. Change the version only in `gradle/libs.versions.toml` (or the wrapper, with
   `./gradlew wrapper --gradle-version X --gradle-distribution-sha256-sum <official sum>`).
2. Read the release notes and the AGP/Kotlin/Gradle compatibility tables.
3. Run `./eng/verify.sh`. Update this table in the same PR.
