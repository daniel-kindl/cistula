# 0004. Versioning strategy

- Status: Accepted
- Date: 2026-10-14
- Issue: #7

## Context

We need SemVer, a single version source, and a version code that Android accepts.

## Decision

- The single source is `cistula.version` in `gradle.properties` (`MAJOR.MINOR.PATCH`). It starts at `0.0.0`.
- The convention plugin derives `versionName` (same text) and `versionCode`
  (`MAJOR * 1_000_000 + MINOR * 1_000 + PATCH + 1`).
- `verify.sh` and the build reject values that are not `MAJOR.MINOR.PATCH`.
- A release is a PR from `dev` to `main` that sets the version and moves `CHANGELOG.md` entries
  from Unreleased to the version. After merge, the maintainer tags the merge commit `vX.Y.Z` on `main`.
- Conventional Commit types decide the bump: `feat` = minor, `fix`/`perf` = patch, `!` = major
  (minor while the version is 0.x).
- No release publishing in M0.
- `0.0.0` gives `versionCode` 1 and `versionName` `0.0.0`, the same values as the issue #2 build.

## Consequences

The version changes only in release PRs, so feature PRs do not conflict on it. Builds from `dev`
carry the last released version; that is acceptable because nothing is published from `dev`.

## Alternatives considered

- Version from `git describe`: needs full history and tags in every checkout, and shallow CI
  clones break it.
- release-please: useful once releases exist. Revisit before the first release.
