package com.lyc.data.recommend

import com.lyc.data.api.Network

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class RecommendRepository {
    fun getItems(date: String) =  Network.gankIoApi.getRecommendResults(date)

    fun getDates() = Network.gankIoApi.historyDates
}