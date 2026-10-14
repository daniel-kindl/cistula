# Agent workflow

An implementation task ends with a pull request to `dev`. The agent does not merge that pull request. The agent does not approve that pull request.

## Order

1. Read the assigned issue. Done when you can name the scope, the exclusions, and the acceptance criteria.
2. Read `README.md`, `AGENTS.md`, `docs/architecture/README.md`, and each ADR that the change touches.
3. Create a branch from `dev`. Use `feature/<issue>-<desc>`, `fix/<issue>-<desc>`, or `chore/<issue>-<desc>`.
4. When the issue adds behavior, write a failing test. Done when the test fails for the expected reason. Skip this step when the issue adds no behavior.
5. Make the test pass. Then refactor. Skip this step when you skipped step 4.
6. Change only the issue scope. Put a new idea in a new issue.
7. Run `./eng/verify.sh`. Done when the script prints `PASS: all verification steps succeeded.`
8. Complete the specification review in this document.
9. Complete the standards review in this document.
10. Open a pull request to `dev`. Use `.github/PULL_REQUEST_TEMPLATE.md`. The body contains `Closes #<issue>` for this issue only. The body records the verify result and both reviews.
11. Stop. The maintainer merges.

Report a command you did not run as **NOT RUN**.

## Reviews

Run the two reviews separately. A review by the author is a self-review. Label a self-review as a self-review.

### Specification review

Compare the diff with the issue.

- The diff does the work the issue requests.
- The diff stays inside the exclusions.
- The diff does not add behavior that the issue does not name.

Done when the review lists each mismatch, or states that there is no mismatch. Fix each mismatch before you open the pull request. Record the result in the pull request.

### Standards review

Compare the diff with the repository rules.

- The diff follows `AGENTS.md`.
- The diff follows the accepted ADRs.
- New prose follows `docs/development/technical-writing.md`.
- The diff adds no warning, suppression, baseline, or dependency that the issue does not allow.

Done when the review lists each mismatch, or states that there is no mismatch. Fix each mismatch before you open the pull request. Record the result in the pull request.

## File map

The file map is in `AGENTS.md`.

## Architecture guardrails

Read `docs/architecture/README.md` before a design change.

- The application has one module, `app`.
- A new module needs an issue and an ADR.
- A feature dependency needs an issue and an ADR.
- `./eng/verify.sh` rejects a known feature library or tracking library in `gradle/libs.versions.toml`.
- An agent skill is not a module. An agent skill is not a build dependency. ADR 0007 records that decision.

## Skill registry

This repository evaluated two public catalogs on 2026-10-14. This repository does not vendor either catalog.

| Catalog | Location | License | Router |
| --- | --- | --- | --- |
| mattpocock/skills | https://github.com/mattpocock/skills | MIT, Matt Pocock | `ask-matt` |
| pstack | https://github.com/cursor/plugins/tree/main/pstack | MIT, Lauren Tan | `poteto-mode` |

GitHub returned HTTP 404 for `https://github.com/poteto/pstack` on that date. The plugin name is `pstack`. The plugin version on that date was 0.15.24.

mattpocock/skills has two invocation types. A user-invoked skill runs when a person types its name. A model-invoked skill can also run when the task matches its description.

pstack uses `/poteto-mode` to select a playbook. `/how` explains how a subsystem runs. `/why` explains why the code has its current shape. `/why` reads git history. `/why` also reads an evidence source that the local session exposes.

A later catalog release does not change this decision.

## Overlap

| Repository step | mattpocock/skills | pstack | Follow |
| --- | --- | --- | --- |
| Failing test, then code | `tdd` | `tdd` | `AGENTS.md` rule 4 |
| Specification review and standards review | `code-review` | `interrogate` | The reviews in this document |
| ADR | `domain-modeling` | domain-model principle | `docs/decisions/` |
| Technical English | `writing-for-agents` | `technical-writing` | `docs/development/technical-writing.md` |
| Runtime explanation | none | `how` | Only read. It does not approve a change. |
| Decision rationale | none | `why` | Only read. ADRs and git history stay the record. |
| Pull request | `pr` | `opening-a-pr` | The pull request template and `CONTRIBUTING.md` |
| Issue tracker setup | `setup-matt-pocock-skills` | `setup-pstack` | Do not run setup in this repository. |

`code-review` uses a specification axis and a standards axis. `interrogate` asks other models to break a diff. Neither skill replaces the reviews in this document.

`setup-matt-pocock-skills` writes `docs/agents/`. The skill can select a different issue tracker. Do not run it in this repository. Issues stay on GitHub. ADRs stay in `docs/decisions/`.

`setup-pstack` writes a model rule on the developer machine. It does not change this repository. A developer may run it locally. A developer may skip it.

## Precedence

Apply the first matching row.

| Conflict | Action |
| --- | --- |
| `AGENTS.md` and the assigned issue disagree | Stop and ask. |
| A skill, a prompt, or a tool instruction disagrees with `AGENTS.md` | Follow `AGENTS.md`. |
| A skill disagrees with an accepted ADR | Follow the ADR. |
| A skill disagrees with a guide in `docs/development/` | Follow the guide. |
| Two skills match one repository step | Follow the repository procedure. Do not run the second skill. |

A skill may suggest a method. A skill does not add scope. A skill does not add a dependency. A skill does not add a suppression. A skill does not skip `./eng/verify.sh`.

## Local installation

Installation is optional. A clean checkout does not need a skill catalog.

Do not run an install command as part of a repository task. Do not commit the installed files.

`./eng/verify.sh` fails when git tracks a file at the repository root under `skills/`, `.agents/`, or `.claude/skills/`.

On 2026-10-14 the mattpocock/skills README showed this command:

```text
npx skills@latest add mattpocock/skills
```

On 2026-10-14 the pstack README showed this Cursor command:

```text
/add-plugin pstack
```

Another agent may copy skill files onto the developer machine. The copy stays outside this repository.
