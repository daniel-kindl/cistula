# 0005. Testing strategy

- Status: Accepted
- Date: 2026-10-14
- Issue: #4

## Context

The application has one screen. The screen shows the application name. M0 has no book behavior.
Tests must be deterministic. A coverage number with no product basis must not fail the build.

## Decision

- JVM unit tests use JUnit 4. Instrumentation tests use `AndroidJUnit4` and `AndroidJUnitRunner`.
  Compose UI tests use `androidx.compose.ui.test.junit4.v2` from the Compose BOM. The v1 rule is
  deprecated. Kotlin warnings are errors, so the deprecated rule does not compile.
- A test that uses `createAndroidComposeRule<MainActivity>` does not call `setContent` on the
  rule. `MainActivity` already sets content. The test calls
  `androidx.activity.compose.setContent` on the activity from `runOnUiThread`. The test calls
  `waitForIdle` after that call.
- The tested behavior is the application title. `CistulaAppViewModel` takes the title and exposes
  `CistulaAppContent`. `MainActivity` passes `R.string.app_name`. The composable shows
  `content.title`. The type is plain Kotlin. It does not extend `androidx.lifecycle.ViewModel`.
  The screen has no lifecycle state, no saved state, and no asynchronous work.
- Shared synthetic data lives in the `testFixtures` source set (`CistulaAppFixture`). AGP 9.4
  compiles that Kotlin source set. The experimental property
  `android.experimental.enableTestFixturesKotlinSupport` is not set.
- The Compose compiler plugin checks every Kotlin compilation. Unit-test and test-fixture
  compilations that do not call Compose depend on `androidx.compose.runtime:runtime` as
  `compileOnly`. That keeps the runtime off the test runtime classpath.
- A test must not use the network, the clock, `Thread.sleep`, or shared mutable state.
  `testOptions.animationsDisabled` is true. The Instrumentation workflow also disables animations
  (ADR 0006).
- Debug coverage uses the AGP JaCoCo report. There is no minimum percentage.
  `./eng/verify.sh` runs `:app:createDebugUnitTestCoverageReport`. The HTML report is
  `app/build/reports/coverage/test/debug/index.html`. Instrumentation coverage is
  `:app:createDebugAndroidTestCoverageReport`. `verify.sh` does not run it. ADR 0006 keeps the
  Instrumentation workflow on `:app:connectedDebugAndroidTest`.
- Android Lint `InvalidPackage` ignores the JaCoCo agent jar. The agent references
  `java.lang.management` and `javax.management`. Android does not include those packages. The
  application does not call the agent. The ignore is in `config/lint/lint.xml`.

## Consequences

- A book test needs a book behavior and its own issue.
- An `androidx.lifecycle.ViewModel` needs a later issue that has lifecycle state to store.
- A coverage percentage gate needs its own issue.
- Instrumentation tests stay manual. Run them with `./gradlew :app:connectedDebugAndroidTest` on a
  device, or with `gh workflow run Instrumentation --ref <branch>`.

## Alternatives considered

- JUnit 6 for unit tests and JUnit 4 for devices. That is two frameworks for one test style.
- Robolectric, so Compose tests run on the JVM. The issue asks for instrumentation tests.
- `androidx.lifecycle.ViewModel` now. That adds a dependency and a factory, and there is no
  lifecycle behavior to test.
- A coverage threshold such as 80 percent. The report shows the gaps. A fixed number does not.
- Gradle-managed devices in CI. ADR 0006 already selects the API 26 emulator workflow. Managed
  devices start at API 27.
- The AndroidX Test Orchestrator. These tests do not share process state. The orchestrator is
  another APK.
