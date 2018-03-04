package com.lyc.data.search

import com.lyc.data.api.Network
import com.lyc.data.resp.checkSearchResp

/**
 * Created by Liu Yuchuan on 2018/3/4.
 */
class SearchRepository {
    fun search(parameter: String, page: Int) = Network.gankIoApi
            .search(parameter, "all", 20, page)
            .checkSearchResp()
}