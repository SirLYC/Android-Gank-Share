package com.lyc.gank.bean;

import com.lyc.gank.bean.ResultItem;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存收藏内容的bean
 */

public class CollectItem extends DataSupport{
    public int id;

    @Column(unique = true)
    public String idOnServer;

    public String title;

    public String publishTime;

    public String type;

    public String url;

    public String author;

    public String imgUrl;

    public CollectItem(ResultItem item){
        idOnServer = item.idOnServer;
        title = item.title;
        publishTime = item.publishTime;
        type = item.type;
        url = item.url;
        author = item.author;
        imgUrl = item.imgUrl;
    }


    public ResultItem toResultItem(){
        ResultItem item = new ResultItem();
        item.idOnServer = idOnServer;
        item.title = title;
        item.publishTime = publishTime;
        item.url = url;
        item.author = author;
        item.type = type;
        item.imgUrl = imgUrl;
        return item;
    }
}
