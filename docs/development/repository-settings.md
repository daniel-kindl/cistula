# Repository settings

State on 2026-10-14. The repository is **public**. Nobody changed the repository visibility.

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
| Code scanning (CodeQL default setup) | Configured for `java-kotlin` |
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

## Not applied yet

| Feature | API call | Response |
| --- | --- | --- |
| CodeQL for `actions` | `PATCH /repos/{repo}/code-scanning/default-setup` with `java-kotlin` and `actions` | 422 "One or more languages you selected are not present in the repository." |

The default branch (`main`) has no workflow files yet. After the M0 tooling reaches `main`, add
`actions`:

```sh
gh api -X PATCH repos/daniel-kindl/cistula/code-scanning/default-setup \
  -f state=configured -f query_suite=default -f 'languages[]=java-kotlin' -f 'languages[]=actions'
```

If CodeQL default setup fails to build Kotlin, use an advanced `codeql.yml` workflow with
`build-mode: manual` and `./gradlew :app:assembleDebug`.
