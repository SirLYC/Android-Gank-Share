package com.lyc.gank.utils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 提示工具的封装
 */

public class TipUtil {
    private static Toast mToast;

    private static Runnable cancel = new Runnable() {
        @Override
        public void run() {
            if(mToast!=null){
                mToast.cancel();
            }
        }
    };

    private static Handler mHandler = new Handler();

    private TipUtil(){}

    private static void show(Context context, String text, int duration){
        if(mToast != null){
            mToast.setText(text);
        }else {
            mToast = Toast.makeText(context, text, duration);
        }
        mHandler.postDelayed(cancel, duration);
        mToast.show();
    }

    public static void showShort(Context context, String text){
        show(context, text, 2000);
    }

    public static void showShort(Context context, @StringRes int ResId){
        showShort(context, context.getString(ResId));
    }

    public static void showLong(Context context, String text){
        show(context, text, 4000);
    }

    public static void showLong(Context context, @StringRes int ResId){
        showLong(context, context.getString(ResId));
    }
}
