package com.lyc.gank.api;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by 972694341@qq.com on 2017/7/10.
 */

public interface BingPicApi {
    @GET("bing_pic")
    public Observable<String> getBingPic();
}
