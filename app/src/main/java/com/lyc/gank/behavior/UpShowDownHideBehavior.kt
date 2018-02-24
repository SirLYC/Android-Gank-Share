package com.lyc.gank.behavior

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */
class UpShowDownHideBehavior : CoordinatorLayout.Behavior<FloatingActionButton>() {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        return dependency is RecyclerView
    }
}