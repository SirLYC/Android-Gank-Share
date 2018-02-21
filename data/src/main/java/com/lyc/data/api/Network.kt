package com.lyc.data.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.lyc.data.BuildConfig.DEBUG
import com.lyc.data.resp.GankItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
object Network {
    private val okhttp by lazy {
        OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .apply {
                    if(DEBUG){
                        addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                    }
                }
                .build()
    }

    private val gson by lazy {
        GsonBuilder()
                .registerTypeAdapter(GankItem::class.java, JsonDeserializer<GankItem> { json, _, _ ->
                    Gson().fromJson(json, GankItem::class.java).apply {
                        if(type == "福利"){
                            imgUrl = url
                        }else if(images != null && images.isNotEmpty()){
                            imgUrl = images[0]
                        }
                    }
                })
                .create()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("http://gank.io/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okhttp)
                .build()
    }

    val gankIoApi by lazy {
        retrofit.create(GankIoApi::class.java)
    }
}