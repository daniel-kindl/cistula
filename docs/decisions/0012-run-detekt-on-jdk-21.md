# 0012. Run Detekt on JDK 21

- Status: Proposed
- Date: 2026-10-28
- Issue: #24

## Context

Detekt 1.23.8 embeds Kotlin compiler 2.0.21. Its IntelliJ utility code calls `sun.misc.Unsafe.objectFieldOffset`.
The JVM emits an Unsafe warning when the Detekt process runs on JDK 25.
The uncached Detekt task reproduces the warning on the merged `dev` commit `acfad73`.

Detekt's [compatibility table](https://detekt.dev/docs/1.23.8/introduction/compatibility/) lists JDK 21 as its highest tested JDK.
Detekt 1.23.8 remains the latest stable CLI release. Detekt 2 is still an alpha release.
The issue excludes warning suppression and unstable releases. Replacing Detekt's embedded compiler would change its tested tool combination.

## Decision

This decision supersedes ADR 0002's open-ended Gradle JDK policy.
It adds an explicit Detekt runtime to ADR 0003. All other decisions in those ADRs remain in effect.

- Require an installed JDK 21 for the Detekt process.
- Use Gradle's built-in `jvm-toolchains` plugin and `JavaExec.javaLauncher` to select that JDK.
- Store the Detekt JDK version in the version catalog.
- Verify the full build with Gradle on JDK 21 and 25. Do not claim verification for other Gradle JDK versions.
- Keep Detekt 1.23.8, its embedded compiler, source inputs, rule configuration, and failure behavior.
- Keep Java and Kotlin bytecode targets at 17.
- Do not configure automatic JDK downloads or add a resolver plugin.
- Run `eng/check-detekt-runtime.sh` on each documented Gradle JDK. It rejects the reproduced Unsafe warning.

## Consequences

A developer who runs Gradle on JDK 25 also needs JDK 21. Gradle reports a missing toolchain instead of running Detekt on JDK 25.
CI already installs JDK 21. The Detekt launcher works with that installation.
The regression check executes the real Detekt CLI without cached task results.
Full verification and independent PR review remain required.

## Alternatives considered

- Run all tools on JDK 21 only. Rejected because an explicit Detekt launcher preserves the documented JDK 25 Gradle environment.
- Upgrade to Detekt 2 alpha. Rejected because the issue excludes unstable dependency upgrades.
- Override the embedded Kotlin compiler. Rejected because Detekt must use its tested compiler version.
- Suppress the Unsafe warning. Rejected because the issue excludes warning suppression.
