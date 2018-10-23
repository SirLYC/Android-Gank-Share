package com.lyc.gank.article

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.utils.openWebPage
import com.lyc.gank.utils.textCopy
import com.lyc.gank.utils.toast
import kotlinx.android.synthetic.main.activity_article.*


class ArticleActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Article"
        private const val KEY_URL = "KEY_URL"

        fun start(context: Context, gankItem: GankItem) {
            val intent = Intent(context, ArticleActivity::class.java)
                    .apply {
                        putExtra(KEY_URL, gankItem.url)
                    }
            context.startActivity(intent)
        }
    }

    private var showActionBar = true
    private var url: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(tb_article)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        url = intent.getStringExtra(KEY_URL)
        if (url == null) {
            //todo : show invalid url
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_article.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        wv_article.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress in 1..99) {
                    pb_article.visibility = VISIBLE
                    pb_article.progress = newProgress
                } else {
                    pb_article.visibility = GONE
                }
            }
        }

        wv_article.settings.javaScriptEnabled = true
        wv_article.settings.builtInZoomControls = true
        wv_article.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        wv_article.loadUrl(url)
        tb_article.translationY = 0f
        showActionBar = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (wv_article.canGoBack()) {
                    wv_article.goBack()
                } else {
                    finish()
                }
                true
            }
            R.id.close -> {
                finish()
                true
            }
            R.id.refresh -> {
                wv_article.reload()
                true
            }
            R.id.share -> {
                textCopy(wv_article.url)
                toast("已将网址复制到剪贴板")
                true
            }
            R.id.open_in_browser -> {
                openWebPage(wv_article.url)
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        if (wv_article.canGoBack()) {
            wv_article.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        wv_article.onResume()
        wv_article.resumeTimers()
        super.onResume()
    }

    override fun onPause() {
        wv_article.onPause()
        wv_article.pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        (wv_article.parent as ViewGroup)
                .removeView(wv_article)
        wv_article.visibility = GONE
        wv_article.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        return true
    }


}
