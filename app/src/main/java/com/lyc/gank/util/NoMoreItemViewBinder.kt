package com.lyc.gank.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.gank.R
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/18.
 */
class NoMoreItemViewBinder: ItemViewBinder<ErrorItem, NoMoreItemViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup)
            = ViewHolder(inflater.inflate(R.layout.item_no_more, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, item: ErrorItem) = Unit

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}