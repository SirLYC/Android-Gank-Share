//package com.lyc.gank.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.lyc.gank.bean.CollectItem;
//import com.lyc.gank.R;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * 显示收藏的adapter
// */
//
//public class CollectRecyclerAdapter extends BaseRecyclerAdapter<CollectRecyclerAdapter.CollectHolder> {
//
//    private List<CollectItem> mData;
//    private Context mContext;
//    private boolean onSelect = false;
//
//    private static final int ITEM_WITH_IMG = 0;
//    private static final int ITEM_WITHOUT_IMG = 1;
//    private static final int ITEM_GIRLS = 2;
//
//    public CollectRecyclerAdapter(List<CollectItem> resultItemList, Context context) {
//        mData = resultItemList;
//        mContext = context;
//    }
//
//    public void setOnSelect(boolean onSelect) {
//        this.onSelect = onSelect;
//    }
//
//    @Override
//    public CollectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if(viewType == ITEM_WITH_IMG) {
//            View view = LayoutInflater.from(mContext)
//                    .inflate(R.layout.item_collect_with_img, parent, false);
//            return new CollectHolderWithImg(view);
//        }
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.item_collect_without_img, parent, false);
//        return new CollectHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final CollectHolder holder, final int position) {
//        final CollectItem item = mData.get(position);
//        String author = item.author == null? "匿名":item.author;
//        final int type = getItemViewType(position);
//
//        holder.title.setText(item.title);
//        holder.publishTime.setText(item.publishTime);
//        holder.type.setText(item.type);
//        holder.author.setText(author);
//        if(onSelect){
//            holder.check.setVisibility(View.VISIBLE);
//        }else {
//            holder.check.setVisibility(View.GONE);
//        }
//
//        if(type == ITEM_WITH_IMG){
//            Glide.with(mContext).load(item.imgUrl)
//                    .into(((CollectHolderWithImg)holder).itemImg);
//        }
//
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mOnItemClickListener != null){
//                    if(type == ITEM_GIRLS) {
//                        v = ((CollectHolderWithImg) holder).itemImg;
//                    }
//                    mOnItemClickListener.onClick(position, v);
//                }
//            }
//        });
//        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return mOnItemClickListener != null && mOnItemClickListener.onLongClick(position, v);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }
//
//    class CollectHolderWithImg extends CollectHolder{
//        @BindView(R.id.item_img)
//        ImageView itemImg;
//
//        CollectHolderWithImg(View itemView) {
//            super(itemView);
//        }
//    }
//
//    class CollectHolder extends BaseRecyclerAdapter.BaseHolder{
//        @BindView(R.id.text_author)
//        TextView author;
//
//        @BindView(R.id.text_title)
//        TextView title;
//
//        @BindView(R.id.text_publish_time)
//        TextView publishTime;
//
//        @BindView(R.id.text_type)
//        TextView type;
//
//        @BindView(R.id.check_img)
//        ImageView check;
//
//        CollectHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        CollectItem item = mData.get(position);
//        if(item.imgUrl != null){
//            return ITEM_WITH_IMG;
//        }
//        return ITEM_WITHOUT_IMG;
//    }
//}
