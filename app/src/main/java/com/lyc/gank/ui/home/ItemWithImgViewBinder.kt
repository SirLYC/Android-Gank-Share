package com.lyc.gank.ui.home

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lyc.data.resp.ResultItem
import com.lyc.gank.R
import com.lyc.gank.util.TimeUtil
import com.lyc.gank.util.gankOption
import kotlinx.android.synthetic.main.item_article_with_img.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class ItemWithImgViewBinder : ItemViewBinder<ResultItem, ItemWithImgViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) =
            ViewHolder(inflater.inflate(R.layout.item_article_with_img, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, item: ResultItem) {
        viewHolder.title.text = item.title
        viewHolder.info.text = "${item.type} ${TimeUtil.publishTime(item.publishTime)} ${item.author}"
        Glide.with(viewHolder.img)
                .load(item.images[0])
                .gankOption()
                .into(viewHolder.img)
    }

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_article_item_title
        val info = itemView.tv_article_item_info
        val img = itemView.iv_article_item_img
    }
}