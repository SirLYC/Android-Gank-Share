package com.lyc.gank.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.Adapter.ArticleRecyclerAdapter;
import com.lyc.gank.Adapter.BaseRecyclerAdapter;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.R;
import com.lyc.gank.WebActivity;

/**
 * 展示文章的fragment
 */
public class ArticleFragment extends BaseFragment {

    ArticleRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.article_recycler);
        mAdapter = new ArticleRecyclerAdapter(mData, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickedListener(new BaseRecyclerAdapter.OnItemClickedListener() {
            @Override
            public void onClick(ResultItem item) {
                 if(item.type.equals("休息视频")){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(item.url);
                    intent.setData(uri);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getContext(), WebActivity.class);
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            }
        });
        mAdapter.setOnItemLongClickedListener(new BaseRecyclerAdapter.OnItemLongClickedListener() {
            @Override
            public boolean onLongClick(int pos, View v) {
                collect(pos, v);
                return true;
            }
        });
        setOnFinishLoadListener(new OnFinishLoadListener() {
            @Override
            public void onFinish(int flag) {
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }


}
