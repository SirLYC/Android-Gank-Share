package com.lyc.gank;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lyc.gank.util.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        collapsingToolbarLayout.setTitle("Version "+BuildConfig.VERSION_NAME);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setBackGround(){
        String url = getIntent().getStringExtra(MainActivity.KEY_BG_URL);
        Log.e(toString(), url + "");
        Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .error(R.drawable.bg_about_default)
                .into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.fab_about_contact_by_qq)
    public void contactWithMeByQQ(){
        String url11 = "mqqwpa://im/chat?chat_type=wpa&uin=972694341&version=1";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
    }
}
