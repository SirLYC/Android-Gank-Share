package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * Created by Liu Yuchuan on 2018/3/4.
 */
class SearchResp(
        @SerializedName("count") val count: Int,
        @SerializedName("error") val error: Boolean,
        @SerializedName("results") val results: List<GankItem>
)