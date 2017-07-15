package com.lyc.gank;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lyc.gank.adapter.MainPagerAdapter;
import com.lyc.gank.api.JinshanApi;
import com.lyc.gank.api.RetrofitFactory;
import com.lyc.gank.bean.EveryDayAWord;
import com.lyc.gank.fragment.CategoryFragment;
import com.lyc.gank.fragment.CollectFragment;
import com.lyc.gank.fragment.GankRecommendFragment;
import com.lyc.gank.fragment.GirlFragment;
import com.lyc.gank.receiver.InternetReceiver;
import com.lyc.gank.receiver.TimeReceiver;
import com.lyc.gank.util.TimeUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_main)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_main)
    NavigationView navigationView;

    @BindView(R.id.pager_main)
    ViewPager fragmentPager;

    View header;

    TextView dateText;

    TextView wordText;

    ImageView bgImg;

    private static final String TAG = "MainActivity";

    private static final String KEY_EVERY_WORD = "every";

    private static final String DEFAULT_WORD = "干货集中营——分享每日技术干货";

    private JinshanApi mJinshanApi = RetrofitFactory.getJinshanApi();

    private MainPagerAdapter adapter;

    private List<Fragment> mFragmentList = new ArrayList<>();

    private boolean needRefresh;

    private Date today;

    private TimeReceiver timeReceiver;

    private InternetReceiver internetReceiver;

    private EveryDayAWord word;

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
        today = getLastDate();
        header = navigationView.getHeaderView(0);
        wordText = ButterKnife.findById(header, R.id.text_everyday_word);
        dateText = ButterKnife.findById(header, R.id.text_date);
        bgImg = ButterKnife.findById(header, R.id.header_bg);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!needRefresh && word.fenxiang_img != null){
                    Picasso.with(MainActivity.this).load(word.fenxiang_img)
                            .fetch(new Callback() {
                                @Override
                                public void onSuccess() {
                                    startPhotoActivity();
                                }
                                @Override
                                public void onError() {}
                            });
                }
            }
        });
        mFragmentList.add(new GankRecommendFragment());
        mFragmentList.add(new CategoryFragment());
        mFragmentList.add(new GirlFragment());
        mFragmentList.add(new CollectFragment());
    }

    private void startPhotoActivity(){
        Intent intent = PhotoActivity.getIntent(MainActivity.this, word.fenxiang_img, word.caption + word.dateline);
        ActivityOptionsCompat optionsCompat =  ActivityOptionsCompat
                .makeSceneTransitionAnimation(MainActivity.this, bgImg, PhotoActivity.KEY_PHOTO);
        try {
            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            startActivity(intent);
        }
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

    /**
     * 有缓存时的加载本地数据
     */
    public void setBackGround(){
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                word = getEveryDayAWord();
                if(word != null){
                    e.onNext(true);
                }else {
                    e.onNext(false);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            wordText.setText(word.content + "\n" + word.note);
                            dateText.setText(word.dateline);
                            Glide.with(MainActivity.this)
                                    .load(word.picture2)
                                    .error(R.drawable.bg_about_default)
                                    .into(bgImg);
                            needRefresh = false;
                        }

                        if(!aBoolean || TimeUtil.imgNeedRefresh(today, new Date())){
                            loadBackGround();
                        }
                    }
                });
    }

    /**
     * 直接从服务器加载
     */
    public void loadBackGround() {
        mJinshanApi.getEveryWord()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<EveryDayAWord>() {
                    @Override
                    public void accept(EveryDayAWord everyDayAWord) throws Exception {
                        today = TimeUtil.fromDateLine(word.dateline);
                        saveData(everyDayAWord);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EveryDayAWord>() {
                    @Override
                    public void accept(EveryDayAWord everyDayAWord) throws Exception {
                        word = everyDayAWord;
                        wordText.setText(everyDayAWord.content + "\n" + everyDayAWord.note);
                        dateText.setText(everyDayAWord.dateline);
                        Glide.with(MainActivity.this)
                                .load(everyDayAWord.picture2)
                                .error(R.drawable.bg_about_default)
                                .into(bgImg);
                        needRefresh = false;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(toString(), throwable.toString());
                        wordText.setText(DEFAULT_WORD);
                        dateText.setText(TimeUtil.getDateLine(today));
                        Glide.with(MainActivity.this)
                                .load(R.drawable.bg_about_default)
                                .into(bgImg);
                        needRefresh = true;
                    }
                });
    }

    private EveryDayAWord getEveryDayAWord(){
        SharedPreferences preferences =
                getSharedPreferences(TAG, MODE_PRIVATE);
        String json = preferences.getString(KEY_EVERY_WORD, null);
        if(json != null){
            return new Gson().fromJson(json, EveryDayAWord.class);
        }
        return null;
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

    private void saveData(EveryDayAWord everyDayAWord){
        SharedPreferences.Editor editor =
                getSharedPreferences(TAG, MODE_PRIVATE).edit();
        editor.putString(KEY_EVERY_WORD, new Gson().toJson(everyDayAWord));
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
