package com.lyc.gank;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.adapter.MainPagerAdapter;
import com.lyc.gank.api.BingPicApi;
import com.lyc.gank.api.RetrofitFactory;
import com.lyc.gank.fragment.CategoryFragment;
import com.lyc.gank.fragment.CollectFragment;
import com.lyc.gank.fragment.GankRecommendFragment;
import com.lyc.gank.fragment.GirlFragment;
import com.lyc.gank.receiver.InternetReceiver;
import com.lyc.gank.receiver.TimeReceiver;
import com.lyc.gank.util.TimeUtil;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_main)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_main)
    NavigationView navigationView;
    @BindView(R.id.pager_main)
    ViewPager fragmentPager;

    public static final String KEY_BG_URL = "bg url";

    private BingPicApi mBingPicApi = RetrofitFactory.getBingPicApi();

    private MainPagerAdapter adapter;

    private List<Fragment> mFragmentList = new ArrayList<>();

    private boolean needRefresh = false;

    private Date today = new Date();

    private TimeReceiver timeReceiver;

    private InternetReceiver internetReceiver;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        QbSdk.initX5Environment(this, null);
        init();
        setViewPager();
        setNavigationView();
        setBackGround();
        setReceiver();
    }

    private void setReceiver() {
        IntentFilter timeFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver();
        timeReceiver.setActivity(this);
        timeReceiver.setRecommendFragment((GankRecommendFragment) mFragmentList.get(0));
        internetReceiver = new InternetReceiver();
        registerReceiver(timeReceiver, timeFilter);
        internetReceiver = new InternetReceiver();
        internetReceiver.setActivity(this);
        IntentFilter InternetFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetReceiver, InternetFilter);
    }

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public Date getToday() {
        return today;
    }

    private void setNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()) {
                    case R.id.recommend:
                        item.setChecked(true);
                        openOrCloseDrawer(false);
                        fragmentPager.setCurrentItem(0);
                        break;
                    case R.id.category:
                        item.setChecked(true);
                        openOrCloseDrawer(false);
                        fragmentPager.setCurrentItem(1);
                        break;
                    case R.id.girl:
                        item.setChecked(true);
                        openOrCloseDrawer(false);
                        fragmentPager.setCurrentItem(2);
                        break;
                    case R.id.collect:
                        item.setChecked(true);
                        openOrCloseDrawer(false);
                        fragmentPager.setCurrentItem(3);
                        break;
                    case R.id.about:
                        intent.setClass(MainActivity.this, AboutActivity.class);
                        intent.putExtra(KEY_BG_URL,url);
                        startActivity(intent);
                        break;
                    case R.id.exit:
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    private void setViewPager() {
        adapter = new MainPagerAdapter(getSupportFragmentManager(), mFragmentList);
        fragmentPager.setAdapter(adapter);
        fragmentPager.setOffscreenPageLimit(3);
    }

    void init() {
        mFragmentList.add(new GankRecommendFragment());
        mFragmentList.add(new CategoryFragment());
        mFragmentList.add(new GirlFragment());
        mFragmentList.add(new CollectFragment());
    }

    @Override
    public void onBackPressed() {

        if (fragmentPager != null && fragmentPager.getCurrentItem() == 3
                && ((CollectFragment) adapter.getItem(3)).onSelect) {
            ((CollectFragment) adapter.getItem(3)).endSelect();
        } else {
            if (mDrawer != null) {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    openOrCloseDrawer(false);
                } else {
                    openOrCloseDrawer(true);
                }
            }
        }
    }

    public void openOrCloseDrawer(Boolean open) {
        if (mDrawer != null) {
            if (open) {
                mDrawer.openDrawer(GravityCompat.START);
            } else {
                mDrawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    public void initToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer(true);
                }
            });
        }
    }

    public void gotoFragment(int index) {
        switch (index) {
            case 0:
                navigationView.setCheckedItem(R.id.recommend);
                break;
            case 1:
                navigationView.setCheckedItem(R.id.category);
                break;
            case 2:
                navigationView.setCheckedItem(R.id.girl);
                break;
            case 3:
                navigationView.setCheckedItem(R.id.collect);
                break;
            default:
                break;
        }
        fragmentPager.setCurrentItem(index);
    }

    public void initToolbar(Toolbar toolbar, String title, int menuRes) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer(true);
                }
            });
            toolbar.inflateMenu(menuRes);
        }
    }

    public void setBackGround(){
        url = getUrl();
        Date now = new Date();
        Date last = getLastDate();
        needRefresh = false;
        if(last == null || url == null) {
            needRefresh = true;
        }else{
            needRefresh = TimeUtil.needRefresh(last, now);
        }
        if(needRefresh){
            loadBackGround();
        }else {
            loadBackGround(url);
        }
    }

    /**
     * 有缓存时的加载
     * @param url 上一次的图片背景url
     */
    public void loadBackGround(String url){
        View header = navigationView.getHeaderView(0);
        final TextView dateText = ButterKnife.findById(header, R.id.text_date);
        final ImageView bgImg = ButterKnife.findById(header, R.id.header_bg);
        dateText.setText(TimeUtil.getDateString(new Date()));
        Glide.with(MainActivity.this)
                .load(url)
                .skipMemoryCache(true)
                .centerCrop()
                .into(bgImg);
        needRefresh = false;
    }

    /**
     * 直接从服务器加载
     */
    public void loadBackGround() {
        View header = navigationView.getHeaderView(0);
        final TextView dateText = ButterKnife.findById(header, R.id.text_date);
        final ImageView bgImg = ButterKnife.findById(header, R.id.header_bg);
        mBingPicApi.getBingPic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        dateText.setText(TimeUtil.getDateString(new Date()));
                    }

                    @Override
                    public void onNext(String value) {
                        Glide.with(MainActivity.this)
                                .load(value)
                                .centerCrop()
                                .into(bgImg);
                        url = value;
                    }

                    @Override
                    public void onError(Throwable e) {
                        needRefresh = true;
                    }

                    @Override
                    public void onComplete() {
                        needRefresh = false;
                        today = new Date();
                        saveData(url, today);
                    }
                });
    }

    private String getUrl(){
        SharedPreferences preferences =
                getSharedPreferences(getString(R.string.main_activity), MODE_PRIVATE);
        return preferences.getString(getString(R.string.url_background), null);
    }

    private Date getLastDate(){
        SharedPreferences preferences =
                getSharedPreferences(getString(R.string.main_activity), MODE_PRIVATE);
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

    private void saveData(String url, Date date){
        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.main_activity), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.url_background), url);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        editor.putInt(getString(R.string.year), calendar.get(Calendar.YEAR));
        editor.putInt(getString(R.string.month), calendar.get(Calendar.MONTH));
        editor.putInt(getString(R.string.day), calendar.get(Calendar.DAY_OF_MONTH));
        editor.putInt(getString(R.string.hour), calendar.get(Calendar.HOUR_OF_DAY));
        editor.putInt(getString(R.string.minute), calendar.get(Calendar.MINUTE));
        editor.putInt(getString(R.string.second), calendar.get(Calendar.SECOND));
        editor.apply();
    }


    /**
     * 断网之后的刷新
     */
    public void refresh(){
        if(needRefresh) {
            loadBackGround();
        }
        ((GankRecommendFragment)mFragmentList.get(0)).refresh();
        ((CategoryFragment)mFragmentList.get(1)).refresh();
        ((GirlFragment)mFragmentList.get(2)).refresh();
    }

    @Override
    protected void onDestroy() {
        if(timeReceiver != null){
            unregisterReceiver(timeReceiver);
        }
        if(internetReceiver != null){
            unregisterReceiver(internetReceiver);
        }
        super.onDestroy();
    }
}
