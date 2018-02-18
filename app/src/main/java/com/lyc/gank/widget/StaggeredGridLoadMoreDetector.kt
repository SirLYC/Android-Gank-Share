package com.lyc.gank.widget

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

/**
 * Created by hgj on 13/01/2018.
 */
class StaggeredGridLoadMoreDetector(
        private val lm: StaggeredGridLayoutManager,
        private val callback: LoadMoreDetector.LoadMoreCallback
) : LoadMoreDetector() {

    private val loadMoreAction = Runnable { callback.loadMore() }

    private var lastVisible: IntArray = IntArray(lm.spanCount) // cache

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        when (lm.orientation) {
            StaggeredGridLayoutManager.VERTICAL -> if (dy < 0) return
            StaggeredGridLayoutManager.HORIZONTAL -> if (dx < 0) return
        }

        val itemCount = lm.itemCount
        if (itemCount <= 0) {
            return
        }

        if (!callback.canLoadMore()) {
            return
        }

        val spanCount = lm.spanCount
        if (spanCount != lastVisible.size) {
            lastVisible = IntArray(lm.spanCount)
        }

        val lastPositions = lm.findLastVisibleItemPositions(lastVisible)
        val reachBottom = lastPositions.contains(itemCount - 2 * lm.spanCount)
        if (reachBottom) {
            // https://stackoverflow.com/questions/39445330/cannot-call-notifyiteminserted-method-in-a-scroll-callback-recyclerview-v724-2
            recyclerView.removeCallbacks(loadMoreAction)
            recyclerView.post(loadMoreAction)
        }
    }
}
