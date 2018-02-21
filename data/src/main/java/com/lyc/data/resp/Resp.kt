package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * Created by Liu Yuchuan on 2018/2/21.
 */
class  Resp<out T>(
    @SerializedName("error") val error: Boolean,
    @SerializedName("results") val results: T
)