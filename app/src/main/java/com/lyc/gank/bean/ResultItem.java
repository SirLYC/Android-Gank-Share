package com.lyc.gank.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

/**
 * 接收返回数据的实体类
 */

public class ResultItem{
    @SerializedName("_id")
    public String idOnServer;

    @SerializedName("desc")
    public String title;

    @SerializedName("publishedAt")
    public String publishTime;

    public String type;

    public String url;

    @SerializedName("who")
    public String author;

    public List<String> images;

    public String imgUrl;
}
