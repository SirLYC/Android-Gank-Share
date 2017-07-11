package com.lyc.gank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.tool_bar_about)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
