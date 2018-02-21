package com.lyc.gank.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.util.ListUpdateCallback
import me.drakeet.multitype.MultiTypeAdapter

/**
 * Created by Liu Yuchuan on 2018/2/17.
 */
class ReactiveAdapter(
        private val dataList: ObservableList<Any>
): MultiTypeAdapter(dataList), ListUpdateCallback{

    override fun onChanged(position: Int, count: Int, payload: Any?) = notifyItemChanged(position)
    override fun onMoved(fromPosition: Int, toPosition: Int) = notifyItemMoved(fromPosition, toPosition)
    override fun onInserted(position: Int, count: Int) = notifyItemRangeInserted(position, count)
    override fun onRemoved(position: Int, count: Int) = notifyItemRangeRemoved(position, count)

    fun observe(activity: Activity) {
        activity.application.registerActivityLifecycleCallbacks(ReactiveActivityRegistry(activity))
        dataList.addCallback(this)
    }

    fun observe(fragment: Fragment) {
        fragment.fragmentManager!!.registerFragmentLifecycleCallbacks(ReactiveFragmentRegistry(fragment), false)
        dataList.addCallback(this)
    }


    inner class ReactiveActivityRegistry(
            private val activity: Activity
    ) : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (this.activity == activity) {
                dataList.removeCallback(this@ReactiveAdapter)
                activity.application.unregisterActivityLifecycleCallbacks(this)
            }
        }
    }

    inner class ReactiveFragmentRegistry(
            private val fragment: Fragment
    ) : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            if (fragment == f) {
                dataList.removeCallback(this@ReactiveAdapter)
                fm.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }
}