package com.lyc.gank.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;

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

    /**
     * 实现展示图片的动画
     * @param sharedView 图片的imageView
     * @param url 图片的url
     * @param title 图片的标题
     */
    protected void startPhotoActivity(View sharedView, String url, String title){
        Intent intent = new Intent(mActivity, PhotoActivity.class);
        intent.putExtra(PhotoActivity.KEY_IMG_URL, url);
        intent.putExtra(PhotoActivity.KEY_IMG_TITLE, title);
        ActivityOptionsCompat optionsCompat =  ActivityOptionsCompat
                .makeSceneTransitionAnimation(mActivity, sharedView, getString(R.string.photo));
        try {
            ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            startActivity(intent);
        }
    }
}
