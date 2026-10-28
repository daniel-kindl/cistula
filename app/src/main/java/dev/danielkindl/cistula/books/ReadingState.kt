package dev.danielkindl.cistula.books

/** User-selected lifecycle states. Completion does not require a known page count. */
enum class ReadingStatus {
    TO_READ,
    READING,
    COMPLETED,
    PAUSED,
    DROPPED,
}

/** User-reported pages read, with an optional positive total. Zero pages read is known progress. */
data class PageProgress(
    val pagesRead: Int,
    val totalPages: Int? = null,
) {
    init {
        require(pagesRead >= 0) { "Pages read must not be negative." }
        require(totalPages == null || totalPages > 0) { "Total pages must be positive when supplied." }
        require(totalPages == null || pagesRead <= totalPages) { "Pages read must not exceed total pages." }
    }
}

/** User-owned reading state. Null progress means unknown. All transitions preserve progress. */
data class ReadingState(
    val status: ReadingStatus = ReadingStatus.TO_READ,
    val progress: PageProgress? = null,
) {
    /** Returns a new state. Repeated states are allowed. Other invalid transitions throw IllegalArgumentException. */
    fun transitionTo(target: ReadingStatus): ReadingState {
        val allowed =
            when (status) {
                ReadingStatus.TO_READ -> setOf(ReadingStatus.READING, ReadingStatus.DROPPED)
                ReadingStatus.READING -> setOf(ReadingStatus.COMPLETED, ReadingStatus.PAUSED, ReadingStatus.DROPPED)
                ReadingStatus.PAUSED -> setOf(ReadingStatus.READING, ReadingStatus.DROPPED)
                ReadingStatus.COMPLETED -> setOf(ReadingStatus.READING)
                ReadingStatus.DROPPED -> setOf(ReadingStatus.TO_READ, ReadingStatus.READING)
            }
        require(target == status || target in allowed) { "Reading transition is not allowed." }
        return copy(status = target)
    }
}
