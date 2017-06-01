package com.lyc.gank.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lyc.gank.Fragment.BaseFragment;

import java.util.List;

/**
 * 主页fragment的adapter
 */

public class HomePagerAdapter extends FragmentPagerAdapter {

    List<BaseFragment> mFragmentList;
    String [] mTitles;

    public HomePagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList, String[] titles) {
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
