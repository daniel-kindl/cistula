# Repository settings

State on 2026-10-14. The repository is **private** on the GitHub Free plan. Some features need a
public repository, GitHub Pro, or GitHub Advanced Security. They are listed below with the exact
API response. Nobody changed the repository visibility.

## Applied

| Setting | Value |
| --- | --- |
| Merge methods | Squash only (merge commits and rebase merges off) |
| Squash commit | Title = PR title, message = PR body |
| Delete branch on merge | On |
| Auto-merge | Off |
| Dependabot alerts | On |
| Dependabot security updates | On |
| Dependabot version updates | `.github/dependabot.yml` (Gradle, Actions; weekly; target `dev`) |
| Workflow token | `contents: read` in every workflow; `pull-requests: read` for `pr-title` |

## Not applied: private repo/plan

| Feature | API call | Response |
| --- | --- | --- |
| Rulesets for `main` and `dev` | `GET/POST /repos/{repo}/rulesets` | 403 "Upgrade to GitHub Pro or make this repository public to enable this feature." |
| Classic branch protection | `PUT /repos/{repo}/branches/main/protection` | 403, same message |
| Code scanning (CodeQL) | `PATCH /repos/{repo}/code-scanning/default-setup` | 403 "Code scanning is not enabled for this repository." (needs GitHub Advanced Security on private repos) |
| Secret scanning and push protection | `PATCH /repos/{repo}` `security_and_analysis` | 422 "Secret scanning is not available for this repository." |
| Private vulnerability reporting | `PUT /repos/{repo}/private-vulnerability-reporting` | 404 (public repositories only) |
| Dependency review action | not added | Needs GitHub Advanced Security on private repositories |

Until rulesets work, the rules below are process rules (`AGENTS.md`, `CONTRIBUTING.md`), not
enforced by GitHub. Direct pushes to `main` and `dev` are technically possible.

## Apply when the repository is public (or on GitHub Pro)

Rulesets are ready in `.github/rulesets/`. Both require a PR, 0 approvals (single maintainer;
agent reviews are recorded in the PR), dismissal of stale reviews, resolved threads, squash only,
linear history, no force pushes, no deletion, and these up-to-date status checks from GitHub
Actions (`integration_id` 15368): `verify` and `pr-title`.
The `Instrumentation` workflow is manual and is not a required check. The repository admin role (`RepositoryRole` 5) can always bypass.

```sh
for b in main dev; do
  gh api -X POST repos/daniel-kindl/cistula/rulesets --input .github/rulesets/protect-$b.json
done
gh api -X PATCH repos/daniel-kindl/cistula --input - <<'JSON'
{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}
JSON
gh api -X PUT repos/daniel-kindl/cistula/private-vulnerability-reporting
gh api -X PATCH repos/daniel-kindl/cistula/code-scanning/default-setup \
  -f state=configured -f query_suite=default -F 'languages[]=java-kotlin' -F 'languages[]=actions'
```

CodeQL default setup builds Kotlin automatically. If it fails on this build, use an advanced
`codeql.yml` workflow with `build-mode: manual` and `./gradlew :app:assembleDebug :wear:assembleDebug`.
Add the dependency review action to CI at the same time.
