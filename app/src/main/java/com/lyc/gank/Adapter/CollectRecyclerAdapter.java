package com.lyc.gank.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.Database.Item;
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

    public interface OnItemSelectedListener {
        public void onSelected(int pos, View v);
    }

    private OnItemSelectedListener mOnItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener){
        mOnItemSelectedListener = listener;
    }

    public interface OnItemLongClickListener{
        public boolean onLongClickedListener(int pos, View v);
    }

    private OnItemLongClickListener mOnItemLongClickListener;


    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mOnItemLongClickListener = listener;
    }

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
                    .inflate(R.layout.collect_item_with_img, parent, false);
            return new ItemWithImg(view);
        }else {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.collect_item_without_img, parent, false);
            return new ItemWithoutImg(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, final int position) {
        final Item item = mData.get(position);
        if(getItemViewType(position) == ITEM_WITH_IMG) {
            ItemWithImg mHolder = (ItemWithImg) holder;
            mHolder.title.setText(item.getTitle());
            mHolder.publishTime.setText(item.getPublishTime().substring(0, 10));
            mHolder.type.setText(item.getType());
            if (item.getAuthor() != null) {
                mHolder.author.setText(item.getAuthor());
            } else {
                mHolder.author.setText("匿名");
            }
            Glide.with(mContext).load(item.getImages().get(0))
                    .into(mHolder.itemImg);
            if(onSelect){
                mHolder.check.setVisibility(View.VISIBLE);
            }else {
                mHolder.check.setVisibility(View.GONE);
            }
        }else if(getItemViewType(position) == ITEM_GIRLS){
            ItemWithImg mHolder = (ItemWithImg) holder;
            mHolder.title.setText(item.getTitle());
            mHolder.publishTime.setText(item.getPublishTime().substring(0, 10));
            mHolder.type.setText(item.getType());
            if (item.getAuthor() != null) {
                mHolder.author.setText(item.getAuthor());
            } else {
                mHolder.author.setText("匿名");
            }
            Glide.with(mContext).load(item.getUrl())
                    .into(mHolder.itemImg);
            if(onSelect){
                mHolder.check.setVisibility(View.VISIBLE);
            }else {
                mHolder.check.setVisibility(View.GONE);
            }
        }else {
            ItemWithoutImg mHolder = (ItemWithoutImg) holder;
            mHolder.title.setText(item.getTitle());
            mHolder.publishTime.setText(item.getPublishTime().substring(0, 10));
            mHolder.type.setText(item.getType());
            if (item.getAuthor() != null) {
                mHolder.author.setText(item.getAuthor());
            } else {
                mHolder.author.setText("匿名");
            }
            if(onSelect){
                mHolder.check.setVisibility(View.VISIBLE);
            }else {
                mHolder.check.setVisibility(View.GONE);
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemSelectedListener != null){
                    mOnItemSelectedListener.onSelected(position, v);
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemLongClickListener != null){
                    return mOnItemLongClickListener.onLongClickedListener(position, v);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private static class ItemWithImg extends BaseHolder{
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
            cardView = itemView.findViewById(R.id.card_view);
            check = (ImageView) itemView.findViewById(R.id.check_img);
        }
    }

    private static class ItemWithoutImg extends BaseHolder{
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
            cardView = itemView.findViewById(R.id.card_view);
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
