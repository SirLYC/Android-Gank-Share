package com.lyc.gank.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片地址的包装类，用于intent传递数据
 */

public class ImageUrls implements Serializable{
    private List<String> urls = new ArrayList<>();

    public ImageUrls(){}

    public ImageUrls(List<ResultItem> items){
        for (ResultItem item : items) {
            urls.add(item.url);
        }
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls){
        for(String u: urls){
            this.urls.add(u);
        }
    }
}
