package com.androiddevs.mvvmnewsapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Entity(tableName = "article")
@Parcelize
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int?= null,
    val author: String = "",
    val content: String = "",
    val description: String = "",
    val publishedAt: String = "",
    val source: Source = Source(),
    val title: String = "",
    val url: String = "",
    val urlToImage: String = ""
): Parcelable {
    @Parcelize
    data class Source(
        val id: String = "",
        val name: String = ""
    ):Parcelable
}