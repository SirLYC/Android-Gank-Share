package com.lyc.gank

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.lyc.gank.base.BaseActivity
import com.lyc.gank.category.CategoryFragment
import com.lyc.gank.discover.DiscoverFragment
import com.lyc.gank.home.HomeFragment
import com.lyc.gank.user.UserFragment
import com.lyc.gank.utils.logi
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

        private const val KEY_FRAGMENT_NOW = "KEY_FRAGMENT_NOW"

        private const val TAG = "MainActivity"
    }

    private var tmpToExit = false
    private var fragmentNow = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(tb_main)
        bnv_main.setOnNavigationItemSelectedListener(this)

        supportFragmentManager.fragments.forEach {
            supportFragmentManager.beginTransaction()
                    .hide(it)
                    .commitAllowingStateLoss()
        }

        if (savedInstanceState != null) {
            logi(TAG, savedInstanceState.toString())
        }

        switchFragment(savedInstanceState?.getInt(KEY_FRAGMENT_NOW, FRAGMENT_HOME) ?: FRAGMENT_HOME)
    }

    private fun switchFragment(id: Int) {
        val toClazz = fragmentClass(id)
        val tf = supportFragmentManager.findFragmentByTag(toClazz.name) ?: toClazz.newInstance()
        supportFragmentManager.beginTransaction()
                .apply {
                    if (!tf.isAdded) {
                        add(R.id.container_main, tf, toClazz.name)
                    }
                    supportFragmentManager.fragments.forEach {
                        if (it != tf && !it.isHidden) {
                            hide(it)
                        }
                    }
                }
                .show(tf)
                .commitAllowingStateLoss()
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

    private fun fragmentClass(id: Int) = when (id) {
        FRAGMENT_HOME -> HomeFragment::class.java
        FRAGMENT_CATEGORY -> CategoryFragment::class.java
        FRAGMENT_DISCOVER -> DiscoverFragment::class.java
        FRAGMENT_USER -> UserFragment::class.java
        else -> throw IllegalArgumentException("Unknown fragment")
    }

    private fun menuItem(id: Int) = when (id) {
        FRAGMENT_HOME -> R.id.menu_home
        FRAGMENT_CATEGORY -> R.id.menu_category
        FRAGMENT_DISCOVER -> R.id.menu_discover
        FRAGMENT_USER -> R.id.menu_me
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(KEY_FRAGMENT_NOW, fragmentNow)
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
