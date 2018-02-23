package com.lyc.gank.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
fun Context.textCopy(content: CharSequence) {
    val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipBoardManager.primaryClip = ClipData.newPlainText(null, content)
}

fun Context.textPaste(): CharSequence? {
    val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    return if (clipBoardManager.hasPrimaryClip())
        clipBoardManager.primaryClip.getItemAt(0).text
    else null
}