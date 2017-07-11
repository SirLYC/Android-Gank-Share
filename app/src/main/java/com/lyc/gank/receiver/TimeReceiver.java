package com.lyc.gank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lyc.gank.MainActivity;
import com.lyc.gank.fragment.GankRecommendFragment;

import java.util.Date;

/**
 * 根据时间来进行刷新的广播
 */

public class TimeReceiver extends BroadcastReceiver{
    MainActivity activity;

    GankRecommendFragment recommendFragment;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info == null)
            return;

        Date date = new Date();
        if(activity != null) {
            Date aDate = activity.getToday();

            if(date.getDay() != aDate.getDay()){
                activity.setToday(date);
            }

            //每天中午12点自动更新背景
            if ((date.getDay() != aDate.getDay()
                    || (aDate.getDay() == date.getDay() && aDate.getHours() <= 12))
                    && date.getHours() > 12) {
                activity.setLoadBackGround(false);
            }

            if (!activity.isLoadBackGround()) {
                activity.setBackGround();
            }
        }

        if(recommendFragment != null){
            Date fDate = recommendFragment.getToday();

            if(date.getDay() != fDate.getDay()){
                recommendFragment.setToday(date);
                recommendFragment.setDate(date);
            }

            //13点自动更新每日推荐
            if ((date.getDay() != fDate.getDay()
                    || (fDate.getDay() == date.getDay() && fDate.getHours() <= 13))
                    && date.getHours() > 13) {
                recommendFragment.setNeedRefresh(true);
            }

            recommendFragment.refresh();
        }
    }

    public void setActivity(MainActivity mainActivity){
        activity = mainActivity;
    }

    public void setRecommendFragment(GankRecommendFragment recommendFragment) {
        this.recommendFragment = recommendFragment;
    }
}
