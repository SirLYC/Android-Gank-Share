package com.lyc.gank

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.lyc.gank.category.SingleContentViewModel
import com.lyc.gank.home.HomeViewModel
import com.lyc.gank.post.PostViewModel
import com.lyc.gank.search.SearchViewModel

/**
 * Created by hgj on 22/12/2017.
 */
class Injection(context: Context): ViewModelProvider.Factory{
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T  = modelClass.run {
       when {
           isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel()
           isAssignableFrom(SingleContentViewModel::class.java) -> SingleContentViewModel()
           isAssignableFrom(PostViewModel::class.java) -> PostViewModel()
           isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel()
           else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
       }
    } as T
}
