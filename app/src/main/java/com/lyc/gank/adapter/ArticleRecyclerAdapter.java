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
//import com.lyc.gank.R;
//import com.lyc.gank.bean.GankItem;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * 显示推荐的adapter
// */
//
//public class ArticleRecyclerAdapter extends BaseRecyclerAdapter<ArticleRecyclerAdapter.ArticleHolder> {
//
//    private List<GankItem> mData;
//    private Context mContext;
//
//    private static final int ITEM_WITH_IMG = 0;
//    private static final int ITEM_WITHOUT_IMG = 1;
//
//    public ArticleRecyclerAdapter(List<GankItem> resultItemList, Context context) {
//        mData = resultItemList;
//        mContext = context;
//    }
//
//
//    @Override
//    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if(viewType == ITEM_WITHOUT_IMG) {
//            View view = LayoutInflater.from(mContext)
//                    .inflate(R.layout.item_article_without_img, parent, false);
//            return new ArticleHolder(view);
//        }
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.item_article_with_img, parent, false);
//        return new ArticleHolderWithImg(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ArticleHolder holder, final int position) {
//        final GankItem resultItem = mData.get(position);
//        String title = resultItem.title;
//        final int type = getItemViewType(position);
//        if(title.length() > 80 && type == ITEM_WITHOUT_IMG){
//            title = title.substring(0, 75) + "...";
//        }else if(title.length() > 50 && type == ITEM_WITH_IMG){
//            title = title.substring(0, 50) + "...";
//        }
//        String author = resultItem.author == null? "匿名":resultItem.author;
//
//        holder.title.setText(title);
//        holder.publishTime.setText(resultItem.publishTime);
//        holder.type.setText(resultItem.type);
//        holder.author.setText(author);
//        if(type == ITEM_WITH_IMG){
//            Glide.with(mContext).load(resultItem.imgUrl)
//                    .into(((ArticleHolderWithImg)holder).itemImg);
//        }
//
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mOnItemClickListener != null){
//                    if(type != ITEM_WITHOUT_IMG)
//                        v = ((ArticleHolderWithImg)holder).itemImg;
//                    mOnItemClickListener.onClick(position, v);
//                }
//            }
//        });
//        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return mOnItemClickListener!= null && mOnItemClickListener.onLongClick(position, v);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }
//
//    class ArticleHolderWithImg extends ArticleHolder{
//        @BindView(R.id.item_img)
//        ImageView itemImg;
//
//        ArticleHolderWithImg(View itemView) {
//            super(itemView);
//        }
//    }
//
//    class ArticleHolder extends BaseRecyclerAdapter.BaseHolder{
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
//        ArticleHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        GankItem resultItem = mData.get(position);
//        if(resultItem.imgUrl == null){
//            return ITEM_WITHOUT_IMG;
//        }
//
//        return ITEM_WITH_IMG;
//    }
//}
