package dev.danielkindl.cistula.books.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BookDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(book: BookRow)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(book: BookRow): Int

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun delete(id: String): Int

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun find(id: String): BookRow?

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE, title, id")
    suspend fun list(): List<BookRow>

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE, title, id")
    fun observe(): Flow<List<BookRow>>
}
