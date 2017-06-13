package com.lyc.gank.Util;

import android.content.Context;
import android.util.Log;

import com.lyc.gank.Bean.ResultItem;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by 972694341@qq.com on 2017/6/1.
 */

public class ShareUtil {
    private ShareUtil(){}

    public static void shareItem(Context context, ResultItem item){

            OnekeyShare oks = new OnekeyShare();
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
            oks.setTitle(item.title);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(item.url);
            // text是分享文本，所有平台都需要这个字段
            oks.setText("来自GanK.io每日干货");
//            if(item.images != null && item.images.size() > 0)
//            //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//                oks.setImageUrl(item.images.get(0));
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(item.url);
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(item.url);
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(item.url);
            oks.show(context);
    }

    public static void shareImage(Context context, ResultItem item){
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle("图片分享");
        oks.setTitleUrl(item.url);
        Log.e("分享", item.url);
        oks.setText("来自GanK.io每日干货");
        oks.setImageUrl(item.url);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(item.url);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(item.url);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(item.url);
        oks.show(context);
    }
}
