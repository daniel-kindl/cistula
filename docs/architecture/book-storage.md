# Local book storage

Issue #31 implements the [book domain contract](books.md) with Room.
[ADR 0014](../decisions/0014-local-book-storage.md) records the dependency and schema decisions.

## Repository contract

`BookRepository` uses book types and coroutine `Flow`. It has no Android or Room types in its signatures.
`RoomBookRepository` maps complete entries to and from Room rows.

| Operation | Result |
| --- | --- |
| `insert(entry)` | Insert a new local ID atomically. Duplicate IDs fail without overwriting existing data. |
| `update(entry)` | Replace that existing ID atomically. Return false if it does not exist. |
| `delete(id)` | Delete that local ID. Return false if it does not exist. |
| `find(id)` | Return the entry, or null if it does not exist. |
| `list()` | Return the ordered snapshot, including an empty list for an empty collection. |
| `observe()` | Emit an initial ordered snapshot and snapshots after committed changes. Intermediate snapshots can be combined. |

All one-shot operations suspend. Room performs database work outside the caller's thread.
The adapter starts no background scope. Callers own the operation and observation lifetimes.
Cancellation propagates. Storage and validation failures propagate instead of becoming an empty collection.
Flow can emit equal snapshots after invalidation. It is not an event log.

Snapshots sort by title with SQLite `NOCASE`, then binary title, then canonical UUID text.
`NOCASE` folds ASCII letters only. Ordering does not depend on the device locale or insertion time.
Different local IDs can retain identical titles and ISBNs.

Updates replace the supplied complete entry. Callers must retain fields they do not intend to edit.
Use `entry.copy(metadata = replacement)` when editing metadata without changing user-owned fields.
The storage adapter does not infer an enrichment policy or modify personal fields.

## Database lifetime

Within the application module, open `BookDatabase.open(applicationContext)` and pass `database.books()` to `RoomBookRepository`.
The default file is `books.db` in Android's private application database directory.
The owner must reuse that database for its lifetime. Stop all repository operations and collectors before closing it.
The application screen does not open the database yet. UI composition and use cases belong to later issues.

This issue adds no network permission, HTTP client, account, sync, backup service, or credential store.
Clearing application data or uninstalling the application removes the local collection.
No export or backup feature exists in this change.

## Schema and mapping

Schema version 1 has one `books` table. The only unique key is the canonical UUID string in `id`.
Bibliographic fields and personal fields occupy separate columns in the same atomic row.
Lists of authors, ISBNs, categories, and tags use JSON text with stable element order.
JSON preserves punctuation, quotes, line breaks, and Unicode text.
No delimiter splitting or extra serialization dependency is used.

Date columns retain unknown month and day values. Null progress differs from known zero pages read.
Notes preserve the distinction between null, empty text, and text with surrounding whitespace.
Reading states use their domain enum names.
Mappings validate restored domain values. Invalid persisted dates, ISBNs, states, or orphan date/progress parts fail explicitly.
The adapter does not replace corrupt values with invented defaults.

Room exports the schema under `app/schemas/`. Commit schema changes with their implementation.
Version 1 is the first schema. There is no invented migration from an unreleased earlier version.
Do not enable destructive fallback. A missing migration must retain data and report failure.
Before a released schema changes, add explicit migrations and test data preservation from each supported released schema.
A change to JSON layout, reading-state names, or field meaning can require a data migration even without new columns.

## Verification

JVM mapping tests cover all fields, partial dates, every reading state, unknown values, empty notes, and corrupt stored values.
Device tests use a disposable file and real Room queries.
They cover CRUD, missing IDs, duplicate bibliographic values, deterministic ordering, Flow changes, reopen persistence, rollback, and unavailable migrations.
Tests use synthetic entries. They do not use a network, real clock, or sleeps.

Run `./eng/verify.sh` for the regular checks.
Run `./gradlew :app:connectedDebugAndroidTest` on a device for storage tests.
The manual Instrumentation workflow tests API 26. Report device tests as NOT RUN unless they ran.
