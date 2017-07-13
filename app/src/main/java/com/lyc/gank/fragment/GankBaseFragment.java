package com.lyc.gank.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.WebActivity;
import com.lyc.gank.adapter.BaseRecyclerAdapter;
import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.bean.Results;
import com.lyc.gank.database.Item;
import com.lyc.gank.api.GankIoApi;
import com.lyc.gank.api.RetrofitFactory;
import com.lyc.gank.util.ImageSave;
import com.lyc.gank.util.Shares;
import com.lyc.gank.util.TipUtil;
import com.lyc.gank.view.EmptyView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

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

    protected String type;

    protected String lastIdOnServer;

    protected BaseRecyclerAdapter adapter;

    protected GankIoApi gankIoApi = RetrofitFactory.getGankIoApi();

    protected View view;

    protected EmptyView noInternetEmptyView;

    protected boolean isFirstVisibleToUser = true;

    protected Date today = new Date();

    /**
     * 防止加载过程在setContentView之前进行
     */
    protected boolean isPreparedCreate;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPreparedCreate = true;
    }

    /**
     * 从本地读取数据
     * @return 是否有本地数据
     */
    protected boolean loadDataFromLocal(){
        SharedPreferences preferences = getContext()
                .getSharedPreferences(type, Context.MODE_PRIVATE);
        String json = preferences.getString(getString(R.string.data), null);
        if(json != null){
            Results results = new Gson().fromJson(json, Results.class);
            mData.addAll(results.resultItems);
            adapter.notifyDataSetChanged();
            lastIdOnServer = mData.get(0)._id;
            return true;
        }
        return false;
    }


    protected Date getLastDate(){
        SharedPreferences preferences =
                getContext().getSharedPreferences(getString(R.string.main_activity), MODE_PRIVATE);
        int year = preferences.getInt(getString(R.string.year), -1);
        if(year == -1)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, preferences.getInt(getString(R.string.month), -1));
        calendar.set(Calendar.DAY_OF_MONTH, preferences.getInt(getString(R.string.day), -1));
        calendar.set(Calendar.HOUR_OF_DAY, preferences.getInt(getString(R.string.hour), -1));
        calendar.set(Calendar.MINUTE, preferences.getInt(getString(R.string.minute), -1));
        calendar.set(Calendar.SECOND, preferences.getInt(getString(R.string.second), -1));
        return calendar.getTime();
    }

    protected void saveData(){
        Results results = new Results();
        if(mData.size() <= 40) {
            results.resultItems = mData;
        }else {
            results.resultItems = mData.subList(0, 40);
        }
        String json = new Gson().toJson(results);
        SharedPreferences.Editor editor =
                getContext().getSharedPreferences(type, MODE_PRIVATE).edit();
        editor.putString(getString(R.string.data), json);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        editor.putInt(getString(R.string.year), calendar.get(Calendar.YEAR));
        editor.putInt(getString(R.string.month), calendar.get(Calendar.MONTH));
        editor.putInt(getString(R.string.day), calendar.get(Calendar.DAY_OF_MONTH));
        editor.putInt(getString(R.string.hour), calendar.get(Calendar.HOUR_OF_DAY));
        editor.putInt(getString(R.string.minute), calendar.get(Calendar.MINUTE));
        editor.putInt(getString(R.string.second), calendar.get(Calendar.SECOND));
        editor.apply();
    }

    protected void collect(final ResultItem item, final View v){
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

    protected void saveImg(String url, String title){
        ImageSave.saveImageAndGetPathObservable(mActivity, url, title)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Uri>() {
                    @Override
                    public void accept(Uri uri) throws Exception {
                        TipUtil.showShort(mActivity, getString(R.string.save_img_success) + ImageSave.path);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        TipUtil.showShort(mActivity, R.string.internet_is_not_ok);
                    }
                });
    }

    protected void setRecyclerView(){
        //TODO:子类需要先初始化adapter然后调用这个方法
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int pos, final View v) {
                final ResultItem item = mData.get(pos);
                Intent intent = new Intent();
                switch (item.type){
                    case "福利":
                        Picasso.with(mActivity).load(item.url).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                startPhotoActivity(v, item.url, item.publishTime.substring(0, 10));
                            }

                            @Override
                            public void onError() {

                            }
                        });
                        break;
                    case "休息视频":
                        intent.setAction(Intent.ACTION_VIEW);
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
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            ResultItem i = mData.get(itemNow);
            switch (item.getItemId()){
                case 0:
                    collect(i, getView());
                    break;
                case 1:
                    if(i.type.equals("福利")){
                        ImageSave.saveImageAndGetPathObservable(getContext(), i.url, i.title)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Uri>() {
                                    @Override
                                    public void accept(Uri uri) throws Exception {
                                        Shares.shareImage(getContext(), uri);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        TipUtil.showShort(getContext(), R.string.internet_exception);
                                    }
                                });
                    }else {
                        String text = getString(R.string.share_found)
                                + "\n" + i.title + "\n" + i.url;
                        Shares.share(getContext(), text);
                    }
                    break;
                case 2:
                    saveImgWithPermissionCheck(i);
                    break;
            }
            return true;
        }
        return false;
    }

    private void saveImgWithPermissionCheck(ResultItem i) {
        if(ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            saveImg(i.url, i.title);
        }
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
                    ResultItem item = mData.get(itemNow);
                    saveImg(item.url, item.title);
                }else {
                    TipUtil.showShort(mActivity, R.string.no_permission);
                }
                break;
            default:
                break;
        }
    }
}
