# Branches, versions, and releases

Decision record: [ADR 0004](../decisions/0004-versioning-strategy.md).

- `main`: stable. `dev`: integration. Short-lived `feature/*`, `fix/*`, `chore/*` from `dev`.
- All changes go through squash-merged PRs. The PR title is a Conventional Commit.
- Single version source: `cistula.version` in `gradle.properties`. Current: `0.0.0`.
- `versionCode = MAJOR * 1_000_000 + MINOR * 1_000 + PATCH + 1`.

## Release procedure (not used in M0)

1. Open `chore(release): vX.Y.Z` from `dev` to `main`. Set `cistula.version` and update `CHANGELOG.md`.
2. After CI passes and review, merge.
3. Tag the merge commit on `main`: `git tag -s vX.Y.Z -m "vX.Y.Z" && git push origin vX.Y.Z`.
4. Fast-forward `dev` to `main` if they differ.

No workflow publishes artifacts. Signing and store publishing need a separate issue.
