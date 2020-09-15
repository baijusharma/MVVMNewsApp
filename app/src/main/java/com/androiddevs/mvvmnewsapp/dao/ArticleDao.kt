package com.androiddevs.mvvmnewsapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.model.Article
import retrofit2.http.DELETE

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM article")
    fun getAllArticles(): LiveData<List<Article>> // YOU should not use suspend  fun in LiveData

    @Delete
    suspend fun deleteArticle(article: Article)

}