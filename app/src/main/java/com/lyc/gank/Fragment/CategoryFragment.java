package com.lyc.gank.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lyc.gank.Adapter.CategoryPagerAdapter;
import com.lyc.gank.Bean.Messages;
import com.lyc.gank.MainActivity;
import com.lyc.gank.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

public class CategoryFragment extends Fragment{

    private List<BaseFragment> mFragmentList = new ArrayList<>();

    private BaseFragment fragmentNow;

    @BindView(R.id.fab_back_top)
    FloatingActionButton backTopFAB;

    @BindView(R.id.refresh_layout_category)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tool_bar_category)
    Toolbar toolbar;

    @BindView(R.id.pager_category)
    ViewPager pager;

    public Handler handler = new Handler(){
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


    /**
     * 标题
     */
    private final String [] titles = {
            "Android", "iOS", "前端", "拓展资源", "休息视频", "福利"
    };

    /**
     * 数据接口地址前缀
     */
    private final String [] addressPres = {
            "http://gank.io/api/data/Android/", //Android
            "http://gank.io/api/data/iOS/", //iOS
            "http://gank.io/api/data/前端/", //前端
            "http://gank.io/api/data/拓展资源/", //拓展资源
            "http://gank.io/api/data/%E4%BC%91%E6%81%AF%E8%A7%86%E9%A2%91/", //休息视频
    };

    public CategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).initToolbar(toolbar, "分类干货");

        init();

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                fragmentNow.setFABVisibility(backTopFAB.getVisibility());
            }

            @Override
            public void onPageSelected(int position) {
                int visibilityOld = fragmentNow.getFABVisibility();
                fragmentNow = mFragmentList.get(position);
                int visibilityNew = fragmentNow.getFABVisibility();
                if(visibilityNew != visibilityOld){
                    if(visibilityOld == View.INVISIBLE && visibilityNew == View.VISIBLE){
                        backTopFAB.show();
                    }else if(visibilityOld == View.VISIBLE && visibilityNew == View.INVISIBLE){
                        backTopFAB.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        CategoryPagerAdapter adapter = new CategoryPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList, titles);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(6);
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_category);
        tabLayout.setupWithViewPager(pager);

        backTopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentNow.backToTop();
                backTopFAB.setVisibility(View.INVISIBLE);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentNow.getItemsFromServer(BaseFragment.FLAG_REFRESH);
            }
        });

        return view;
    }



    /**
     * 初始化fragment list
     */
    private void init(){
        for (int i = 0; i < 5; i++){
            mFragmentList.add(new ArticleFragment());
        }
        fragmentNow = mFragmentList.get(0);
        bind();
    }

    private void bind(){
        if(mFragmentList == null || mFragmentList.size() == 0){
            return;
        }
        for(int i = 0; i < mFragmentList.size(); i++){
            mFragmentList.get(i)
                    .setAddressPre(addressPres[i])
                    .setHandler(handler);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
