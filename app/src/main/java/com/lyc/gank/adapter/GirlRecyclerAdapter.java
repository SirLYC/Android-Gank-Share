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
//import com.lyc.gank.bean.GankItem;
//import com.lyc.gank.R;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * 显示妹子的adapter
// */
//
//public class GirlRecyclerAdapter extends BaseRecyclerAdapter<GirlRecyclerAdapter.GirlHolder>{
//
//    private List<GankItem> mData;
//    private Context mContext;
//
//    public GirlRecyclerAdapter(List<GankItem> mGirlList, Context mContext) {
//        this.mData = mGirlList;
//        this.mContext = mContext;
//    }
//
//    @Override
//    public GirlHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.item_girls, parent, false);
//        return new GirlHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final GirlHolder holder, final int position) {
//        final GankItem item = mData.get(position);
//        holder.date.setText(item.publishTime);
//        Glide.with(mContext).load(item.imgUrl)
//                .skipMemoryCache(true)
//                .placeholder(R.drawable.loading)
//                .into(holder.girlImg);
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mOnItemClickListener != null){
//                    mOnItemClickListener.onClick(position, holder.girlImg);
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
//    class GirlHolder extends BaseRecyclerAdapter.BaseHolder{
//        @BindView(R.id.img_girl)
//        ImageView girlImg;
//
//        @BindView(R.id.text_img_date)
//        TextView date;
//
//        GirlHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//}
