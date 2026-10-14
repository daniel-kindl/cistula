# Contributing

## Workflow

1. Every change starts as a GitHub issue. The issue states the scope and the acceptance criteria.
2. Create a branch from `dev`.
3. Make small commits. Use Conventional Commits.
4. Run `./eng/verify.sh`. It must pass.
5. Open a pull request to `dev`. Link the issue (`Closes #123`).
6. CI must pass. Review threads must be resolved. The PR is squash-merged.

An agent also follows [docs/development/agent-workflow.md](docs/development/agent-workflow.md).

## Branches

| Branch | Purpose |
| --- | --- |
| `main` | Stable. Changes arrive only from `dev` through a release PR. |
| `dev` | Integration. Default target for PRs. |
| `feature/<issue>-<short-desc>` | New behavior. Example: `feature/12-book-list`. |
| `fix/<issue>-<short-desc>` | Defect fix. |
| `chore/<issue>-<short-desc>` | Tooling, CI, docs, dependencies. |

Branches are short-lived. Delete them after merge (GitHub does this automatically).

## Commits and PR titles

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/):
`type(scope): summary`. Types: `feat`, `fix`, `docs`, `test`, `refactor`, `perf`, `build`, `ci`,
`chore`, `revert`. Scopes: `app`, `build`, `ci`, `deps`, `docs`.

The PR title becomes the squash commit message, so CI checks the PR title.
Mark breaking changes with `!` (`feat(app)!: ...`).

## Pull request requirements

- The PR template is complete: related issue, summary, scope, tests, verification results, risks,
  documentation changes.
- `./eng/verify.sh` passes locally and in CI.
- No new warnings, lint findings, or Detekt findings. No baselines. No inline suppressions.
- New dependencies need a reason in the PR and, if significant, an ADR.
- Architecture decisions get an ADR in `docs/decisions/`.

## Testing requirements

- Behavioral changes use test-driven development: write a failing test first.
- Every behavioral change has tests. Bug fixes include a regression test.
- Tests are deterministic: no real network, no real clock, no shared state.
- Details: [docs/development/testing.md](docs/development/testing.md).

## Local setup

See [README.md](README.md#prerequisites).
