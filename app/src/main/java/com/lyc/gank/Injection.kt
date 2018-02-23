package com.lyc.gank

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.lyc.gank.ui.category.SingleContentViewModel
import com.lyc.gank.ui.home.HomeViewModel
import com.lyc.gank.ui.post.PostViewModel

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
           else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
       }
    } as T
}
