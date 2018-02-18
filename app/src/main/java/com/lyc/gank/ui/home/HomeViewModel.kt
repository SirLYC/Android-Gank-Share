package com.lyc.gank.ui.home

import android.arch.lifecycle.ViewModel
import com.lyc.data.recommend.RecommendRepository
import com.lyc.data.resp.async
import com.lyc.gank.util.*
import io.reactivex.disposables.CompositeDisposable

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
        recommendRepository.getDates()
                .async()
                .subscribe({
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
        recommendRepository.getItems(dateList[0])
                .async()
                .subscribe({
                    homeList.clear()
                    it.addAll(homeList)
                    loadIndex = 0
                    refreshState.value.result(homeList.isEmpty()).let(refreshState::setValue)
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
                    loadState.value = LoadState.Error("没有网络连接")
                    homeList.add(ErrorItem)
                }else{
                    loadState.value = nextState
                    doLoadMore()
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

        recommendRepository.getItems(dateList[loadIndex + 1])
                .async()
                .subscribe({
                    homeList.clear()
                    it.addAll(homeList)
                    loadIndex++
                    if(loadIndex >= dateList.size - 1){
                        loadState.value = LoadState.NoMore
                    }else{
                        loadState.value = LoadState.HasMore
                    }
                }, {
                    loadState.value.error("加载更多失败").let(loadState::setValue)
                    loge(TAG, dateList[loadIndex + 1], it)
                })
    }
}