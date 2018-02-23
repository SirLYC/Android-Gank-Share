package com.lyc.gank.ui

import android.support.v7.widget.RecyclerView
import com.lyc.data.resp.GankItem
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/21.
 */
abstract class AbstractGankItemViewBinder<VH : RecyclerView.ViewHolder>(
        private val onGankItemClickListener: OnGankItemClickListener
) : ItemViewBinder<GankItem, VH>() {
    override fun onBindViewHolder(holder: VH, item: GankItem) {
        holder.itemView.setOnClickListener {
            onGankItemClickListener.onGankItemClick(item)
        }
    }

    interface OnGankItemClickListener {
        fun onGankItemClick(item: GankItem)
    }
}