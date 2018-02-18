package com.lyc.gank.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Liu Yuchuan on 2018/1/17.
 */
class NetworkStateReceiver(
        context: Context?
): BroadcastReceiver() {

    init {
        networkStateSubject.onNext(getNetWorkState(context))
    }

    companion object {
        const val NETWORK_DISCONNECTED = 0
        const val NETWORK_MOBILE = 1
        const val NETWORK_WIFI = 2
        private var networkStateSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(NETWORK_DISCONNECTED)

        fun isNetWorkConnected() = networkStateSubject.value != NETWORK_DISCONNECTED
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        networkStateSubject.onNext(getNetWorkState(context))
    }

    private fun getNetWorkState(context: Context?): Int {

        val manager = context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo ?: return NETWORK_DISCONNECTED

        if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI
        }

        return if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            NETWORK_MOBILE
        } else NETWORK_DISCONNECTED

    }
}

