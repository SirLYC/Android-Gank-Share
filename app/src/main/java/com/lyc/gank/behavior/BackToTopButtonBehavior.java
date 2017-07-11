package com.lyc.gank.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 浮动按钮的Behavior
 */

public class BackToTopButtonBehavior extends FloatingActionButton.Behavior {
    public BackToTopButtonBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                       FloatingActionButton child, View directTargetChild,
                                       View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed) {
        if(dyConsumed > 0 && child.getVisibility() == View.INVISIBLE){
            child.show();
        }else if(dyConsumed < 0){
            //官方的hide方法会设置为gone,而这个方法在view为gone时不会调用
            child.setVisibility(View.INVISIBLE);
        }
    }
}
