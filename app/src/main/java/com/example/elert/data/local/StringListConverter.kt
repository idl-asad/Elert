package com.example.elert.data.local

import androidx.room.TypeConverter
import org.json.JSONArray

object StringListConverter {

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>): String {
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(value)
            buildList {
                for (index in 0 until array.length()) {
                    add(array.optString(index))
                }
            }
        }.getOrDefault(emptyList())
    }
}
