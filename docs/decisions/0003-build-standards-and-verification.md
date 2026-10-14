# 0003. Build standards and verification

- Status: Accepted
- Date: 2026-10-14
- Issue: #3

## Context

Standards must be enforced by tools, the same way locally and in CI.

## Decision

- Formatting: ktlint 1.8.0 through Spotless (`ktlint_official` code style, configured in
  `.editorconfig`). `spotlessCheck` verifies; `./eng/format.sh` applies.
- Static analysis: Detekt 1.23.8 with the default rules plus stricter rules
  (`config/detekt/detekt.yml`), warnings as errors, zero issues allowed. Detekt runs through its
  official CLI from a root `detekt` task. Reason: the Detekt 1.23 Gradle plugin uses Gradle APIs
  that are deprecated in Gradle 9, and Detekt 2 is not stable yet. Move to the Detekt 2 Gradle
  plugin when it is stable.
- Android Lint: all checks on (`checkAllWarnings`), warnings as errors, test sources included,
  no baseline. Three checks are off because they depend on network data and the date, which is
  not deterministic: `GradleDependency`, `NewerVersionAvailable`, `AndroidGradlePluginVersion`.
  Dependabot covers version updates.
- Kotlin: `allWarningsAsErrors`. Gradle: `org.gradle.warning.mode=fail`.
- Dependency analysis (`com.autonomousapps.dependency-analysis`): unused, misdeclared, and
  wrong-configuration dependencies fail the build. "Used transitive dependency" advice is ignored:
  Compose artifacts come through umbrella libraries with versions that the BOM aligns, and
  declaring each one would duplicate versions.
- `./eng/verify.sh` also fails on inline suppressions, Lint or Detekt baselines, CRLF line
  endings, feature dependencies in the catalog, an invalid version, and any change to tracked files.

## Consequences

A rule exception needs a PR that changes `config/` with a written reason. Inline suppressions are
blocked by `verify.sh`; if one becomes necessary, the PR must update `verify.sh` and this ADR.

## Alternatives considered

- detekt-formatting (ktlint wrapper inside Detekt): it bundles an older ktlint. Spotless uses the
  current ktlint directly.
- Gradle dependency verification metadata: strong supply-chain control, but Dependabot does not
  update it, so every update PR would fail. Revisit when the dependency set grows.
