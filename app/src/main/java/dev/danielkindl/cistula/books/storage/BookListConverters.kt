package dev.danielkindl.cistula.books.storage

import androidx.room.TypeConverter
import org.json.JSONArray

/** JSON retains list order and embedded punctuation without a runtime serialization dependency. */
internal class BookListConverters {
    @TypeConverter
    fun encode(values: List<String>): String = JSONArray(values).toString()

    @TypeConverter
    fun decode(value: String): List<String> {
        val array = JSONArray(value)
        return List(array.length()) { index ->
            val element = array.get(index)
            require(element is String) { "Stored list elements must be strings." }
            element
        }
    }
}
