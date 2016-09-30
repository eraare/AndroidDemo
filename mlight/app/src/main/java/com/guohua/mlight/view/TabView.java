package com.guohua.mlight.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guohua.mlight.R;

/**
 * @author Leo
 *         #time 2016-08-23
 *         #detail 自定义图像文字组合控件
 */
public class TabView extends RelativeLayout {
    private TextView titleView;
    private ImageView iconView;

    public String titleValue;// 标题
    public int titleColor; // 标题默认颜色
    public int selectTitleColor; // 标题选择颜色
    public int iconValue; // 默认图标
    public int selectIcon; // 选择图标
    public boolean isSelected; // 是否被选择

    public TabView(Context context) {
//        super(context);
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
//        super(context, attrs);
        this(context, attrs, 0);
    }

    // 核心构造函数
    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 加载属性
        initValues(context, attrs, defStyleAttr);
        // 加载布局文件
        initViews(context);
    }

    /**
     * 进行一些初始化
     */
    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab, this, true);
        titleView = (TextView) view.findViewById(R.id.tv_title_tab);
        iconView = (ImageView) view.findViewById(R.id.iv_icon_tab);
        titleView.setText(titleValue);
        if (isSelected) {
            titleView.setTextColor(selectTitleColor);
            iconView.setImageResource(selectIcon);
        } else {
            titleView.setTextColor(titleColor);
            iconView.setImageResource(iconValue);
        }
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initValues(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabView, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.TabView_titleValue:
                    titleValue = ta.getString(i);
                    break;
                case R.styleable.TabView_titleColor:
                    titleColor = ta.getColor(i, Color.BLACK);
                    break;
                case R.styleable.TabView_selectTitleColor:
                    selectTitleColor = ta.getColor(i, Color.RED);
                    break;
                case R.styleable.TabView_iconValue:
                    iconValue = ta.getResourceId(i, R.drawable.icon_home_normal);
                    break;
                case R.styleable.TabView_selectIcon:
                    selectIcon = ta.getResourceId(i, R.drawable.icon_home_checked);
                    break;
                case R.styleable.TabView_isSelected:
                    isSelected = ta.getBoolean(i, false);
                    break;
                default:
                    break;
            }
        }
        // 释放资源
        ta.recycle();
    }

    // 设置标题
    public void setTitle(String titleValue) {
        this.titleValue = titleValue;
    }

    // 设置标题颜色
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    // 设置标题选择着色
    public void setSelectTitleColor(int selectTitleColor) {
        this.selectTitleColor = selectTitleColor;
    }

    // 设置图标
    public void setIcon(int iconValue) {
        this.iconValue = iconValue;
    }

    // 设置选择图标
    public void setSelectIcon(int selectIcon) {
        this.selectIcon = selectIcon;
    }

    // 设置选择状态
    public void setSelected(boolean isSelected) {
        if (this.isSelected != isSelected) {
            this.isSelected = isSelected;
            if (isSelected) {
                titleView.setTextColor(selectTitleColor);
                iconView.setImageResource(selectIcon);
            } else {
                titleView.setTextColor(titleColor);
                iconView.setImageResource(iconValue);
            }
        }
        //postInvalidate();
    }

    // 判断当前状态
    public boolean isSelected() {
        return this.isSelected;
    }

    public String getTitleValue() {
        return this.titleValue;
    }
}
