package dev.danielkindl.cistula.books

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class BookEntryTest {
    @Test
    fun identifiesLocalEntriesIndependentlyOfTitleAndIsbn() {
        val metadata = BookMetadata("Fixture title", publication = PublicationDetails(isbns = listOf(Isbn.parse("0306406152"))))
        val first = BookEntry(BookId(UUID(0, 1)), metadata)
        val second = BookEntry(BookId(UUID(0, 2)), metadata)
        assertNotEquals(first.id, second.id)
        assertEquals(BookId(UUID(0, 1)), first.copy(metadata = BookMetadata("Changed title")).id)
        assertEquals(first.id, first.copy(metadata = BookMetadata("No ISBN")).id)
    }

    @Test
    fun replacesMetadataWithoutChangingUserData() {
        val tags = mutableListOf(" Favorite ", "Favorite")
        val userData =
            UserBookData(
                notes = "  Personal note\nwith spacing  ",
                tags = tags,
                reading = ReadingState(ReadingStatus.PAUSED, PageProgress(12, 100)),
            )
        val original = BookEntry(BookId(UUID(0, 1)), BookMetadata("Old title"), userData)
        val updated = original.copy(metadata = BookMetadata("Enriched title", authors = listOf("Author")))
        tags.clear()
        assertSame(userData, updated.userData)
        assertEquals("  Personal note\nwith spacing  ", updated.userData.notes)
        assertEquals(listOf("Favorite"), updated.userData.tags)
        assertEquals(ReadingState(ReadingStatus.PAUSED, PageProgress(12, 100)), updated.userData.reading)
        assertEquals("Old title", original.metadata.title)
        assertThrows(UnsupportedOperationException::class.java) { (userData.tags as MutableList).clear() }
    }

    @Test
    fun preservesEmptyAndAbsentNotesAndRejectsBlankTags() {
        assertNull(UserBookData().notes)
        assertEquals("", UserBookData(notes = "").notes)
        assertThrows(IllegalArgumentException::class.java) { UserBookData(tags = listOf(" ")) }
        assertEquals(ReadingState(), UserBookData().reading)
    }
}
