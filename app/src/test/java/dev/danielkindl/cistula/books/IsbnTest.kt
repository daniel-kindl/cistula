package dev.danielkindl.cistula.books

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class IsbnTest {
    @Test
    fun normalizesValidIsbns() {
        assertEquals("0306406152", Isbn.parse(" 0-306-40615-2 ").value)
        assertEquals("080442957X", Isbn.parse("0 8044 2957 x").value)
        assertEquals("9780306406157", Isbn.parse("978-0-306-40615-7").value)
        assertEquals("9791090636071", Isbn.parse("9791090636071").value)
    }

    @Test
    fun rejectsInvalidChecksumsLengthsCharactersAndPrefixes() {
        listOf(
            "",
            "0306406153",
            "9780306406158",
            "978030640615",
            "97803064061570",
            "978030640615X",
            "X804429570",
            "９780306406157",
            "978.0306406157",
            "9770306406158",
            "ISBN 0306406152",
            "000000000X",
        ).forEach { value ->
            assertThrows(value, IllegalArgumentException::class.java) { Isbn.parse(value) }
        }
    }

    @Test
    fun comparesNormalizedValues() {
        assertEquals(Isbn.parse("0-306-40615-2"), Isbn.parse("0306406152"))
    }
}
