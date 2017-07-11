package com.lyc.gank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lyc.gank.MainActivity;

/**
 * 根据网络状态变化来刷新
 */

public class InternetReceiver extends BroadcastReceiver {
    private boolean lastTime = true;
    private MainActivity activity;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info == null){
            lastTime = false;
        }else {
            if(!lastTime && activity.isNeedRefresh()){
                Log.e("tag", "refresh");
                activity.refresh();
            }
            lastTime = true;
        }
    }

    public void setActivity(MainActivity activity){
        this.activity = activity;
    }
}
