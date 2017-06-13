package com.lyc.gank.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.Adapter.ArticleRecyclerAdapter;
import com.lyc.gank.Adapter.BaseRecyclerAdapter;
import com.lyc.gank.Bean.EveryDayRecommend;
import com.lyc.gank.Bean.Messages;
import com.lyc.gank.Bean.ResultItem;
import com.lyc.gank.Database.Item;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.SinglePhotoActivity;
import com.lyc.gank.Util.HttpUtility;
import com.lyc.gank.Util.ImageUtil;
import com.lyc.gank.Util.ShareUtil;
import com.lyc.gank.Util.TimeUtil;
import com.lyc.gank.Util.ToastUtil;
import com.lyc.gank.Util.Utility;
import com.lyc.gank.WebActivity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecommendFragment extends Fragment{

    @BindView(R.id.recycler_view_recommend)
    RecyclerView mRecyclerView;

    @BindView(R.id.tool_bar_recommend)
    Toolbar toolbar;

    private EveryDayRecommend mRecommend = new EveryDayRecommend();

    private List<ResultItem> mData = new ArrayList<>();

    private ArticleRecyclerAdapter adapter;

    private int itemNow;

    private static final int UPDATE_TIME = 1000 * 60 * 60;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Messages.MSG_START_LOAD:
                    initData();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 接口地址前缀
     */
    private Date mDate = new Date();
    private Date today = new Date();
    private final String addressPre = "http://gank.io/api/day/";

    public RecommendFragment() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ButterKnife.bind(this, view);

        adapter = new ArticleRecyclerAdapter(mData, getContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((MainActivity)getActivity()).initToolbar(toolbar, "今日推荐");

        adapter.setOnItemClickedListener(new BaseRecyclerAdapter.OnItemClickedListener() {
            @Override
            public void onClick(ResultItem item) {
                Intent intent = new Intent();
                switch (item.type){
                    case "福利":
                        intent.setClassName(getContext(), SinglePhotoActivity.class.getName());
                        intent.putExtra("url", item.url);
                        startActivity(intent);
                        break;
                    case "休息视频":
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(item.url);
                        intent.setData(uri);
                        startActivity(intent);
                        break;
                    default:
                        intent.setClassName(getContext(), WebActivity.class.getName());
                        intent.putExtra("item", item);
                        startActivity(intent);
                        break;
                }
            }
        });

        adapter.setOnItemLongClickedListener(new BaseRecyclerAdapter.OnItemLongClickedListener() {
            @Override
            public boolean onLongClick(int pos, View v) {
                itemNow = pos;
                return false;
            }
        });

        registerForContextMenu(mRecyclerView);
        return view;
    }

    private void collect(ResultItem item, View v){
        final Item itemCollect = new Item(item);
        if(DataSupport.where("idOnServer = ?",
                itemCollect.getIdOnServer()).find(Item.class).isEmpty()){
            itemCollect.save();
        }else {
            Snackbar.make(v, "已经在收藏列表中了！", Snackbar.LENGTH_SHORT)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        }
    }

    private void save(String url){
        ImageUtil.saveFromUrl(url, new ImageUtil.onFinishListener() {
            @Override
            public void onSuccess(final String path) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getActivity(), "成功保存至" + path, 2000);
                    }
                });
            }

            @Override
            public void onFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getActivity(), "网络出错", 2000);
                    }
                });
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()){
            ResultItem i = mData.get(itemNow);
            Log.e("推荐", "Context Menu");
            switch (item.getItemId()){
                case 0:
                    collect(i, getView());
                    break;
                case 1:
                    Log.e("item", itemNow + " " + i.title);
                    if(i.type.equals("福利")){
                        Log.e("推荐", "福利");
                        ShareUtil.shareImage(getContext(), i);
                    }else {
                        ShareUtil.shareItem(getContext(), i);
                    }
                    break;
                case 2:
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else {
                        save(i.url);
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 0, Menu.NONE, "收藏");
        menu.add(Menu.NONE, 1, Menu.NONE, "分享");
        if(mData.get(itemNow).type.equals("福利"))
            menu.add(Menu.NONE, 2, Menu.NONE, "保存妹子");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    save(mData.get(itemNow).url);
                }else {
                    ToastUtil.show(getActivity(),"没有相关权限！", 1000);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData(){
        HttpUtility.sendOkHttpRequest(addressPre + TimeUtil.getDateString(mDate), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                mRecommend = Utility.getRecommendFromJson(res);
                List<ResultItem> itemList = mRecommend.merge();
                //今日数据可能还没有更新，如果没有就展示往期
                if(itemList == null || itemList.size() ==  0){
                    mDate = TimeUtil.getYesterday(mDate);
                    initData();
                    return;
                }
                mData.clear();
                for (ResultItem item : itemList) {
                    mData.add(item);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                handler.removeCallbacksAndMessages(null);
                                handler.sendEmptyMessageDelayed(Messages.MSG_START_LOAD, UPDATE_TIME);
                                ResultItem item = mData.get(mData.size() - 1);
                                if(item.type.equals("福利")){
                                    ((MainActivity)getActivity()).setBackGround(item.url, TimeUtil.getDateString(today));
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
