package com.lyc.data.api;

import java.util.List;

import com.lyc.data.resp.CategoryResp;
import io.reactivex.Observable;
import com.lyc.data.resp.EveryDayRecommend;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

public interface GankIoApi {
    @GET("data/{type}/{count}/{page}")
    Observable<CategoryResp> getDataResults(@Path("type") String type,  @Path("page") int page, @Path("count") int count);

    @GET("day/{dateString}")
    Observable<EveryDayRecommend> getRecommendResults(@Path("dateString") String date);

    @GET("day/history")
    Observable<List<String>> getHistoryDates();
}