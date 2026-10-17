# 0010. Compile build logic with catalog Kotlin

- Status: Proposed
- Date: 2026-10-17
- Issue: #12

## Context

Gradle 9.7.1's `kotlin-dsl` plugin brings Kotlin Gradle plugin 2.4.0 into the convention build.
That version has advisory GHSA-r937-wjx7-w2jp. The application already uses Kotlin 2.4.21 from
the version catalog. The convention plugin is a binary Kotlin plugin. It has no precompiled
script plugins that need the `kotlin-dsl` plugin.

Upgrading Gradle to 9.8.1 is not the selected fix. The existing toolchain decision records
AGP 9.4.1 deprecations with that version. Gradle warnings must continue to fail verification.

## Decision

- Apply `java-gradle-plugin` and the catalog's `org.jetbrains.kotlin.jvm` plugin in the
  convention build. Both application compilation and convention compilation use Kotlin 2.4.21.
- Use `compileOnly(gradleKotlinDsl())` for the existing public Kotlin DSL helpers. This supplies
  Gradle APIs from the distribution without applying the `kotlin-dsl` plugin.
- Set the Java and Kotlin JVM targets from the catalog's `jvm-target` value, currently 17.
  Keep Kotlin warnings as errors in the convention build.
- Use explicit configuration objects where the old plugin supplied implicit SAM receivers.
  Keep the Android release and Lint constraint behavior unchanged.
- Remove the CI allowance for GHSA-r937-wjx7-w2jp. Dependency review must reject this advisory
  if an affected Kotlin Gradle plugin version enters the graph again.

This ADR supersedes the advisory allowance in [ADR 0006](0006-ci-strategy.md). The other CI
decisions still apply. [ADR 0002](0002-technology-choices.md) still defines the toolchain.
The `.gradle.kts` build scripts remain Kotlin DSL scripts. Gradle still uses its embedded
Kotlin compiler for those scripts. That compiler is distinct from the affected Gradle plugin.

## Consequences

The convention build no longer resolves Kotlin Gradle plugin 2.4.0 through `kotlin-dsl`.
The compiler plugin version now follows the existing catalog entry. No Gradle or Android
plugin upgrade is needed. No application dependency or extra application module is added.

The convention build now declares its JVM targets and warning policy explicitly. Future
precompiled script plugins would require a new decision about their compiler setup.
Dependency submission and dependency review provide the CI check for the resolved graph.

## Alternatives considered

- Override the Kotlin version inside `kotlin-dsl`. Gradle ties that plugin to its distribution.
  Overriding its Kotlin version is not the supported fix.
- Upgrade Gradle and permit AGP deprecations. This would weaken the warning policy.
- Keep the advisory allowance. Issue #12 requires its removal once a compatible fix passes.
- Add the SAM-with-receiver compiler plugin to preserve implicit callback receivers. Explicit
  configuration objects avoid another compiler plugin for two callbacks.

## References

- [Gradle Kotlin DSL plugin](https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin).
- [Kotlin Gradle configuration](https://kotlinlang.org/docs/gradle-configure-project.html).
- [GHSA-r937-wjx7-w2jp](https://github.com/advisories/GHSA-r937-wjx7-w2jp).
