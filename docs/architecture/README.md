# Architecture

## Current state (M0, binding)

```text
app                    (Android application, com.android.application)
build-logic/convention (Gradle convention plugin, build time only)
```

- One application module, `app`. It has one Compose `MainActivity`. The activity passes the
  application name to `CistulaAppViewModel`. The screen shows that title. No library modules exist.
- `app` applies `cistula.android.application` from `build-logic`. The plugin holds the SDK levels,
  JVM target, Kotlin warnings-as-errors, strict Lint, and the release build type (no minify).
- `applicationId` and namespace: `dev.danielkindl.cistula`.
- The product scope is an offline-first personal book collection (`README.md`). M0 contains no
  book features.

## Dependency rules (binding)

- A dependency points from an application module to a library module.
- Feature dependencies (networking, persistence, DI, background work) need an approved issue and an ADR.
  `eng/verify.sh` rejects known feature and tracking libraries in the version catalog.
- An agent skill is not a module and not a build dependency. ADR 0007 records that decision.

## Future considerations (non-binding)

These are not decisions. A module split needs an issue and an ADR when there is code for it.
