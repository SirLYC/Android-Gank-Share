package com.lyc.gank.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.adapter.BaseRecyclerAdapter;
import com.lyc.gank.adapter.ArticleRecyclerAdapter;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.R;
import com.lyc.gank.WebActivity;

import butterknife.ButterKnife;

/**
 * 展示推荐的fragment
 */
public class ArticleFragment extends GankDataFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_article, container, false);
        setRecyclerView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(KEY_TYPE);
            Log.e(toString(), type + "");
        }
    }

    public static ArticleFragment getInstance(String type){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        ArticleFragment fragment = new ArticleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void setRecyclerView(View view) {
        mRecyclerView = ButterKnife.findById(view, R.id.recycler_view_article);
        ArticleRecyclerAdapter mAdapter = new ArticleRecyclerAdapter(mData, getContext());
        adapter = mAdapter;
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, View v) {
                ResultItem item = mData.get(pos);
                Intent intent = new Intent();
                switch (item.type){
                    case "休息视频":
                        intent.setAction("android.intent.setAction.VIEW");
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
            @Override
            public boolean onLongClick(int pos, View v) {
                itemNow = pos;
                return false;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
