package com.lyc.gank.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.Bean.ImageUrls;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;

import java.util.List;

/**
 * 显示推荐的adapter
 */

public class ArticleRecyclerAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseHolder> {

    private List<ResultItem> mData;
    private Context mContext;

    private static final int ITEM_WITH_IMG = 0;
    private static final int ITEM_WITHOUT_IMG = 1;
    private static final int ITEM_GIRLS = 2;

    public ArticleRecyclerAdapter(List<ResultItem> resultItemList, Context context) {
        mData = resultItemList;
        mContext = context;
    }


    @Override
    public BaseRecyclerAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_WITHOUT_IMG) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_article_without_img, parent, false);
            return new ArticleWithoutImg(view);
        }

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_article_with_img, parent, false);
        return new ArticleWithImg(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, final int position) {
        final ResultItem resultItem = mData.get(position);
        String title = resultItem.title;
        if(title.length() > 75){
            title = title.substring(0, 75) + "...";
        }

        int type = getItemViewType(position);
        String author = resultItem.author == null? "匿名":resultItem.author;
        String imgUrl = null;
        if(type == ITEM_GIRLS)
            imgUrl = resultItem.url;
        else if(type == ITEM_WITH_IMG)
            imgUrl = resultItem.images.get(0);

        if(type == ITEM_WITHOUT_IMG){
            ArticleWithoutImg mHolder = (ArticleWithoutImg) holder;
            mHolder.title.setText(title);
            mHolder.publishTime.setText(resultItem.publishTime.substring(0, 10));
            mHolder.type.setText(resultItem.type);
            mHolder.author.setText(author);
        }else {
            ArticleWithImg mHolder = (ArticleWithImg) holder;
            mHolder.title.setText(title);
            mHolder.publishTime.setText(resultItem.publishTime.substring(0, 10));
            mHolder.type.setText(resultItem.type);
            mHolder.author.setText(author);
            Glide.with(mContext).load(imgUrl)
                    .into(mHolder.itemImg);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClick(mData.get(position));
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mLongListener != null && mLongListener.onLongClick(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private static class ArticleWithImg extends BaseHolder{
        TextView author;
        TextView title;
        ImageView itemImg;
        TextView publishTime;
        TextView type;

        ArticleWithImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            itemImg = (ImageView)itemView.findViewById(R.id.item_img);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            type = (TextView) itemView.findViewById(R.id.text_type);
        }
    }

    private static class ArticleWithoutImg extends BaseHolder{
        TextView author;
        TextView title;
        TextView publishTime;
        TextView type;

        ArticleWithoutImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            type = (TextView) itemView.findViewById(R.id.text_type);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ResultItem resultItem = mData.get(position);
        if(resultItem.type.equals("福利"))
            return ITEM_GIRLS;

        if(resultItem.images != null && resultItem.images.size() > 0)
            return ITEM_WITH_IMG;

            return ITEM_WITHOUT_IMG;
    }
}
