package com.lyc.gank;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lyc.gank.util.ImageUtil;
import com.lyc.gank.util.TipUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;


/**
 * 用于展示单张照片的activity
 */
public class SinglePhotoActivity extends AppCompatActivity {
    String url;

    @BindView(R.id.btn_save)
    Button saveButton;

    @BindView(R.id.photo_img)
    PhotoView img;

    @BindView(R.id.photo_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.view_photo);
        overridePendingTransition(R.anim.magnify_fade_in, 0);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("url");
        Glide.with(this).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        TipUtil.showShort(SinglePhotoActivity.this, R.string.internet_is_not_ok);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
        img.setEnabled(true);
        saveButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.photo_layout)
    public void back(){
        finish();
    }

    @OnClick(R.id.btn_save)
    public void savePhoto(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            save();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    save();
                }else {
                    TipUtil.showShort(this, R.string.no_permission);
                }
                break;
            default:
                break;
        }
    }

    private void save(){
        ImageUtil.saveFromUrl(url, new ImageUtil.onFinishListener() {
            @Override
            public void onSuccess(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(SinglePhotoActivity.this, getString(R.string.save_img_success) + path);
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(SinglePhotoActivity.this, R.string.internet_exception);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.shrink_fade_out);
    }
}
