package com.lyc.gank

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.lyc.gank.util.NetworkStateReceiver

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */

class App : Application(){
    private lateinit var injection: Injection
    private lateinit var networkReceiver: NetworkStateReceiver

    fun injection() = injection

    override fun onCreate() {
        super.onCreate()
        injection = Injection(this)

        networkReceiver = NetworkStateReceiver(this)
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
    }
}