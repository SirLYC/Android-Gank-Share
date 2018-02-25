package com.lyc.gank.category

import android.arch.lifecycle.ViewModel
import com.lyc.data.category.CategoryRepository
import com.lyc.data.resp.GankItem
import com.lyc.data.resp.async
import com.lyc.gank.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class SingleContentViewModel : ViewModel() {
    private lateinit var type: String

    val singleContentList = ObservableList<Any>(mutableListOf())
    val refreshState = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val loadState = NonNullSingleLiveEvent<LoadState>(LoadState.Empty)

    private val compositeDisposable = CompositeDisposable()
    private lateinit var categoryRepository: CategoryRepository

    //for lazy load
    private var dataLoad = false

    private var loadIndex = -1

    private var loadMoreDisposable: Disposable? = null

    companion object {
        private const val TAG = "SingleCategory"
    }

    fun dataLoad() = dataLoad

    fun setType(type: String) {
        this.type = type
        categoryRepository = CategoryRepository(type)
    }


    fun refresh() {
        dataLoad = true
        refreshState.value.refresh()?.let { nextState ->
            if (NetworkStateReceiver.isNetWorkConnected()) {
                refreshState.value = nextState
                doRefresh()
            } else {
                refreshState.value = RefreshState.Error("没有网络连接")
            }
        }
    }

    private fun doRefresh() {
        loadMoreDisposable?.dispose()
        if (singleContentList.contains(LoadMoreItem)) {
            singleContentList.remove(LoadMoreItem)
        }
        categoryRepository.getItems(0)
                .async()
                .subscribe({
                    if (it.size > 0 && singleContentList.size > 0
                            && singleContentList[0] is GankItem
                            && it[0].idOnServer == (singleContentList[0] as GankItem).idOnServer) {
                        refreshState.value = RefreshState.Error("已经是最新内容")
                        refreshState.value = RefreshState.NotEmpty
                        return@subscribe
                    }

                    singleContentList.clear()
                    singleContentList.addAll(it)
                    refreshState.value.result(singleContentList.isEmpty())?.let(refreshState::setValue)
                    loadState.value = LoadState.HasMore
                }, {
                    refreshState.value.error("获取{$type}干货失败")?.let(refreshState::setValue)
                })
    }

    fun loadMore() {
        if (refreshState.value is RefreshState.Refreshing) {
            return
        }

        if (loadState.value is LoadState.NoMore && !singleContentList.contains(NoMoreItem)) {
            singleContentList.add(NoMoreItem)
        }

        loadState.value.loadMore()?.let { nextState ->
            if (nextState is LoadState.Loading) {
                if (NetworkStateReceiver.isNetWorkConnected()) {
                    loadState.value = nextState
                    doLoadMore()
                } else {
                    loadState.value = LoadState.Error("没有网络连接")
                    if (!singleContentList.contains(ErrorItem)) {
                        singleContentList.add(ErrorItem)
                    }
                }
            }
        }
    }

    private fun doLoadMore() {
        if (singleContentList.contains(ErrorItem)) {
            singleContentList.remove(ErrorItem)
        }
        singleContentList.add(LoadMoreItem)
        categoryRepository.getItems(loadIndex + 1)
                .async()
                .subscribe({
                    if (it.size == 0) {
                        loadState.value = LoadState.NoMore
                        singleContentList.add(NoMoreItem)
                        return@subscribe
                    }

                    singleContentList.remove(LoadMoreItem)
                    singleContentList.addAll(it)
                    loadIndex++
                    loadState.value.result(true)?.let(loadState::setValue)
                }, {
                    singleContentList.remove(LoadMoreItem)
                    loadState.value.error("加载更多失败").let(loadState::setValue)
                    loge(TAG, "page ${loadIndex + 1}", it)
                })
                .also {
                    compositeDisposable.add(it)
                    loadMoreDisposable = it
                }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}