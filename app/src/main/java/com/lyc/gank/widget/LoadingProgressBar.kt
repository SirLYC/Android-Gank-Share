package com.lyc.gank.widget

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.ContentLoadingProgressBar
import android.util.AttributeSet
import com.lyc.gank.R

/**
 * Created by Liu Yuchuan on 2018/1/14.
 */
class LoadingProgressBar : ContentLoadingProgressBar {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        context.obtainStyledAttributes(attrs, R.styleable.LoadingProgressBar).apply {
            val defColor = ResourcesCompat.getColor(context.resources, R.color.pb_loading, context.theme)
            val color = getColor(R.styleable.LoadingProgressBar_color, defColor)

            progressDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            indeterminateDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }.recycle() // dispose
    }
}