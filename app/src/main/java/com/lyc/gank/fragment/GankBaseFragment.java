package com.lyc.gank.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.database.Item;
import com.lyc.gank.api.GankIoApi;
import com.lyc.gank.api.RetrofitFactory;
import com.lyc.gank.util.ImageUtil;
import com.lyc.gank.util.ShareUtil;
import com.lyc.gank.util.TipUtil;
import com.lyc.gank.view.EmptyView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 972694341@qq.com on 2017/7/11.
 */

public abstract class GankBaseFragment extends BaseFragment {
    /**
     * 数据源
     */
    protected List<ResultItem> mData = new ArrayList<>();

    protected RecyclerView mRecyclerView;

    protected int itemNow;

    protected GankIoApi gankIoApi = RetrofitFactory.getGankIoApi();

    protected View view;

    protected EmptyView noInternetEmptyView;

    protected boolean needRefresh = false;

    public abstract void refresh();

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    protected void showNoInternetEmptyView(boolean show){
        if(noInternetEmptyView == null){
            EmptyView.Builder builder = new EmptyView.Builder();
            noInternetEmptyView = builder.with(mActivity)
                    .parent((ViewGroup) view)
                    .emptyHint(R.string.empty_hint_internet)
                    .buttonText(R.string.refresh)
                    .listener(new EmptyView.onClickListener() {
                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    })
                    .build();
        }
        if(mData == null || mData.size() == 0){
            noInternetEmptyView.show(show);
        }else {
            noInternetEmptyView.show(false);
        }
    }


    protected void collect(final ResultItem item,final View v){
        Observable.create(new ObservableOnSubscribe<Item>() {
            @Override
            public void subscribe(ObservableEmitter<Item> e) throws Exception {
                e.onNext(new Item(item));
            }
        }).map(new Function<Item, Boolean>() {
            @Override
            public Boolean apply(Item item) throws Exception {
                return DataSupport
                        .where("idOnServer = ?", item.getIdOnServer())
                        .find(Item.class)
                        .isEmpty() && item.save();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                int resId;
                if(aBoolean){
                    resId = R.string.collect_success;
                }else {
                   resId = R.string.already_in_collect;
                }
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)mActivity).gotoFragment(3);
                    }
                };
                Snackbar.make(v, resId, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.go_to_collect, listener)
                        .show();
            }
        });
    }

    protected void save(String url){
        ImageUtil.saveFromUrl(url, new ImageUtil.onFinishListener() {
            @Override
            public void onSuccess(final String path) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(mActivity, getString(R.string.save_img_success)+ path);
                    }
                });
            }

            @Override
            public void onFailed() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(mActivity, R.string.internet_exception);
                    }
                });
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            ResultItem i = mData.get(itemNow);
            switch (item.getItemId()){
                case 0:
                    collect(i, getView());
                    break;
                case 1:
                    if(i.type.equals("福利")){
                        ShareUtil.shareImage(mActivity, i);
                    }else {
                        ShareUtil.shareItem(mActivity, i);
                    }
                    break;
                case 2:
                    if(ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else {
                        save(i.url);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 0, Menu.NONE, "收藏");
        menu.add(Menu.NONE, 1, Menu.NONE, "分享");
        if(mData.get(itemNow).type.equals("福利"))
            menu.add(Menu.NONE, 2, Menu.NONE, "保存妹子");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    save(mData.get(itemNow).url);
                }else {
                    TipUtil.showShort(mActivity, R.string.no_permission);
                }
                break;
            default:
                break;
        }
    }
}
