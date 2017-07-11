package com.lyc.gank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lyc.gank.adapter.BaseRecyclerAdapter;
import com.lyc.gank.adapter.GirlRecyclerAdapter;
import com.lyc.gank.bean.ImageUrls;
import com.lyc.gank.MainActivity;
import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;
import com.lyc.gank.util.TipUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 972694341@qq.com on 2017/6/7.
 */

public class GirlFragment extends GankDataFragment {

    private GirlRecyclerAdapter mAdapter;

    @BindView(R.id.fab_back_top)
    FloatingActionButton fab;

    @BindView(R.id.tool_bar_girls)
    Toolbar toolbar;

    @BindView(R.id.refresh_layout_girls)
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_girls, container, false);
        type = "福利";
        ButterKnife.bind(this, view);
        setRefreshLayout();
        setRecyclerView(view);
        ((MainActivity)mActivity).initToolbar(toolbar, "妹子图");
        setOnLoadListener(new GankDataFragment.OnLoadListener() {
            @Override
            public void onStart() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinish() {
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed() {
                refreshLayout.setRefreshing(false);
                TipUtil.showShort(mActivity, R.string.load_failed);
            }
        });
        return view;
    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(FLAG_REFRESH);
            }
        });
    }

    @OnClick(R.id.fab_back_top)
    public void backToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void setRecyclerView(View view) {
        mRecyclerView = ButterKnife.findById(view, R.id.recycler_view_girls);
        mAdapter = new GirlRecyclerAdapter(mData, getContext());
        adapter = mAdapter;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, View v) {
                Intent intent = new Intent(mActivity, PhotoActivity.class);
                intent.putExtra("urls", new ImageUrls(mData));
                intent.putExtra("page_now", pos);
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int pos, View v) {
                itemNow = pos;
                return false;
            }
        });
    }
}