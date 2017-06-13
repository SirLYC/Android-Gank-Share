package com.lyc.gank.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.Adapter.BaseRecyclerAdapter;
import com.lyc.gank.Adapter.ArticleRecyclerAdapter;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.R;
import com.lyc.gank.WebActivity;

/**
 * 展示推荐的fragment
 */
public class ArticleFragment extends BaseFragment {

    private ArticleRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_article);
        mAdapter = new ArticleRecyclerAdapter(mData, getContext());
        mAdapter.setOnItemClickedListener(new BaseRecyclerAdapter.OnItemClickedListener() {
            @Override
            public void onClick(ResultItem item) {
                Intent intent = new Intent();
                switch (item.type){
                    case "休息视频":
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(item.url);
                        intent.setData(uri);
                        startActivity(intent);
                        break;
                    default:
                        intent.setClassName(getContext(), WebActivity.class.getName());
                        intent.putExtra("item", item);
                        startActivity(intent);
                        break;
                }
            }
        });
        mAdapter.setOnItemLongClickedListener(new BaseRecyclerAdapter.OnItemLongClickedListener() {
            @Override
            public boolean onLongClick(int pos, View v) {
                itemNow = pos;
                return false;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnFinishLoadListener(new OnFinishLoadListener() {
            @Override
            public void onFinish(int flag) {
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
