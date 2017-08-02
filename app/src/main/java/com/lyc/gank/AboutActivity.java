package com.lyc.gank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lyc.gank.api.BingPicApi;
import com.lyc.gank.api.RetrofitFactory;
import com.lyc.gank.bean.EveryDayAWord;
import com.lyc.gank.util.Shares;
import com.lyc.gank.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.tool_bar_about)
    Toolbar toolbar;

    @BindView(R.id.img_about)
    ImageView imageView;

    @BindView(R.id.collapsing_toolbar_about)
    CollapsingToolbarLayout collapsingToolbarLayout;

    private BingPicApi mBingPicApi = RetrofitFactory.getBingPicApi();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String KEY_URL = "url";

    private String url;

    private String TAG = "AboutActivity";

    private Date today;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setToolbar();
        setBackGround();
    }

    private void setToolbar() {
        today = getLastDate();
        collapsingToolbarLayout.setTitle("Version "+BuildConfig.VERSION_NAME);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setBackGround(){
        url = getImgUrl();
        if(url == null || TimeUtil.imgNeedRefresh(getLastDate(), new Date())){
            loadBackGround();
        }else {
            Glide.with(AboutActivity.this)
                    .load(url)
                    .skipMemoryCache(true)
                    .error(R.drawable.bg_about_default)
                    .into(imageView);
        }
    }

    private void loadBackGround() {
        mBingPicApi.getBingPic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Glide.with(AboutActivity.this)
                                .load(s)
                                .skipMemoryCache(true)
                                .error(R.drawable.bg_about_default)
                                .into(imageView);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        today = new Date();
                        url = s;
                        saveData();
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if(compositeDisposable != null && disposable != null) {
                            compositeDisposable.add(disposable);
                        }
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {}
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Glide.with(AboutActivity.this)
                                .load(R.drawable.bg_about_default)
                                .into(imageView);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.text_share_github)
    void shareGithub(){
        Shares.share(this, R.string.share_project_text);
    }

    @OnClick(R.id.fab_about_contact_by_qq)
    void contactWithMeByQQ(){
        String url11 = "mqqwpa://im/chat?chat_type=wpa&uin=972694341&version=1";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
    }

    private String getImgUrl(){
        SharedPreferences preferences =
                getSharedPreferences(TAG, MODE_PRIVATE);
        return preferences.getString(KEY_URL, null);
    }

    private Date getLastDate(){
        SharedPreferences preferences =
                getSharedPreferences(TAG, MODE_PRIVATE);
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

    private void saveData(){
        SharedPreferences.Editor editor =
                getSharedPreferences(TAG, MODE_PRIVATE).edit();
        editor.putString(KEY_URL, url);
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

    @Override
    protected void onDestroy() {
        if(compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        super.onDestroy();
    }
}
