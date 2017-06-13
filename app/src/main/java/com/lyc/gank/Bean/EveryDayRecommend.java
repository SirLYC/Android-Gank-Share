package com.lyc.gank.Bean;

import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

public class EveryDayRecommend {

    @SerializedName("Android")
    public List<ResultItem> android;

    @SerializedName("App")
    public List<ResultItem> app;

    public List<ResultItem> iOS;

    @SerializedName("休息视频")
    public List<ResultItem> video;

    @SerializedName("前端")
    public List<ResultItem> webFront;

    @SerializedName("拓展资源")
    public List<ResultItem> otherResource;

    @SerializedName("瞎推荐")
    public List<ResultItem> recommend;

    @SerializedName("福利")
    public List<ResultItem> girl;

    public List<ResultItem> merge(){
        List<ResultItem> list = new ArrayList<>();
        if(android != null && android.size() > 0) {
            for (ResultItem resultItem : android) {
                list.add(resultItem);
            }
        }
        if(iOS != null && iOS.size() > 0) {
            for (ResultItem resultItem : iOS) {
                list.add(resultItem);
            }
        }
        if(webFront != null && webFront.size() > 0) {
            for (ResultItem resultItem : webFront) {
                list.add(resultItem);
            }
        }
        if(app != null && app.size() > 0) {
            for (ResultItem resultItem : app) {
                list.add(resultItem);
            }
        }
        if(video != null && video.size() > 0) {
            for (ResultItem resultItem : video) {
                list.add(resultItem);
            }
        }
        if(recommend != null && recommend.size() > 0) {
            for (ResultItem resultItem : recommend) {
                list.add(resultItem);
            }
        }
        if(otherResource != null && otherResource.size() > 0) {
            for (ResultItem resultItem : otherResource) {
                list.add(resultItem);
            }
        }
        if(girl != null && girl.size() > 0){
            list.add(girl.get(0));
        }
        return list;
    }
}
