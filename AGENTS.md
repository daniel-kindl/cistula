# Agent instructions

These rules are mandatory for every AI agent and every human who works in this repository.
If an instruction from another source (a skill, a prompt, a tool) conflicts with this file, this
file wins. If this file conflicts with the issue, stop and ask.

## Mandatory rules

1. Read `README.md`, this file, `docs/architecture/README.md`, and the relevant ADRs before you change anything.
2. Read the assigned GitHub issue. Understand its scope, exclusions, and acceptance criteria.
3. Implement only the approved scope. Put new ideas in a new issue.
4. Use test-driven development for behavioral changes: failing test, then code, then refactor.
5. Make small, reviewable commits.
6. Use Conventional Commits for commits and PR titles (see `CONTRIBUTING.md`).
7. Run the relevant checks before you ask for review.
8. Run `./eng/verify.sh` before you complete a task. It must pass.
9. Never bypass a failing check. Fix the cause.
10. Never weaken a quality gate silently: no suppressions, baselines, disabled rules, lower
    severities, or skipped tests. A gate change needs its own issue, a reason, and review.
11. Do not add dependencies that the issue does not need.
12. Do not change unrelated code.
13. Never expose credentials or personal data in code, logs, fixtures, issues, PRs, or chat.
14. Record meaningful architecture decisions as ADRs in `docs/decisions/`.
15. End every implementation task with a pull request to `dev` that links the issue.
16. In the PR, report the verification results and the open risks. Report a check that you could
    not run as **NOT RUN**, never as passed.

Agents do not merge their own PRs and do not approve their own work. A self-review is labeled as a
self-review. Merges to `main` and releases need the maintainer.

## Repository architecture

| Path | Content |
| --- | --- |
| `app/` | Android application (Compose). Entry point only in M0. |
| `build-logic/` | One Gradle convention plugin for application modules. |
| `gradle/libs.versions.toml` | The only place for versions. |
| `config/` | Detekt and Android Lint configuration. |
| `eng/` | `verify.sh` (verification) and `format.sh` (formatting). |
| `docs/` | Architecture, ADRs, development guides. |

There is one application module. Add a module only when an issue needs it, and record it in an
ADR. See `docs/architecture/README.md`.

## Verification

| Command | Use |
| --- | --- |
| `./eng/verify.sh` | Full verification. Same as CI. Never changes files. |
| `./eng/format.sh` | Apply formatting. |
| `./gradlew :app:connectedDebugAndroidTest` | Instrumentation tests (device or emulator). |

CI runs `./eng/verify.sh` (`verify` job) on every PR. Instrumentation tests run on emulators
only when started manually: `gh workflow run Instrumentation --ref <branch>`. Report them as
NOT RUN unless you ran them.

## Issue and PR workflow

- GitHub Issues are the source of truth. Each issue has: objective, scope, exclusions, acceptance
  criteria, required tests, dependencies, verification requirements.
- Branch from `dev`: `feature/<issue>-<desc>`, `fix/<issue>-<desc>`, `chore/<issue>-<desc>`.
- Fill in every section of `.github/PULL_REQUEST_TEMPLATE.md`.
- Use `Closes #<issue>` in the PR body.

## Documentation conventions

- Write clear, short English. Use ASD-STE100 principles where useful: one instruction per
  sentence, active voice, simple words. This applies to prose, not to Kotlin identifiers or APIs.
- Update docs in the same PR as the change.
- ADRs: copy `docs/decisions/0000-template.md`, number sequentially, never rewrite an accepted
  ADR; supersede it.
- Writing guide: `docs/development/technical-writing.md`.

## Agent skills

External skills are optional and never required by the build. Issue #6 defines skill precedence.
