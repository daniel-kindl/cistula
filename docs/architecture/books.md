# Book domain contract

Issue #30 introduces `dev.danielkindl.cistula.books` inside `app`.
These types use Kotlin and JDK APIs only. They do not call Android, storage, or network APIs.
[ADR 0013](../decisions/0013-book-domain-ownership.md) records the ownership and identity boundaries.

## Identity and ownership

A `BookEntry` represents one locally owned book entry.
The caller supplies its stable `BookId`, which wraps a UUID.
Compare `BookEntry.id` to determine collection identity. Whole-object equality is not an identity check.
Changing metadata must retain the existing ID. The domain does not generate IDs or inspect a collection.

Duplicate titles and duplicate ISBNs are allowed, including multiple owned copies of the same edition.
Neither title nor ISBN is a unique collection key. An entry can have no ISBN.
ISBN-10 and ISBN-13 values remain separate identifiers, even when they describe the same edition.
This contract does not detect duplicates, convert ISBN formats, or merge entries.

`BookMetadata` holds bibliographic fields. `UserBookData` holds notes, personal tags, and reading state.
Use `entry.copy(metadata = replacement)` for metadata replacement. This retains the ID and user data.
The replacement is a complete metadata snapshot. An unknown field in it stays unknown.
A future enrichment adapter must decide how to combine bibliographic snapshots before replacement.
It must not replace `userData` as part of metadata enrichment.

## Field semantics

| Field | Meaning and validation |
| --- | --- |
| `BookMetadata.title` | Required. Trim outer whitespace and reject blank text. Title has no uniqueness constraint. |
| `authors` | Ordered names. An empty list means unknown or unavailable. Do not invent an author. |
| `publication.publisher` | Optional publisher. Null means unknown or unavailable. |
| `publication.date` | Null means wholly unknown. Otherwise retain year, year/month, or year/month/day precision. |
| `publication.isbns` | Empty means unknown or unavailable. Each supplied ISBN must pass syntax and checksum checks. |
| `description` | Optional bibliographic description. Null means unknown or unavailable. |
| `categories` | Bibliographic categories. An empty list means none supplied. |
| `cover` | Optional opaque `CoverReference`. Its value must be nonblank. No URI scheme or target existence check occurs. |
| `UserBookData.notes` | Exact user text. Null means absent. An empty string means explicitly empty. Preserve all whitespace. |
| `tags` | User-owned labels, independent of bibliographic categories. An empty list means no labels. |
| `reading` | User-owned state. Defaults to `TO_READ` with unknown progress. |

Optional bibliographic text becomes null when blank. Other bibliographic text and cover references retain internal whitespace.
Authors, categories, and tags trim outer whitespace. Blank list elements are invalid.
These lists remove exact duplicates after trimming. Comparison is case-sensitive, and the first occurrence determines order.
ISBN lists remove duplicate normalized values and retain order.
All list fields hold defensive, unmodifiable snapshots. Input mutation cannot change a constructed object.

`PublicationDate` permits years 1 through 9999 and valid Gregorian months and days.
A day requires a month. Calendar validation includes leap years.
Unknown date parts stay null. The contract does not substitute January or the first day of a month.

`Isbn.parse` removes ASCII spaces and hyphens and accepts lowercase `x` as `X`.
It rejects other characters, invalid lengths, invalid checksums, and ISBN-13 prefixes other than 978 and 979.
The [International ISBN Agency](https://www.isbn-international.org/index.php/node/10) defines the formats and prefixes.
The [ISBN Users' Manual](https://www.isbn-international.org/content/isbn-users-manual/29) defines checksum calculation.
Validation does not prove that an agency assigned the number. It does not check registration ranges or edition consistency.
Invalid ISBN input raises `IllegalArgumentException`. An adapter must explicitly correct, omit, or report invalid source data.
Other invalid domain input also raises `IllegalArgumentException`.

## Reading lifecycle

`ReadingState` is an immutable snapshot. Constructors can restore any listed status without replaying its history.
Call `transitionTo` to apply the lifecycle policy to an existing state.
A transition returns a new state and preserves progress. The original state does not change.
Repeating the current status is allowed. Every other transition absent from this table raises `IllegalArgumentException`.

| Current status | Allowed different status |
| --- | --- |
| `TO_READ` | `READING`, `DROPPED` |
| `READING` | `COMPLETED`, `PAUSED`, `DROPPED` |
| `PAUSED` | `READING`, `DROPPED` |
| `COMPLETED` | `READING` |
| `DROPPED` | `TO_READ`, `READING` |

Completed entries can return to reading. Dropped entries can return to the reading queue or reading.
Transitions do not clear or infer progress, even when reading starts again.
A caller can explicitly replace progress with `state.copy(progress = replacement)`.

`PageProgress` counts completed pages. Pages read must be nonnegative.
Its optional total must be positive. Pages read cannot exceed a supplied total.
Null progress means unknown. Zero pages read means known zero progress. A null total means an unknown total.
Progress is independent of status. Completion does not invent a total or force a page count.
Page progress serves paginated books. Audiobook duration and other progress units need a later issue.

## Extension boundaries

Future UI, storage, and metadata adapters can consume these book-specific types.
No adapter, repository interface, persistence annotation, or metadata provider exists in this change.
The package can move to a Kotlin module when an approved issue requires that boundary.
There is no universal media type, collection framework, account model, or synchronization contract.
