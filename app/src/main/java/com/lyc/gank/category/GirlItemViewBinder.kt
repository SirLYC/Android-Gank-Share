package com.lyc.gank.category

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.utils.gankOption
import com.lyc.gank.utils.logi
import kotlinx.android.synthetic.main.item_girl.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class GirlItemViewBinder(
        private val onGankItemClickListener: OnGankItemClickListener
) : ItemViewBinder<GankItem, GirlItemViewBinder.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, item: GankItem) {
        holder.itemView.setOnClickListener {
            onGankItemClickListener.onGirlItemClick(holder.img, item)
        }
        Glide.with(holder.img)
                .load(item.imgUrl)
                .gankOption()
                .into(holder.img)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_girl, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.iv_girl!!
    }
}