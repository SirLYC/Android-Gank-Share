package com.lyc.gank.discover

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import com.lyc.data.resp.GankItem
import com.lyc.gank.GankWithImgViewBinder
import com.lyc.gank.GankWithoutImgViewBinder
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.R
import com.lyc.gank.article.ArticleActivity
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.category.GirlItemViewBinder
import com.lyc.gank.photo.PhotoActivity
import com.lyc.gank.search.SearchActivity
import com.lyc.gank.utils.*
import com.lyc.gank.widget.LoadRetryView
import kotlinx.android.synthetic.main.fragment_discover.*

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class DiscoverFragment : BaseFragment(), View.OnClickListener, LoadRetryView.OnRetryClickListener, OnGankItemClickListener, LoadRetryView.OnInflateListener {
    override fun onRetryViewInflate(stub: ViewStub, inflated: View) {

    }

    override fun onLoadViewInflate(stub: ViewStub, inflated: View) {
        val param = inflated.layoutParams
        param.width = WRAP_CONTENT
        param.height = WRAP_CONTENT
        inflated.layoutParams = param
    }

    private lateinit var discoverViewModel: DiscoverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        discoverViewModel = provideViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_search.setOnClickListener(this)
        tv_refresh.setOnClickListener(this)
        lrv_discover.onRetryClickListener = this
        lrv_discover.setOnInflateListener(this)

        rv_discover.layoutManager = LinearLayoutManager(activity)
        rv_discover.addItemDecoration(DividerItemDecoration(activity, LinearLayout.VERTICAL))
        rv_discover.adapter = ReactiveAdapter(discoverViewModel.discoverList).apply {
            register(GankItem::class.java)
                    .to(GankWithImgViewBinder(this@DiscoverFragment),
                            GankWithoutImgViewBinder(this@DiscoverFragment),
                            GirlItemViewBinder(this@DiscoverFragment))
                    .withClassLinker { _, t ->
                        return@withClassLinker when {
                            t.type == "福利" -> GirlItemViewBinder::class.java
                            t.imgUrl != null -> GankWithImgViewBinder::class.java
                            else -> GankWithoutImgViewBinder::class.java
                        }
                    }
            observe(this@DiscoverFragment)
        }

        discoverViewModel.refreshState.observe(this, Observer { state ->
            when (state) {
                is RefreshState.Refreshing -> lrv_discover.showLoadView()
                is RefreshState.Error -> lrv_discover.showRetryView()
                else -> lrv_discover.hideAll()
            }

            tv_refresh.isEnabled = state !is RefreshState.Refreshing

            if (state === RefreshState.Empty) {
                discoverViewModel.refresh()
            }
        })

        discoverViewModel.refreshEvent.observe(this, Observer { event ->
            if (event is RefreshState.Error) {
                toast(event.msg)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_search -> startActivity(Intent(activity(), SearchActivity::class.java))
            R.id.tv_refresh -> {
                loge("refresh","refresh")
                discoverViewModel.refresh()
            }
        }
    }

    override fun onRetryClicked(v: View) {
        discoverViewModel.refresh()
    }

    override fun onGirlItemClick(iv: ImageView, item: GankItem) {
        PhotoActivity.start(activity!!, item)
    }

    override fun onArticleItemClick(item: GankItem) {
        ArticleActivity.start(activity!!, item)
    }

    override fun onVideoItemClick(item: GankItem) {
        openWebPage(item.url)
    }

}
