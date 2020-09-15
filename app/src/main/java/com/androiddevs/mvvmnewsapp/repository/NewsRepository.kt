package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.dao.AppDatabase
import com.androiddevs.mvvmnewsapp.model.Article
import com.androiddevs.mvvmnewsapp.model.NewsResponse
import retrofit2.Response

// All API and DB calls are done in repository
class NewsRepository(val db: AppDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchForNews(searchNews: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.searchForNews(searchNews, pageNumber)
    }

    suspend fun upsert(article: Article) {
        db.articleDao().upsert(article)
    }

    fun getSavedNews() = db.articleDao().getAllArticles() // YOU should not use suspend  fun in LiveData

    suspend fun deleteArticle(article: Article) = db.articleDao().deleteArticle(article)
}