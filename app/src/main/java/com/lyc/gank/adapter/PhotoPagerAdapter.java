package com.lyc.gank.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lyc.gank.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * 用于福利频道浏览妹子图
 */

public class PhotoPagerAdapter extends PagerAdapter {
    private List<String> mUrls;
    private Context mContext;

    public PhotoPagerAdapter(List<String> urls, Context context) {
        this.mUrls = urls;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_photo, container, false);
        final ProgressBar bar = (ProgressBar) view.findViewById(R.id.photo_progress);
        final PhotoView photo = (PhotoView)view.findViewById(R.id.photo_img);
        Glide.with(mContext).load(mUrls.get(position)).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }
        }).into(photo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)container.getContext()).finish();
            }
        });
        photo.setEnabled(true);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
