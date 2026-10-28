package dev.danielkindl.cistula.books.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BookRow::class], version = 1, exportSchema = true)
@TypeConverters(BookListConverters::class)
internal abstract class BookDatabase : RoomDatabase() {
    abstract fun books(): BookDao

    companion object {
        /** The caller owns this database and must close it after all repository work stops. */
        fun open(
            context: Context,
            name: String = "books.db",
        ): BookDatabase = Room.databaseBuilder(context.applicationContext, BookDatabase::class.java, name).build()
    }
}
