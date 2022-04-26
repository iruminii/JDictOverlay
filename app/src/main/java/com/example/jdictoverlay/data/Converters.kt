package com.example.jdictoverlay.data

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromList(detailList: List<String>?) : String {
        return Json.encodeToString(detailList)
    }

    @TypeConverter
    fun toList(detailString: String?) : List<String> {
        return Json.decodeFromString<List<String>>(detailString ?: "")
    }
}