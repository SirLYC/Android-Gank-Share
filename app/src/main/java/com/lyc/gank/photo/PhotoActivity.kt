package com.lyc.gank.photo

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lyc.data.resp.GankItem
import com.lyc.gank.R
import com.lyc.gank.utils.gankOption
import com.lyc.gank.utils.loge
import com.lyc.gank.utils.logi
import com.lyc.gank.widget.LoadRetryView
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoActivity : AppCompatActivity(), LoadRetryView.OnInflateListener, Animator.AnimatorListener {
    companion object {
        private const val TAG = "Photo"
        private const val KEY_URL = "KEY_URL"

        fun start(context: Context, gankItem: GankItem) {
            val intent = Intent(context, PhotoActivity::class.java)
                    .apply {
                        putExtra(KEY_URL, gankItem.url)
                    }
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(R.anim.photo_zoom_in, 0)
        }
    }

    private var showToolBar = true

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_photo)
        setSupportActionBar(tb_photo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        url = intent.getStringExtra(KEY_URL)
        if (url == null) {
            //todo : show invalid url
            return
        }

        lrv_photo.setOnInflateListener(this)
        lrv_photo.showLoadView()

        pv_photo.attacher.setOnClickListener { showOrHideToolBar() }

        Glide.with(this)
                .load(url)
                .gankOption()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        loge(TAG, "loadPhoto $url", e)
                        pv_photo.visibility = INVISIBLE
                        lrv_photo.showRetryView()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        logi(TAG, "resource ready $url")
                        lrv_photo.hideAll()
                        pv_photo.visibility = VISIBLE
                        return false
                    }
                })
                .into(pv_photo)
    }

    override fun onLoadViewInflate(stub: ViewStub, inflated: View) {
        val params = inflated.layoutParams
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        inflated.layoutParams = params
    }

    override fun onRetryViewInflate(stub: ViewStub, inflated: View) {
        //todo: set on retry listener
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun showOrHideToolBar() {
        val transAnim = ObjectAnimator.ofFloat(tb_photo, "translationY", tb_photo.translationY,
                if (showToolBar) -tb_photo.height.toFloat() else 0f)
        val alphaAnim = ObjectAnimator.ofFloat(tb_photo, "alpha", tb_photo.alpha,
                if (showToolBar) 0f else 1.0f)
        AnimatorSet().apply {
            play(transAnim).with(alphaAnim)
            addListener(this@PhotoActivity)
        }.start()
    }

    override fun onAnimationRepeat(animation: Animator) {}

    override fun onAnimationEnd(animation: Animator) {
        showToolBar = !showToolBar
        animation.removeAllListeners()
    }

    override fun onAnimationCancel(animation: Animator) {
        animation.removeAllListeners()
    }

    override fun onAnimationStart(animation: Animator) {}

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.photo_zoom_out)
    }
}
