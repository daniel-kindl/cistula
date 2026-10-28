package dev.danielkindl.cistula.books.storage

import dev.danielkindl.cistula.books.BookEntry
import dev.danielkindl.cistula.books.BookId
import dev.danielkindl.cistula.books.BookMetadata
import dev.danielkindl.cistula.books.CoverReference
import dev.danielkindl.cistula.books.Isbn
import dev.danielkindl.cistula.books.PageProgress
import dev.danielkindl.cistula.books.PublicationDate
import dev.danielkindl.cistula.books.PublicationDetails
import dev.danielkindl.cistula.books.ReadingState
import dev.danielkindl.cistula.books.ReadingStatus
import dev.danielkindl.cistula.books.UserBookData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class BookMappingTest {
    @Test
    fun roundTripsEveryFieldWithoutChangingUserText() {
        val entry =
            BookEntry(
                BookId(UUID(0, 1)),
                BookMetadata(
                    "Fixture title",
                    listOf("First author", "Second author"),
                    PublicationDetails(
                        "Publisher",
                        PublicationDate(2024, 2, 29),
                        listOf(Isbn.parse("0306406152"), Isbn.parse("9780306406157")),
                    ),
                    "Description\nsecond line",
                    listOf("Category", "Other"),
                    CoverReference("local:fixture"),
                ),
                UserBookData("  Exact note\n ", listOf("Tag", "Other tag"), ReadingState(ReadingStatus.PAUSED, PageProgress(12, 100))),
            )
        val restored = entry.toRow().toBookEntry()
        assertEquals(entry.id, restored.id)
        assertEquals("Fixture title", restored.metadata.title)
        assertEquals(listOf("First author", "Second author"), restored.metadata.authors)
        assertEquals("Publisher", restored.metadata.publication.publisher)
        assertEquals(PublicationDate(2024, 2, 29), restored.metadata.publication.date)
        assertEquals(listOf(Isbn.parse("0306406152"), Isbn.parse("9780306406157")), restored.metadata.publication.isbns)
        assertEquals("Description\nsecond line", restored.metadata.description)
        assertEquals(listOf("Category", "Other"), restored.metadata.categories)
        assertEquals("local:fixture", restored.metadata.cover?.value)
        assertEquals("  Exact note\n ", restored.userData.notes)
        assertEquals(listOf("Tag", "Other tag"), restored.userData.tags)
        assertEquals(ReadingState(ReadingStatus.PAUSED, PageProgress(12, 100)), restored.userData.reading)
    }

    @Test
    fun roundTripsUnknownFieldsPartialDatesAndEveryStatus() {
        ReadingStatus.entries.forEach { status ->
            listOf(null, PublicationDate(2024), PublicationDate(2024, 2)).forEach { date ->
                val entry =
                    BookEntry(
                        BookId(UUID(0, 1)),
                        BookMetadata("Fixture", publication = PublicationDetails(date = date)),
                        UserBookData(reading = ReadingState(status)),
                    )
                val restored = entry.toRow().toBookEntry()
                assertEquals(entry.toRow(), restored.toRow())
                assertEquals(date, restored.metadata.publication.date)
                assertEquals(status, restored.userData.reading.status)
                assertNull(restored.userData.notes)
                assertNull(restored.userData.reading.progress)
            }
        }
        val zero =
            BookEntry(BookId(UUID(0, 1)), BookMetadata("Fixture"), UserBookData("", reading = ReadingState(progress = PageProgress(0))))
        assertEquals(zero.toRow(), zero.toRow().toBookEntry().toRow())
    }

    @Test
    fun rejectsCorruptStoredValuesInsteadOfInventingDefaults() {
        val valid = BookEntry(BookId(UUID(0, 1)), BookMetadata("Fixture")).toRow()
        listOf(
            valid.copy(id = "invalid"),
            valid.copy(title = ""),
            valid.copy(readingStatus = "UNKNOWN"),
            valid.copy(isbns = listOf("invalid")),
            valid.copy(publicationMonth = 2),
            valid.copy(publicationYear = 2023, publicationMonth = 2, publicationDay = 29),
            valid.copy(pagesRead = -1),
            valid.copy(totalPages = 10),
        ).forEach { corrupt ->
            assertThrows(IllegalArgumentException::class.java) { corrupt.toBookEntry() }
        }
    }
}
