package com.lyc.gank.api;

import com.lyc.gank.bean.RecommendResults;
import com.lyc.gank.bean.Results;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

public interface GankIoApi {
    @GET("data/{type}/{count}/{page}")
    Observable<Results> getDataResults(@Path("type") String type, @Path("count") int count, @Path("page") int page);

    @GET("day/{dateString}")
    Observable<RecommendResults> getReconmmendResults(@Path("dateString") String date);
}
