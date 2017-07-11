package com.lyc.gank.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyc.gank.R;

/**
 * 分类推荐中所有adapter的基类
 */

public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerAdapter.BaseHolder> extends RecyclerView.Adapter<VH> {
    /**
     * item点击事件回调接口
     */
    public interface OnItemClickListener{

        void onClick(int pos, View v);

        boolean onLongClick(int pos, View v);
    }
    protected OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    class BaseHolder extends RecyclerView.ViewHolder{
        View view;

        BaseHolder(View itemView){
            super(itemView);
            view = itemView;
            itemView.setBackgroundResource(R.drawable.touch_bg);
            itemView.setClickable(true);
        }
    }
}
