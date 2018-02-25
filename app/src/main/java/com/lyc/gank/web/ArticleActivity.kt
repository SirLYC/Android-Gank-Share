package com.lyc.gank.web

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.utils.logi
import com.lyc.gank.widget.ScrollWebView
import kotlinx.android.synthetic.main.activity_article.*


class ArticleActivity : AppCompatActivity(), ScrollWebView.OnScrollListener {
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
    private var finishLoadPage = false
    private var url: String? = null
    private var appBarHeight: Float = 0f

    override fun onScroll(dx: Int, dy: Int) {
        logi(TAG, "scrolled dx:$dx dy:$dy")

        if (!finishLoadPage) return

        val slop = ViewConfiguration.get(this).scaledTouchSlop

        if (dy > slop && !showActionBar) {
            showOrHideActionBar()
        } else if (dy < -slop && showActionBar) {
            showOrHideActionBar()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        setSupportActionBar(tb_article)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        abl_article.post {
            appBarHeight = abl_article.height.toFloat()
        }

        url = intent.getStringExtra(KEY_URL)
        if (url == null) {
            //todo : show invalid url
            return
        }

        swv_article.setOnScrollListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swv_article.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        swv_article.settings.javaScriptEnabled = true
        swv_article.settings.builtInZoomControls = true
        swv_article.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        swv_article.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                finishLoadPage = true
                swv_article.loadUrl("javascript:document.body.style.paddingTop=" +
                        "\"${appBarHeight / resources.displayMetrics.density}px\"; " +
                        "void 0")
            }
        }

        swv_article.loadUrl(url)
        abl_article.translationY = 0f
        showActionBar = true
    }

    private fun showOrHideActionBar() {
        ObjectAnimator.ofFloat(abl_article, "translationY",
                abl_article.translationY, if (showActionBar) -appBarHeight else 0f)
                .apply {
                    interpolator = DecelerateInterpolator(2f)
                }
                .start()
        showActionBar = !showActionBar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (swv_article.canGoBack()) {
                    swv_article.goBack()
                } else {
                    onBackPressed()
                }
                true
            }
            else -> false
        }
    }

    override fun onResume() {
        swv_article.onResume()
        swv_article.resumeTimers()
        super.onResume()
    }

    override fun onPause() {
        swv_article.onPause()
        swv_article.pauseTimers()
        super.onPause()
    }
}
