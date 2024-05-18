package com.debugdesk.mono.domain.data.local.localdatabase.typeconverter

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromStringList(stringList: List<String>?): String? {
        return stringList?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toStringList(string: String?): List<String>? {
        return string?.split(",")?.map { it.trim() }
    }
}
