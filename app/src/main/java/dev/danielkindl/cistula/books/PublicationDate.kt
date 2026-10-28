package dev.danielkindl.cistula.books

import java.time.YearMonth

/** A known year with optional month and day. A null date in PublicationDetails means wholly unknown. */
data class PublicationDate(
    val year: Int,
    val month: Int? = null,
    val day: Int? = null,
) {
    init {
        require(year in MIN_YEAR..MAX_YEAR) { "Publication year must be between 1 and 9999." }
        require(month != null || day == null) { "A publication day requires a month." }
        require(month == null || month in 1..MONTHS_PER_YEAR) { "Publication month must be between 1 and 12." }
        if (day != null && month != null) {
            require(day in 1..YearMonth.of(year, month).lengthOfMonth()) { "Publication day is invalid for the month." }
        }
    }

    private companion object {
        const val MIN_YEAR = 1
        const val MAX_YEAR = 9999
        const val MONTHS_PER_YEAR = 12
    }
}
