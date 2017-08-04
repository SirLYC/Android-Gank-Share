package com.lyc.gank.api;

import com.lyc.gank.bean.EveryDayAWord;
import com.lyc.gank.bean.RecommendResults;
import com.lyc.gank.bean.Results;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;

/**
 * Created by 972694341@qq.com on 2017/8/2.
 */

interface CacheProviders {
    @LifeCache(duration = 1, timeUnit = TimeUnit.HOURS)
    Observable<Results> getDataResults(Observable<Results> oResults, DynamicKeyGroup pageAndType, EvictDynamicKeyGroup refresh);

    @LifeCache(duration = 1, timeUnit =  TimeUnit.HOURS)
    Observable<RecommendResults> getRecommendResults(Observable<RecommendResults> oRecommend, DynamicKey dateString, EvictProvider refresh);

    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<EveryDayAWord> getEveryWord(Observable<EveryDayAWord> oEveryDayAWord, EvictProvider refresh);

    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<String> getBingPic(Observable<String> oBingPic, EvictProvider refresh);
}
