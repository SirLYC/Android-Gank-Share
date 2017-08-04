package com.lyc.gank.api;

import com.lyc.gank.bean.EveryDayAWord;
import com.lyc.gank.bean.RecommendResults;
import com.lyc.gank.bean.Results;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;

/**
 * Created by 972694341@qq.com on 2017/8/2.
 */

public class DataLoader {
    private CacheProviders mCacheProviders;

    private GankIoApi mGankIoApi = RetrofitFactory.getGankIoApi();

    private BingPicApi mBingPicApi = RetrofitFactory.getBingPicApi();

    private JinshanApi mJinshanApi = RetrofitFactory.getJinshanApi();

//    Observable<Results> getDataResults(int page, int count, String type, boolean update){
//        return mCacheProviders.getDataResults(mGankIoApi.getDataResults(type, count, page))
//    }
//
//    Observable<RecommendResults> getRecommendResults(Observable<RecommendResults> oRecommend, DynamicKey dateString, EvictProvider refresh);
//
//    Observable<EveryDayAWord> getEveryWord(Observable<EveryDayAWord> oEveryDayAWord, EvictProvider refresh);
//
//    Observable<String> getBingPic(Observable<String> oBingPic, EvictProvider refresh);

}
