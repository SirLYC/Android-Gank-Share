package com.lyc.gank.widget

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by hgj on 13/01/2018.
 */
class GridLoadMoreDetector(
        private val lm: GridLayoutManager,
        private val callback: LoadMoreCallback
) : LoadMoreDetector() {

    private val loadMoreAction = Runnable { callback.loadMore() }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        when (lm.orientation) {
            GridLayoutManager.VERTICAL -> if (dy < 0) return
            GridLayoutManager.HORIZONTAL -> if (dx < 0) return
        }

        val itemCount = lm.itemCount
        if (itemCount <= 0) {
            return
        }

        if (!callback.canLoadMore()) {
            return
        }

        val reachBottom = lm.findLastVisibleItemPosition() >= itemCount - 2 * lm.spanCount // threshold
        if (reachBottom) {
            // https://stackoverflow.com/questions/39445330/cannot-call-notifyiteminserted-method-in-a-scroll-callback-recyclerview-v724-2
            recyclerView.removeCallbacks(loadMoreAction)
            recyclerView.post(loadMoreAction)
        }
    }
}
