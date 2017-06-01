package com.lyc.gank.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.Bean.Results;
import com.lyc.gank.Database.Item;
import com.lyc.gank.HomeActivity;
import com.lyc.gank.Util.HttpUtility;
import com.lyc.gank.Util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 首页展示Viewpager的fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    /**
     * 数据接口地址前缀
     */
    protected String addressPre;

    protected int pageLoadNow = 2;
    /**
     * 数据源
     */
    protected List<ResultItem> mData = new ArrayList<>();
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
    /**
     * 和HomeActivity通信
     */
    protected Handler handler;
    /**
     * 懒加载fragment的标志
     */
    private boolean isFirstVisibleToUser = true;
    /**
     * 防止加载过程在setContentView之前进行
     */
    protected boolean isPreparedCreate;
    /**
     * 结束获取数据的回调接口
     */
    public interface OnFinishLoadListener{
        void onFinish(int flag);
    }
    protected OnFinishLoadListener mListener;

    protected void setOnFinishLoadListener(OnFinishLoadListener listener){
        mListener = listener;
    }

    public void backToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
            FABVisibility = View.INVISIBLE;
        }
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
        isPreparedCreate = true;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!recyclerView.canScrollVertically(1) && dy > 0){
                    getItemsFromServer(FLAG_ADD);
                }
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        handler = ((HomeActivity)getActivity()).handler;
        if(getUserVisibleHint() && isFirstVisibleToUser){
            onFirstLoadData();
        }
    }

    private void onFirstLoadData() {
        isFirstVisibleToUser = false;
        getItemsFromServer(FLAG_INIT);
        handler.sendEmptyMessage(HomeActivity.MSG_START_LOAD);
    }

    /**
     * 实现懒加载
     * @param isVisibleToUser
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
     * @param flag
     */
    public void getItemsFromServer(final int flag) {
        Log.e(this.toString(), "load data");
        if(addressPre == null){
            return;
        }
        String address = null;
        switch (flag){
            case FLAG_INIT:
                address = addressPre + "40/1";
                break;
            case FLAG_ADD:
                pageLoadNow++;
                address = addressPre + "20/" + pageLoadNow;
                break;
            case FLAG_REFRESH:
                pageLoadNow = pageLoadNow > 2? 2 : pageLoadNow;
                address = addressPre + (20 * pageLoadNow) + "/1";
                handler.sendEmptyMessageDelayed(HomeActivity.MSG_REFRESH_FAILED, 8000);
                break;
            default:
                break;
        }

        if(address == null){
            throw new IllegalArgumentException("Not invalid flag!");
        }

        HttpUtility.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(HomeActivity.MSG_REFRESH_FAILED);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Results res = Utility.handleResponse(response.body().string());
                if(res != null) {
                    if(flag != FLAG_ADD) {
                        mData.clear();
                    }

                    for (ResultItem item : res.resultItems) {
                        mData.add(item);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(HomeActivity.MSG_STOP_REFRESH);
                            if(mListener != null){
                                mListener.onFinish(flag);
                            }
                        }
                    });
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(HomeActivity.MSG_REFRESH_FAILED);
                        }
                    });
                }
            }
        });
    }

    public void setAddressPre(String addressPre) {
        this.addressPre = addressPre;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void collect(int pos, View v){
        final Item itemCollect = new Item(mData.get(pos));
        if(DataSupport.where("idOnServer = ?",
                itemCollect.getIdOnServer()).find(Item.class).isEmpty()){
            Snackbar.make(v, "加入收藏？", Snackbar.LENGTH_LONG)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemCollect.save();
                        }
                    }).show();

        }else {
            Snackbar.make(v, "已经在收藏列表中了！", Snackbar.LENGTH_SHORT)
                    .setAction("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}
                    }).show();
        }
    }
}
