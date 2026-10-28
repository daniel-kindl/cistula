package dev.danielkindl.cistula.books

import java.util.UUID

/** Stable local collection identity. The caller supplies a UUID independently of bibliographic data. */
@JvmInline
value class BookId(
    val value: UUID,
)

/** A locally owned entry. Compare [id] for identity. Equal titles or ISBNs do not imply equal identities. */
data class BookEntry(
    val id: BookId,
    val metadata: BookMetadata,
    val userData: UserBookData = UserBookData(),
)

/** User-owned fields. Notes remain exact, including whitespace. Null notes mean absent. Empty notes mean explicitly empty. */
class UserBookData(
    val notes: String? = null,
    tags: List<String> = emptyList(),
    val reading: ReadingState = ReadingState(),
) {
    val tags: List<String> = normalizedTexts(tags)
}
