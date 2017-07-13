package com.lyc.gank.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.adapter.ArticleRecyclerAdapter;
import com.lyc.gank.bean.RecommendResults;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.util.TimeUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.value;

public class GankRecommendFragment extends GankBaseFragment {

    @BindView(R.id.tool_bar_recommend)
    Toolbar toolbar;

    @BindView(R.id.refresh_layout_recommend)
    SwipeRefreshLayout refreshLayout;

    private ArticleRecyclerAdapter mAdapter;

    private Date mDate = new Date();

    private static final int STATE_REFRESH = 0;

    private static final int STATE_NO_CHANGE = 1;

    private static final int STATE_NO_DATA = 2;

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

    @Override
    protected void setRecyclerView() {
        mAdapter = new ArticleRecyclerAdapter(mData, getContext());
        adapter = mAdapter;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        super.setRecyclerView();
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
                })
                .map(new Function<List<ResultItem>, Integer>() {
                    @Override
                    public Integer apply(List<ResultItem> list) throws Exception {
                        if(list != null && list.size() > 1 && !list.get(0)._id.equals(lastIdOnServer)) {
                            mData.clear();
                            mData.addAll(list);
                            return STATE_REFRESH;
                        }

                        if(list == null || list.size() <= 1) {
                            return STATE_NO_DATA;
                        }

                        return STATE_NO_CHANGE;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        refreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(Integer value) {
                        switch (value){
                            case STATE_REFRESH:
                                mAdapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                                needRefresh = false;
                                saveData();
                                today = new Date();
                                break;
                            case STATE_NO_DATA:
                                mDate = TimeUtil.getYesterday(mDate);
                                loadData();
                                break;
                            case STATE_NO_CHANGE:
                                refreshLayout.setRefreshing(false);
                            default:
                                break;
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

                    @Override
                    public void onComplete() {}
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
}
