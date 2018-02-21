//package com.lyc.gank.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//import com.lyc.gank.MainActivity;
//import com.lyc.gank.fragment.GankRecommendFragment;
//import com.lyc.gank.util.TimeUtil;
//
//import java.util.Date;
//
///**
// * 根据时间来进行刷新的广播
// */
//
//public class TimeReceiver extends BroadcastReceiver{
//    MainActivity activity;
//
//    GankRecommendFragment recommendFragment;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager manager = (ConnectivityManager)
//                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = manager.getActiveNetworkInfo();
//        if(info == null)
//            return;
//        Date now = new Date();
//        if(activity != null) {
//            activity.setNeedRefresh(TimeUtil.imgNeedRefresh(activity.getToday(), now));
//            if (activity.isNeedRefresh()) {
//                activity.loadBackGround();
//            }
//        }
//
//        if(recommendFragment != null){
//            recommendFragment.setNeedRefresh(TimeUtil.needRefresh(recommendFragment.getToday(), now));
//            recommendFragment.refresh();
//        }
//    }
//
//    public void setActivity(MainActivity mainActivity){
//        activity = mainActivity;
//    }
//
//    public void setRecommendFragment(GankRecommendFragment recommendFragment) {
//        this.recommendFragment = recommendFragment;
//    }
//}
