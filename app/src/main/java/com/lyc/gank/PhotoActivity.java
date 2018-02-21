//package com.lyc.gank;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.AppBarLayout;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.ViewCompat;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.ImageView;
//
//import com.lyc.gank.util.ImageSave;
//import com.lyc.gank.util.Shares;
//import com.lyc.gank.util.TipUtil;
//import com.squareup.picasso.Picasso;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import uk.co.senab.photoview.PhotoViewAttacher;
//
//
///**
// * 用于展示单张照片的activity
// */
//public class PhotoActivity extends AppCompatActivity {
//    public static final String KEY_IMG_URL = "url";
//
//    public static final String KEY_IMG_TITLE = "title";
//
//    public static final String KEY_PHOTO = "photo";
//
//    private String url;
//
//    private String title;
//
//    private boolean mIsHidden;
//
//    @BindView(R.id.photo_img)
//    ImageView img;
//
//    @BindView(R.id.tool_bar_photo)
//    Toolbar toolbar;
//
//    @BindView(R.id.app_bar_photo)
//    AppBarLayout mAppBar;
//
//
//    private PhotoViewAttacher mPhotoViewAttacher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photo);
//        ButterKnife.bind(this);
//        ViewCompat.setTransitionName(img, getString(R.string.photo));
//        setToolbar();
//        Picasso.with(this).load(url).into(img);
//        setupPhotoAttacher();
//    }
//
//    private void setToolbar() {
//        Intent intent = getIntent();
//        title = intent.getStringExtra(KEY_IMG_TITLE);
//        url = intent.getStringExtra(KEY_IMG_URL);
//        toolbar.setTitle(title);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null)
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        if (Build.VERSION.SDK_INT >= 21) {
//            mAppBar.setElevation(10.6f);
//        }
//        mAppBar.setAlpha(0.7f);
//    }
//
//    private void setupPhotoAttacher() {
//        mPhotoViewAttacher = new PhotoViewAttacher(img);
//        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
//            @Override
//            public void onViewTap(View view, float x, float y) {
//                hideOrShowToolbar();
//            }
//        });
//        img.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
//                builder.setMessage(R.string.is_save_girl);
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        saveImgWithPermissionCheck();
//                    }
//                });
//                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {}
//                });
//                builder.show();
//                return true;
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 1:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    saveImg();
//                }else {
//                    TipUtil.showShort(this, R.string.no_permission);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    protected void saveImg(){
//        ImageSave.saveImageAndGetPathObservable(this, url, title)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Uri>() {
//                    @Override
//                    public void accept(Uri uri) throws Exception {
//                        TipUtil.showShort(PhotoActivity.this, getString(R.string.save_img_success) + ImageSave.path);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        TipUtil.showShort(PhotoActivity.this, R.string.internet_is_not_ok);
//                    }
//                });
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            case R.id.share_photo:
//                ImageSave.saveImageAndGetPathObservable(this, url, title)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Uri>() {
//                            @Override
//                            public void accept(Uri uri) throws Exception {
//                                Shares.shareImage(PhotoActivity.this, uri);
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                TipUtil.showShort(PhotoActivity.this, R.string.internet_exception);
//                            }
//                        });
//                return true;
//            case R.id.save_photo:
//                saveImgWithPermissionCheck();
//                return true;
//        }
//
//        return true;
//    }
//
//    private void saveImgWithPermissionCheck() {
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }else {
//            saveImg();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_photo, menu);
//        return true;
//    }
//
//    protected void hideOrShowToolbar() {
//        mAppBar.animate()
//                .translationY(mIsHidden ? 0 : -mAppBar.getHeight())
//                .setInterpolator(new DecelerateInterpolator(2))
//                .start();
//        mIsHidden = !mIsHidden;
//    }
//
//    public static Intent getIntent(Context context, String url, String title){
//        Intent intent = new Intent(context, PhotoActivity.class);
//        intent.putExtra(KEY_IMG_URL, url);
//        intent.putExtra(KEY_IMG_TITLE, title);
//        return intent;
//    }
//}
