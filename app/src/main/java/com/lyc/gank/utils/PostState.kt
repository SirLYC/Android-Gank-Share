package com.lyc.gank.utils

import android.support.annotation.CheckResult

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
sealed class PostState {
    @CheckResult open fun result(): PostState? = null
    @CheckResult open fun post(): PostState? = null
    @CheckResult open fun error(msg: String): PostState? = null

    object NoAction : PostState() {
        override fun post(): PostState? = Posting
    }

    object Posting : PostState() {
        override fun result() = Success
        override fun error(msg: String) = Error(msg)
    }

    object Success : PostState()

    class Error(val msg: String) : PostState() {
        override fun post() = Posting
    }
}