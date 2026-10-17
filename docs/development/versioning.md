# Branches, versions, and releases

Decision records: [ADR 0004](../decisions/0004-versioning-strategy.md) and the proposed promotion
procedure in [ADR 0009](../decisions/0009-promote-dev-with-a-temporary-branch.md).

- `main`: stable. `dev`: integration. Short-lived `feature/*`, `fix/*`, `chore/*` from `dev`.
- All changes go through squash-merged PRs. The PR title is a Conventional Commit.
- Single version source: `cistula.version` in `gradle.properties`. Current: `0.0.0`.
- `versionCode = MAJOR * 1_000_000 + MINOR * 1_000 + PATCH + 1`.

## Release procedure (not used in M0)

The maintainer performs releases. This procedure changes no ruleset and uses no administrator bypass.

1. Create a short-lived branch from `dev` for the release issue.
2. Set `cistula.version` and move the changelog entries from Unreleased to the release section.
3. Run verification and complete review. Open a PR to `dev`. Merge it after CI passes.
4. Fetch `origin`. Pin the release source from `origin/dev` and the base from `origin/main`.
5. Read the previous promotion PR. Find its recorded source SHA. Confirm that source is an
   ancestor of the new source and has the same tree as the current base.
6. If either check fails, stop normal promotion. Require a separately reviewed recovery plan
   that establishes a verified baseline. A PR to `dev` alone cannot repair this tree check.
7. Create a new promotion branch from the pinned source. Merge the pinned base with the `ours`
   strategy on this temporary branch. Verify the source tree and base ancestry.
8. Run `./eng/verify.sh`. Open the promotion PR to `main`. Use `chore(release): vX.Y.Z` as its title.
9. Record the hashes and review the exact promotion diff. Wait for required CI checks and maintainer review.
10. Fetch again before merge. If `main` moved, repeat preparation and checks against its new tip.
11. Squash-merge the reviewed promotion PR. Confirm the merge commit tree equals the pinned source tree.
12. The maintainer tags that merge commit on `main`. Use `git tag -s vX.Y.Z <merge-sha> -m "vX.Y.Z"`,
    then `git push origin vX.Y.Z`.
13. Delete the temporary promotion branch. Leave `dev` at its own tip.

`--strategy=ours` preserves the whole source tree. It is different from `-X ours`, which only
prefers one side for conflicting lines. Do not substitute `-X ours`.

## Prepare the promotion branch

Start with a clean working tree. Set `PREVIOUS_PROMOTION_SOURCE` to the source SHA recorded in
the previous promotion PR. Set `PROMOTION_BRANCH` to a new `chore/<issue>-promote-v<version>` name.
Run this block from the repository root. A failed check stops the block before the merge.

```sh
(
    set -eu
    previous_source=${PREVIOUS_PROMOTION_SOURCE:?Set the previous promotion source SHA}
    promotion_branch=${PROMOTION_BRANCH:?Set a new promotion branch name}
    test -z "$(git status --porcelain)"
    git fetch --prune origin
    promotion_source=$(git rev-parse origin/dev)
    promotion_base=$(git rev-parse origin/main)
    git merge-base --is-ancestor "$previous_source" "$promotion_source"
    test "$(git rev-parse "${previous_source}^{tree}")" = "$(git rev-parse "${promotion_base}^{tree}")"
    git switch -c "$promotion_branch" "$promotion_source"
    git merge --no-ff --strategy=ours "$promotion_base" -m "chore(release): prepare promotion"
    git merge-base --is-ancestor "$promotion_base" HEAD
    test "$(git rev-parse 'HEAD^{tree}')" = "$(git rev-parse "${promotion_source}^{tree}")"
    printf 'source=%s\nbase=%s\nprevious_source=%s\ntree=%s\n' \
        "$promotion_source" "$promotion_base" "$previous_source" "$(git rev-parse 'HEAD^{tree}')"
    git diff --stat "${promotion_base}...HEAD"
    ./eng/verify.sh
)
```

Copy the printed hashes into the promotion PR. Publish the branch and open the PR to `main`
only after verification passes. The temporary merge commit stays on this branch. GitHub's
squash merge writes one single-parent commit to `main`, so its history stays linear.
The base is an ancestor of the promotion head, as the up-to-date rule requires.

For the first promotion after PR #19, the previous source is
`1fcb7f5d98db80eb1f3b9609c2603606fb1d977f`. Its tree matches the `main` commit
`28f081021586ee1d5029c9a7210aa1902ce9af74`. The baseline checks still apply.

After promotion, the source and released trees match. `dev` can already contain later changes.
Do not fast-forward, reset, or force-push either protected branch to make their histories match.

## Prove the procedure locally

Run `python3 eng/check-branch-flow.py`. This requires Python 3 and Git. It uses only the Python
standard library and a temporary repository. It does not change this checkout or call GitHub.

The proof reproduces the old direct PR diff, then checks two promotions and a later `dev`
change. The first corrected diff contains only `next.txt`. The second contains `next.txt`,
the deletion of `old.txt`, and `release2.txt`. The next prepared diff contains only `later.txt`.
It checks source trees, base ancestry, linear protected histories, and the unchanged `dev` ref.
It also rejects unexpected `main` contents and detects a base that advances after preparation.

The proof models local Git operations. Live rulesets and Actions are checked separately in
the implementation PR. It does not create a release or test GitHub enforcement by bypassing it.

No workflow publishes artifacts. Signing and store publishing need a separate issue.
