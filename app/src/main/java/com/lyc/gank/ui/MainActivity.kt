package com.lyc.gank.ui

import android.os.Bundle
import com.lyc.gank.R
import com.lyc.gank.base.BaseActivity
import com.lyc.gank.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */

class MainActivity : BaseActivity() {
    companion object {
        const val FRAGMENT_HOME = 0
    }

    private var fragmentNow = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tb_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        val homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name) ?: HomeFragment()
        if(homeFragment.isAdded){
            supportFragmentManager.beginTransaction()
                    .show(homeFragment)
                    .commitAllowingStateLoss()
        }else{
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_main, homeFragment, HomeFragment::class.java.name)
                    .commitAllowingStateLoss()
        }
    }

    private fun switchFragment(id: Int){
        val fromClazz = fragmentClass(fragmentNow)
        val toClazz = fragmentClass(id)
        val ff = supportFragmentManager.findFragmentByTag(fromClazz.name) ?: fromClazz.newInstance()
        val tf = supportFragmentManager.findFragmentByTag(toClazz.name) ?: toClazz.newInstance()
        if(tf.isAdded){
            supportFragmentManager.beginTransaction()
                    .hide(ff)
                    .show(tf)
                    .commitAllowingStateLoss()
        }else{
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_main, tf, toClazz.name)
                    .hide(ff)
                    .show(tf)
                    .commitAllowingStateLoss()
        }
        fragmentNow = id
    }

    private fun fragmentClass(id: Int) = when(id){
        FRAGMENT_HOME -> HomeFragment::class.java
        else -> throw IllegalArgumentException("Unknown fragment")
    }
}