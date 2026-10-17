# Repository settings

State checked on 2026-10-17 during the M0 handoff. The repository is **public**.

## Applied

| Setting | Value |
| --- | --- |
| Merge methods | Squash only (merge commits and rebase merges off) |
| Squash commit | Title = PR title, message = PR body |
| Delete branch on merge | On |
| Default workflow token permissions | Read; Actions cannot approve PRs |
| Dependabot alerts | On |
| Dependabot security updates | On |
| Dependabot version updates | `.github/dependabot.yml` (Gradle, Actions; weekly; target `dev`) |
| Rulesets | `protect-main` and `protect-dev`, active, from `.github/rulesets/` |
| Secret scanning | On |
| Secret scanning push protection | On |
| Private vulnerability reporting | On |
| Code scanning (CodeQL default setup) | Configured for `actions` and `java-kotlin` |
| Dependency review | `dependency-review` CI job on PRs |

## Rulesets

Both rulesets require a PR, 0 approvals (single maintainer; agent reviews are recorded in the PR),
dismissal of stale reviews, resolved threads, squash only, linear history, no force pushes, no
deletion, and these up-to-date status checks from GitHub Actions (`integration_id` 15368):
`verify`, `pr-title`, `dependency-review`. The `Instrumentation` workflow is manual and is not a
required check. The repository admin role (`RepositoryRole` 5) can always bypass.

Recreate them with:

```sh
for b in main dev; do
  gh api -X POST repos/daniel-kindl/cistula/rulesets --input .github/rulesets/protect-$b.json
done
```

## CodeQL

The live default setup enables both `actions` and `java-kotlin`. PR #19 brought the workflow
files to `main` on 2026-10-14. The earlier attempt to enable `actions` before that promotion
returned HTTP 422. That setup limitation no longer applies.

If CodeQL default setup fails to build Kotlin, use an advanced `codeql.yml` workflow with
`build-mode: manual` and `./gradlew :app:assembleDebug`.
