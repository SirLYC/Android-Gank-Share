package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * 接收返回数据的实体类
 */

class CategoryResp(
        @SerializedName("results") val error: Boolean,
        @SerializedName("results") val resultItems: List<ResultItem>
)
