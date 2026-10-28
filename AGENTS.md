# Agent instructions

## Shared guidelines

Read and follow [dkstack v1](https://raw.githubusercontent.com/daniel-kindl/dkstack/v1/AGENTS.md).
Start each task with its [triage skill](https://raw.githubusercontent.com/daniel-kindl/dkstack/v1/skills/triage/SKILL.md).
Read the sections that triage selects. Resolve relative links against the linked dkstack document, not this checkout.
Keep shared rules in dkstack. Link to them instead of copying them into Cistula.
Do not change dkstack from this project.

The following sections define Cistula-specific requirements and overrides.
Cistula's requirements take precedence over shared guidelines on the same subject.
If a required shared document is unavailable, report the blocked read. Do not claim that you applied unread rules.

## Project authority

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
15. Before the pull request, complete a specification review and a standards review. The procedure
    is `docs/development/agent-workflow.md`. Record both results in the pull request.
16. End every implementation task with a pull request to `dev` that links the issue.
17. In the PR, report the verification results and the open risks. Report a check that you could
    not run as **NOT RUN**, never as passed.

Agents do not merge PRs and do not approve their own work. A self-review is labeled as a
self-review. Merges to `main` and releases need the maintainer.

Cistula uses `dev` as its integration branch and PR target, including when shared examples use `main`.
Update a working branch from `dev`. Follow Cistula's release procedure for promotion to `main`.
Complete the required self-reviews before opening a PR. They do not replace independent PR review.
These requirements override dkstack's branch examples and reviewer merge permissions.
The dkstack agent names do not assign roles to Cistula agents.

## Repository architecture

| Path | Content |
| --- | --- |
| `app/` | Android application (Compose). Entry point only in M0. |
| `build-logic/` | One Gradle convention plugin for application modules. |
| `gradle/libs.versions.toml` | The only place for versions. |
| `config/` | Detekt and Android Lint configuration. |
| `eng/` | `verify.sh` (verification) and `format.sh` (formatting). |
| `docs/` | Architecture, ADRs, development guides. |
| `.github/` | Issue templates, pull request template, workflows, and rulesets. |
| `AGENTS.md` | Agent rules for every task. |

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
- ADRs: copy `docs/decisions/0000-template.md` and number them in sequence. Never rewrite an
  accepted ADR. Supersede an accepted ADR with a new ADR.
- Writing guide: `docs/development/technical-writing.md`.

## Agent skills

Reading dkstack's triage and selected guidelines is required. It does not require a local skill installation.
Other skill catalogs are optional. The build does not run skills. CI does not run skills.

Install a skill on the developer machine. Do not commit a skill directory.

`./eng/verify.sh` fails when git tracks a file at the repository root under `skills/`, `.agents/`, or `.claude/skills/`.

For one repository step, follow the repository procedure. Use dkstack guidance where that procedure does not define the step.
Do not run a second skill for the same step.

`/how` and `/why` only read. They do not approve a change.

The registry, the overlap, and the local install notes are in `docs/development/agent-workflow.md`.
ADR 0007 records the local skill policy. ADR 0011 records dkstack adoption and its precedence.
