package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.model.impl.RxLightService;
import com.guohua.mlight.view.widget.holocolorpicker.ColorPicker;
import com.guohua.mlight.view.widget.holocolorpicker.SaturationBar;
import com.guohua.mlight.view.widget.holocolorpicker.ValueBar;

import butterknife.BindView;

/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class PalletActivity extends BaseActivity {
    /*绑定控件*/
    @BindView(R.id.cp_picker_pallet)
    ColorPicker mPickerView; /*颜色采集器*/
    @BindView(R.id.sb_saturation_pallet)
    SaturationBar mSaturationView; /*色彩饱和度*/
    @BindView(R.id.vb_value_pallet)
    ValueBar mValueView; /*色彩亮度*/

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pallet3;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        setToolbarTitle(R.string.scene_color_pallet);
        initial();
    }

    /**
     * 初始化数据和控件
     */
    private void initial() {
        setupColorPicker(); /*设置调色相关*/
    }

    /**
     * 配置调色环
     */
    private void setupColorPicker() {
       /* *//*初始化颜色*//*
        mSaturationView.setSaturation(AppContext.getInstance().currentHSV[1]);
        mValueView.setValue(AppContext.getInstance().currentHSV[2]);
        *//*设置中心圆环的颜色*//*
        int color = Color.HSVToColor(AppContext.getInstance().currentHSV);
        mPickerView.setNewCenterColor(color);
        mPickerView.setOldCenterColor(color);*/

        mSaturationView.setOnSaturationChangedListener(mOnSaturationChangedListener);
        mValueView.setOnValueChangedListener(mOnValueChangedListener);
        mPickerView.addSaturationBar(mSaturationView);
        mPickerView.addValueBar(mValueView);
        mPickerView.setShowOldCenterColor(true);
        mPickerView.setOnColorChangedListener(mOnColorChangedListener);
    }

    private ColorPicker.OnColorChangedListener mOnColorChangedListener = new ColorPicker.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            /*把颜色发送出去并设置到缓存*/
            mPickerView.setOldCenterColor(color);
            RxLightService.getInstance().adjustColor(color);
            Color.colorToHSV(color, AppContext.getInstance().currentHSV);
        }
    };

    /*SaturationBar监听器*/
    private SaturationBar.OnSaturationChangedListener mOnSaturationChangedListener = new SaturationBar.OnSaturationChangedListener() {
        @Override
        public void onSaturationChanged(int saturation) {
            AppContext.getInstance().currentHSV[1] = saturation;
            int color = Color.HSVToColor(AppContext.getInstance().currentHSV);
            RxLightService.getInstance().adjustColor(color);
        }
    };

    /*ValueBar监听器*/
    private ValueBar.OnValueChangedListener mOnValueChangedListener = new ValueBar.OnValueChangedListener() {
        @Override
        public void onValueChanged(int value) {
            AppContext.getInstance().currentHSV[2] = value;
            int color = Color.HSVToColor(AppContext.getInstance().currentHSV);
            RxLightService.getInstance().adjustColor(color);
        }
    };

}
