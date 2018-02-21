package com.lyc.gank.ui.home

import android.arch.lifecycle.ViewModel
import com.lyc.data.recommend.RecommendRepository
import com.lyc.data.resp.async
import com.lyc.gank.util.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class HomeViewModel : ViewModel() {
    val homeList = ObservableList<Any>(mutableListOf())
    val refreshState = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val loadState = NonNullSingleLiveEvent<LoadState>(LoadState.Empty)

    private val compositeDisposable = CompositeDisposable()
    private val recommendRepository = RecommendRepository()
    private val dateList = mutableListOf<String>()

    private var loadIndex = -1

    private var loadMoreDisposable: Disposable? = null

    companion object {
        private const val TAG = "Home"
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

                    //check if it needs to refresh
                    if(dateList.isNotEmpty() && it.isNotEmpty()
                            && it[0] == dateList[0] && homeList.isNotEmpty()){
                        refreshState.value.result(true)?.let (refreshState::setValue)
                        loadState.value = if(dateList.size > 1) LoadState.HasMore else LoadState.NoMore
                        return@subscribe
                    }

                    dateList.clear()
                    dateList.addAll(it)
                    if (dateList.isNotEmpty()){
                        refreshDayRecommend()
                    }else{
                        refreshState.value.error("没有查找到可获取干货").let(refreshState::setValue)
                    }
                }, {
                    refreshState.value.error("获取干货日期失败").let(refreshState::setValue)
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
                    loadIndex = 0
                    refreshState.value.result(homeList.isEmpty()).let(refreshState::setValue)
                    loadState.value = if(dateList.size > 1) LoadState.HasMore else LoadState.NoMore
                }, {
                    refreshState.value.error("获取${dateList[0]}干货失败").let(refreshState::setValue)
                    loge(TAG, dateList[0], it)
                })
                .also { compositeDisposable.add(it) }
    }

    fun loadMore(){
        if(refreshState.value is RefreshState.Refreshing){
            return
        }

        if(loadState.value is LoadState.NoMore && homeList.contains(NoMoreItem)){
            homeList.add(NoMoreItem)
        }

        loadState.value.loadMore()?.let { nextState ->
            if(nextState is LoadState.Loading){
                if(NetworkStateReceiver.isNetWorkConnected()){
                    loadState.value = nextState
                    doLoadMore()
                }else{
                    loadState.value = LoadState.Error("没有网络连接")
                    homeList.add(ErrorItem)
                }
            }
        }
    }

    private fun doLoadMore(){
        if(loadIndex >= dateList.size - 1 && loadState.value !is LoadState.NoMore) {
            loadState.value = LoadState.NoMore
            if(!homeList.contains(NoMoreItem)) {
                homeList.add(NoMoreItem)
            }else{
                homeList.add(NoMoreItem)
            }
            return
        }

        if(loadState.value is LoadState.Error){
            homeList.remove(ErrorItem)
        }
        homeList.add(LoadMoreItem)
        val dates = dateList[loadIndex + 1].split("-")
        recommendRepository.getItems(dates[0], dates[1], dates[2])
                .async()
                .subscribe({
                    homeList.remove(LoadMoreItem)
                    it.addAllTo(homeList)
                    loadIndex++
                    if(loadIndex >= dateList.size - 1){
                        loadState.value = LoadState.NoMore
                    }else{
                        loadState.value = LoadState.HasMore
                    }
                }, {
                    homeList.remove(LoadMoreItem)
                    loadState.value.error("加载更多失败").let(loadState::setValue)
                    loge(TAG, dateList[loadIndex + 1], it)
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