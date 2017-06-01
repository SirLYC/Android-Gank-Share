package com.lyc.gank;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lyc.gank.Adapter.HomePagerAdapter;
import com.lyc.gank.Fragment.ArticleFragment;
import com.lyc.gank.Fragment.BaseFragment;
import com.lyc.gank.Fragment.GirlsFragment;
import com.lyc.gank.Fragment.RecommendFragment;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class HomeActivity extends AppCompatActivity {

    private List<BaseFragment> mFragmentList = new ArrayList<>();

//    private DrawerLayout mDrawer;

    private FloatingActionButton backTopFAB;

    private BaseFragment fragmentNow;

    private SwipeRefreshLayout refreshLayout;


    /**
     * Handler的MSG常量
     */
    public static final int MSG_STOP_REFRESH = 0;

    public static final int MSG_EXIT = 1;

    public static final int MSG_REFRESH_FAILED = 2;

    public static final int MSG_START_LOAD = 3;

    private boolean isExit = false;

    /**
     * 标题
     */
    private final String [] titles = {
            "瞎推荐", "Android", "iOS", "前端", "拓展资源", "休息视频", "福利"
    };

    /**
     * 数据接口地址前缀
     */
    private final String [] addressPres = {
            "http://gank.io/api/data/all/",  //推荐
            "http://gank.io/api/data/Android/", //Android
            "http://gank.io/api/data/iOS/", //iOS
            "http://gank.io/api/data/前端/", //前端
            "http://gank.io/api/data/拓展资源/", //拓展资源
            "http://gank.io/api/data/%E4%BC%91%E6%81%AF%E8%A7%86%E9%A2%91/", //休息视频
            "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/" //福利
    };

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_STOP_REFRESH:
                    if(refreshLayout != null){
                        refreshLayout.setRefreshing(false);
                    }
                    break;
                case MSG_EXIT:
                    isExit = false;
                    break;
                case MSG_REFRESH_FAILED:
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(HomeActivity.this, "加载失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_START_LOAD:
                    refreshLayout.setRefreshing(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ShareSDK.initSDK(this);
        QbSdk.initX5Environment(this, null);
        Toolbar toolbar = (Toolbar)findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        init();
        ViewPager pager = (ViewPager)findViewById(R.id.home_pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                fragmentNow.setFABVisibility(backTopFAB.getVisibility());
            }

            @Override
            public void onPageSelected(int position) {
                int visibilityOld = fragmentNow.getFABVisibility();
                fragmentNow = mFragmentList.get(position);
                int visibilityNew = fragmentNow.getFABVisibility();
                if(visibilityNew != visibilityOld){
                    if(visibilityOld == View.INVISIBLE && visibilityNew == View.VISIBLE){
                        backTopFAB.show();
                    }else if(visibilityOld == View.VISIBLE && visibilityNew == View.INVISIBLE){
                        backTopFAB.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        mDrawer = (DrawerLayout)findViewById(R.id.drawer_home);
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), mFragmentList, titles);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(6);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.home_tab);
        tabLayout.setupWithViewPager(pager);

//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        }

        backTopFAB = (FloatingActionButton)findViewById(R.id.back_top_fab);
        backTopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentNow.backToTop();
                backTopFAB.setVisibility(View.INVISIBLE);
            }
        });

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.fresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentNow.getItemsFromServer(BaseFragment.FLAG_REFRESH);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        //防止在activity之间跳转fragment变量被回收
        bindAddressPre();
    }

    /**
     * 初始化fragment list
     */
    private void init(){
        RecommendFragment recommendFragment = new RecommendFragment();
        mFragmentList.add(recommendFragment);
        fragmentNow = recommendFragment;
        ArticleFragment fragment = new ArticleFragment();
        mFragmentList.add(fragment);
        fragment = new ArticleFragment();
        mFragmentList.add(fragment);
        fragment = new ArticleFragment();
        mFragmentList.add(fragment);
        fragment = new ArticleFragment();
        mFragmentList.add(fragment);
        fragment = new ArticleFragment();
        mFragmentList.add(fragment);
        GirlsFragment girlsFragment = new GirlsFragment();
        mFragmentList.add(girlsFragment);
        bindAddressPre();
    }

    private void bindAddressPre(){
        if(mFragmentList == null || mFragmentList.size() == 0){
            return;
        }
        for(int i = 0; i < mFragmentList.size(); i++){
            mFragmentList.get(i).setAddressPre(addressPres[i]);
        }
    }


    @Override
    public void onBackPressed() {
        if(isExit) {
            super.onBackPressed();
        }else {
            Toast.makeText(this, "再按一次返回键退出！", Toast.LENGTH_SHORT)
                    .show();
            isExit = true;
            handler.sendEmptyMessageDelayed(MSG_EXIT, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case android.R.id.home:
//                mDrawer.openDrawer(GravityCompat.START);
//                break;
            case R.id.home_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_collect:
                Intent intent = new Intent(this, CollectActivity.class);
                startActivity(intent);
            default:
                break;
        }
        return true;
    }
}
