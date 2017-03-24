package com.guohua.mlight.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.model.impl.RxLightService;
import com.guohua.mlight.view.widget.TimerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class HomeFragment extends BaseFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private volatile static HomeFragment mainFragment = null;

    public static HomeFragment newInstance() {
        if (mainFragment == null) {
            synchronized (HomeFragment.class) {
                if (mainFragment == null) {
                    mainFragment = new HomeFragment();
                }
            }
        }
        return mainFragment;
    }

    /**
     * 布局中的控件
     */
    @BindView(R.id.iv_switch_home)
    ImageView mSwitchView;//开关
    @BindView(R.id.sb_value_home)
    SeekBar mValueView;//亮度调节
    @BindView(R.id.sb_saturation_home)
    SeekBar mSaturationView;//饱和度
    @BindView(R.id.tv_timer_five_home)
    TimerView mFiveView;// 5分钟
    @BindView(R.id.tv_timer_fifteen_home)
    TimerView mFifteenView;// 15分钟
    @BindView(R.id.tv_timer_thirty_home)
    TimerView mThirtyView;//30分钟
    @BindView(R.id.tv_timer_sixty_home)
    TimerView mSixyView;//60分钟
    @BindView(R.id.ll_top_home)
    LinearLayout mTopView; /*顶部视图*/
    @BindView(R.id.ll_bottom_home)
    LinearLayout mBottomView; /*底部视图*/
    @BindView(R.id.tv_switch_tip_home)
    TextView mTipView; /*开关提示*/

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        initial();
    }

    /*初始化操作*/
    private void initial() {
        initView();
        changeViewState(AppContext.getInstance().isLightOn);
    }

    private void initView() {
        float saturation = AppContext.getInstance().currentHSV[1] * 100;
        float value = AppContext.getInstance().currentHSV[2] * 100;

        mSaturationView.setProgress((int) saturation);
        mValueView.setProgress((int) value);

        mValueView.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mSaturationView.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        if (AppContext.getInstance().isLightOn) {
            mSwitchView.setImageResource(R.drawable.icon_light_on);
        } else {
            mSwitchView.setImageResource(R.drawable.icon_light_off);
        }
    }


    /*滑块滑动监听事件*/
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            changeValueOrSaturation();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 改变亮度和饱和度
     */
    private void changeValueOrSaturation() {
        AppContext.getInstance().currentHSV[1] = mSaturationView.getProgress() / 100F;
        AppContext.getInstance().currentHSV[2] = mValueView.getProgress() / 100F;
        int color = Color.HSVToColor(AppContext.getInstance().currentAlpha, AppContext.getInstance().currentHSV);
        RxLightService.getInstance().adjustColor(color);
    }

    /**
     * 相关的单击事件监听器
     */
    @OnClick({R.id.iv_switch_home, R.id.tv_timer_five_home, R.id.tv_timer_fifteen_home,
            R.id.tv_timer_thirty_home, R.id.tv_timer_sixty_home})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_switch_home: {
                switchLight();
            }
            break;
            case R.id.tv_timer_five_home: {
                handleTimerView(mFiveView);
            }
            break;
            case R.id.tv_timer_fifteen_home: {
                handleTimerView(mFifteenView);
            }
            break;
            case R.id.tv_timer_thirty_home: {
                handleTimerView(mThirtyView);
            }
            break;
            case R.id.tv_timer_sixty_home: {
                handleTimerView(mSixyView);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 开启关闭定时器
     *
     * @param timerView
     */
    private void handleTimerView(TimerView timerView) {
        /*获取状态和数值*/
        boolean checked = timerView.isChecked();
        String value = timerView.getValue().trim();
        int time = Integer.parseInt(value) * 60;

        /*初始话状态*/
        mFiveView.uncheck();
        mFifteenView.uncheck();
        mThirtyView.uncheck();
        mSixyView.uncheck();

        /*开启或取消定时关灯*/
        if (checked) {
            RxLightService.getInstance().delayOff(-1);
            mContext.toast("已取消延时关灯");
        } else {
            if (TextUtils.equals(value, "5")) {
                mFiveView.check();
            } else if (TextUtils.equals(value, "15")) {
                mFifteenView.check();
            } else if (TextUtils.equals(value, "30")) {
                mThirtyView.check();
            } else if (TextUtils.equals(value, "60")) {
                mSixyView.check();
            }
            RxLightService.getInstance().delayOff(time);
            mContext.toast(value + "分钟后关灯");
        }
    }

    /**
     * 开关灯
     */
    private void switchLight() {
        if (AppContext.getInstance().isLightOn) {
            RxLightService.getInstance().turnOff();
            AppContext.getInstance().isLightOn = false;
            mSwitchView.setImageResource(R.drawable.icon_light_off);
        } else {
            RxLightService.getInstance().turnOn();
            AppContext.getInstance().isLightOn = true;
            mSwitchView.setImageResource(R.drawable.icon_light_on);
        }
        changeViewState(AppContext.getInstance().isLightOn);
    }

    /**
     * 根据灯泡开关状态进行控件状态的改变
     *
     * @param state
     */
    private void changeViewState(boolean state) {
        if (state) {
            mTopView.setVisibility(View.VISIBLE);
            mBottomView.setVisibility(View.VISIBLE);
            mTipView.setVisibility(View.GONE);
        } else {
            mTopView.setVisibility(View.GONE);
            mBottomView.setVisibility(View.GONE);
            mTipView.setVisibility(View.VISIBLE);
        }
    }
}
