package com.lyc.gank.utils

import android.content.Intent
import com.lyc.data.resp.GankItem

/**
 * Created by Liu Yuchuan on 2018/2/25.
 */

//GankItem
private const val KEY_ID = "KEY_ID"
private const val KEY_URL = "KEY_URL"
private const val KEY_IMAGE = "KEY_IMAGE"
private const val KEY_WHO = "KEY_WHO"
private const val KEY_TIME = "KEY_TIME"
private const val KEY_TYPE = "KEY_TYPE"
private const val KEY_DESC = "KEY_DESC"

fun Intent.putGankItem(gankItem: GankItem) =
        putExtra(KEY_ID, gankItem.idOnServer)
                .putExtra(KEY_URL, gankItem.url)
                .putExtra(KEY_IMAGE, gankItem.imgUrl)
                .putExtra(KEY_WHO, gankItem.author)
                .putExtra(KEY_TIME, gankItem.publishTime)
                .putExtra(KEY_TYPE, gankItem.type)
                .putExtra(KEY_DESC, gankItem.title)

fun Intent.getGankItem(): GankItem? {
    val id = getStringExtra(KEY_ID)
    val url = getStringExtra(KEY_URL)
    val image = getStringExtra(KEY_IMAGE)
    val who = getStringExtra(KEY_WHO)
    val time = getStringExtra(KEY_TIME)
    val type = getStringExtra(KEY_TYPE)
    val desc = getStringExtra(KEY_DESC)
    if (id == null || url == null || who == null
            || time == null || type == null || desc == null)
        return null
    return GankItem(id, desc, time, type, url, who, null)
            .apply { imgUrl = image }
}