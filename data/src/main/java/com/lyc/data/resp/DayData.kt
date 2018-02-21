package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * Created by Liu Yuchuan on 2018/2/21.
 */
class DayData (
        @SerializedName("Android") val androidItems: List<GankItem>?,
        @SerializedName("App") val appItems: List<GankItem>?,
        @SerializedName("iOS") val iOSItems: List<GankItem>?,
        @SerializedName("休息视频") val videoItems: List<GankItem>?,
        @SerializedName("前端") val webEndItems: List<GankItem>?,
        @SerializedName("拓展资源") val otherResourceItems: List<GankItem>?,
        @SerializedName("瞎推荐") val recommendItems: List<GankItem>?,
        @SerializedName("福利") val girlItems: List<GankItem>?
){
    fun addAllTo(list: AbstractMutableList<Any>){
        androidItems?.let { list.addAll(it) }
        iOSItems?.let { list.addAll(it) }
        webEndItems?.let { list.addAll(it) }
        recommendItems?.let { list.addAll(it) }
        videoItems?.let { list.addAll(it) }
        girlItems?.let { list.addAll(it) }
        appItems?.let { list.addAll(it) }
        otherResourceItems?.let { list.addAll(it) }
    }
}