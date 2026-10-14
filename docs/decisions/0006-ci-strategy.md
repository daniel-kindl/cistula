# 0006. CI strategy

- Status: Accepted
- Date: 2026-10-14
- Issue: #5

## Decision

- GitHub Actions. Workflow `CI` runs on PRs and pushes to `main` and `dev`:
  - `verify`: Gradle wrapper validation, then `./eng/verify.sh` on `ubuntu-24.04` with Temurin 21.
  - `pr-title`: checks that the PR title is a Conventional Commit (the squash commit message).
  - `dependency-submission`: `gradle/actions/dependency-submission` submits the resolved Gradle
    dependency graph to the GitHub dependency graph. It runs on pushes to `main` and `dev` (base
    snapshots) and on same-repository PRs (head snapshots). It is the only job with
    `contents: write`; the dependency submission API needs it.
  - `dependency-review`: runs after `dependency-submission` on PRs. It compares the base and head
    dependency graphs (Gradle and GitHub Actions) and fails a PR that adds a dependency with a
    known vulnerability (any severity) or a denied license. If the base branch has no Gradle
    snapshot yet, the action reports a snapshot warning and can only compare what the base has.
    One exception: `allow-ghsas: GHSA-r937-wjx7-w2jp` (KGP 2.4.0 pinned by Gradle's embedded kotlin-dsl, build-logic only); removal is tracked in issue #12.
- Workflow `Instrumentation` runs the instrumentation tests on an Android API 26 emulator
  (minSdk). The repository has no instrumentation tests yet; issue #4 adds them. It is manual (`workflow_dispatch`) because emulators are slow
  and use many Actions minutes. By default, instrumentation tests are NOT RUN in
  PR CI. Run it before releases and for UI or platform changes.
- Every job and long step has `timeout-minutes`, so a hung Gradle daemon or emulator stops early.
- Every action is pinned to a full commit SHA. Workflows have `contents: read` only.
- `setup-gradle` caches Gradle; only `main` and `dev` write the cache.
- Superseded PR runs are cancelled. Reports are uploaded only on failure.
- Dependabot updates Gradle dependencies and actions weekly, targeting `dev`.
- No release or deploy workflow.

## Consequences

Linux only: Android builds do not depend on the host OS for this project. Repository settings:
`docs/development/repository-settings.md`.
