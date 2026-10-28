package dev.danielkindl.cistula.books

/** A normalized ISBN-10 or ISBN-13. Syntax and checksum checks do not prove agency assignment. */
@JvmInline
value class Isbn private constructor(
    val value: String,
) {
    companion object {
        /** Removes spaces and hyphens, accepts lowercase x, and rejects invalid input with IllegalArgumentException. */
        fun parse(input: String): Isbn {
            val value = input.replace(" ", "").replace("-", "").replace('x', 'X')
            require(isValid(value)) { "ISBN syntax or checksum is invalid." }
            return Isbn(value)
        }

        private fun isValid(value: String): Boolean =
            when (value.length) {
                ISBN_10_LENGTH -> {
                    value.take(ISBN_10_LENGTH - 1).all { it in '0'..'9' } &&
                        (value.last() in '0'..'9' || value.last() == 'X') &&
                        value.withIndex().sumOf { (index, char) ->
                            (ISBN_10_LENGTH - index) * if (char == 'X') X_DIGIT else char.digitToInt()
                        } % ISBN_10_MODULUS == 0
                }

                ISBN_13_LENGTH -> {
                    (value.startsWith("978") || value.startsWith("979")) &&
                        value.all { it in '0'..'9' } &&
                        value.withIndex().sumOf { (index, char) ->
                            char.digitToInt() * if (index % 2 == 0) 1 else ALTERNATE_WEIGHT
                        } % ISBN_13_MODULUS == 0
                }

                else -> {
                    false
                }
            }

        private const val ISBN_10_LENGTH = 10
        private const val ISBN_13_LENGTH = 13
        private const val ISBN_10_MODULUS = 11
        private const val ISBN_13_MODULUS = 10
        private const val X_DIGIT = 10
        private const val ALTERNATE_WEIGHT = 3
    }
}
