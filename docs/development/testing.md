# Testing

ADR 0005 records the decisions. This page is the procedure.

## What the tests check

The application has one screen. The screen shows the application name.

`CistulaAppViewModel` takes that title and exposes `CistulaAppContent`. `MainActivity` passes the
string resource `app_name`. The composable shows `content.title`.

Issue #30 adds JVM tests for the [book domain contract](../architecture/books.md).
They cover ISBN validation, partial metadata, immutable collections, local identity, reading transitions, and user data preservation.
Issue #31 adds JVM mapping tests and real Room storage tests in `androidTest`.
Storage tests use disposable files to check CRUD, ordered observation, reopen persistence, rollback, and migration refusal.
See the [storage contract](../architecture/book-storage.md).

## Where tests go

| Source set | Purpose |
| --- | --- |
| `app/src/test/java` | JVM unit tests. JUnit 4. No Android framework. |
| `app/src/androidTest/java` | Instrumentation tests and Compose UI tests. |
| `app/src/testFixtures/java` | Synthetic data shared by the two test source sets. |

The shared fixture title is `Fixture Title`. It is not the application name. It is not user data.

## Rules

- Write the failing test first when you add or change behavior.
- Do not use the network, the clock, `Thread.sleep`, or shared mutable state in a test.
- Do not put real personal data in a fixture.
- Use JUnit assertions. Do not add a second assertion library for one check.
- Call `androidx.compose.ui.test.junit4.v2.createAndroidComposeRule` in a Compose UI test.
- Do not call the deprecated `androidx.compose.ui.test.junit4` rule. Kotlin warnings fail the build.
- Do not call `setContent` on that rule when the activity already set content.
- Call `androidx.activity.compose.setContent` on the activity from `runOnUiThread`.
- Call `waitForIdle` after that call.

## Commands

| Command | Result |
| --- | --- |
| `./eng/verify.sh` | Runs the unit tests and writes the unit-test coverage report. It does not run instrumentation tests. |
| `./gradlew :app:testDebugUnitTest` | Unit tests only. |
| `./gradlew :app:createDebugUnitTestCoverageReport` | Unit tests and the HTML report. |
| `./gradlew :app:connectedDebugAndroidTest` | Instrumentation tests. This needs a device or an emulator. |
| `./gradlew :app:createDebugAndroidTestCoverageReport` | Instrumentation tests and the HTML report. This needs a device or an emulator. |
| `gh workflow run Instrumentation --ref <branch>` | Manual CI emulator run on API 26. See ADR 0006. |

Unit-test report path: `app/build/reports/coverage/test/debug/index.html`.

Instrumentation report path: `app/build/reports/coverage/androidTest/debug/connected/index.html`.

There is no coverage percentage gate. A low number does not fail the build.

`testOptions.animationsDisabled` is true. The Instrumentation workflow also disables animations.
