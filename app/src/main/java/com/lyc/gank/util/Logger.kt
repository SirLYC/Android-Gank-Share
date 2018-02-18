package com.lyc.gank.util

import android.util.Log
import com.lyc.gank.DEBUG

/**
 * Created by Liu Yuchuan on 2018/2/18.
 */
fun logd(tag: String, msg: String, tr: Throwable? = null){
    if(DEBUG){
        if(tr == null) {
            Log.d(tag, msg)
        }else{
            Log.d(tag, msg, tr)
        }
    }
}

fun loge(tag: String, msg: String, tr: Throwable? = null){
    if(DEBUG){
        if(tr == null) {
            Log.e(tag, msg)
        }else{
            Log.e(tag, msg, tr)
        }
    }
}

fun logw(tag: String, msg: String, tr: Throwable? = null){
    if(DEBUG){
        if(tr != null) {
            Log.w(tag, msg, tr)
        }else {
            Log.w(tag, msg)
        }
    }
}

fun logw(tag: String, tr: Throwable){
    if(DEBUG){
        Log.w(tag, tr)
    }
}

fun logi(tag: String, msg: String, tr: Throwable? = null){
    if(DEBUG){
        if(tr != null){
            Log.i(tag, msg)
        }else{
            Log.i(tag, msg, tr)
        }
    }
}