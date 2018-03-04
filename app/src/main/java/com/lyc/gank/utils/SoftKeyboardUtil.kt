package com.lyc.gank.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Liu Yuchuan on 2018/3/4.
 */
object SoftKeyboardUtil {
    fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager? = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}