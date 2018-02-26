package com.lyc.gank

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lyc.data.resp.GankItem
import com.lyc.gank.utils.TimeUtil
import com.lyc.gank.utils.gankOption
import com.lyc.gank.utils.logi
import kotlinx.android.synthetic.main.item_article_with_img.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class GankWithImgViewBinder(
        private val onGankItemClickListener: OnGankItemClickListener
) : ItemViewBinder<GankItem, GankWithImgViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) =
            ViewHolder(inflater.inflate(R.layout.item_article_with_img, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, item: GankItem) {
        holder.itemView.setOnClickListener {
            logi("GankItemClick", "${item.type} ${item.type == "福利"}")
            when (item.type) {
                "福利" -> onGankItemClickListener.onGirlItemClick(holder.img, item)
                "休息视频" -> onGankItemClickListener.onVideoItemClick(item)
                else -> onGankItemClickListener.onArticleItemClick(item)
            }
        }
        holder.title.text = item.title
        holder.info.text = ("${item.type} ${TimeUtil.publishTime(item.publishTime)} ${item.author}")
        Glide.with(holder.img)
                .load(item.imgUrl)
                .gankOption()
                .into(holder.img)
    }

    class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.tv_article_item_title!!
        val info = itemView.tv_article_item_info!!
        val img = itemView.iv_article_item_img!!
    }
}