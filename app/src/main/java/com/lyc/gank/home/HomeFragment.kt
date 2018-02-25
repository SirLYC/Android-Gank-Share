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
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.GankWithImgViewBinder
import com.lyc.gank.GankWithoutImgViewBinder
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.post.PostActivity
import com.lyc.gank.web.ArticleActivity
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

        rv_home.let {
            it.addItemDecoration(LinearItemDivider(activity()!!))
            it.adapter = reactiveAdapter
            it.layoutManager = LinearLayoutManager(activity())
            LoadMoreDetector.detect(rv_home, this)
            rv_home.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    val slop = ViewConfiguration.get(activity()).scaledTouchSlop
                    if (dy < -slop) {
                        fab_home.show()
                    } else if (dy > slop) {
                        fab_home.hide()
                    }
                }
            })
        }
        refresher_home.setOnRefreshListener(this)
        fab_home.setOnClickListener(this)

        homeViewModel.refreshState.let {
            when(it.value){
                is RefreshState.Empty -> homeViewModel.refresh()
                is RefreshState.Refreshing -> refresher_home.isRefreshing = true
            }

            homeViewModel.refreshState.observe(this, Observer {
                when(it){
                    is RefreshState.Refreshing -> refresher_home.isRefreshing = true
                    is RefreshState.RefreshEmpty -> refresher_home.isRefreshing = false
                    is RefreshState.NotEmpty -> refresher_home.isRefreshing = false
                    is RefreshState.Error -> {
                        refresher_home.isRefreshing = false
                        toast(it.msg)
                    }
                }
            })
        }

        homeViewModel.loadState.observe(this, Observer {
            if(it is LoadState.Error){
                toast(it.msg)
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


    //others are handled by homeViewModel
    override fun canLoadMore() = homeViewModel.loadState.value !is LoadState.Error

    override fun loadMore() {
        homeViewModel.loadMore()
    }

    override fun onGirlItemClick(iv: ImageView, item: GankItem) {
        //todo: PhotoActivity
    }

    override fun onVideoItemClick(item: GankItem) {
        //todo: Video player or user system's web browser
    }

    override fun onArticleItemClick(item: GankItem) {
        ArticleActivity.start(activity()!!, item)
    }
}