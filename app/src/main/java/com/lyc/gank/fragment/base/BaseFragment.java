package com.lyc.gank.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 本项目所有fragment的基类
 * 建立activity全局引用，防止activity重建后的内存泄漏
 */

public class BaseFragment extends Fragment{
    protected AppCompatActivity mActivity;

    protected Unbinder mUnbinder;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

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
        Intent intent = PhotoActivity.getIntent(mActivity, url, title);
        ActivityOptionsCompat optionsCompat =  ActivityOptionsCompat
                .makeSceneTransitionAnimation(mActivity, sharedView, getString(R.string.photo));
        try {
            ActivityCompat.startActivity(mActivity, intent, optionsCompat.toBundle());
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            startActivity(intent);
        }
    }

    protected void addDisposable(Disposable disposable){
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        if(disposable != null) {
            mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onDestroyView() {
        if(mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
        if(mUnbinder != null){
            mUnbinder.unbind();
        }
        super.onDestroyView();
    }
}
