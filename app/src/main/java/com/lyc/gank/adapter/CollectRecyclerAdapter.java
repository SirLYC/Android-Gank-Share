package com.lyc.gank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.database.Item;
import com.lyc.gank.R;

import java.util.List;

/**
 * 显示收藏的adapter
 */

public class CollectRecyclerAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseHolder> {

    private List<Item> mData;
    private Context mContext;
    private boolean onSelect = false;

    private static final int ITEM_WITH_IMG = 0;
    private static final int ITEM_WITHOUT_IMG = 1;
    private static final int ITEM_GIRLS = 2;

    public CollectRecyclerAdapter(List<Item> resultItemList, Context context) {
        mData = resultItemList;
        mContext = context;
    }

    public void setOnSelect(boolean onSelect) {
        this.onSelect = onSelect;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_WITH_IMG || viewType ==ITEM_GIRLS) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_collect_with_img, parent, false);
            return new ItemWithImg(view);
        }else {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_collect_without_img, parent, false);
            return new ItemWithoutImg(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.BaseHolder holder, final int position) {
        final Item item = mData.get(position);
        String author = item.getAuthor() == null? "匿名":item.getAuthor();
        String imgUrl = null;
        int type = getItemViewType(position);
        if(type == ITEM_GIRLS)
            imgUrl = item.getUrl();
        else if(type == ITEM_WITH_IMG)
            imgUrl = item.getImages().get(0);

        if(type == ITEM_WITHOUT_IMG){
            ItemWithoutImg mHolder = (ItemWithoutImg) holder;
            mHolder.title.setText(item.getTitle());
            mHolder.publishTime.setText(item.getPublishTime().substring(0, 10));
            mHolder.type.setText(item.getType());
            mHolder.author.setText(author);
            if(onSelect){
                mHolder.check.setVisibility(View.VISIBLE);
            }else {
                mHolder.check.setVisibility(View.GONE);
            }
        }else {
            ItemWithImg mHolder = (ItemWithImg) holder;
            mHolder.title.setText(item.getTitle());
            mHolder.publishTime.setText(item.getPublishTime().substring(0, 10));
            mHolder.type.setText(item.getType());
            mHolder.author.setText(author);
            Glide.with(mContext).load(imgUrl)
                    .into(mHolder.itemImg);
            if(onSelect){
                mHolder.check.setVisibility(View.VISIBLE);
            }else {
                mHolder.check.setVisibility(View.GONE);
            }
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onClick(position, v);
                }
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mOnItemClickListener != null && mOnItemClickListener.onLongClick(position, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private class ItemWithImg extends BaseHolder{
        TextView author;
        TextView title;
        ImageView itemImg;
        ImageView check;
        TextView publishTime;
        TextView type;

        ItemWithImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            itemImg = (ImageView)itemView.findViewById(R.id.item_img);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            type = (TextView) itemView.findViewById(R.id.text_type);
            check = (ImageView) itemView.findViewById(R.id.check_img);
        }
    }

    private class ItemWithoutImg extends BaseHolder{
        TextView author;
        TextView title;
        TextView publishTime;
        TextView type;
        ImageView check;

        ItemWithoutImg(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.text_author);
            title = (TextView) itemView.findViewById(R.id.text_title);
            publishTime = (TextView) itemView.findViewById(R.id.text_publish_time);
            type = (TextView) itemView.findViewById(R.id.text_type);
            check = (ImageView) itemView.findViewById(R.id.check_img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Item item = mData.get(position);
        if(item.getImages() != null && item.getImages().size() > 0){
            return ITEM_WITH_IMG;
        }else if(item.getType().equals("\u798f\u5229")){
            return ITEM_GIRLS;
        }else {
            return ITEM_WITHOUT_IMG;
        }
    }
}
