package com.lyc.gank.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Gravity
import android.view.MenuItem
import com.lyc.gank.R
import com.lyc.gank.base.BaseActivity
import com.lyc.gank.ui.category.CategoryFragment
import com.lyc.gank.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val FRAGMENT_HOME = 0
        const val FRAGMENT_CATEGORY = 1
    }

    private var fragmentNow = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tb_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        nav_main.setNavigationItemSelectedListener(this)

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
        fragmentNow = FRAGMENT_HOME
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
        FRAGMENT_CATEGORY -> CategoryFragment::class.java
        else -> throw IllegalArgumentException("Unknown fragment")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                openOrCloseDrawer()
                true
            }
            else -> false
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {
                switchFragment(FRAGMENT_HOME)
            }
            R.id.menu_category -> {
                switchFragment(FRAGMENT_CATEGORY)
            }
        }

        openOrCloseDrawer()
        return true
    }

    private fun openOrCloseDrawer() {
        if (dl_main.isDrawerOpen(Gravity.START)) {
            dl_main.closeDrawer(Gravity.START)
        } else {
            dl_main.openDrawer(Gravity.START)
        }
    }
}