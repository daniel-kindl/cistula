package dev.danielkindl.cistula.books

import kotlinx.coroutines.flow.Flow

/**
 * Local collection storage. Methods suspend without blocking the caller's thread.
 * Storage and validation failures propagate. Cancellation propagates to the storage operation.
 * Title ordering is ASCII case-insensitive, then binary title, then canonical UUID text.
 */
interface BookRepository {
    /** Inserts a new local ID. An existing ID is an error, even if the metadata is equal. */
    suspend fun insert(entry: BookEntry)

    /** Replaces an existing entry atomically. Returns false when the ID does not exist. */
    suspend fun update(entry: BookEntry): Boolean

    /** Deletes an existing ID. Returns false when the ID does not exist. */
    suspend fun delete(id: BookId): Boolean

    /** Returns null when the ID does not exist. */
    suspend fun find(id: BookId): BookEntry?

    /** Returns the ordered collection snapshot. An empty list means no stored entries. */
    suspend fun list(): List<BookEntry>

    /** Emits the initial ordered snapshot and committed changes. Intermediate snapshots can be combined. */
    fun observe(): Flow<List<BookEntry>>
}
