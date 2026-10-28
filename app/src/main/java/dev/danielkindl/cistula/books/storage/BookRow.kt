package dev.danielkindl.cistula.books.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
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
import java.util.UUID

@Entity(tableName = "books")
internal data class BookRow(
    @PrimaryKey val id: String,
    val title: String,
    val authors: List<String>,
    val publisher: String?,
    val publicationYear: Int?,
    val publicationMonth: Int?,
    val publicationDay: Int?,
    val isbns: List<String>,
    val description: String?,
    val categories: List<String>,
    val cover: String?,
    val notes: String?,
    val tags: List<String>,
    val readingStatus: String,
    val pagesRead: Int?,
    val totalPages: Int?,
)

internal fun BookEntry.toRow(): BookRow =
    BookRow(
        id = id.value.toString(),
        title = metadata.title,
        authors = metadata.authors,
        publisher = metadata.publication.publisher,
        publicationYear = metadata.publication.date?.year,
        publicationMonth = metadata.publication.date?.month,
        publicationDay = metadata.publication.date?.day,
        isbns = metadata.publication.isbns.map { it.value },
        description = metadata.description,
        categories = metadata.categories,
        cover = metadata.cover?.value,
        notes = userData.notes,
        tags = userData.tags,
        readingStatus = userData.reading.status.name,
        pagesRead = userData.reading.progress?.pagesRead,
        totalPages = userData.reading.progress?.totalPages,
    )

internal fun BookRow.toBookEntry(): BookEntry {
    require(publicationYear != null || (publicationMonth == null && publicationDay == null)) {
        "Stored date parts require a publication year."
    }
    require(pagesRead != null || totalPages == null) { "Stored total pages require pages read." }
    return BookEntry(
        id = BookId(UUID.fromString(id)),
        metadata =
            BookMetadata(
                title = title,
                authors = authors,
                publication =
                    PublicationDetails(
                        publisher = publisher,
                        date = publicationYear?.let { PublicationDate(it, publicationMonth, publicationDay) },
                        isbns = isbns.map(Isbn::parse),
                    ),
                description = description,
                categories = categories,
                cover = cover?.let(::CoverReference),
            ),
        userData =
            UserBookData(
                notes = notes,
                tags = tags,
                reading =
                    ReadingState(
                        status = ReadingStatus.valueOf(readingStatus),
                        progress = pagesRead?.let { PageProgress(it, totalPages) },
                    ),
            ),
    )
}
