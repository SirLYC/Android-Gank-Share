package com.lyc.gank.ui.category

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.data.types
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_category.*

/**
 * Created by Liu Yuchuan on 2018/2/23.
 */
class CategoryFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vp_category.adapter = CategoryPagerAdapter(childFragmentManager)
        vp_category.offscreenPageLimit = 8
        tab_category.setupWithViewPager(vp_category)
    }

    private class CategoryPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragments = arrayOfNulls<SingleContentFragment>(8)

        override fun getItem(position: Int): Fragment {
            return fragments[position] ?: SingleContentFragment.instance(types[position])
        }

        override fun getCount() = fragments.size

        override fun getPageTitle(position: Int) = types[position]
    }
}