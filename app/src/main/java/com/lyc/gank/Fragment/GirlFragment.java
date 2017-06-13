package com.lyc.gank.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lyc.gank.Adapter.BaseRecyclerAdapter;
import com.lyc.gank.Adapter.GirlRecyclerAdapter;
import com.lyc.gank.Bean.ImageUrls;
import com.lyc.gank.Bean.Messages;
import com.lyc.gank.MainActivity;
import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 972694341@qq.com on 2017/6/7.
 */

public class GirlFragment extends BaseFragment {

    private GirlRecyclerAdapter mAdapter;

    @BindView(R.id.fab_back_top)
    FloatingActionButton fab;

    @BindView(R.id.tool_bar_girls)
    Toolbar toolbar;

    @BindView(R.id.refresh_layout_girls)
    SwipeRefreshLayout refreshLayout;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Messages.MSG_LOAD_FINISHED:
                    if(refreshLayout != null){
                        refreshLayout.setRefreshing(false);
                    }
                    break;
                case Messages.MSG_LOAD_FAILED:
                    if(refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "加载失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Messages.MSG_START_LOAD:
                    refreshLayout.setRefreshing(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_girls, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_girls);
        mAdapter = new GirlRecyclerAdapter(mData, getContext());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
        setHandler(handler);
        addressPre = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";
        setOnFinishLoadListener(new OnFinishLoadListener() {
            @Override
            public void onFinish(int flag) {
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setOnShowPhotoListener(new GirlRecyclerAdapter.ShowPhotoListener() {
            @Override
            public void show(int pos) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra("urls", new ImageUrls(mData));
                intent.putExtra("page_now", pos);
                startActivity(intent);
            }
        });
        mAdapter.setOnItemLongClickedListener(new BaseRecyclerAdapter.OnItemLongClickedListener() {
            @Override
            public boolean onLongClick(int pos, View v) {
                itemNow = pos;
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessage(Messages.MSG_START_LOAD);
                getItemsFromServer(FLAG_REFRESH);
            }
        });
        ((MainActivity)getActivity()).initToolbar(toolbar, "妹子图");

        return view;
    }


}
