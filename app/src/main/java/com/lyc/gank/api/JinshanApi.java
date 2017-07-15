package com.lyc.gank.api;

import com.lyc.gank.bean.EveryDayAWord;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by 972694341@qq.com on 2017/7/15.
 */

public interface JinshanApi {
    @GET("dsapi")
    Observable<EveryDayAWord> getEveryWord();
}
