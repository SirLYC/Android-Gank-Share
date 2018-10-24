package com.lyc.gank.discover

import android.arch.lifecycle.ViewModel
import com.lyc.data.category.CategoryRepository
import com.lyc.data.resp.async
import com.lyc.gank.utils.*
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class DiscoverViewModel : ViewModel() {
    val discoverList = ObservableList<Any>(mutableListOf())
    val refreshEvent = NonNullSingleLiveEvent<RefreshState>(RefreshState.Empty)
    val refreshState = NonNullLiveData<RefreshState>(RefreshState.Empty)

    private val compositeDisposable = CompositeDisposable()
    private var categoryRepository = CategoryRepository(TYPE)


    companion object {
        private const val TYPE = "瞎推荐"
    }

    init {
        refreshState.observeForever { refreshEvent.value = it!! }
    }

    fun refresh() {
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
        categoryRepository.getItems(1)
                .async()
                .subscribe({ itemList ->

                    if (itemList.isNotEmpty()) {
                        discoverList.clear()
                        discoverList.addAll(itemList)
                    }

                    assertState(refreshState.value is RefreshState.Refreshing, "${refreshState.value}")

                    refreshState.value.result(discoverList.isEmpty())?.let {
                        refreshState.value = it
                    }

                }, {
                    loge("SingleContentViewModel", "", it)
                    refreshState.value.error("获取推荐失败")?.let(refreshState::setValue)
                })
                .also {
                    compositeDisposable.add(it)
                }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
