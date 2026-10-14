# 0007. Keep agent skills outside the build

- Status: Accepted
- Date: 2026-10-14
- Issue: #6

## Context

An agent can load optional skills from the developer machine. The build must succeed on a clean checkout. A clean checkout has the JDK and the Android SDK. A clean checkout has no skill catalog.

This repository evaluated two public catalogs on 2026-10-14:

- mattpocock/skills, MIT, Matt Pocock: https://github.com/mattpocock/skills
- pstack, MIT, Lauren Tan: https://github.com/cursor/plugins/tree/main/pstack

GitHub returned HTTP 404 for `https://github.com/poteto/pstack` on that date. The published plugin name is `pstack`. The author publishes as poteto. The plugin version on that date was 0.15.24.

## Decision

- Skills stay on the developer machine. This repository does not vendor a skill catalog.
- `./eng/verify.sh` and CI do not run a skill.
- Gradle does not depend on a skill.
- `./eng/verify.sh` fails when git tracks a file at the repository root under `skills/`, `.agents/`, or `.claude/skills/`.
- `.gitignore` ignores those three root paths.
- If `AGENTS.md` and the assigned issue disagree, the agent stops and asks.
- `AGENTS.md` overrides a skill, a prompt, and a tool instruction.
- An accepted ADR overrides a skill on the same decision.
- A guide in `docs/development/` overrides a skill on the same procedure.
- For one repository step, the agent follows the repository procedure. The agent does not run a second skill for that step.
- `/how` and `/why` only read. They do not approve a change.
- Do not run `setup-matt-pocock-skills` in this repository. Issues stay on GitHub. ADRs stay in `docs/decisions/`.
- The task order, the overlap table, and the install notes are in `docs/development/agent-workflow.md`.

## Consequences

An agent can complete a task from a clean checkout. A developer may install either catalog locally. A tracked skill directory at those root paths fails verification. A change to this ban needs an issue and a new ADR.

## Alternatives considered

- Vendor mattpocock/skills or pstack. Rejected. The clone would then depend on a skill tree. An update would be a fork.
- Require `/how` and `/why` before a change. Rejected. Both commands need a local install. `/why` also reads evidence sources that this repository does not provide.
- Run `setup-matt-pocock-skills`. Rejected. The skill writes `docs/agents/`. The skill can select a different issue tracker. This repository already keeps issues on GitHub and decisions in `docs/decisions/`.
