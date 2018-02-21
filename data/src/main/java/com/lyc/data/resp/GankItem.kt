package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * 接收返回数据的实体类
 */

class GankItem(
        @SerializedName("_id") val idOnServer: String,
        @SerializedName("desc") val title: String,
        @SerializedName("publishedAt") val publishTime: String,
        @SerializedName("type") val type: String,
        @SerializedName("url") val url: String,
        @SerializedName("who") val author: String,
        @SerializedName("images") val images: List<String>?
){
    var imgUrl: String? =  null
}