package com.lyc.gank.utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.gank.R
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/18.
 */

object LoadMoreItemViewBinder : ItemViewBinder<LoadMoreItem, LoadMoreItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup)
            = ViewHolder(inflater.inflate(R.layout.item_load_more, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, item: LoadMoreItem) {}

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}