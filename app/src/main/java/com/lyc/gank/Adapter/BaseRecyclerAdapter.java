package com.lyc.gank.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.R;

/**
 * 分类推荐中所有adapter的基类
 */

public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerAdapter.BaseHolder> extends RecyclerView.Adapter<VH> {
    OnItemClickedListener mListener;
    OnItemLongClickedListener mLongListener;

    /**
     * item点击事件回调接口
     */
    public interface OnItemClickedListener {
        public void onClick(ResultItem item);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener){
        mListener = listener;
    }

    public interface OnItemLongClickedListener {
        public boolean onLongClick(int pos, View v);
    }

    public void setOnItemLongClickedListener(OnItemLongClickedListener listener){
        mLongListener = listener;
    }

    static class BaseHolder extends RecyclerView.ViewHolder{
        View view;

        BaseHolder(View itemView){
            super(itemView);
            view = itemView;
            itemView.setBackgroundResource(R.drawable.touch_bg);
            itemView.setClickable(true);
        }
    }


}
