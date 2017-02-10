package com.guohua.mlight.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.guohua.mlight.R;

/**
 * Created by Leo on 2016/8/24.
 */
public class TabBarView extends LinearLayout {
    private TabView mainTab;
    private TabView sceneTab;
    private TabView centerTab;

    public TabBarView(Context context) {
        this(context, null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_bar, this, true);
        mainTab = (TabView) view.findViewById(R.id.tv_main_bar);
        sceneTab = (TabView) view.findViewById(R.id.tv_scene_bar);
        centerTab = (TabView) view.findViewById(R.id.tv_center_bar);
        mainTab.setOnClickListener(mOnClickListener);
        sceneTab.setOnClickListener(mOnClickListener);
        centerTab.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = 0;
            int id = v.getId();
            switch (id) {
                case R.id.tv_main_bar: {
                    index = 0;
                }
                break;
                case R.id.tv_scene_bar: {
                    index = 1;
                }
                break;
                case R.id.tv_center_bar: {
                    index = 2;
                }
                break;
                default:
                    break;
            }
            clickTab(index, v);
        }
    };

    public void clickTab(int index, View v) {
        if (index == 0) {
            mainTab.setSelected(true);
            sceneTab.setSelected(false);
            centerTab.setSelected(false);
        } else if (index == 1) {
            mainTab.setSelected(false);
            sceneTab.setSelected(true);
            centerTab.setSelected(false);
        } else if (index == 2) {
            mainTab.setSelected(false);
            sceneTab.setSelected(false);
            centerTab.setSelected(true);
        }
        if (mOnTabCheckedListener != null) {
            mOnTabCheckedListener.onTabChecked(index, v);
        }
    }

    private OnTabCheckedListener mOnTabCheckedListener;

    public void setOnTabCheckedListener(OnTabCheckedListener mOnTabCheckedListener) {
        this.mOnTabCheckedListener = mOnTabCheckedListener;
    }

    public interface OnTabCheckedListener {
        void onTabChecked(int index, View view);
    }
}
