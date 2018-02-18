package com.lyc.gank.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.data.resp.ResultItem
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.util.*
import com.lyc.gank.widget.LoadMoreDetector
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class HomeFragment: BaseFragment(), LoadMoreDetector.LoadMoreCallback,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    private lateinit var reactiveAdapter: ReactiveAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel = provideViewModel()
        reactiveAdapter = ReactiveAdapter(homeViewModel.homeList).apply {
            observe(this@HomeFragment)
            register(ResultItem::class.java)
                    .to(ItemWithImgViewBinder(), ItemWithoutImgViewBinder())
                    .withClassLinker { _, t ->
                        if(t.images.isNotEmpty()){
                            ItemWithImgViewBinder::class.java
                        }else{
                            ItemWithoutImgViewBinder::class.java
                        }
                    }
            register(ErrorItem::class.java, ErrorItemViewBinder(homeViewModel::loadMore))
        }
        refresher_home.setOnRefreshListener(this)


        homeViewModel.refreshState.let {
            when(it.value){
                is RefreshState.Empty -> {}
                is RefreshState.Refreshing -> refresher_home.isRefreshing = true
            }

            homeViewModel.refreshState.observe(this, Observer {
                when(it){
                    is RefreshState.Refreshing -> refresher_home.isRefreshing = true
                    is RefreshState.RefreshEmpty -> refresher_home.isRefreshing = false
                    is RefreshState.NotEmpty -> refresher_home.isRefreshing = false
                    is RefreshState.Error -> {
                        refresher_home.isRefreshing = false
                        refresher_home.snackBar(it.msg,getString(R.string.action_retry), this)
                    }
                }
            })
        }

        homeViewModel.loadState.observe(this, Observer {
            if(it is LoadState.Error){
                toast(it.msg)
            }
        })

        rv_home.let {
            it.adapter = reactiveAdapter
            it.layoutManager = LinearLayoutManager(activity())
            LoadMoreDetector.detect(rv_home, this)
        }
    }

    override fun onRefresh() {
        homeViewModel.refresh()
    }

    override fun onClick(v: View) {
        homeViewModel.refresh()
    }


    //handled by homeViewModel
    override fun canLoadMore() = true

    override fun loadMore() {
        homeViewModel.loadMore()
    }
}