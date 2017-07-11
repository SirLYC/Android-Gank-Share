package com.lyc.gank.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * 本项目所有fragment的基类
 * 建立activity全局引用，防止activity重建后的内存泄漏
 */

public class BaseFragment extends Fragment{
    AppCompatActivity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }
}
