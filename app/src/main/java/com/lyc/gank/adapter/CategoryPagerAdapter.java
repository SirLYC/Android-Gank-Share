package com.lyc.gank.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lyc.gank.fragment.GankDataFragment;

import java.util.List;

/**
 * 主页fragment的adapter
 */

public class CategoryPagerAdapter extends FragmentPagerAdapter {

    List<GankDataFragment> mFragmentList;
    String [] mTitles;

    public CategoryPagerAdapter(FragmentManager fm, List<GankDataFragment> fragmentList, String[] titles) {
        super(fm);
        mFragmentList = fragmentList;
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
