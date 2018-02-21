package com.lyc.data.recommend

import com.lyc.data.api.Network
import com.lyc.data.resp.unwrap

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class RecommendRepository {
    fun getItems(year: String, month: String, day: String)
            = Network.gankIoApi
            .getRecommendResults(year, month, day)
            .unwrap()

    fun getDates()
            = Network.gankIoApi
            .historyDates
            .unwrap()
}