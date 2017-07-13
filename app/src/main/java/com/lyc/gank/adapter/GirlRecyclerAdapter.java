package com.lyc.gank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.R;

import java.util.List;

/**
 * 显示妹子的adapter
 */

public class GirlRecyclerAdapter extends BaseRecyclerAdapter<GirlRecyclerAdapter.GirlHolder>{

    private List<ResultItem> mData;
    private Context mContext;

    public GirlRecyclerAdapter(List<ResultItem> mGirlList, Context mContext) {
        this.mData = mGirlList;
        this.mContext = mContext;
    }

    @Override
    public GirlHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_girls, parent, false);
        return new GirlHolder(view);
    }

    @Override
    public void onBindViewHolder(final GirlHolder holder, final int position) {
        final ResultItem item = mData.get(position);
        holder.date.setText(item.publishTime.substring(0, 10));
        Glide.with(mContext).load(item.url)
                .skipMemoryCache(true)
                .placeholder(R.drawable.loading)
                .into(holder.girlImg);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onClick(position, holder.girlImg);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mOnItemClickListener!= null && mOnItemClickListener.onLongClick(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class GirlHolder extends BaseRecyclerAdapter.BaseHolder{
        ImageView girlImg;
        TextView date;
        GirlHolder(View itemView) {
            super(itemView);
            girlImg = (ImageView) itemView.findViewById(R.id.img_girl);
            date = (TextView) itemView.findViewById(R.id.text_img_date);
            view = itemView;
        }
    }
}
