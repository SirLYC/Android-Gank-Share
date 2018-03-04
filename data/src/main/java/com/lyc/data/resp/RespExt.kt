package com.lyc.data.resp

import android.accounts.NetworkErrorException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun<T> Observable<T>.async(): Observable<T>
        = this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Observable<Resp<T>>.unwrap(): Observable<T>
        = this.flatMap {
    return@flatMap if(it.error){
        Observable.error(NetworkErrorException("error response"))
    }else{
        Observable.just(it.results)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Observable<SearchResp>.checkSearchResp(): Observable<SearchResp>
        = this.flatMap {
    return@flatMap if (it.error) {
        Observable.error(NetworkErrorException("搜索失败"))
    } else {
        Observable.just(it)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Observable<PostResp>.checkPostResp(): Observable<PostResp>
        = this.flatMap {
    return@flatMap if (it.error) {
        Observable.error(NetworkErrorException("发送失败"))
    } else {
        Observable.just(it)
    }
}