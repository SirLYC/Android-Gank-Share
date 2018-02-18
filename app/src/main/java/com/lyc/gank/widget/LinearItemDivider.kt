package com.gigavalue.mobile.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Liu Yuchuan on 2018/1/14.
 */
class LinearItemDivider(
        context: Context,
        private val mOrientation: Int = LinearLayoutManager.VERTICAL
) : RecyclerView.ItemDecoration() {

    private companion object {
        private const val mDividerWidth = 1
    }

    private val mDivider: Drawable

    init {
        val arr = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        mDivider = arr.getDrawable(0)
        arr.recycle()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerWidth)
        } else {
            outRect.set(0, 0, mDividerWidth, 0)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        when (mOrientation) {
            LinearLayoutManager.VERTICAL -> drawVertical(c, parent)
            LinearLayoutManager.HORIZONTAL -> drawHorizontal(c, parent)
            else -> throw IllegalArgumentException("Unsupported orientation " + mOrientation)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.measuredHeight - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mDividerWidth
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mDividerWidth
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}
