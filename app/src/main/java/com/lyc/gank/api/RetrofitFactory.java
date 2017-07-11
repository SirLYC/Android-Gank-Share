package com.lyc.gank.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

public class RetrofitFactory {

    private static Retrofit mRetrofit;

    private static GankIoApi mGankIoApi;

    private static BingPicApi mBingPicApi;

    private static final Object monitor = new Object();

    private static void init(){
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        httpBuilder.connectTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = httpBuilder.build();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BaseService.GANK_IO_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        mRetrofit = builder.build();
        mGankIoApi = mRetrofit.create(GankIoApi.class);
        builder = new Retrofit.Builder()
                .baseUrl(BaseService.GUO_LIN_TECH_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create());
        mRetrofit = builder.build();
        mBingPicApi = mRetrofit.create(BingPicApi.class);
    }

    public static GankIoApi getGankIoApi(){
        synchronized (monitor) {
            if (mGankIoApi == null)
                init();
            return mGankIoApi;
        }
    }

    public static BingPicApi getBingPicApi(){
        synchronized (monitor){
            if(mBingPicApi == null)
                init();
            return mBingPicApi;
        }
    }
}
