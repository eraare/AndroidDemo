package com.guohua.mlight.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guohua.mlight.R;

/**
 * @author Leo
 *         #time 2016-09-01
 *         #detail 延时关灯的自定义组合控件
 */
public class TimerView extends LinearLayout {
    private LinearLayout timer;
    private TextView value; // 值
    private TextView unit; // 单位

    private String valueString; // 刻度值
    private String unitString; //单位值
    private boolean checked;//是否选择

    // 构造函数统一调到最多参数的那个
    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValues(context, attrs, defStyleAttr); //读取属性值
        initViews(context); // 加载视图布局文件初始化控件
    }

    /**
     * 加载属性文件读取属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initValues(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimerView, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.TimerView_value:
                    valueString = ta.getString(i);
                    break;
                case R.styleable.TimerView_unit:
                    unitString = ta.getString(i);
                    break;
                case R.styleable.TimerView_selected:
                    checked = ta.getBoolean(i, false);
                    break;
                default:
                    break;
            }
        }
        ta.recycle();
    }

    /**
     * 加载布局文件初始化控件
     *
     * @param context
     */
    private void initViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_timer, this, true);
        timer = (LinearLayout) view.findViewById(R.id.ll_timer_timer);
        value = (TextView) view.findViewById(R.id.tv_value_timer);
        unit = (TextView) view.findViewById(R.id.tv_unit_timer);
        value.setText(valueString);
        unit.setText(unitString);
        if (checked) {
            check();
        } else {
            uncheck();
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        value.setTextColor(color);
        unit.setTextColor(color);
    }

    /**
     * 选择
     */
    public void check() {
        checked = true;
        setTextColor(getResources().getColor(R.color.white));
        timer.setBackgroundResource(R.drawable.selector_timer_selected);
    }

    /**
     * 取消
     */
    public void uncheck() {
        checked = false;
        setTextColor(getResources().getColor(R.color.main));
        timer.setBackgroundResource(R.drawable.selector_timer_normal);
    }

    public boolean isChecked() {
        return checked;
    }

    public String getValue() {
        return valueString.trim();
    }
}
