package com.lyc.gank.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment

/**
 * Created by Liu Yuchuan on 2018/2/25.
 */

fun Activity.openWebPage(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        toast(getString(R.string.tip_no_matched_app))
    }
}

fun BaseFragment.openWebPage(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(activity()!!.packageManager) != null) {
        startActivity(intent)
    } else {
        toast(getString(R.string.tip_no_matched_app))
    }
}