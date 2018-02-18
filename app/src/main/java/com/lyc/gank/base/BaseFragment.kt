package com.lyc.gank.base

import android.content.Context
import android.support.v4.app.Fragment

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
abstract class BaseFragment: Fragment(){
    private var mActivity: BaseActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    fun activity() = mActivity
}