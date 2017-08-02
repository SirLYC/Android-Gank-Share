package com.lyc.gank;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.lyc.gank.bean.ResultItem;
import com.lyc.gank.util.Shares;
import com.lyc.gank.util.TipUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 用于展示干货内容的activity
 * 基于腾讯x5内核的webview
 */
public class WebActivity extends AppCompatActivity {

    private static final String KEY_URL = "url";

    private static final String KEY_TITLE = "title";

    private String url;

    private String title;

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
        getUrlAndTitle();
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
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
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
                if(s!=null && !s.equals(""))
                    toolbar.setTitle(s);
            }
        });

        if(url != null){
            webView.loadUrl(url);
        }
    }

    private void getUrlAndTitle() {
        Intent intent = getIntent();
        url = intent.getStringExtra(KEY_URL);
        title = intent.getStringExtra(KEY_TITLE);
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
                String text = getString(R.string.share_found)
                        + "\n" + title + "\n" + url;
                Shares.share(this, text);
                break;
            case R.id.web_refresh:
                if(webView != null)
                    webView.reload();
                break;
            case R.id.web_copy:
                String urlNow = webView.getUrl();
                ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText(null, urlNow));
                TipUtil.showShort(this, R.string.copy_success);
                break;
            case R.id.web_launch:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
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
        if(webView!=null) {
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    public static Intent getIntent(Context context, String url, String title){
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        return intent;
    }
}
