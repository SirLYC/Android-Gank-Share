package com.lyc.gank.ui

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.utils.TimeUtil
import kotlinx.android.synthetic.main.item_article_without_img.view.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class GankWithoutImgViewBinder(
        onGankItemClickListener: OnGankItemClickListener
) : AbstractGankItemViewBinder<GankWithoutImgViewBinder.ViewHolder>(onGankItemClickListener) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) =
            ViewHolder(inflater.inflate(R.layout.item_article_without_img, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: GankItem) {
        super.onBindViewHolder(holder, item)
        holder.title.text = item.title
        holder.info.text = "${item.type} ${TimeUtil.publishTime(item.publishTime)} ${item.author}"
    }

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_article_item_title!!
        val info = itemView.tv_article_item_info!!
    }
}