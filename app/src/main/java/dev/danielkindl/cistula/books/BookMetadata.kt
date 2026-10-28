package dev.danielkindl.cistula.books

import java.util.Collections

/** Bibliographic fields only. Null scalars and empty lists mean unknown or unavailable. */
class BookMetadata(
    title: String,
    authors: List<String> = emptyList(),
    val publication: PublicationDetails = PublicationDetails(),
    description: String? = null,
    categories: List<String> = emptyList(),
    val cover: CoverReference? = null,
) {
    val title: String = requiredText(title)
    val authors: List<String> = normalizedTexts(authors)
    val description: String? = optionalText(description)
    val categories: List<String> = normalizedTexts(categories)
}

/** Publication details. ISBNs can include either format, both formats, or neither format. */
class PublicationDetails(
    publisher: String? = null,
    val date: PublicationDate? = null,
    isbns: List<Isbn> = emptyList(),
) {
    val publisher: String? = optionalText(publisher)
    val isbns: List<Isbn> = immutableSnapshot(isbns.distinct())
}

/** An opaque cover location. The domain does not open, fetch, or validate its target. */
class CoverReference(
    value: String,
) {
    val value: String = requiredText(value)
}

internal fun requiredText(value: String): String = value.trim().also { require(it.isNotEmpty()) { "Text must not be blank." } }

internal fun optionalText(value: String?): String? = value?.trim()?.takeIf { it.isNotEmpty() }

internal fun normalizedTexts(values: List<String>): List<String> = immutableSnapshot(values.map(::requiredText).distinct())

internal fun <T> immutableSnapshot(values: List<T>): List<T> = Collections.unmodifiableList(ArrayList(values))
