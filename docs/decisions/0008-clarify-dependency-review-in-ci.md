# 0008. Clarify dependency review in CI

- Status: Proposed
- Date: 2026-10-17
- Issue: #13

## Context

The review of PR #10 found gaps in the dependency review description in ADR 0006.
Issue #13 permits documenting the fork PR gap instead of changing CI behavior.
The maintainer authorized a new ADR because repository rules prohibit rewriting an accepted ADR.

This ADR supersedes the dependency submission and dependency review descriptions in
[ADR 0006](0006-ci-strategy.md). The other decisions in ADR 0006 still apply.

## Decision

- Document the existing fork PR gap. A fork PR skips `dependency-submission` because its token
  has read-only access. The `dependency-review` job needs that job and is therefore also skipped.
  GitHub accepts a skipped required check. Thus, fork PRs receive no dependency review from
  this workflow, including no review of Gradle or GitHub Actions dependencies.
- Keep `contents: write` on the `dependency-submission` job only. The submission API requires
  this permission. The job runs on pushes to `main` and `dev` and on same-repository PRs.
  Fork PRs cannot run this job. The Gradle action is pinned to a full commit SHA, and checkout
  uses `persist-credentials: false`.
- Accept that the submission job executes PR-controlled Gradle code with a write token on
  same-repository PRs. Those authors already need repository write access. The pinned action
  and checkout setting do not remove this risk.
- Correct the missing baseline description. If the base has no Gradle snapshot but the head
  has one, the comparison reports every head Gradle dependency as added. Existing vulnerable
  dependencies can then fail the review. This makes the comparison stricter, not less complete.
  A successful submission on a push to `main` or `dev` provides a base snapshot for later PRs.

## Consequences

A green required check does not prove that a fork PR passed dependency review. A maintainer
must account for this gap when reviewing a fork PR. This decision records the gap and does not
add an automated check for forks.

CI permissions, job conditions, dependencies, and quality checks stay unchanged. ADR 0006
remains unchanged as the historical record.

## Alternatives considered

- Run dependency review after a skipped submission job. This would require a separate strategy
  for the missing Gradle head snapshot on fork PRs. Issue #13 also permits documentation.
- Fail dependency review for all fork PRs. This would block fork contributions until a
  maintainer provides another review path. This change uses the documentation option in #13.
- Rewrite ADR 0006. Repository rules require a new ADR for an accepted decision.

## References

- [Review findings from PR #10](https://github.com/daniel-kindl/cistula/pull/10#issuecomment-6168455487).
- [GitHub Actions job dependencies](https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-jobs#defining-prerequisite-jobs).
- [Skipped required checks](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#require-status-checks-before-merging).
- [Current CI workflow](../../.github/workflows/ci.yml).
