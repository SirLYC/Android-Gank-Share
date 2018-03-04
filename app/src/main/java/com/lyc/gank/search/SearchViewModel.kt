package com.lyc.gank.search

import android.arch.lifecycle.ViewModel
import com.lyc.data.resp.async
import com.lyc.data.search.SearchRepository
import com.lyc.gank.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Liu Yuchuan on 2018/3/4.
 */
class SearchViewModel : ViewModel() {

    companion object {
        const val TAG = "Search"
    }

    val searchList = ObservableList<Any>(mutableListOf())
    val refreshState = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val loadState = NonNullSingleLiveEvent<LoadState>(LoadState.Empty)

    private val compositeDisposable = CompositeDisposable()
    private val searchRepository = SearchRepository()


    private var loadIndex = 0

    private var loadMoreDisposable: Disposable? = null
    private var searchDisposable: Disposable? = null

    private val parameterSubject = BehaviorSubject.createDefault<String>("")


    fun search(parameter: String) {
        searchList.clear()
        refreshState.value.refresh()?.let { nextState ->
            if (NetworkStateReceiver.isNetWorkConnected()) {
                refreshState.value = nextState
                doSearch(parameter)
            } else {
                refreshState.value = RefreshState.Error("没有网络连接")
            }
        }
    }

    private fun doSearch(parameter: String) {
        loadMoreDisposable?.dispose()
        if (searchList.contains(LoadMoreItem)) {
            searchList.remove(LoadMoreItem)
        }

        searchRepository.search(parameter, 0)
                .async()
                .subscribe({
                    parameterSubject.onNext(parameter)
                    if (it.count == 0) {
                        refreshState.value.error("没有找到相关内容")?.let(refreshState::setValue)
                        loadState.value = LoadState.NoMore
                    } else {
                        searchList.addAll(it.results)
                        loadState.value = LoadState.HasMore
                        loadIndex = 1
                    }
                    refreshState.value.result(it.count == 0)?.let(refreshState::setValue)
                }, {
                    refreshState.value.error("搜索失败")?.let(refreshState::setValue)
                    loge(TAG, "search $parameter", it)
                })
                .also {
                    searchDisposable = it
                    compositeDisposable.add(it)
                }
    }

    fun loadMore() {
        if (refreshState.value is RefreshState.Refreshing) {
            return
        }

        if (loadState.value is LoadState.NoMore && !searchList.contains(NoMoreItem)) {
            searchList.add(NoMoreItem)
        }

        if (parameterSubject.value == "") {
            return
        }

        loadState.value.loadMore()?.let { nextState ->
            if (nextState is LoadState.Loading) {
                if (NetworkStateReceiver.isNetWorkConnected()) {
                    loadState.value = nextState
                    doLoadMore(parameterSubject.value!!)
                } else {
                    loadState.value = LoadState.Error("没有网络连接")
                    if (!searchList.contains(ErrorItem)) {
                        searchList.add(ErrorItem)
                    }
                }
            }
        }
    }

    private fun doLoadMore(parameter: String) {
        if (searchList.contains(ErrorItem)) {
            searchList.remove(ErrorItem)
        }
        searchList.add(LoadMoreItem)
        searchRepository.search(parameter, loadIndex + 1)
                .async()
                .subscribe({

                    if (it.count == 0) {
                        loadState.value = LoadState.NoMore
                        searchList.add(NoMoreItem)
                        return@subscribe
                    } else {
                        searchList.remove(LoadMoreItem)
                        searchList.addAll(it.results)
                        loadIndex++
                    }
                    loadState.value.result(it.count == 0)?.let(loadState::setValue)
                }, {
                    searchList.remove(LoadMoreItem)
                    loadState.value.error(it.message ?: "加载更多失败").let(loadState::setValue)
                    loge(TAG, "page ${loadIndex + 1}", it)
                })
                .also {
                    compositeDisposable.add(it)
                    loadMoreDisposable = it
                }
    }

    fun reset() {
        loadMoreDisposable?.dispose()
        searchDisposable?.dispose()
        refreshState.value = RefreshState.Empty
        loadState.value = LoadState.Empty
        loadIndex = 0
        searchList.clear()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}