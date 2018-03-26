package com.lyc.gank.utils

/**
 * Created by Liu Yuchuan on 2018/3/15.
 */
fun assertState(boolean: Boolean, msg: String) {
    if (!boolean) {
        throw WrongStateException(msg)
    }
}
