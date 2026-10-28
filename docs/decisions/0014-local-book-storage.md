# 0014. Store the local book collection with Room

- Status: Proposed
- Date: 2026-10-28
- Issue: #31

## Context

The book contract needs durable storage without a network dependency.
Repository callers need suspend operations and an observable collection.
Issue #31 prefers Room and authorizes the necessary persistence dependencies, subject to this ADR and PR review.

## Decision

Use Room with the Android SQLite implementation in the existing `app` module.
Use KSP to generate checked DAO implementations. Add coroutines for suspend operations and `Flow`.
Use coroutines-test and AndroidX Test Core for deterministic device tests.
Versions stay in the version catalog. The [toolchain guide](../development/toolchain.md) lists versions and sources.
Constrain Room code generation to patched Guava for GHSA-xxph-c9ww-hj94. This constraint does not add Guava to the APK.
No HTTP, synchronization, dependency injection, or serialization library is required.

Keep `BookRepository` in the book package. Keep Room types and mappings in `books.storage`.
Store one complete book entry per row. The canonical local UUID is the only primary key.
Retain partial dates as nullable year, month, and day columns.
Store ordered string lists as JSON in text columns, using Android's JSON implementation.
Store lifecycle state names as text. Preserve notes exactly and retain unknown fields as null or empty lists.
Order by ASCII case-insensitive title, binary title, and canonical UUID text.

Start at schema version 1 and commit Room's exported schema.
Do not register placeholder migrations or permit destructive migration fallback.
A future schema change requires an explicit migration, exported schema, and tests from every supported released schema.
An unavailable migration must fail without deleting data.

The feature dependency gate retains its deny patterns.
`config/approved-feature-dependencies.txt` permits only the exact Room runtime, compiler, plugin, and KSP plugin declarations for #31.
The gate permits stable Room/KSP version declarations. It rejects other matching catalog entries.
Verification tests the permitted entries and rejects unrelated Room, Retrofit, Firebase, and plugin entries.
This approval does not permit other feature dependencies. The PR must review this policy change explicitly.
This decision supersedes ADR 0002's M0 exclusion of Room and KSP only for the storage work in #31.

## Consequences

Room checks SQL and mappings during compilation. Its suspend and Flow APIs own database query dispatch.
The adapter starts no coroutine scope and propagates cancellation and storage failures.
Single-row writes are atomic. Duplicate local IDs fail instead of replacing existing data.
An update never inserts a missing ID. Missing lookups return null. Missing updates and deletes return false.

The database owner must stop repository work before closing the database.
Tests use real SQLite files on an emulator to prove reopen persistence, rollback, and migration refusal.
JVM tests check mapping independently of a device.

JSON columns preserve list order and punctuation without extra tables or dependencies.
They do not support efficient SQL queries over individual authors or tags. A later issue can add indexing or normalized tables.
Renaming stored fields or reading-state names is a schema or data migration concern.
There is no UI or application-level database lifetime wiring in this issue.

## Alternatives considered

- Android SQLite APIs directly. Rejected because SQL mapping and change observation would need more handwritten infrastructure.
- A separate persistence module. Rejected because package boundaries meet the current scope.
- A JSON file store. Rejected because transactions, concurrent observation, and future schema changes would need custom infrastructure.
- A separate serialization dependency. Rejected because Android already provides JSON support for the required string lists.
- Replace-on-conflict inserts. Rejected because an accidental duplicate ID could overwrite personal data.
