package com.guohua.mlight.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Leo
 *         #time 2016-08-26
 *         #detail item的间距设置
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {
    // 系统的样式
    private static final int[] attrs = new int[]{android.R.attr.listDivider};
    private Drawable mDivider; // 分割线
    private int orientation; // 方向

    public RecyclerViewDivider(Context context, int orientation) {
        TypedArray ta = context.obtainStyledAttributes(attrs);
        mDivider = ta.getDrawable(0);
        ta.recycle();
        this.orientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontalDecoration(c, parent, state);
        drawVerticalDecoration(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (OrientationHelper.HORIZONTAL == orientation) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }

    // 水平修饰
    private void drawHorizontalDecoration(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    // 垂直修饰
    private void drawVerticalDecoration(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}