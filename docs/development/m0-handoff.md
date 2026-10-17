# M0 handoff evidence

## Status

Issue #9 remains open. Verification passes, but the DAGP compatibility warning remains.
The zero-warning acceptance criterion is not met. Issue #11 tracks the warning. The latest
upstream release checked on 2026-10-17 is still 3.19.2, the version already in this repository.
Do not close M0 parent issue #1 until all child work has merged and its acceptance criteria hold.

## Verified source and environment

The check used a fresh clone of `dev` at
`2c9a2f224fd5bb7279f293a50f3a2b6f6072ca31` on 2026-10-17. This is the merge commit for PR #20.
The checkout had no local build outputs or `local.properties` before verification.
The machine supplied its existing Android SDK and Gradle dependency and build caches.
This was a fresh-checkout check, not a test of an empty dependency cache or a newly provisioned OS.

| Item | Observed value |
| --- | --- |
| JDK | OpenJDK 25.0.4.1, Red Hat build |
| Gradle | 9.7.1 from the committed wrapper |
| Android platform | Installed `platforms/android-37.0` |
| Build-Tools | Installed `build-tools/36.0.0` |
| SDK location | `ANDROID_HOME` from the machine environment |
| Local skill catalog | Not used by the build or verification command |

## Commands and results

Run the following commands on a machine that meets the requirements in the repository README.
Use a new directory for the clone.

```sh
git clone --single-branch --branch dev https://github.com/daniel-kindl/cistula.git cistula-m0-check
cd cistula-m0-check
git checkout --detach 2c9a2f224fd5bb7279f293a50f3a2b6f6072ca31
./eng/verify.sh
./gradlew --console=plain --warning-mode=fail --no-build-cache --rerun-tasks :app:testDebugUnitTest :app:lintDebug
git status --short
```

`./eng/verify.sh` passed all nine steps. The second Gradle command also passed. It reran the
unit test and Lint without cached task results. The tracked working tree remained clean.

| Evidence | Result |
| --- | --- |
| Wrapper checksum and repository policies | PASS |
| Spotless and Detekt | PASS |
| `app/build/reports/lint-results-debug.xml` | 0 issues |
| `app/build/test-results/testDebugUnitTest/TEST-dev.danielkindl.cistula.ui.CistulaAppViewModelTest.xml` | 1 test, 0 failures, 0 errors, 0 skipped |
| `app/build/reports/coverage/test/debug/index.html` | Generated; no percentage gate |
| `app/build/outputs/apk/debug/app-debug.apk` | Built |
| `app/build/outputs/apk/release/app-release-unsigned.apk` | Built; unsigned by design |
| `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk` | Built |
| `buildHealth` | PASS |
| DAGP compatibility warning | Present at configuration time; unresolved in #11 |
| API 26 instrumentation tests | PASS; manual CI run at the verified SHA |

The warning occurred six times during the fresh-checkout verification. DAGP reports that its
known AGP range ends at 9.3.1, while this project uses AGP 9.4.1. It is a plugin log message,
so Gradle's warning failure mode does not reject it. A passing command does not resolve #11.

## CI evidence

- [CI on the verified dev commit](https://github.com/daniel-kindl/cistula/actions/runs/39018067010) passed.
- [Manual API 26 instrumentation run](https://github.com/daniel-kindl/cistula/actions/runs/39018083824) passed at the same commit.

The instrumentation run was started with `gh workflow run Instrumentation --ref dev`.
The ordinary verification command builds the test APK but does not run device tests.

## Branch and repository checks

Live repository settings checked on 2026-10-17 match the intended integration policy:

- `main` is the default branch. Implementation PRs target `dev`.
- Only squash merges are enabled. Branch deletion after merge is enabled.
- Both active rulesets require a PR, resolved review threads, linear history, and up-to-date
  `verify`, `pr-title`, and `dependency-review` checks. Required approval count is zero.
- The administrator bypass remains configured. This handoff did not change the rulesets.
- CodeQL default setup enables `actions` and `java-kotlin`.

PR #19 promoted `dev` to `main` on 2026-10-14. PR #20 subsequently changed `dev` only.
At this check, `main` is `28f081021586ee1d5029c9a7210aa1902ce9af74`.
Neither branch is an ancestor of the other after squash promotion. The fast-forward instruction
in `versioning.md` therefore cannot apply to the current histories. Issue #21 tracks the
procedure correction. No release, tag, or merge to `main` was performed during this handoff.

## Open work and next-agent procedure

Issues #2 through #8 and #13 are closed. These are the completed foundation tasks.
The follow-ups below remain open and prevent an unconditional M0 handoff.

| Issue | Remaining work |
| --- | --- |
| #11 | Upgrade DAGP when a stable release supports AGP 9.4; rerun verification without the compatibility warning. |
| #12 | Remove the embedded Kotlin advisory exception after a compatible toolchain change. |
| #21 | Correct and prove the main/dev synchronization procedure for squash-only promotion. |
| #9 | Finish the clean-room handoff after the remaining acceptance gaps are resolved. |
| #1 | Reconcile the child checklist and close M0 only after the child work has merged. |

1. Read the assigned issue, `AGENTS.md`, the repository README, and the relevant ADRs.
2. Check the current `dev` SHA and open follow-up issues. The results above apply to the recorded SHA.
3. Use a short-lived branch from `dev` for each approved issue.
4. Run `./eng/verify.sh` from a clean tracked working tree.
5. Inspect the Lint and test XML files. Record the actual counts and any warning in the output.
6. Run the manual instrumentation workflow when device evidence is needed. Record its SHA and result.
7. Complete separate specification and standards reviews. Issue #9 requires independent reviewers.
8. Open a PR to `dev` with the evidence and remaining risks. Do not report #9 complete while a criterion fails.

## References

- [Issue #9](https://github.com/daniel-kindl/cistula/issues/9).
- [DAGP latest release checked](https://github.com/autonomousapps/dependency-analysis-gradle-plugin/releases/tag/v3.19.2).
- [Testing procedure](testing.md).
- [Repository settings](repository-settings.md).
- [Agent workflow](agent-workflow.md).
