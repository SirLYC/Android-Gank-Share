package com.lyc.gank.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.R;

import java.util.List;

/**
 * 显示文章的adapter
 */

public class ArticleRecyclerAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseHolder> {

    List<ResultItem> mData;
    Context mContext;

    private static final int ITEM_WITH_IMG = 0;
    private static final int ITEM_WITHOUT_IMG = 1;

    public ArticleRecyclerAdapter(List<ResultItem> resultItemList, Context context) {
        mData = resultItemList;
        mContext = context;
    }


    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_WITH_IMG) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.article_item_with_img, parent, false);
            return new ArticleWithImg(view);
        }else {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.article_item_without_img, parent, false);
            return new ArticleWithoutImg(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, final int position) {
        final ResultItem resultItem = mData.get(position);
        if(getItemViewType(position) == ITEM_WITH_IMG) {
            ArticleWithImg mHolder = (ArticleWithImg) holder;
            mHolder.title.setText(resultItem.title);
            mHolder.publishTime.setText(resultItem.publishTime.substring(0, 10));
            if (resultItem.author != null) {
                mHolder.author.setText(resultItem.author);
            } else {
                mHolder.author.setText("匿名");
            }
            Glide.with(mContext).load(resultItem.images.get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(mHolder.itemImg);
        }else {
            ArticleWithoutImg mHolder = (ArticleWithoutImg) holder;
            mHolder.title.setText(resultItem.title);
            mHolder.publishTime.setText(resultItem.publishTime.substring(0, 10));
            if (resultItem.author != null) {
                mHolder.author.setText(resultItem.author);
            } else {
                mHolder.author.setText("匿名");
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClick(resultItem);
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
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

        ArticleWithImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            itemImg = (ImageView)itemView.findViewById(R.id.item_img);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    private static class ArticleWithoutImg extends BaseHolder{
        TextView author;
        TextView title;
        TextView publishTime;

        ArticleWithoutImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ResultItem resultItem = mData.get(position);
        if(resultItem.images != null && resultItem.images.size() > 0){
            return ITEM_WITH_IMG;
        }else {
            return ITEM_WITHOUT_IMG;
        }
    }
}
