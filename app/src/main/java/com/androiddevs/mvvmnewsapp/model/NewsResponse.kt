package com.androiddevs.mvvmnewsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String = "",
    val totalResults: Int = 0
)