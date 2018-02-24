package com.lyc.gank.ui

import android.widget.ImageView
import com.lyc.data.resp.GankItem

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
interface OnGankItemClickListener {
    fun onGirlItemClick(iv: ImageView, item: GankItem)
    fun onVideoItemClick(item: GankItem)
    fun onArticleItemClick(item: GankItem)
}