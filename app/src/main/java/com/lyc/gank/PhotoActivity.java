package com.lyc.gank;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lyc.gank.adapter.PhotoPagerAdapter;
import com.lyc.gank.bean.ImageUrls;
import com.lyc.gank.util.ImageUtil;
import com.lyc.gank.util.TipUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoActivity extends AppCompatActivity {

    @BindView(R.id.pager_photo)
    ViewPager photoPager;

    @BindView(R.id.page_now)
    TextView pageNowText;

    @BindView(R.id.btn_save)
    Button saveButton;

    ImageUrls urls;
    private int pageNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_photo);
        overridePendingTransition(R.anim.magnify_fade_in, 0);

        ButterKnife.bind(this);

        urls = (ImageUrls) getIntent().getSerializableExtra("urls");
        pageNow = getIntent().getIntExtra("page_now", 0);
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(urls.getUrls(), this);

        photoPager.setAdapter(adapter);
        photoPager.setOffscreenPageLimit(6);
        photoPager.setCurrentItem(pageNow);
        pageNowText.setText((pageNow + 1) + "/" + urls.getUrls().size());
        photoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                pageNow = position;
                pageNowText.setText((position + 1) + "/" + urls.getUrls().size());
                if(saveButton.getVisibility() == View.GONE){
                    saveButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_DRAGGING){
                    saveButton.setVisibility(View.GONE);
                }else {
                    if(saveButton.getVisibility() == View.GONE){
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(PhotoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PhotoActivity.this,
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
                    TipUtil.showShort(this, R.string.no_permission);
                }
                break;
            default:
                break;
        }
    }

    private void save(){
        ImageUtil.saveFromUrl(urls.getUrls().get(pageNow), new ImageUtil.onFinishListener() {
            @Override
            public void onSuccess(final String path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(PhotoActivity.this, getString(R.string.save_img_success));
                    }
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TipUtil.showShort(PhotoActivity.this, R.string.internet_exception);
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
