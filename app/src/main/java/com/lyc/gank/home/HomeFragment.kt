package com.lyc.gank.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import com.gigavalue.mobile.widget.LinearItemDivider
import com.lyc.data.resp.GankItem
import com.lyc.gank.GankWithImgViewBinder
import com.lyc.gank.GankWithoutImgViewBinder
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.R
import com.lyc.gank.article.ArticleActivity
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.photo.PhotoActivity
import com.lyc.gank.post.PostActivity
import com.lyc.gank.utils.*
import com.lyc.gank.widget.LoadMoreDetector
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class HomeFragment: BaseFragment(), LoadMoreDetector.LoadMoreCallback,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener
        , OnGankItemClickListener {

    private lateinit var reactiveAdapter: ReactiveAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refresher_home.setOnRefreshListener(this)
        fab_home.setOnClickListener(this)

        homeViewModel = provideViewModel()
        reactiveAdapter = ReactiveAdapter(homeViewModel.homeList).apply {
            observe(this@HomeFragment)
            register(GankItem::class.java)
                    .to(GankWithImgViewBinder(this@HomeFragment),
                            GankWithoutImgViewBinder(this@HomeFragment))
                    .withClassLinker { _, t ->
                        return@withClassLinker if(t.imgUrl != null)
                            GankWithImgViewBinder::class.java
                        else
                            GankWithoutImgViewBinder::class.java

                    }
            register(ErrorItem::class.java, ErrorItemViewBinder(homeViewModel::loadMore))
            register(LoadMoreItem::class.java, LoadMoreItemViewBinder)
            register(NoMoreItem::class.java, NoMoreItemViewBinder)
        }

        rv_home.addItemDecoration(LinearItemDivider(activity()!!))
        rv_home.adapter = reactiveAdapter
        rv_home.layoutManager = LinearLayoutManager(activity())
        LoadMoreDetector.detect(rv_home, this)
        rv_home.addOnScrollListener(FabActionScrollListener())

        homeViewModel.refreshEvent.observe(this, Observer { event ->
            when (event) {
                is RefreshState.Error -> toast(event.msg)
            }
        })

        homeViewModel.refreshState.observe(this, Observer { state ->
            when (state) {
                is RefreshState.Empty -> {
                    homeViewModel.refresh()
                    refresher_home.isRefreshing = false
                }
                is RefreshState.Refreshing -> refresher_home.isRefreshing = true
                else -> refresher_home.isRefreshing = false
            }
        })

        homeViewModel.loadMoreEvent.observe(this, Observer { event ->
            if (event is LoadState.Error) {
                toast(event.msg)
            }
        })
    }

    override fun onRefresh() {
        homeViewModel.refresh()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_home -> startActivity(Intent(activity(), PostActivity::class.java))
            else -> homeViewModel.refresh()
        }
    }


    // handled by homeViewModel
    override fun canLoadMore() = true

    override fun loadMore() {
        homeViewModel.loadMore()
    }

    override fun onGirlItemClick(iv: ImageView, item: GankItem) {
        PhotoActivity.start(activity()!!, item)
    }

    override fun onVideoItemClick(item: GankItem) {
        openWebPage(item.url)
    }

    override fun onArticleItemClick(item: GankItem) {
        ArticleActivity.start(activity()!!, item)
    }

    private inner class FabActionScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            val slop = ViewConfiguration.get(activity()).scaledTouchSlop
            if (dy < -slop) {
                fab_home.show()
            } else if (dy > slop) {
                fab_home.hide()
            }
        }
    }
}
