package com.lyc.gank

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.lyc.gank.ui.home.HomeViewModel

/**
 * Created by hgj on 22/12/2017.
 */
class Injection(context: Context): ViewModelProvider.Factory{
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T  = modelClass.run {
       when {
           isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel()
           else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
       }
    } as T
}
