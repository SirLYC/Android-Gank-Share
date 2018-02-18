package com.lyc.data.resp

import com.google.gson.annotations.SerializedName

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class HistoryDates (
        @SerializedName("error") val error: Boolean,
        @SerializedName("results") val dates: List<String>
)