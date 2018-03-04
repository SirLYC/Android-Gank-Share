package com.lyc.gank.discover

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment
import com.lyc.gank.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_discover.*

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class DiscoverFragment : BaseFragment(), View.OnClickListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_search.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_search -> startActivity(Intent(activity(), SearchActivity::class.java))
        }
    }
}