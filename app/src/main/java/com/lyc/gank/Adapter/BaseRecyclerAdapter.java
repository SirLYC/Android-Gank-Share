package com.lyc.gank.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lyc.gank.Bean.ResultItem;

/**
 * 所有adapter的基类
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
        View cardView;

        BaseHolder(View itemView){
            super(itemView);
        }
    }


}
