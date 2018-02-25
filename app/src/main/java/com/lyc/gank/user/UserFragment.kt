package com.lyc.gank.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyc.gank.R
import com.lyc.gank.base.BaseFragment

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class UserFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }
}