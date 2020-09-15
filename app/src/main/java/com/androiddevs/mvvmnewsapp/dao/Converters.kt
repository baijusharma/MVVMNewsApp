package com.androiddevs.mvvmnewsapp.dao

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.model.Article

class Converters {

    @TypeConverter
    fun fromSource(source: Article.Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(mName: String): Article.Source {
        return Article.Source(mName, mName)
    }
}