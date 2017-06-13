package com.lyc.gank.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lyc.gank.Bean.Messages;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.Bean.Results;
import com.lyc.gank.Database.Item;
import com.lyc.gank.PhotoActivity;
import com.lyc.gank.Util.HttpUtility;
import com.lyc.gank.Util.ImageUtil;
import com.lyc.gank.Util.ShareUtil;
import com.lyc.gank.Util.ToastUtil;
import com.lyc.gank.Util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 分类浏览的fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    /**
     * 数据接口地址前缀
     */
    protected String addressPre;

    protected int itemNow;

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
    protected boolean isFirstVisibleToUser = true;
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
        registerForContextMenu(mRecyclerView);
        if(getUserVisibleHint() && isFirstVisibleToUser){
            onFirstLoadData();
        }
    }

    protected void onFirstLoadData() {
        isFirstVisibleToUser = false;
        getItemsFromServer(FLAG_INIT);
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
     * @param flag 加载的标志，初始化、刷新、添加
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
                break;
            default:
                break;
        }

        if(address == null){
            throw new IllegalArgumentException("Not invalid flag!");
        }

        handler.sendEmptyMessage(Messages.MSG_START_LOAD);
        HttpUtility.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(Messages.MSG_LOAD_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Results res = Utility.getResultsFromJson(response.body().string());
                if(res != null) {
                    if(flag != FLAG_ADD) {
                        mData.clear();
                    }

                    for (ResultItem item : res.resultItems) {
                        mData.add(item);
                    }
                    handler.sendEmptyMessage(Messages.MSG_LOAD_FINISHED);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mListener != null){
                                mListener.onFinish(flag);
                            }
                        }
                    });
                }else {
                    handler.sendEmptyMessage(Messages.MSG_LOAD_FAILED);
                }
            }
        });
    }

    public BaseFragment setAddressPre(String addressPre) {
        this.addressPre = addressPre;
        return this;
    }

    public BaseFragment setHandler(Handler handler){
        this.handler = handler;
        return this;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 0, Menu.NONE, "收藏");
        menu.add(Menu.NONE, 1, Menu.NONE, "分享");
        if(this instanceof GirlFragment){
            menu.add(Menu.NONE, 2, Menu.NONE, "保存妹子");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            ResultItem i = mData.get(itemNow);
            switch (item.getItemId()){
                case 0:
                    collect(i, mRecyclerView);
                    break;
                case 1:
                    if(i.type.equals("福利")){
                        Log.e("福利", "分享");
                        ShareUtil.shareImage(getContext(), i);
                    }else {
                        ShareUtil.shareItem(getContext(), i);
                    }
                    break;
                case 2:
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else {
                        save(i);
                    }
                    break;
            }
        }
        return true;
    }

    protected void collect(ResultItem i, View v){
        final Item itemCollect = new Item(i);
        if(DataSupport.where("idOnServer = ?",
                itemCollect.getIdOnServer()).find(Item.class).isEmpty()){
            itemCollect.save();

        }else {
            Snackbar.make(v, "已经在收藏列表中了！", Snackbar.LENGTH_SHORT)
                    .setAction("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    save(mData.get(itemNow));
                }else {
                    ToastUtil.show(getContext(),"没有相关权限！", 1000);
                }
                break;
            default:
                break;
        }
    }

    protected void save(ResultItem i){
        ImageUtil.saveFromUrl(i.url, new ImageUtil.onFinishListener() {
            @Override
            public void onSuccess(final String path) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getContext(), "成功保存至" + path, 2000);
                    }
                });
            }

            @Override
            public void onFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getContext(), "网络出错", 2000);
                    }
                });
            }
        });
    }
}
