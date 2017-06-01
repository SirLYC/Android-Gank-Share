package com.lyc.gank.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.Adapter.BaseRecyclerAdapter;
import com.lyc.gank.Adapter.GirlRecyclerAdapter;
import com.lyc.gank.Bean.ImageUrls;
import com.lyc.gank.PhotoActivity;
import com.lyc.gank.R;

/**
 * 展示妹子图的fragment
 */
public class GirlsFragment extends BaseFragment {

    private GirlRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gils, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.girls_recycler);
        mAdapter = new GirlRecyclerAdapter(mData, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
                collect(pos, v);
                return true;
            }
        });
        return view;
    }
}
