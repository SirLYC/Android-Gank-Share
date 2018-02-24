package com.lyc.gank.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by Liu Yuchuan on 2018/2/24.
 */

public class ScrollWebView extends WebView {
    private OnScrollListener onScrollListener;

    private int mLastX, mLastY;
    private boolean mOnScroll;

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mOnScroll = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnScroll && onScrollListener != null) {
                    onScrollListener.onScroll(x - mLastX, y - mLastY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mOnScroll = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(int dx, int dy);
    }
}
