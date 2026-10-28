# Toolchain

Versions checked on 2026-10-14 against official sources (Google Maven, Maven Central, Gradle
Plugin Portal, services.gradle.org, kotlinlang.org, developer.android.com). All are stable releases.

The convention build's Kotlin plugin resolution was rechecked on 2026-10-17 for issue #12.
AGP was updated to 9.5.0 on 2026-10-28 for issue #27 after CI Lint rejected 9.4.1.
The [AGP 9.5 compatibility table](https://developer.android.com/build/releases/agp-9-5-0-release-notes#compatibility)
requires Gradle 9.6.0+, JDK 17+, and Build-Tools 36.0.0. The existing toolchain meets those requirements.

| Tool | Version | Source of truth | Notes |
| --- | --- | --- | --- |
| JDK | 21 for Detekt, 21 or 25 for Gradle | `detekt-jdk` in `libs.versions.toml`, CI `setup-java` | Temurin 21.0.13+9 and OpenJDK 25.0.4.1 verified locally. Bytecode target 17. |
| Gradle | 9.7.1 | `gradle/wrapper/gradle-wrapper.properties` | Distribution SHA-256 pinned. Meets the AGP 9.5 minimum without a wrapper update. |
| Android Gradle Plugin | 9.5.0 | `libs.versions.toml` | Needs Gradle 9.6.0+, supports up to API 37. Built-in Kotlin. |
| Kotlin | 2.4.21 | `libs.versions.toml` | Application, Compose compiler, and convention build. See ADR 0010. |
| compileSdk / targetSdk | 37 | `libs.versions.toml` | Platform `android-37.0`. |
| minSdk | 26 | `libs.versions.toml` | See ADR 0002. |
| Build-Tools | 36.0.0 | AGP default | Downloaded by AGP. |
| Compose BOM | 2026.09.00 | `libs.versions.toml` | UI (ui, ui-tooling, material3). |
| Activity Compose | 1.13.0 | `libs.versions.toml` | |
| Lifecycle | 2.10.0 minimum | `libs.versions.toml` | Constraint on the existing transitive runtime dependency. Lifecycle 2.9.4 Lint checks reference an API removed from AGP 9.5. The constraint preserves those checks. |
| Detekt (CLI) | 1.23.8 | `libs.versions.toml` | Runs on a JDK 21 toolchain. Detekt 2 is alpha. |
| ktlint | 1.8.0 | `libs.versions.toml` | Run by Spotless. |
| Spotless | 8.10.4 | `libs.versions.toml` | |
| Dependency Analysis Gradle Plugin | 3.19.2 | `libs.versions.toml` | `buildHealth`. Latest stable on 2026-10-14. It officially supports AGP 8.10.0 to 9.3.1 and logs a compatibility warning for AGP 9.5.0. The warning is a log line, not a Gradle deprecation; `buildHealth` passes. Tracked in issue #11. |
| JUnit | 4.13.2 | `libs.versions.toml` | Unit tests and instrumentation tests. |
| AndroidX Test JUnit | 1.3.0 | `libs.versions.toml` | `androidx.test.ext:junit`. It brings `AndroidJUnitRunner`. |
| Compose UI test (`ui-test-junit4`) | Compose BOM | `libs.versions.toml` | Resolved to 1.12.1 on 2026-10-14. Tests call the `junit4.v2` rule. |
| JaCoCo | AGP default | Not pinned | 0.8.14 when checked on 2026-10-14. Report only. No percentage gate. |

## Local book storage dependencies

Issue #31 and [ADR 0014](../decisions/0014-local-book-storage.md) add these dependencies.
Versions were checked against official sources on 2026-10-28.

| Dependency | Version | Purpose and source |
| --- | --- | --- |
| Room | 2.8.5 | Runtime, compiler, and schema plugin. [AndroidX release notes](https://developer.android.com/jetpack/androidx/releases/room). |
| Guava, compiler classpath only | 33.7.2-jre | Constraint for Room code generation. Fixes [GHSA-xxph-c9ww-hj94](https://github.com/advisories/GHSA-xxph-c9ww-hj94). |
| KSP | 2.3.12 | Room code generation. [KSP release notes](https://github.com/google/ksp/releases/tag/2.3.12). |
| Coroutines | 1.11.0 | Suspend/Flow repository contract and device-test scheduler. [Coroutines release notes](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.11.0). |
| AndroidX Test Core | 1.7.0 | Application context for storage instrumentation tests. [AndroidX Test release notes](https://developer.android.com/jetpack/androidx/releases/test). |

Room uses Android SQLite. JSON list conversion uses Android APIs. No SQLite or serialization runtime is added separately.
`config/approved-feature-dependencies.txt` permits only the exact Room/KSP declarations for this issue.
`./eng/check-feature-dependencies-test.sh` verifies that unapproved feature dependencies still fail.

## JDK policy

Install JDK 21 even when Gradle runs on JDK 25. The `detekt` task selects JDK 21 through a Gradle Java toolchain.
Set `JAVA_HOME` to the JDK that runs Gradle. Gradle must also detect the JDK 21 installation.
Use a standard installation directory, such as `~/.jdks`, or set `org.gradle.java.installations.paths` in your local Gradle properties.
See [Gradle toolchain detection](https://docs.gradle.org/9.7.1/userguide/toolchains.html#sec:auto_detection).
This build does not configure automatic JDK downloads. A missing JDK 21 causes the task to fail with Gradle's toolchain diagnostic.

Full verification covers Gradle on JDK 21 and 25. Other Gradle JDK versions are not verified by this policy.
CI uses JDK 21. Local verification used Temurin 21.0.13+9 and OpenJDK 25.0.4.1.
Java and Kotlin bytecode targets stay at 17.

Detekt 1.23.8 uses Kotlin compiler 2.0.21. That compiler calls `sun.misc.Unsafe.objectFieldOffset`, which emits a JVM warning on JDK 25.
Detekt's [compatibility table](https://detekt.dev/docs/1.23.8/introduction/compatibility/) lists JDK 21 as its highest tested JDK.
The dedicated launcher keeps Detekt on that runtime without replacing its compiler or suppressing JVM warnings.
The task still checks the same sources with the same rules. [ADR 0012](../decisions/0012-run-detekt-on-jdk-21.md) records this decision.

To check the runtime selection:

1. Set `JAVA_HOME` to an installed JDK 21.
2. Run `./eng/check-detekt-runtime.sh`. It executes Detekt without cached task results and rejects the Unsafe warning.
3. Run `./eng/verify.sh`.
4. Set `JAVA_HOME` to an installed JDK 25.
5. Run both commands again.

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
