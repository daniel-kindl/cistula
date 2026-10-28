# 0013. Separate book identity, metadata, and user data

- Status: Proposed
- Date: 2026-10-28
- Issue: #30

## Context

Books can lack ISBNs. A collection can contain multiple copies with the same ISBN or title.
Later bibliographic enrichment must preserve personal notes, tags, and reading progress.
The first domain contract must remain independent of Android UI, persistence, and metadata providers.

## Decision

Keep book-specific Kotlin types in the existing `app` module under `books`.
Use a caller-supplied UUID inside `BookId` for stable local identity.
Keep bibliographic fields in `BookMetadata` and user-owned fields in `UserBookData`.
Compose both in `BookEntry`. Metadata replacement preserves identity and user data by default.
Use immutable snapshots, defensive list copies, explicit reading transitions, and validation at construction.
Preserve unknown fields and partial date precision.

The detailed rules are in the [book domain contract](../architecture/books.md).

## Consequences

Metadata adapters can replace metadata without access to personal field parameters.
Duplicate ISBNs and titles remain valid. Duplicate detection needs a later policy and implementation.
The package uses only Kotlin and JDK APIs. It can move to a separate module when needed.
There is no new dependency, storage contract, or shared media hierarchy.
Snapshot classes with collection fields use reference equality. Callers compare `BookId` for identity.

## Alternatives considered

- Use ISBN or title as identity. Rejected because neither identifies a locally owned copy.
- Put all fields in one object. Rejected because enrichment could replace user-owned fields accidentally.
- Add a domain module now. Rejected because package separation meets the issue without changing the module structure.
- Define a generic media model. Rejected because this issue covers books only.
