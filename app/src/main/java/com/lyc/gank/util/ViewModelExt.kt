package com.lyc.gank.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.lyc.gank.App
import com.lyc.gank.base.BaseFragment

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
inline fun <reified T : ViewModel> FragmentActivity.provideViewModel(): T {
    return ViewModelProviders.of(this, (application as App).injection()).get(T::class.java)
}

inline fun <reified T : ViewModel> BaseFragment.provideViewModel(): T {
    return ViewModelProviders.of(this, (activity()!!.application as App).injection()).get(T::class.java)
}