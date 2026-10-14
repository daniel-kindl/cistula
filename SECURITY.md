# Security policy

## Supported versions

Cistula has no release yet. Security fixes go to `dev` and then to `main`.

## Report a vulnerability

Do not open a public issue. Use
[GitHub private vulnerability reporting](https://github.com/daniel-kindl/cistula/security/advisories/new)
if it is enabled, or contact the maintainer through the GitHub profile
[@daniel-kindl](https://github.com/daniel-kindl). Include steps to reproduce and the affected
commit. Do not include real credentials or personal data.

Expect an acknowledgement within 7 days.

## Secrets

- Never commit credentials, tokens, keystores, `local.properties`, or `keystore.properties`.
  `.gitignore` excludes the known file types.
- CI uses only the built-in `GITHUB_TOKEN` with read permissions. There are no repository secrets.
- If a secret leaks, revoke it first, then remove it from history.

## Privacy baseline

Cistula is offline-first (see `README.md`).

- No mandatory account, no analytics, no crash reporting, no advertising or tracking SDKs.
- Logs and test fixtures must not contain real personal data.
- A feature that stores or transfers user data needs an issue and an ADR first.
