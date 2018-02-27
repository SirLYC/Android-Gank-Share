package com.lyc.gank.category

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.gigavalue.mobile.widget.LinearItemDivider
import com.lyc.data.resp.GankItem
import com.lyc.gank.App
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.GankWithImgViewBinder
import com.lyc.gank.GankWithoutImgViewBinder
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.photo.PhotoActivity
import com.lyc.gank.article.ArticleActivity
import com.lyc.gank.utils.*
import com.lyc.gank.widget.LoadMoreDetector
import kotlinx.android.synthetic.main.fragment_single_content.*

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class SingleContentFragment : BaseFragment(), OnGankItemClickListener,
        LoadMoreDetector.LoadMoreCallback, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var type: String
    private lateinit var singleContentViewModel: SingleContentViewModel
    private lateinit var reactiveAdapter: ReactiveAdapter
    private var initViewModel = false

    companion object {
        private const val KEY_TYPE = "KEY_TYPE"

        fun instance(type: String) = SingleContentFragment()
                .apply {
                    val bundle = Bundle()
                    bundle.putString(KEY_TYPE, type)
                    arguments = bundle
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_single_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val s = arguments?.getString(KEY_TYPE)
        type = s ?: throw IllegalArgumentException("No Type Found")
        singleContentViewModel = viewModel()

        reactiveAdapter = ReactiveAdapter(singleContentViewModel.singleContentList).apply {
            observe(this@SingleContentFragment)
            register(GankItem::class.java)
                    .to(GankWithImgViewBinder(this@SingleContentFragment),
                            GankWithoutImgViewBinder(this@SingleContentFragment),
                            GirlItemViewBinder(this@SingleContentFragment))
                    .withClassLinker { _, t ->
                        return@withClassLinker when {
                            t.type == "福利" -> GirlItemViewBinder::class.java
                            t.imgUrl != null -> GankWithImgViewBinder::class.java
                            else -> GankWithoutImgViewBinder::class.java
                        }
                    }
            register(ErrorItem::class.java, ErrorItemViewBinder(singleContentViewModel::loadMore))
            register(LoadMoreItem::class.java, LoadMoreItemViewBinder)
            register(NoMoreItem::class.java, NoMoreItemViewBinder)
        }

        rv_single_content.let {

            it.adapter = reactiveAdapter
            if (type == "福利") {
                it.layoutManager = GridLayoutManager(activity(), 2)
            } else {
                it.addItemDecoration(LinearItemDivider(activity()!!))
                it.layoutManager = LinearLayoutManager(activity())
            }
            LoadMoreDetector.detect(rv_single_content, this)
        }
        refresher_single_content.setOnRefreshListener(this)


        singleContentViewModel.refreshState.let {
            when (it.value) {
                is RefreshState.Empty -> if (userVisibleHint) singleContentViewModel.refresh()
                is RefreshState.Refreshing -> refresher_single_content.isRefreshing = true
            }

            singleContentViewModel.refreshState.observe(this, Observer {
                when (it) {
                    is RefreshState.Refreshing -> refresher_single_content.isRefreshing = true
                    is RefreshState.RefreshEmpty -> refresher_single_content.isRefreshing = false
                    is RefreshState.NotEmpty -> refresher_single_content.isRefreshing = false
                    is RefreshState.Error -> {
                        refresher_single_content.isRefreshing = false
                        toast(it.msg)
                    }
                }
            })
        }

        singleContentViewModel.loadState.observe(this, Observer {
            if (it is LoadState.Error) {
                toast(it.msg)
            }
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && initViewModel && !singleContentViewModel.dataLoad()) {
            singleContentViewModel.refresh()
        }
    }


    override fun onRefresh() {
        singleContentViewModel.refresh()
    }

    private fun viewModel() = ViewModelProviders
            .of(this, (activity()!!.application as App).injection())
            .get(type, SingleContentViewModel::class.java)
            .apply { setType(type) }
            .also { initViewModel = true }

    override fun onGirlItemClick(iv: ImageView, item: GankItem) {
        PhotoActivity.start(activity()!!, item)
    }

    override fun onVideoItemClick(item: GankItem) {
        openWebPage(item.url)
    }

    override fun onArticleItemClick(item: GankItem) {
        ArticleActivity.start(activity()!!, item)
    }

    override fun loadMore() = singleContentViewModel.loadMore()

    override fun canLoadMore() = singleContentViewModel.loadState.value !is LoadState.Error
}