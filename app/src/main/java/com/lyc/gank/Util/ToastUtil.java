package com.lyc.gank.Util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * 使Toast更人性化
 */

public class ToastUtil {
    private static Toast mToast;
    private static Runnable cancel = new Runnable() {
        @Override
        public void run() {
            Log.e("run", "cancel");
            if(mToast!=null){
                mToast.cancel();
            }
        }
    };
    private static Handler mHandler = new Handler();

    private ToastUtil(){}

    public static void show(Context context, String text, int duration){
        if(mToast != null){
            mToast.setText(text);
        }else {
            mToast = Toast.makeText(context, text, duration);
        }
        mHandler.postDelayed(cancel, duration);
        mToast.show();
    }
}
