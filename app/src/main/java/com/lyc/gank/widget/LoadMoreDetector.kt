package com.lyc.gank.widget

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import java.lang.IllegalArgumentException

/**
 * Created by hgj on 06/01/2018.
 */
abstract class LoadMoreDetector : RecyclerView.OnScrollListener() {

    companion object {

        fun detect(recyclerView: RecyclerView, callback: LoadMoreCallback): LoadMoreDetector {
            val lm = recyclerView.layoutManager

            return when (lm) {
                is LinearLayoutManager -> LinearLoadMoreDetector(lm, callback)
                is GridLayoutManager -> GridLoadMoreDetector(lm, callback)
                is StaggeredGridLayoutManager -> StaggeredGridLoadMoreDetector(lm, callback)
                else -> throw IllegalArgumentException("not support LayoutManager: ${lm.javaClass}")
            }.also {
                recyclerView.addOnScrollListener(it)
            }
        }
    }

    interface LoadMoreCallback {

        fun canLoadMore(): Boolean
        fun loadMore()
    }
}
