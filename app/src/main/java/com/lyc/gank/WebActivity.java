package com.lyc.gank;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.Database.Item;
import com.lyc.gank.Util.ShareUtil;
import com.lyc.gank.Util.ToastUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 用于展示干货内容的activity
 * 基于腾讯x5内核的webview
 */
public class WebActivity extends AppCompatActivity {

    ResultItem resultItem;

    @BindView(R.id.tool_bar_web)
    Toolbar toolbar;

    @BindView(R.id.webview_web)
    WebView webView;

    @BindView(R.id.progress_web)
    ProgressBar webProgress;

    private boolean isFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_web);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        resultItem = (ResultItem) intent.getSerializableExtra("item");
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        webView.setDrawingCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int i) {
                if(i == 100){
                    webProgress.setProgress(i);
                    webProgress.setVisibility(View.GONE);
                }else {
                    webProgress.setVisibility(View.VISIBLE);
                    webProgress.setProgress(i);
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                toolbar.setTitle(s);
            }
        });

        if(resultItem != null){
            webView.loadUrl(resultItem.url);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.web_share:
                ShareUtil.shareItem(this, resultItem);
                break;
            case R.id.web_refresh:
                if(webView != null)
                    webView.reload();
                break;
            case R.id.web_collect:
                final Item itemCollect = new Item(resultItem);
                if(DataSupport.where("idOnServer = ?",
                        resultItem.id).find(Item.class).isEmpty()){
                    itemCollect.save();
                }else {
                    Snackbar.make(webView, "已经在收藏列表中了！", Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                }
                break;
            case R.id.web_copy:
                String url = webView.getUrl();
                ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(url);
                ToastUtil.show(this, "已复制到剪贴板", 1000);
                break;
            case R.id.web_launch:
                Intent intent = new Intent();
                intent.setAction("android.textIntent.action.VIEW");
                Uri uri = Uri.parse(resultItem.url);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.web_full_screen:
                setFullScreen();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 实现全屏阅读
     */
    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        isFullScreen = true;
    }

    private void quitFullScreen(){
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().show();
        isFullScreen = false;
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            quitFullScreen();
        }else {
            if(webView.canGoBack()){
                webView.goBack();
            }else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.setVisibility(View.GONE);
        webView.destroy();
    }
}
