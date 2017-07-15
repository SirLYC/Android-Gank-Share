package com.lyc.gank.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyc.gank.MainActivity;
import com.lyc.gank.R;
import com.lyc.gank.adapter.CategoryPagerAdapter;
import com.lyc.gank.fragment.base.BaseFragment;
import com.lyc.gank.fragment.base.GankDataFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CategoryFragment extends BaseFragment {

    private List<GankDataFragment> mFragmentList = new ArrayList<>();

    private GankDataFragment fragmentNow;

    private CategoryPagerAdapter adapter;

    @BindView(R.id.fab_back_top)
    FloatingActionButton backTopFAB;

    @BindView(R.id.refresh_layout_category)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tool_bar_category)
    Toolbar toolbar;

    @BindView(R.id.pager_category)
    ViewPager pager;

    @BindView(R.id.tab_category)
    TabLayout tabLayout;


    /**
     * 标题/类型
     */
    private final String [] titles = {
            "Android", "iOS", "前端", "拓展资源", "休息视频"
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        init();
        ((MainActivity)getActivity()).initToolbar(toolbar, "分类干货");
        setViewPager();
        setRefreshLayout();
        return view;
    }

    private void setViewPager() {
        adapter = new CategoryPagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList, titles);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(pager);
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
    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentNow.loadData(GankDataFragment.FLAG_REFRESH);
            }
        });
    }

    @OnClick(R.id.fab_back_top)
    public void backTop(){
        fragmentNow.backToTop();
        backTopFAB.setVisibility(View.INVISIBLE);
    }


    /**
     * 初始化fragment list
     */
    private void init(){
        if(mFragmentList.size() == 0) {
            for (String title: titles) {
                mFragmentList.add(ArticleFragment.getInstance(title));
            }
        }
        if(adapter == null) {
            fragmentNow = mFragmentList.get(0);
        }else {
            fragmentNow = (GankDataFragment) adapter.getItem(pager.getCurrentItem());
        }
        for (GankDataFragment fragment : mFragmentList) {
            fragment.setOnLoadListener(new GankDataFragment.OnLoadListener() {
                @Override
                public void onStart() {
                    refreshLayout.setRefreshing(true);
                }

                @Override
                public void onFinish() {
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailed() {
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public void refresh(){
        for (GankDataFragment gankDataFragment : mFragmentList) {
            if(!gankDataFragment.isFirstVisibleToUser() && gankDataFragment.isNeedRefresh()){
                gankDataFragment.refresh();
            }
        }
    }
}
