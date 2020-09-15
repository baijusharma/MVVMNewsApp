package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.NewsActivity
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.model.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.Resource
import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragments : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = (newsResponse.totalResults / QUERY_PAGE_SIZE) + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage)
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e("TAG", "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragments_to_articleFragments,
                bundle
            )
        }

        rvBreakingNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val mLayoutManger = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPos = mLayoutManger.findFirstVisibleItemPosition()
                val visibleItemCount = mLayoutManger.childCount
                val totalItemCount = mLayoutManger.itemCount

                val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                val isLastItem = firstVisibleItemPos + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPos >= 0
                val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
                val shouldPaginate =
                    isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

                if (shouldPaginate) {
                    viewModel.getBreakingNews("us")
                    isScrolling = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}