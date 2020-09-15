package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.utils.APIName.Companion.allNews
import com.androiddevs.mvvmnewsapp.utils.APIName.Companion.topHeadLines
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.APIKEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPICall {

    @GET(topHeadLines)
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = APIKEY
    ): Response<NewsResponse>

    @GET(allNews)
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String = "",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = APIKEY
    ): Response<NewsResponse>
}