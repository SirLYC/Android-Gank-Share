package com.lyc.gank.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.adapter.ArticleRecyclerAdapter;
import com.lyc.gank.R;
import com.lyc.gank.fragment.base.GankDataFragment;

import butterknife.ButterKnife;

/**
 * 展示推荐的fragment
 */
public class ArticleFragment extends GankDataFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_article, container, false);
        }
        mRecyclerView = ButterKnife.findById(view, R.id.recycler_view_article);
        setRecyclerView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(KEY_TYPE);
        }
    }

    public static ArticleFragment getInstance(String type){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        ArticleFragment fragment = new ArticleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void setRecyclerView() {
        ArticleRecyclerAdapter mAdapter = new ArticleRecyclerAdapter(mData, mActivity);
        adapter = mAdapter;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        super.setRecyclerView();
    }
}
