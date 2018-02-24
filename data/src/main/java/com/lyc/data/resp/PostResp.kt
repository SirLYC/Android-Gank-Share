package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class PostResp(
        @SerializedName("error") val error: Boolean,
        @SerializedName("msg") val msg: String
)