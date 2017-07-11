package com.lyc.gank.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 保存网络图片的工具
 */

public class ImageUtil {
    private static FileOutputStream fis;
    private static InputStream is;
    private ImageUtil(){}
    private static String dirName = "干货图片";

    public interface onFinishListener {
        void onSuccess(String path);
        void onFailed();
    }

    public static void saveFromUrl(final String url, final onFinishListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File data = new File(Environment.getExternalStorageDirectory(), dirName);
                if(!data.exists()){
                    data.mkdir();
                }
                String [] temp = url.split("/");
                File file = new File(data, temp[temp.length - 1]);
                try {
                    fis = new FileOutputStream(file);
                    byte [] br = new byte[1024];
                    URL imgUrl = new URL(url);
                    is = imgUrl.openStream();
                    int l;
                    while((l = is.read(br)) != -1){
                        fis.write(br, 0, l);
                    }
                    if(listener != null){
                        listener.onSuccess(file.getPath());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(fis != null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            if(listener != null){
                                listener.onFailed();
                            }
                        }
                    }

                    if(is != null){
                        try {
                            is.close();
                        } catch (IOException e) {
                            if(listener != null){
                                listener.onFailed();
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
