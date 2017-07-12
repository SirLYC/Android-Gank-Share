package com.lyc.gank.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lyc.gank.adapter.ArticleRecyclerAdapter;
import com.lyc.gank.adapter.BaseRecyclerAdapter;
import com.lyc.gank.bean.RecommendResults;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.SinglePhotoActivity;
import com.lyc.gank.bean.Results;
import com.lyc.gank.util.TimeUtil;
import com.lyc.gank.util.TipUtil;
import com.lyc.gank.WebActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class GankRecommendFragment extends GankBaseFragment {

    @BindView(R.id.tool_bar_recommend)
    Toolbar toolbar;

    @BindView(R.id.refresh_layout_recommend)
    SwipeRefreshLayout refreshLayout;

    private ArticleRecyclerAdapter mAdapter;

    private Date mDate = new Date();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, view);
        type = "推荐";
        ((MainActivity)mActivity).initToolbar(toolbar, "今日推荐");
        mRecyclerView = ButterKnife.findById(view, R.id.recycler_view_recommend);
        setRecyclerView();
        setRefreshLayout();
        return view;
    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                today = new Date();
                mDate = new Date();
                loadData();
            }
        });
    }

    private void setRecyclerView() {
        mAdapter = new ArticleRecyclerAdapter(mData, getContext());
        adapter = mAdapter;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, View v) {
                ResultItem item = mData.get(pos);
                Intent intent = new Intent();
                switch (item.type){
                    case "福利":
                        intent.setClassName(getContext(), SinglePhotoActivity.class.getName());
                        intent.putExtra("url", item.url);
                        startActivity(intent);
                        break;
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
        registerForContextMenu(mRecyclerView);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isFirstVisibleToUser && isPreparedCreate) {
            isFirstVisibleToUser = false;
            refreshLayout.setRefreshing(true);
            if(!loadDataFromLocal() || TimeUtil.needRefresh(getLastDate(), today)) {
                loadData();
            }else {
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private void loadData(){
        gankIoApi.getReconmmendResults(TimeUtil.getDateString(mDate))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<RecommendResults, List<ResultItem>>() {
                    @Override
                    public List<ResultItem> apply(RecommendResults recommendResults) throws Exception {
                        return recommendResults.recommend.merge();
                    }
                }).filter(new Predicate<List<ResultItem>>() {
            @Override
            public boolean test(List<ResultItem> resultItems) throws Exception {
                if(resultItems != null && resultItems.size() > 1){
                    mData.clear();
                    mData.addAll(resultItems);
                    return true;
                }else {
                    return false;
                }
            }
        }).isEmpty()
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        refreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onSuccess(Boolean value) {
                        showNoInternetEmptyView(false);
                        if(value) {
                            mDate = TimeUtil.getYesterday(mDate);
                            loadData();
                        }else {
                            mAdapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                            needRefresh = false;
                            saveData();
                            today = new Date();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        showNoInternetEmptyView(true);
                        needRefresh = true;
                        Snackbar.make(mRecyclerView, R.string.internet_exception, Snackbar.LENGTH_SHORT)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadData();
                                    }
                                }).show();
                    }
                });
    }

    @Override
    public void refresh() {
        if(needRefresh) {
            mDate = new Date();
            loadData();
        }
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }
}
