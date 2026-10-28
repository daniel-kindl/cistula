# Architecture

## Current state (binding)

```text
app                    (Android application, com.android.application)
build-logic/convention (Gradle convention plugin, build time only)
```

- One application module, `app`. It has one Compose `MainActivity`. The activity passes the
  application name to `CistulaAppViewModel`. The screen shows that title. No library modules exist.
- `app` applies `cistula.android.application` from `build-logic`. The plugin holds the SDK levels,
  JVM target, Kotlin warnings-as-errors, strict Lint, and the release build type (no minify).
- `applicationId` and namespace: `dev.danielkindl.cistula`.
- The `books` package defines the [book domain contract](books.md) using Kotlin and JDK APIs only.
  It separates bibliographic metadata from user-owned fields. ADR 0013 records the design.
- The [local book storage](book-storage.md) adapter uses Room in `books.storage`. ADR 0014 records the schema and approved dependencies.
  The repository contract uses suspend operations and coroutine Flow. The screen does not open the database yet.
- The product scope is an offline-first personal book collection (`README.md`). The UI still has no
  book features.

## Dependency rules (binding)

- A dependency points from an application module to a library module.
- Feature dependencies (networking, persistence, DI, background work) need an approved issue and an ADR.
  `eng/verify.sh` rejects unapproved feature and tracking libraries in the version catalog.
  Issue #31 and ADR 0014 permit the exact storage declarations in `config/approved-feature-dependencies.txt`.
- An agent skill is not a module and not a build dependency. ADR 0007 records that decision.

## Future considerations (non-binding)

These are not decisions. A module split needs an issue and an ADR when there is code for it.
