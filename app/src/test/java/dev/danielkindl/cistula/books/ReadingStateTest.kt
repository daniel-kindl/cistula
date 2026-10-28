package dev.danielkindl.cistula.books

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class ReadingStateTest {
    @Test
    fun startsToReadWithUnknownProgress() {
        assertEquals(ReadingStatus.TO_READ, ReadingState().status)
        assertNull(ReadingState().progress)
    }

    @Test
    fun enforcesEveryTransitionAndPreservesProgress() {
        val allowed =
            mapOf(
                ReadingStatus.TO_READ to setOf(ReadingStatus.READING, ReadingStatus.DROPPED),
                ReadingStatus.READING to setOf(ReadingStatus.COMPLETED, ReadingStatus.PAUSED, ReadingStatus.DROPPED),
                ReadingStatus.PAUSED to setOf(ReadingStatus.READING, ReadingStatus.DROPPED),
                ReadingStatus.COMPLETED to setOf(ReadingStatus.READING),
                ReadingStatus.DROPPED to setOf(ReadingStatus.TO_READ, ReadingStatus.READING),
            )
        ReadingStatus.entries.forEach { from ->
            ReadingStatus.entries.forEach { to ->
                val original = ReadingState(from, PageProgress(12, 100))
                if (from == to || to in allowed.getValue(from)) {
                    val result = original.transitionTo(to)
                    assertEquals(to, result.status)
                    assertEquals(PageProgress(12, 100), result.progress)
                    assertEquals(from, original.status)
                } else {
                    assertThrows("$from to $to", IllegalArgumentException::class.java) { original.transitionTo(to) }
                }
            }
        }
    }

    @Test
    fun validatesPageProgressWithoutInventingTotals() {
        assertNull(PageProgress(0).totalPages)
        assertEquals(100, PageProgress(100, 100).pagesRead)
        assertEquals(Int.MAX_VALUE, PageProgress(Int.MAX_VALUE).pagesRead)
        listOf(
            { PageProgress(-1) },
            { PageProgress(0, 0) },
            { PageProgress(0, -1) },
            { PageProgress(101, 100) },
        ).forEach { invalid -> assertThrows(IllegalArgumentException::class.java) { invalid() } }
        assertNull(ReadingState(ReadingStatus.READING).transitionTo(ReadingStatus.COMPLETED).progress)
    }
}
