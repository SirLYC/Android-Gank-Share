package com.lyc.gank.widget

import android.content.Context
import android.support.v4.view.NestedScrollingChild2
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.webkit.WebView
import android.widget.OverScroller
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Created by Liu Yuchuan on 2019/1/19.
 */
class VerticalNestedScrollWebView(context: Context?, attrs: AttributeSet?) : WebView(context, attrs), NestedScrollingChild2 {

    private val scrollHelper = NestedScrollingChildHelper(this)
    private val tracker = VelocityTracker.obtain()
    private val mNestedOffsets = intArrayOf(0, 0)
    private val mScrollOffsets = intArrayOf(0, 0)
    private val mScrollConsumed = intArrayOf(0, 0)
    private var scrollPointerId = -1
    private var initialTouchY = 0
    private var cancelWhileMove = false
    private val minFlinVY = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    private val scroller = OverScroller(context, sQuinticInterpolator)
    private var lastFlingY = 0

    init {
        scrollHelper.isNestedScrollingEnabled = true
    }

    companion object {

        private val sQuinticInterpolator: Interpolator = Interpolator {
            val t = it - 1.0f
            t * t * t * t * t + 1.0f
        }
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
                                      offsetInWindow: IntArray?, type: Int) =
            scrollHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)

    override fun startNestedScroll(axes: Int, type: Int) = scrollHelper.startNestedScroll(axes, type)

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int) = scrollHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)

    override fun stopNestedScroll(type: Int) = scrollHelper.stopNestedScroll(type)

    override fun hasNestedScrollingParent(type: Int) = scrollHelper.hasNestedScrollingParent(type)

    override fun isNestedScrollingEnabled(): Boolean {
        return scrollHelper.isNestedScrollingEnabled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        super.setNestedScrollingEnabled(enabled)
        scrollHelper.isNestedScrollingEnabled = enabled
    }

    private var lastY = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {

        var eventAddedToVelocityTracker = false
        val vtev = MotionEvent.obtain(event)
        val action = event.actionMasked
        val actionIndex = event.actionIndex
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsets[1] = 0
        }
        vtev.offsetLocation(0f, mNestedOffsets[1].toFloat())

        var result = false

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
                cancelWhileMove = false
                scrollPointerId = event.getPointerId(0)
                lastY = event.y.roundToInt()
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
//                Log.e(TAG, "primary pointer down id = $scrollPointerId")
                result = super.onTouchEvent(vtev)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
                cancelWhileMove = false
                scrollPointerId = event.getPointerId(actionIndex)
                initialTouchY = event.getY(actionIndex).roundToInt()
                lastY = initialTouchY
            }

            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(scrollPointerId)
                if (index < 0) {
                    result = super.onTouchEvent(vtev)
                }

                val y = event.getY(index).roundToInt()
                var dy = lastY - y
                if (dispatchNestedPreScroll(0, dy, mScrollConsumed, mScrollOffsets, ViewCompat.TYPE_TOUCH)) {
                    dy -= mScrollConsumed[1]
                    vtev.offsetLocation(0f, (mScrollOffsets[1]).toFloat())
                    mNestedOffsets[1] += mScrollOffsets[1]
                }

                val oldScrollY = scrollY
                lastY = y - mScrollOffsets[1]
                val newScrollY = max(0, oldScrollY + dy)
                val yConsumed = newScrollY - oldScrollY
                dy -= yConsumed
                if (dispatchNestedScroll(0, yConsumed, 0, dy, mScrollOffsets, ViewCompat.TYPE_TOUCH)) {
                    vtev.offsetLocation(0f, (mScrollOffsets[1]).toFloat())
                    mNestedOffsets[1] += mScrollOffsets[1]
                    lastY -= mScrollOffsets[1]
                }

                if (mScrollConsumed[1] != 0 || mScrollOffsets[1] != 0) {
                    cancelWhileMove = true
                    result = super.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
                } else {
                    if (cancelWhileMove) {
                        vtev.action = MotionEvent.ACTION_DOWN
                        super.onTouchEvent(vtev)
                        cancelWhileMove = false
                    } else {
                        result = super.onTouchEvent(vtev)
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onPointerUp(event)
            }

            MotionEvent.ACTION_UP -> {
                tracker.addMovement(vtev)
                eventAddedToVelocityTracker = true
                tracker.computeCurrentVelocity(1000)
                if ((abs(tracker.yVelocity) > minFlinVY) && !dispatchNestedPreFling(0f, -tracker.yVelocity)) {
                    dispatchNestedFling(0f, -tracker.yVelocity, true)
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
                    lastFlingY = scrollY
                    scroller.fling(scrollX, scrollY, 0, -tracker.yVelocity.toInt(), 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
                    invalidate()
                    return true
                } else {
                    result = super.onTouchEvent(vtev)
                }
                tracker.clear()
            }

            MotionEvent.ACTION_CANCEL -> {
                tracker.clear()
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
                result = super.onTouchEvent(vtev)
            }
        }


        if (!eventAddedToVelocityTracker) {
            tracker.addMovement(vtev)
        }
        vtev.recycle()
        return result
    }

    private fun onPointerUp(e: MotionEvent) {
        val actionIndex = e.actionIndex
        if (e.getPointerId(actionIndex) == scrollPointerId) {
            // Pick a new pointer to pick up the slack.
            val newIndex = if (actionIndex == 0) 1 else 0
            scrollPointerId = e.getPointerId(newIndex)
            lastY = e.getY(newIndex).roundToInt()
            initialTouchY = lastY
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            var dy = scroller.currY - lastFlingY
            if (dy != 0 && dispatchNestedPreScroll(0, dy, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                dy -= mScrollConsumed[1]
            }

            if (dy != 0) {

                if (scrollY < 0) {
                    scrollY = 0
                }

                val toScrollY = max(scrollY + dy, 0)
                val yConsumed = toScrollY - scrollY

                val yUnConsumed = dy - yConsumed
                if (yUnConsumed != 0 && !dispatchNestedScroll(0, yConsumed, 0, yUnConsumed, mScrollOffsets, ViewCompat.TYPE_NON_TOUCH)) {
                    if (scroller.currVelocity != 0f && mScrollOffsets[1] == 0) {
                        scroller.abortAnimation()
                        return super.computeScroll()
                    }
                }

                if (yConsumed != 0) {
                    scrollBy(0, yConsumed)
                }
            }

            lastFlingY = scroller.currY
            postInvalidate()
        }
        super.computeScroll()
    }
}
