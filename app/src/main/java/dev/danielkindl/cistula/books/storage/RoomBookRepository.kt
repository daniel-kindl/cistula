package dev.danielkindl.cistula.books.storage

import dev.danielkindl.cistula.books.BookEntry
import dev.danielkindl.cistula.books.BookId
import dev.danielkindl.cistula.books.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room owns query dispatch and transactions. This adapter does not start a separate coroutine scope. */
internal class RoomBookRepository(
    private val dao: BookDao,
) : BookRepository {
    override suspend fun insert(entry: BookEntry) = dao.insert(entry.toRow())

    override suspend fun update(entry: BookEntry): Boolean = dao.update(entry.toRow()) > 0

    override suspend fun delete(id: BookId): Boolean = dao.delete(id.value.toString()) > 0

    override suspend fun find(id: BookId): BookEntry? = dao.find(id.value.toString())?.toBookEntry()

    override suspend fun list(): List<BookEntry> = dao.list().map(BookRow::toBookEntry)

    override fun observe(): Flow<List<BookEntry>> = dao.observe().map { rows -> rows.map(BookRow::toBookEntry) }
}
