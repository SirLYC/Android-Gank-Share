package com.lyc.gank.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * 一个简单的recyclerView的item分割线
 * 支持横向纵向，支持自定义分割线（Drawable）
 * 默认使用listView的分割线，默认分割线宽度为1dip
 * 可直接复制使用
 */

public class ItemDecoration extends RecyclerView.ItemDecoration{
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private final int HORIZONTAL = RecyclerView.HORIZONTAL;

    private final int VERTICAL = RecyclerView.VERTICAL;

    private int mOrientation;

    private int mDividerHeight = 1;

    private int mDividerWidth = 1;

    private Drawable mDivider;

    private String TAG = "itemDecoration";

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        }else {
            outRect.set(0, 0, mDividerWidth, 0);
        }
    }

    public ItemDecoration(Context context, int orientation) {
        final TypedArray arr = context.obtainStyledAttributes(ATTRS);
        mDivider = arr.getDrawable(0);
        arr.recycle();
        setOrientation(orientation);
    }

    private void setOrientation(int orientation){
        if(mOrientation != HORIZONTAL && mOrientation != VERTICAL){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    public void setDivider(Drawable dividerDrawable){
        if(dividerDrawable != null){
            mDivider = dividerDrawable;
        }else {
            Log.w(TAG, "invalid divider drawable");
        }
    }

    public void setDividerHeight(int height){
        if(height > 0) {
            mDividerHeight = height;
        }else {
            Log.w(TAG, "invalid divider height");
        }
    }

    public void setDividerWidth(int width){
        if(width > 0){
            mDividerWidth = width;
        }else {
            Log.w(TAG, "invalid divider width");
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == HORIZONTAL) {
            drawHorizontal(c, parent);
        }else {
            drawVertical(c, parent);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent){
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams  =(RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerWidth;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
