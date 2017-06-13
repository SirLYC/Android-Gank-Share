package com.lyc.gank;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lyc.gank.Adapter.MainPagerAdapter;
import com.lyc.gank.Fragment.CategoryFragment;
import com.lyc.gank.Fragment.CollectFragment;
import com.lyc.gank.Fragment.GirlFragment;
import com.lyc.gank.Fragment.RecommendFragment;
import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_main)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_main)
    NavigationView navigationView;
    @BindView(R.id.pager_main)
    ViewPager fragmentPager;

    MainPagerAdapter adapter;

    List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);
        QbSdk.initX5Environment(this, null);
        init();

        adapter = new MainPagerAdapter(getSupportFragmentManager(), mFragmentList);
        fragmentPager.setAdapter(adapter);
        fragmentPager.setOffscreenPageLimit(3);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()){
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

    void init(){
        mFragmentList.add(new RecommendFragment());
        mFragmentList.add(new CategoryFragment());
        mFragmentList.add(new GirlFragment());
        mFragmentList.add(new CollectFragment());
    }

    @Override
    public void onBackPressed() {

        if (fragmentPager != null && fragmentPager.getCurrentItem() == 3
                && ((CollectFragment)adapter.getItem(3)).onSelect) {
            ((CollectFragment)adapter.getItem(3)).endSelect();
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

    public void openOrCloseDrawer(Boolean open){
        if(mDrawer != null){
            if(open) {
                mDrawer.openDrawer(GravityCompat.START);
            }else {
                mDrawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    public void initToolbar(Toolbar toolbar, String title){
        if(toolbar != null){
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

    public void initToolbar(Toolbar toolbar, String title, int menuRes){
        if(toolbar != null){
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

    public void setBackGround(String url, String date){
        View header = navigationView.getHeaderView(0);
        TextView dateText = (TextView)header.findViewById(R.id.text_date);
        ImageView bgImg = (ImageView)header.findViewById(R.id.header_bg);

        if(bgImg !=null)
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(bgImg);

        if(dateText != null)
            dateText.setText(date);
    }
}
