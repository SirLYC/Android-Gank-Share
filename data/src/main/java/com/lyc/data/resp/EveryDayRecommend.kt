package com.lyc.data.resp

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

class EveryDayRecommend (
        @SerializedName("category") val category: List<String>,
        @SerializedName("error") val error: Boolean,
        @SerializedName("results") val results: Results
){

    class Results (
            @SerializedName("Android") val androidItems: List<ResultItem>,
            @SerializedName("App") val appItems: List<ResultItem>,
            @SerializedName("iOS") val iOSItems: List<ResultItem>,
            @SerializedName("休息视频") val videoItems: List<ResultItem>,
            @SerializedName("前端") val webEndItems: List<ResultItem>,
            @SerializedName("拓展资源") val otherResourceItems: List<ResultItem>,
            @SerializedName("瞎推荐") val recommendItems: List<ResultItem>,
            @SerializedName("福利") val girlItems: List<ResultItem>
    )

    fun addAll(list: AbstractMutableList<Any>){
        list.addAll(results.androidItems)
        list.addAll(results.iOSItems)
        list.addAll(results.webEndItems)
        list.addAll(results.recommendItems)
        list.addAll(results.videoItems)
        list.addAll(results.girlItems)
        list.addAll(results.appItems)
        list.addAll(results.otherResourceItems)
    }
}
