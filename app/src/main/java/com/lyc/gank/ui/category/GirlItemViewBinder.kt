package com.lyc.gank.ui.category

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.ui.AbstractGankItemViewBinder
import com.lyc.gank.utils.gankOption
import kotlinx.android.synthetic.main.item_girl.view.*

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class GirlItemViewBinder(onGankItemClickListener: OnGankItemClickListener) :
        AbstractGankItemViewBinder<GirlItemViewBinder.ViewHolder>(onGankItemClickListener) {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_girl, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GankItem, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, item, payloads)
        Glide.with(holder.img)
                .load(item.imgUrl)
                .gankOption()
                .into(holder.img)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.iv_girl!!
    }
}