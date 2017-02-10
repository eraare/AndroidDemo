package com.guohua.mlight.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guohua.mlight.R;

/**
 * @file TitleView.java
 * @author Leo
 * @version 1
 * @detail 自定义的标题控件
 * @since 2017/1/4 17:05
 */

/**
 * 文件名：TitleView.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2017/1/4 17:05
 * 描  述：自定义的标题控件
 */
public class TitleView extends RelativeLayout {
    private LinearLayout leftView; // 标题控件的左边视图
    private TextView titleView; // 标题视图
    private LinearLayout rightView; // 右边视图

    private TextView leftTitle;//左视图标题
    private ImageView leftIcon;//左视图ICON
    private TextView rightTitle;//右视图标题
    private ImageView rightIcon;//右视图ICON

    /*以下为属性*/
    // middle
    private String titleValue;
    // left
    private String leftTitleValue;
    private int leftIconValue;
    // right
    private String rightTitleValue;
    private int rightIconValue;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs, defStyleAttr);
        initViews(context);
        initValues();
    }

    /**
     * 加载属性文件读取属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = ta.getIndex(i);
            switch (index) {
                case R.styleable.TitleView_titleText:
                    titleValue = ta.getString(i);
                    break;
                case R.styleable.TitleView_leftTitle:
                    leftTitleValue = ta.getString(i);
                    break;
                case R.styleable.TitleView_leftIcon:
                    leftIconValue = ta.getResourceId(i, R.drawable.icon_back);
                    break;
                case R.styleable.TitleView_rightTitle:
                    rightTitleValue = ta.getString(i);
                    break;
                case R.styleable.TitleView_rightIcon:
                    rightIconValue = ta.getResourceId(i, R.drawable.icon_back);
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
        View view = LayoutInflater.from(context).inflate(R.layout.view_title, this, true);
        titleView = (TextView) view.findViewById(R.id.tv_title_title);
        // 左边
        leftView = (LinearLayout) view.findViewById(R.id.ll_left_title);
        leftTitle = (TextView) view.findViewById(R.id.tv_left_title);
        leftIcon = (ImageView) view.findViewById(R.id.iv_left_title);
        // 右边
        rightView = (LinearLayout) view.findViewById(R.id.ll_right_title);
        rightTitle = (TextView) view.findViewById(R.id.tv_right_title);
        rightIcon = (ImageView) view.findViewById(R.id.iv_right_title);

        leftView.setOnClickListener(mOnClickListener);
        rightView.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ll_left_title: {
                    if (mOnLeftClickListener != null) {
                        mOnLeftClickListener.onLeftClick(v);
                    }else {

                    }
                }
                break;
                case R.id.ll_right_title: {
                    if (mOnRightClickListener != null) {
                        mOnRightClickListener.onRightClick(v);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化控件值
     */
    private void initValues() {
        // 标题赋值
        if (titleValue == null) {
            titleValue = "This is Title";
        }
        titleView.setText(titleValue);

        //左边
        if (leftTitleValue == null) {
            leftTitle.setVisibility(View.GONE);
        } else {
            leftTitle.setVisibility(View.VISIBLE);
            leftTitle.setText(leftTitleValue);
        }
        if (leftIconValue == 0) {
            leftIcon.setVisibility(View.GONE);
        } else {
            leftIcon.setVisibility(View.VISIBLE);
            leftIcon.setImageResource(leftIconValue);
        }

        //右边
        if (rightTitleValue == null) {
            rightTitle.setVisibility(View.GONE);
        } else {
            rightTitle.setVisibility(VISIBLE);
            rightTitle.setText(rightTitleValue);
        }
        if (rightIconValue == 0) {
            rightIcon.setVisibility(View.GONE);
        } else {
            rightIcon.setVisibility(VISIBLE);
            rightIcon.setImageResource(rightIconValue);
        }
    }

    /**
     * 事件接口
     */
    private OnLeftClickListener mOnLeftClickListener;
    private OnRightClickListener mOnRightClickListener;

    public interface OnLeftClickListener {
        void onLeftClick(View v);
    }

    public interface OnRightClickListener {
        void onRightClick(View v);
    }

    /**
     * 绑定左边的点击事件
     *
     * @param mOnLeftClickListener
     */
    public void setOnLeftClickListener(OnLeftClickListener mOnLeftClickListener) {
        this.mOnLeftClickListener = mOnLeftClickListener;
    }

    /**
     * 绑定右边的点击事件
     *
     * @param mOnRightClickListener
     */
    public void setOnRightClickListener(OnRightClickListener mOnRightClickListener) {
        this.mOnRightClickListener = mOnRightClickListener;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.titleView.setText(title);
    }

    /**
     * 隐藏或显示左边
     *
     * @param left
     */
    public void hiddenLeft(boolean left) {
        if (left) {
            leftView.setVisibility(View.INVISIBLE);
        } else {
            leftView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏或显示右边
     *
     * @param right
     */
    public void hiddenRight(boolean right) {
        if (right) {
            rightView.setVisibility(View.INVISIBLE);
        } else {
            rightView.setVisibility(View.VISIBLE);
        }
    }

    public void setLeftTitle(String leftText) {
        this.leftTitle.setText(leftText);
        this.leftTitleValue = leftText;
    }

    public void setRightTitle(String rightTitle) {
        this.rightTitle.setText(rightTitle);
        this.rightTitleValue = rightTitle;
    }
}
