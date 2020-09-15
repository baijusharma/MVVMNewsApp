package com.androiddevs.mvvmnewsapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.model.Article
import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.repository.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepo: NewsRepository) : ViewModel() {

    val breakingNews: LiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: LiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var searchNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            breakingNews as MutableLiveData
            breakingNews.postValue(Resource.Loading())
            val response = newsRepo.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }
    }

    fun searchNews(searchString: String) = viewModelScope.launch {
        searchNews as MutableLiveData
        searchNews.postValue(Resource.Loading())
        val response = newsRepo.searchForNews(searchString, breakingNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = newsResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: newsResponse)
            }
        }
        return Resource.Error(
            response.message()
        )
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = newsResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: newsResponse)
            }
        }
        return Resource.Error(
            response.message()
        )
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepo.upsert(article)
    }

    fun getSavedNews() = newsRepo.getSavedNews()

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            newsRepo.deleteArticle(article)
        }
    }
}