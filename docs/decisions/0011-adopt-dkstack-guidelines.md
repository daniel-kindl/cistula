# 0011. Adopt dkstack shared guidelines

- Status: Proposed
- Date: 2026-10-28
- Issue: #27

## Context

The owner requested adoption of dkstack's `v1/AGENTS.md` for Cistula.
The shared entry point links to task triage and topic-specific guidelines.
It requires consumers to link shared rules instead of duplicating them.
Cistula already has project-specific architecture, verification, and review requirements.

## Decision

- Link [dkstack v1](https://raw.githubusercontent.com/daniel-kindl/dkstack/v1/AGENTS.md) from Cistula's `AGENTS.md`.
- Start tasks with the linked triage skill. Read only the sections that the task needs.
- Resolve relative dkstack links against the source document URL.
- Keep Cistula requirements in this repository. They override shared guidance on the same subject.
- Keep `dev` as the integration branch and PR target. Update working branches from `dev`.
- Agents do not merge PRs. Required self-reviews do not replace independent PR review.
- The maintainer controls merges to `main` and releases. Shared agent names do not assign local roles.
- Keep the required `./eng/verify.sh` check and both local review procedures.
- Do not vendor shared rules or install skills as part of repository tasks.
- Report an unavailable required document. Do not claim compliance with unread guidance.

This decision supersedes ADR 0007 only where optional skill use would make dkstack's triage and guideline reads optional.
The local procedure still takes precedence for each repository step.
ADR 0007's skill storage, installation, build, and CI restrictions remain in effect.
Accepted ADR text remains unchanged.

## Consequences

Shared rule changes remain in dkstack. Cistula stores links and project-specific requirements.
The `v1` URL follows dkstack's major-version branch. Its contents can change without a Cistula commit.
Agents need network access to read shared documents unless an available local copy supplies them.
The build and CI remain independent of dkstack availability. No quality gate changes.

## Alternatives considered

- Copy dkstack into Cistula. Rejected because the source requires links instead of duplicate shared rules.
- Replace all local instructions with the shared entry point. Rejected because Cistula still needs project-specific requirements.
- Pin a release tag or commit. Rejected for this adoption because the owner requested the `v1` entry point.
