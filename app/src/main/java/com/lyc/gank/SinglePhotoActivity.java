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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lyc.gank.Util.ImageUtil;
import com.lyc.gank.Util.ToastUtil;

import uk.co.senab.photoview.PhotoView;


/**
 * 用于展示单张照片的activity
 */
public class SinglePhotoActivity extends AppCompatActivity {
    String url;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.photo_view);
        overridePendingTransition(R.anim.magnify_fade_in, 0);
        saveButton = (Button)findViewById(R.id.btn_save);
        PhotoView img = (PhotoView)findViewById(R.id.photo_img);
        url = getIntent().getStringExtra("url");
        Glide.with(this).load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        findViewById(R.id.photo_progress).setVisibility(View.GONE);
                        ToastUtil.show(SinglePhotoActivity.this, "网络似乎不太给力...", 1000);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        findViewById(R.id.photo_progress).setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
        img.setEnabled(true);
        findViewById(R.id.photo_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setVisibility(View.VISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(SinglePhotoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SinglePhotoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    save();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    save();
                }else {
                    ToastUtil.show(this,"没有相关权限！", 1000);
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
                        ToastUtil.show(SinglePhotoActivity.this, "成功保存至" + path, 2000);
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(SinglePhotoActivity.this, "网络出错", 2000);
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
