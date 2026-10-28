package dev.danielkindl.cistula.books.storage

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.danielkindl.cistula.books.BookEntry
import dev.danielkindl.cistula.books.BookId
import dev.danielkindl.cistula.books.BookMetadata
import dev.danielkindl.cistula.books.BookRepository
import dev.danielkindl.cistula.books.Isbn
import dev.danielkindl.cistula.books.PageProgress
import dev.danielkindl.cistula.books.PublicationDate
import dev.danielkindl.cistula.books.PublicationDetails
import dev.danielkindl.cistula.books.ReadingState
import dev.danielkindl.cistula.books.ReadingStatus
import dev.danielkindl.cistula.books.UserBookData
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class RoomBookRepositoryTest {
    private lateinit var context: Context
    private lateinit var database: BookDatabase
    private lateinit var repository: BookRepository

    @Before
    fun openDatabase() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(TEST_DATABASE)
        reopen()
    }

    @After
    fun closeDatabase() {
        database.close()
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun createsReadsUpdatesAndDeletesWithoutUpsertingMissingIds() =
        runTest {
            val original = fixture(1, "Original")
            assertEquals(emptyList<BookEntry>(), repository.list())
            assertNull(repository.find(original.id))
            assertFalse(repository.update(original))
            assertFalse(repository.delete(original.id))
            repository.insert(original)
            assertEquals(original.toRow(), repository.find(original.id)?.toRow())
            val changed = original.copy(metadata = BookMetadata("Changed"))
            assertTrue(repository.update(changed))
            assertEquals("Changed", repository.find(original.id)?.metadata?.title)
            assertEquals(original.userData.notes, repository.find(original.id)?.userData?.notes)
            assertTrue(repository.delete(original.id))
            assertNull(repository.find(original.id))
            assertEquals(emptyList<BookEntry>(), repository.list())
        }

    @Test
    fun retainsEveryFieldAfterClosingAndReopeningTheFile() =
        runTest {
            val original = fixture(1, "Fixture")
            repository.insert(original)
            database.close()
            reopen()
            assertEquals(original.toRow(), repository.find(original.id)?.toRow())
            assertEquals(listOf("First, author", "Second \"author\"\nnext line"), repository.find(original.id)?.metadata?.authors)
            assertEquals("  Personal note\n ", repository.find(original.id)?.userData?.notes)
        }

    @Test
    fun preservesPartialAndUnknownFields() =
        runTest {
            val entries =
                listOf(
                    BookEntry(BookId(UUID(0, 1)), BookMetadata("Unknown")),
                    BookEntry(
                        BookId(UUID(0, 2)),
                        BookMetadata("Year", publication = PublicationDetails(date = PublicationDate(2024))),
                        UserBookData("", reading = ReadingState(ReadingStatus.READING, PageProgress(0))),
                    ),
                    BookEntry(
                        BookId(UUID(0, 3)),
                        BookMetadata("Month", publication = PublicationDetails(date = PublicationDate(2024, 2))),
                    ),
                )
            entries.forEach { repository.insert(it) }
            database.close()
            reopen()
            entries.forEach { assertEquals(it.toRow(), repository.find(it.id)?.toRow()) }
        }

    @Test
    fun allowsDuplicateIsbnsAndTitlesWithStableIdTieBreaking() =
        runTest {
            repository.insert(fixture(3, "Same"))
            repository.insert(fixture(2, "Same"))
            repository.insert(fixture(1, "alpha"))
            assertEquals(listOf(BookId(UUID(0, 1)), BookId(UUID(0, 2)), BookId(UUID(0, 3))), repository.list().map { it.id })
            repository.update(fixture(3, "Beta"))
            assertEquals(listOf(BookId(UUID(0, 1)), BookId(UUID(0, 3)), BookId(UUID(0, 2))), repository.list().map { it.id })
        }

    @Test
    fun emitsInitialAndCommittedCollectionChanges() =
        runTest {
            val changes = Channel<List<BookEntry>>(Channel.UNLIMITED)
            val observer =
                backgroundScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    repository.observe().collect { changes.send(it) }
                }
            try {
                assertEquals(emptyList<BookEntry>(), changes.receive())
                repository.insert(fixture(1, "First"))
                assertEquals(listOf("First"), changes.receive().map { it.metadata.title })
                repository.update(fixture(1, "Changed"))
                assertEquals(listOf("Changed"), changes.receive().map { it.metadata.title })
                repository.delete(BookId(UUID(0, 1)))
                assertEquals(emptyList<BookEntry>(), changes.receive())
            } finally {
                observer.cancelAndJoin()
                changes.close()
            }
        }

    @Test
    fun rollsBackEarlierWritesWhenADuplicateIdFailsATransaction() =
        runTest {
            val original = fixture(1, "Original")
            repository.insert(original)
            try {
                database.withTransaction {
                    repository.update(original.copy(metadata = BookMetadata("Uncommitted")))
                    repository.insert(original)
                }
                fail("A duplicate local ID must fail.")
            } catch (expected: SQLiteConstraintException) {
                assertEquals(original.toRow(), repository.find(original.id)?.toRow())
            }
            database.close()
            reopen()
            assertEquals(original.toRow(), repository.find(original.id)?.toRow())
        }

    @Test
    fun refusesMissingMigrationWithoutDeletingBooks() =
        runTest {
            repository.insert(fixture(1, "Preserved"))
            database.close()
            context.openOrCreateDatabase(TEST_DATABASE, Context.MODE_PRIVATE, null).use { it.version = 2 }
            reopen()
            try {
                repository.list()
                fail("An unavailable migration must fail.")
            } catch (expected: IllegalStateException) {
                context.openOrCreateDatabase(TEST_DATABASE, Context.MODE_PRIVATE, null).use { sqlite ->
                    sqlite.rawQuery("SELECT title FROM books", null).use { rows ->
                        assertTrue(rows.moveToFirst())
                        assertEquals("Preserved", rows.getString(0))
                    }
                }
            }
        }

    private fun reopen() {
        database = BookDatabase.open(context, TEST_DATABASE)
        repository = RoomBookRepository(database.books())
    }

    private fun fixture(
        id: Long,
        title: String,
    ): BookEntry =
        BookEntry(
            BookId(UUID(0, id)),
            BookMetadata(
                title,
                listOf("First, author", "Second \"author\"\nnext line"),
                PublicationDetails(
                    "Publisher",
                    PublicationDate(2024, 2, 29),
                    listOf(Isbn.parse("0306406152"), Isbn.parse("9780306406157")),
                ),
                "Description\nnext line",
                listOf("Category", "日本語"),
            ),
            UserBookData("  Personal note\n ", listOf("Tag", "Other"), ReadingState(ReadingStatus.PAUSED, PageProgress(12, 100))),
        )

    private companion object {
        const val TEST_DATABASE = "book-storage-test.db"
    }
}
