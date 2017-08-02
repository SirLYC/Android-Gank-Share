package com.lyc.gank.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lyc.gank.R;
import com.lyc.gank.bean.Results;
import com.lyc.gank.util.TimeUtil;

import java.io.IOException;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * data数据接口下的的fragment基类
 */
public class GankDataFragment extends GankBaseFragment {

    protected static final String KEY_TYPE = "type";

    private OnLoadListener mOnLoadListener;

    private int mPage = 2;

    private static final int COUNT_PER_PAGE = 20;
    /**
     * 请求数据的flag，根据不同的flag对加载后数据源处理
     */
    public static final int FLAG_INIT = 0;
    public static final int FLAG_ADD = 1;
    public static final int FLAG_REFRESH = 2;
    /**
     * 保存每个fragment浮动按钮的状态
     */
    private int FABVisibility = View.INVISIBLE;

    public void backToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
            FABVisibility = View.INVISIBLE;
        }
    }

    public boolean isFirstVisibleToUser() {
        return isFirstVisibleToUser;
    }

    public int getFABVisibility() {
        return FABVisibility;
    }

    public void setFABVisibility(int FABVisibility) {
        this.FABVisibility = FABVisibility;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!recyclerView.canScrollVertically(1) && dy > 0){
                    loadData(FLAG_ADD);
                }
            }
        });
        registerForContextMenu(mRecyclerView);
        if(getUserVisibleHint() && isFirstVisibleToUser){
            onFirstLoadData();
        }
    }
    protected void onFirstLoadData() {
        isFirstVisibleToUser = false;
        if(mOnLoadListener != null){
            mOnLoadListener.onStart();
        }
        if(!loadDataFromLocal() || TimeUtil.needRefresh(getLastDate(), today)) {
            loadData(FLAG_INIT);
        }else if(mOnLoadListener != null){
            mOnLoadListener.onFinish();
        }
    }

    /**
     * 实现懒加载
     * @param isVisibleToUser 是否当前可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isFirstVisibleToUser && isPreparedCreate){
            onFirstLoadData();
        }
    }

    /**
     * 从服务器获取数据
     * @param flag 加载的标志，初始化、刷新、添加
     */
    public void loadData(final  int flag){
        Log.e(this.toString(), "load data for " + type);
        int count, page;
        switch (flag){
            case FLAG_INIT:{
                page = 1;
                count = mPage * COUNT_PER_PAGE;
                break;
            }
            case FLAG_ADD:{
                page = ++mPage;
                count = COUNT_PER_PAGE;
                break;
            }
            default:{
                page = 1;
                mPage = 2;
                count = 2 * COUNT_PER_PAGE;
                break;
            }
        }
        gankIoApi.getDataResults(type, count, page)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Results>() {
                    @Override
                    public boolean test(Results results) throws Exception {
                        return !results.resultItems.get(0).idOnServer.equals(lastIdOnServer);
                    }
                })
                .doOnNext(new Consumer<Results>() {
                    @Override
                    public void accept(Results results) throws Exception {
                        if(flag == FLAG_INIT || flag == FLAG_REFRESH) {
                            mData.clear();
                        }
                        mData.addAll(results.resultItems);
                        if(flag != FLAG_ADD) {
                            today = new Date();
                            saveData();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Results>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        if(mOnLoadListener != null) {
                            mOnLoadListener.onStart();
                        }
                    }

                    @Override
                    public void onNext(Results value) {
                        if(flag == FLAG_ADD){
                            for (int i = mData.size() - 20; i < mData.size(); i++){
                                adapter.notifyItemInserted(i);
                            }
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                        lastIdOnServer = mData.get(0).idOnServer;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if(view != null && e instanceof IOException) {
                            showNoInternetEmptyView(true);
                        }
                        needRefresh = true;
                        if(mOnLoadListener != null) {
                            mOnLoadListener.onFailed();
                        }
                        Snackbar.make(mRecyclerView, R.string.internet_exception, Snackbar.LENGTH_SHORT)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        loadData(FLAG_REFRESH);
                                    }
                                }).show();
                    }

                    @Override
                    public void onComplete() {
                        needRefresh = false;
                        showNoInternetEmptyView(false);
                        if(mOnLoadListener != null) {
                            mOnLoadListener.onFinish();
                        }
                    }
                });
    }

    @Override
    public void refresh() {
        loadData(FLAG_REFRESH);
    }

    public interface OnLoadListener{
        void onStart();

        void onFinish();

        void onFailed();
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
    }
}
