package com.lyc.data.category

import com.lyc.data.api.Network
import com.lyc.data.resp.unwrap

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class CategoryRepository(private val type: String) {
    fun getItems(page: Int)
            = Network.gankIoApi
            .getDataResults(type, page, 20)
            .unwrap()
}