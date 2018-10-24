package com.lyc.gank.widget

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewStub
import android.widget.FrameLayout
import com.lyc.gank.R

/**
 * Created by Liu Yuchuan on 2018/1/28.
 * This is a subclass of FrameLayout Contains two ViewStubs which shows loading and action retry.
 * This view is only for load and retry, so you should never add any child view
 */
class LoadRetryView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), View.OnClickListener{

    private var loadView: View? = null
    private lateinit var loadViewStub: ViewStub
    private var loadViewLoaded = false
    private var retryView: View? = null
        set(value) {
            field = value
            field?.run {
                setOnClickListener(this@LoadRetryView)
            }
        }
    private lateinit var retryViewStub: ViewStub
    private var retryViewLoaded = false
    private var hideLoadDelay = true
    var state = STATE_NOTHING
        private set

    var onRetryClickListener: OnRetryClickListener? = null

    private companion object {
        private const val MIN_DURATION = 600L // ms
        private const val STATE_NOTHING = 0
        private const val STATE_LOAD = 1
        private const val STATE_RETRY = 2
    }

    private val delayHideLoadRunnable = Runnable {
        loadViewStub.visibility = View.GONE
        retryViewStub.visibility = View.VISIBLE
        delayHidePosted = false
        state = STATE_RETRY
    }

    private var lastShowTime = -1L
    private var delayHidePosted = false

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LoadRetryView).apply {
            loadViewStub = ViewStub(context)
            loadViewStub.layoutResource = getResourceId(R.styleable.LoadRetryView_loadLayout, R.layout.widget_load_retry_load_default)
            addView(loadViewStub)
            retryViewStub = ViewStub(context)
            retryViewStub.layoutResource = getResourceId(R.styleable.LoadRetryView_retryLayout, R.layout.widget_load_retry_retry_default)
            addView(retryViewStub)
            hideLoadDelay = getBoolean(R.styleable.LoadRetryView_loadHideDelay, true)
            setViewStubs()
        }.recycle()
    }

    private fun setViewStubs() {
        assert(childCount == 2)
        for (i in 0..1) {
            val child = getChildAt(i)
            val params = child.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            child.layoutParams = params
        }
    }

    fun showLoadView(): View {
        visibility = View.VISIBLE
        if (!loadViewLoaded || loadView == null) {
            loadView = loadViewStub.inflate()
            loadViewLoaded = true
            retryViewStub.visibility = View.GONE
        } else {
            loadViewStub.visibility = View.VISIBLE
            retryViewStub.visibility = View.GONE
        }

        if (delayHidePosted) {
            removeCallbacks(delayHideLoadRunnable)
            delayHidePosted = false
        }
        lastShowTime = SystemClock.elapsedRealtime()
        state = STATE_LOAD

        hideOrShowChildren(false)

        return loadView!!
    }

    fun showRetryView(): View {
        visibility = View.VISIBLE
        if (!retryViewLoaded || retryView == null) {
            retryView = retryViewStub.inflate().apply {
                visibility = View.GONE
            }
            retryViewLoaded = true
        }

        if (!delayHidePosted) {
            val diff = SystemClock.elapsedRealtime() - lastShowTime
            if (diff > MIN_DURATION) {
                delayHideLoadRunnable.run()
            } else {
                delayHidePosted = true
                postDelayed(delayHideLoadRunnable, MIN_DURATION - diff)
            }
        }

        hideOrShowChildren(false)


        return retryView!!
    }

    fun hideAll() {
        retryViewStub.visibility = View.GONE
        loadViewStub.visibility = View.GONE
        loadView?.visibility = View.GONE
        retryView?.visibility = View.GONE
//        visibility = View.GONE
        state = STATE_NOTHING
        hideOrShowChildren(true)
    }

    private fun hideOrShowChildren(show: Boolean) {
        if (show) {
            VISIBLE
        } else {
            GONE
        }.let {
            for (i in 0 until childCount) {
                getChildAt(i).run {
                    if (this != loadViewStub && this != retryViewStub
                            && this != loadView && this != retryView)
                        visibility = it
                }
            }
        }
    }

    fun setOnInflateListener(onInflateListener: OnInflateListener) {
        loadViewStub.setOnInflateListener(onInflateListener::onLoadViewInflate)
        retryViewStub.setOnInflateListener(onInflateListener::onRetryViewInflate)
    }

    interface OnInflateListener {
        fun onLoadViewInflate(stub: ViewStub, inflated: View)
        fun onRetryViewInflate(stub: ViewStub, inflated: View)
    }

    interface OnRetryClickListener {
        fun onRetryClicked(v: View)
    }

    override fun onClick(v: View) {
        when(v) {
            retryView -> {
                onRetryClickListener?.onRetryClicked(v)
            }
        }
    }
}
