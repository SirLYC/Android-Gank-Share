package com.lyc.gank.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 处理与viewpager的滑动冲突的SwipeRefreshLayout
 */

public class MyRefreshLayout extends SwipeRefreshLayout {
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean isDragged;

    public MyRefreshLayout(Context context) {
        super(context);
    }

    public MyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = ev.getX();
                endY = ev.getY();
                float dx = Math.abs(startX - endX);
                float dy = endY - startY;
                //判断是否拦截该event
                if(dy > 5 * dx){
                    isDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragged = false;
                break;
        }
        if(isDragged){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }
}
