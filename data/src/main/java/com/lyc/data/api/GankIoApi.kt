package com.lyc.data.api

import com.lyc.data.resp.DayData
import com.lyc.data.resp.GankItem
import com.lyc.data.resp.PostResp
import com.lyc.data.resp.Resp
import com.lyc.data.resp.SearchResp

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

interface GankIoApi {

    @get:GET("day/history")
    val historyDates: Observable<Resp<List<String>>>

    @GET("data/{type}/{count}/{page}")
    fun getDataResults(@Path("type") type: String,
            @Path("page") page: Int,
            @Path("count") count: Int): Observable<Resp<List<GankItem>>>

    @GET("day/{year}/{month}/{day}")
    fun getRecommendResults(@Path("year") year: String,
            @Path("month") month: String,
            @Path("day") day: String): Observable<Resp<DayData>>

    @POST("add2gank")
    @FormUrlEncoded
    fun add2gank(@Field("url") url: String,
            @Field("desc") description: String,
            @Field("type") Type: String,
            @Field("debug") debugBoolean: String,
            @Field("who") who: String): Observable<PostResp>

    @GET("search/query/{parameter}/category/{category}/count/{count}/page/{page}")
    fun search(@Path("parameter") parameter: String,
            @Path("category") category: String,
            @Path("count") count: Int,
            @Path("page") page: Int): Observable<SearchResp>
}
