package dev.danielkindl.cistula.books

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class BookMetadataTest {
    @Test
    fun preservesUnknownMetadata() {
        val metadata = BookMetadata("  Fixture title  ")
        assertEquals("Fixture title", metadata.title)
        assertEquals(emptyList<String>(), metadata.authors)
        assertEquals(emptyList<String>(), metadata.categories)
        assertEquals(emptyList<Isbn>(), metadata.publication.isbns)
        assertNull(metadata.publication.publisher)
        assertNull(metadata.publication.date)
        assertNull(metadata.description)
        assertNull(metadata.cover)
    }

    @Test
    fun supportsYearMonthAndDayPrecision() {
        assertNull(PublicationDate(2024).month)
        assertNull(PublicationDate(2024, 2).day)
        assertEquals(29, PublicationDate(2024, 2, 29).day)
        listOf(
            { PublicationDate(0) },
            { PublicationDate(10000) },
            { PublicationDate(2024, 0) },
            { PublicationDate(2024, 13) },
            { PublicationDate(2023, 2, 29) },
            { PublicationDate(2024, 4, 31) },
            { PublicationDate(2024, 2, 0) },
            { PublicationDate(2024, day = 1) },
        ).forEach { invalid -> assertThrows(IllegalArgumentException::class.java) { invalid() } }
    }

    @Test
    fun normalizesTextAndCopiesCollections() {
        val authors = mutableListOf(" Author ")
        val categories = mutableListOf(" Category ", "Category")
        val isbns = mutableListOf(Isbn.parse("0306406152"), Isbn.parse("9780306406157"))
        val metadata =
            BookMetadata(
                title = " Title ",
                authors = authors,
                publication = PublicationDetails(" Publisher ", PublicationDate(2024), isbns),
                description = " Description\ntext ",
                categories = categories,
                cover = CoverReference(" local:cover "),
            )
        authors.clear()
        categories.clear()
        isbns.clear()
        assertEquals(listOf("Author"), metadata.authors)
        assertEquals(listOf("Category"), metadata.categories)
        assertEquals(2, metadata.publication.isbns.size)
        assertEquals("Publisher", metadata.publication.publisher)
        assertEquals("Description\ntext", metadata.description)
        assertEquals("local:cover", metadata.cover?.value)
        assertThrows(UnsupportedOperationException::class.java) { (metadata.authors as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) { (metadata.categories as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) { (metadata.publication.isbns as MutableList).clear() }
    }

    @Test
    fun rejectsBlankRequiredTextAndUsesNullForBlankOptionalText() {
        assertThrows(IllegalArgumentException::class.java) { BookMetadata(" \n") }
        assertThrows(IllegalArgumentException::class.java) { BookMetadata("Title", authors = listOf(" ")) }
        assertThrows(IllegalArgumentException::class.java) { BookMetadata("Title", categories = listOf(" ")) }
        assertThrows(IllegalArgumentException::class.java) { CoverReference(" ") }
        assertNull(BookMetadata("Title", description = " ").description)
        assertNull(PublicationDetails(publisher = " ").publisher)
    }
}
