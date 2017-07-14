package com.lyc.gank.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 接收返回数据的实体类
 */

public class Results{
    public int id;

    public String error;

    @SerializedName("results")
    public List<ResultItem> resultItems;
}
