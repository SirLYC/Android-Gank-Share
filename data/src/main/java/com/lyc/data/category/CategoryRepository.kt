package com.lyc.data.category

import com.lyc.data.api.Network

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class CategoryRepository(private val type: String) {
    fun getItems(page: Int, count: Int) = Network.gankIoApi.getDataResults(type, page, count)
}