package com.lyc.gank.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.lyc.gank.bean.ResultItem;

import java.lang.reflect.Type;
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
        httpBuilder.connectTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = httpBuilder.build();

        Retrofit.Builder builder = new Retrofit.Builder();

        JsonDeserializer<ResultItem> deserializer = new JsonDeserializer<ResultItem>() {
            @Override
            public ResultItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                ResultItem item = new Gson().fromJson(json, ResultItem.class);
                item.publishTime = item.publishTime.substring(0, 10);
                if(item.type.equals("福利")){
                    item.imgUrl = item.url;
                }else if(item.images != null && item.images.size() > 0){
                    item.imgUrl = item.images.get(0);
                }else {
                    item.imgUrl = null;
                }
                return item;
            }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ResultItem.class, deserializer)
                .create();

        builder.baseUrl(BaseService.GANK_IO_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
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
