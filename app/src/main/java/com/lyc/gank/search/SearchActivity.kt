package com.lyc.gank.search

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.ImageView
import android.widget.TextView
import com.gigavalue.mobile.widget.LinearItemDivider
import com.lyc.data.resp.GankItem
import com.lyc.gank.GankWithImgViewBinder
import com.lyc.gank.GankWithoutImgViewBinder
import com.lyc.gank.OnGankItemClickListener
import com.lyc.gank.R
import com.lyc.gank.article.ArticleActivity
import com.lyc.gank.category.GirlItemViewBinder
import com.lyc.gank.photo.PhotoActivity
import com.lyc.gank.utils.*
import com.lyc.gank.widget.LoadMoreDetector
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), TextWatcher,
        View.OnClickListener, LoadMoreDetector.LoadMoreCallback,
        OnGankItemClickListener, TextView.OnEditorActionListener {

    private lateinit var reactiveAdapter: ReactiveAdapter
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(tb_search)
        et_search.addTextChangedListener(this)
        et_search.setOnEditorActionListener(this)
        iv_search_cancel.setOnClickListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchViewModel = provideViewModel()
        reactiveAdapter = ReactiveAdapter(searchViewModel.searchList).apply {
            observe(this@SearchActivity)
            register(GankItem::class.java)
                    .to(GankWithImgViewBinder(this@SearchActivity),
                            GankWithoutImgViewBinder(this@SearchActivity),
                            GirlItemViewBinder(this@SearchActivity))
                    .withClassLinker { _, t ->
                        return@withClassLinker when {
                            t.type == "福利" -> GirlItemViewBinder::class.java
                            t.imgUrl != null -> GankWithImgViewBinder::class.java
                            else -> GankWithoutImgViewBinder::class.java
                        }
                    }
            register(ErrorItem::class.java, ErrorItemViewBinder(searchViewModel::loadMore))
            register(LoadMoreItem::class.java, LoadMoreItemViewBinder)
            register(NoMoreItem::class.java, NoMoreItemViewBinder)
        }

        rv_search.adapter = reactiveAdapter
        rv_search.layoutManager = LinearLayoutManager(this)
        rv_search.addItemDecoration(LinearItemDivider(this))
        LoadMoreDetector.detect(rv_search, this)

        refresher_search.isEnabled = false

        searchViewModel.refreshState.observe(this, Observer {
            when (it) {
                is RefreshState.Refreshing -> refresher_search.isRefreshing = true
                is RefreshState.Error -> {
                    refresher_search.isRefreshing = false
                    toast(it.msg)
                }
                else -> refresher_search.isRefreshing = false
            }
        })

        searchViewModel.loadState.observe(this, Observer {
            when (it) {
                is LoadState.Error -> toast(it.msg)
            }
        })
    }

    override fun afterTextChanged(s: Editable) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty()) {
            iv_search_cancel.visibility = INVISIBLE
        } else {
            iv_search_cancel.visibility = VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_search_cancel -> {
                iv_search_cancel.visibility = INVISIBLE
                et_search.setText("")
                searchViewModel.reset()
            }
        }
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

    override fun canLoadMore() = searchViewModel.loadState.value is LoadState.HasMore

    override fun loadMore() {
        searchViewModel.loadMore()
    }

    override fun onGirlItemClick(iv: ImageView, item: GankItem) {
        PhotoActivity.start(this, item)
    }

    override fun onVideoItemClick(item: GankItem) {
        openWebPage(item.url)
    }

    override fun onArticleItemClick(item: GankItem) {
        ArticleActivity.start(this, item)
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_NULL || actionId == IME_ACTION_DONE) {
            search()
            return true
        }

        return false
    }

    private fun search() {
        SoftKeyboardUtil.hideKeyboard(et_search)
        searchViewModel.search(et_search.text.toString())
    }
}
