package com.lyc.data.api;

import com.lyc.data.resp.DayData;
import com.lyc.data.resp.Resp;
import com.lyc.data.resp.GankItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

public interface GankIoApi {
    @GET("data/{type}/{count}/{page}")
    Observable<Resp<List<GankItem>>> getDataResults(@Path("type") String type, @Path("page") int page, @Path("count") int count);

    @GET("day/{year}/{month}/{day}")
    Observable<Resp<DayData>> getRecommendResults(@Path("year") String year, @Path("month") String month, @Path("day") String day);

    @GET("day/history")
    Observable<Resp<List<String>>> getHistoryDates();
}