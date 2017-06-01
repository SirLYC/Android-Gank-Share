package com.lyc.gank.Database;

import com.lyc.gank.Bean.ResultItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存收藏内容的bean
 */

public class Item extends DataSupport{
    private int id;

    private String idOnServer;

    private String title;

    private String publishTime;

    private String type;

    private String url;

    private String author;

    private List<String> images = new ArrayList<>();

    public Item(){super();}

    public Item(ResultItem item){
        idOnServer = item.id;
        title = item.title;
        publishTime = item.publishTime;
        type = item.type;
        url = item.url;
        author = item.author;
        if(item.images != null) {
            for (String image : item.images) {
                images.add(image);
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(String idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public ResultItem toResultItem(){
        ResultItem item = new ResultItem();
        item.id = idOnServer;
        item.title = title;
        item.publishTime = publishTime;
        item.url = url;
        item.author = author;
        item.type = type;
        if(images != null && images.size() > 0){
            item.images = new ArrayList<>();
            for (String s : images) {
                item.images.add(s);
            }
        }
        return item;
    }
}
