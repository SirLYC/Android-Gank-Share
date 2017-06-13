package com.lyc.gank.Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 接收返回数据的实体类
 */

public class ResultItem implements Serializable{
    @SerializedName("_id")
    public String id;

    @SerializedName("desc")
    public String title;

    @SerializedName("publishedAt")
    public String publishTime;

    public String type;

    public String url;

    @SerializedName("who")
    public String author;

    public List<String> images;
}