package com.lyc.gank.widget

import android.content.Context
import android.os.SystemClock
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.lyc.gank.R

/**
 * Created by hgj on 08/01/2018.
 */
class Refresher(
        context: Context,
        attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {

    private companion object {
        private const val MIN_DURATION = 600L // ms
    }

    private val delayHideRunnable = Runnable {
        super.setRefreshing(false)
        delayHidePosted = false
    }

    private var lastShowTime = -1L
    private var delayHidePosted = false

    init {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(delayHideRunnable)
    }

    override fun isRefreshing(): Boolean {
        if (delayHidePosted) {
            return false // will hide soon..
        }

        return super.isRefreshing()
    }

    override fun setRefreshing(refreshing: Boolean) {
        if (refreshing) {
            if (delayHidePosted) {
                removeCallbacks(delayHideRunnable)
                delayHidePosted = false
            }

            lastShowTime = SystemClock.elapsedRealtime()
            super.setRefreshing(true)
        } else {
            if (delayHidePosted) {
                // will hide soon, no need to call/post again
                return
            }

            val diff = SystemClock.elapsedRealtime() - lastShowTime
            if (diff > MIN_DURATION) {
                delayHideRunnable.run()
            } else {
                delayHidePosted = true
                postDelayed(delayHideRunnable, MIN_DURATION - diff)
            }
        }
    }
}
