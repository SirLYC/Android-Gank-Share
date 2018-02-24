package com.lyc.gank.ui

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.lyc.gank.R
import com.lyc.gank.base.BaseActivity
import com.lyc.gank.ui.category.CategoryFragment
import com.lyc.gank.ui.discover.DiscoverFragment
import com.lyc.gank.ui.home.HomeFragment
import com.lyc.gank.ui.user.UserFragment
import com.lyc.gank.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val FRAGMENT_HOME = 0
        private const val FRAGMENT_CATEGORY = 1
        private const val FRAGMENT_DISCOVER = 2
        private const val FRAGMENT_USER = 3
    }

    private var tmpToExit = false
    private var fragmentNow = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tb_main)
        bnv_main.setOnNavigationItemSelectedListener(this)

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
        changeActionBar(FRAGMENT_HOME)
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
        changeActionBar(id)
        fragmentNow = id
    }

    private fun changeActionBar(id: Int) {
        when (id) {
            FRAGMENT_HOME -> {
                supportActionBar?.show()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
                supportActionBar?.setTitle(R.string.title_home)
            }
            FRAGMENT_CATEGORY -> {
                supportActionBar?.hide()
            }
            FRAGMENT_DISCOVER -> {
                supportActionBar?.hide()
            }

            FRAGMENT_USER -> {
                supportActionBar?.show()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_person)
                supportActionBar?.setTitle(R.string.title_me)
            }
        }
    }

    private fun fragmentClass(id: Int) = when(id){
        FRAGMENT_HOME -> HomeFragment::class.java
        FRAGMENT_CATEGORY -> CategoryFragment::class.java
        FRAGMENT_DISCOVER -> DiscoverFragment::class.java
        FRAGMENT_USER -> UserFragment::class.java
        else -> throw IllegalArgumentException("Unknown fragment")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home -> {
                switchFragment(FRAGMENT_HOME)
                true
            }
            R.id.menu_category -> {
                switchFragment(FRAGMENT_CATEGORY)
                true
            }
            R.id.menu_discover -> {
                switchFragment(FRAGMENT_DISCOVER)
                true
            }
            R.id.menu_me -> {
                switchFragment(FRAGMENT_USER)
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        if (tmpToExit) {
            super.onBackPressed()
        } else {
            tmpToExit = true
            Handler().postDelayed({ tmpToExit = false }, 3000)
            toast(R.string.tip_exit)
        }
    }
}