# 0009. Promote dev with a temporary branch

- Status: Proposed
- Date: 2026-10-17
- Issue: #21

## Context

PR #19 gave `main` and `dev` identical files through a squash merge. Their commit histories
then differed. A later direct PR from `dev` repeats earlier changes in its three-dot diff.
The documented fast-forward from `main` to `dev` cannot run against these histories.

Both protected branches require PRs, squash merges, linear history, and current status checks.
A fix must preserve those rules and must not rewrite either branch.

## Decision

This ADR supersedes ADR 0004's release PR source and version-update timing. The other version
decisions in [ADR 0004](0004-versioning-strategy.md) still apply.

- Prepare the version and changelog through a normal PR to `dev`. Merge that PR before promotion.
- Pin the `dev` source commit and the current `main` base commit.
- Check that the previous promoted `dev` source is an ancestor of the new source. Its tree must
  equal the current `main` tree. Stop if either check fails. A mismatch requires a separately
  reviewed recovery plan that establishes a verified baseline. Copying work into `dev` alone
  cannot repair the previous source-to-base tree check. Do not bypass that check.
- Create a temporary promotion branch from the pinned `dev` source. On that branch, merge the
  pinned `main` commit with `git merge --no-ff --strategy=ours`. This records the base ancestry
  and preserves all files from the pinned source. It deliberately ignores the base tree.
- Check that the promotion tree equals the source tree and that the base is its ancestor.
  Review the PR diff against the direct base-to-source tree diff. Record the source, base,
  previous source, and tree hashes in the PR.
- The maintainer reviews and squash-merges the promotion PR to `main` after all required checks
  pass. The temporary merge commit does not enter either protected branch.
- After merge, check that `main` has the pinned source tree. Do not move `dev` to the new
  `main` commit. The released contents match the source even though their histories differ.
- If `main` advances before merge, repeat the baseline checks, prepare against the new base,
  and rerun verification and CI. Do not bypass the up-to-date requirement.

## Consequences

Each promotion uses a new branch. Its PR diff starts at the current `main` commit and contains
only the intended content changes. Protected branch histories remain linear after squash merge.
Normal development can continue on `dev` after the source commit is pinned.

The `ours` strategy is safe here only after the baseline checks. It cannot detect work that
exists only on `main`. The promotion record must preserve the source SHA for the next baseline
check. GitHub's default three-dot comparison of `main` and `dev` remains unsuitable for release
review. Review the prepared promotion branch instead.

The command procedure is in [versioning.md](../development/versioning.md). The local proof is
`python3 eng/check-branch-flow.py`. It uses a temporary repository and performs no real release.

## Alternatives considered

- Fast-forward `dev` to `main`. Squash promotion leaves divergent histories, so this fails.
- Repeat direct `dev` to `main` PRs. Their merge base remains old and their diffs repeat changes.
- Allow merge commits on protected branches. This changes the existing rulesets and is not
  needed for a temporary branch followed by a squash merge.
- Rebase or reset `dev` after each release. This rewrites protected history and requires a
  force push.
- Copy the source tree onto a branch from `main`. This can give the same content result, but
  departs from the repository rule to create working branches from `dev`.

## References

- [Git merge strategies](https://git-scm.com/docs/git-merge#_merge_strategies).
- [GitHub linear history requirement](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#require-linear-history).
- [GitHub merge methods](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/configuring-pull-request-merges/about-merge-methods-on-github).
