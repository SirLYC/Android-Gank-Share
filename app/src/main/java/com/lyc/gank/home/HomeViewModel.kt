package com.lyc.gank.home

import android.arch.lifecycle.ViewModel
import com.lyc.data.recommend.RecommendRepository
import com.lyc.data.resp.async
import com.lyc.gank.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class HomeViewModel : ViewModel() {
    val homeList = ObservableList<Any>(mutableListOf())
    val refreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val loadMoreEvent = NonNullSingleLiveEvent<LoadState>(LoadState.Empty)
    val refreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)
    val loadMoreState = NonNullLiveData<LoadState>(LoadState.Empty)

    private val compositeDisposable = CompositeDisposable()
    private val recommendRepository = RecommendRepository()
    private val dateList = mutableListOf<String>()

    private var loadIndex = 0

    private var loadMoreDisposable: Disposable? = null

    companion object {
        private const val TAG = "Home"
    }

    init {
        refreshState.observeForever { refreshEvent.value = it!! }
        loadMoreState.observeForever { loadMoreEvent.value = it!! }
    }

    fun refresh(){
        refreshState.value.refresh()?.let { nextState ->
            if(NetworkStateReceiver.isNetWorkConnected()){
                refreshState.value = nextState
                doRefresh()
            }else{
                refreshState.value = RefreshState.Error("没有网络连接")
            }
        }
    }

    private fun doRefresh(){
        loadMoreDisposable?.dispose()
        recommendRepository.getDates()
                .async()
                .subscribe({

                    //check if it needs to search
                    if(dateList.isNotEmpty() && it.isNotEmpty()
                            && it[0] == dateList[0] && homeList.isNotEmpty()){

                        assertState(refreshState.value is RefreshState.Refreshing, "${refreshState.value}")

                        refreshState.value = RefreshState.Error("已经是最新内容")
                        refreshState.value = RefreshState.NotEmpty
                        loadMoreState.value = if (dateList.size > 1) LoadState.HasMore else LoadState.NoMore
                        return@subscribe
                    }

                    dateList.clear()
                    dateList.addAll(it)
                    if (dateList.isNotEmpty()){
                        refreshDayRecommend()
                    }else{
                        refreshState.value.error("没有查找到可获取干货")?.let(refreshState::setValue)
                    }
                }, {
                    refreshState.value.error("获取干货日期失败")?.let(refreshState::setValue)
                    loge(TAG, "", it)
                })
                .also { compositeDisposable.add(it) }
    }

    private fun refreshDayRecommend(){
        val dates = dateList[0].split("-")
        recommendRepository.getItems(dates[0], dates[1], dates[2])
                .async()
                .subscribe({
                    homeList.clear()
                    it.addAllTo(homeList)

                    // load index starts from 0
                    loadIndex = 0

                    assertState(refreshState.value is RefreshState.Refreshing, "${refreshState.value}")

                    refreshState.value.result(homeList.isEmpty())?.let {
                        refreshState.value = it
                        resetLoadMoreState()
                    }
                }, {
                    refreshState.value.error("获取${dateList[0]}干货失败")?.let(refreshState::setValue)
                    loge(TAG, dateList[0], it)
                })
                .also { compositeDisposable.add(it) }
    }

    fun loadMore(){
        if(refreshState.value is RefreshState.Refreshing){
            return
        }

        loadMoreState.value.loadMore()?.let { nextState ->
            if (loadMoreState.value is LoadState.Error) {
                homeList.remove(ErrorItem)
            }

            if (NetworkStateReceiver.isNetWorkConnected()) {
                loadMoreState.value = nextState
                doLoadMore()
            } else {
                loadMoreState.value = LoadState.Error("没有网络连接")
                if (!homeList.contains(ErrorItem))
                    homeList.add(ErrorItem)
            }
        }
    }

    private fun doLoadMore(){
        homeList.add(LoadMoreItem)

        // next index
        val dates = dateList[loadIndex + 1].split("-")
        recommendRepository.getItems(dates[0], dates[1], dates[2])
                .async()
                .subscribe({
                    homeList.remove(LoadMoreItem)
                    it.addAllTo(homeList)
                    loadIndex++

                    assertState(loadMoreState.value === LoadState.Loading, "${loadMoreState.value}")

                    loadMoreState.value
                            .result(loadIndex < dateList.size - 1)?.let(loadMoreState::setValue)
                }, {
                    homeList.remove(LoadMoreItem)
                    loadMoreState.value.error("加载更多失败")?.let(loadMoreState::setValue)
                    loge(TAG, dateList[loadIndex + 1], it)
                })
                .also {
                    compositeDisposable.add(it)
                    loadMoreDisposable = it
                }
    }

    private fun resetLoadMoreState() {
        when (loadMoreState.value) {
            LoadState.Loading -> {
                homeList.remove(LoadMoreItem)
                loadMoreDisposable?.dispose()
            }
            LoadState.NoMore -> {
                homeList.remove(NoMoreItem)
            }
            is LoadState.Error -> {
                homeList.remove(ErrorItem)
            }
        }

        loadMoreState.value = if (loadIndex < dateList.size - 1) {
            LoadState.HasMore
        } else {
            LoadState.NoMore
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
