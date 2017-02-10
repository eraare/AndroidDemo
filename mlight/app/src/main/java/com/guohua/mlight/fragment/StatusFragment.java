package com.guohua.mlight.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.communication.BLEConstant;

/**
 * @author Leo
 * @time 2016-01-09
 * @detail 显示所有的状态消息 此为状态模块
 */
public class StatusFragment extends Fragment {
    /**
     * 音例模式
     */
    private volatile static StatusFragment statusFragment = null;

    public static StatusFragment getInstance() {
        if (statusFragment == null) {
            synchronized (StatusFragment.class) {
                if (statusFragment == null) {
                    statusFragment = new StatusFragment();
                }
            }
        }
        return statusFragment;
    }

    private MainActivity mContext;
    private View rootView;
    private TextView temperatureStatus, electricityStatus;

    public StatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_status, container, false);
        init();
        return rootView;
    }

    /**
     * 初始化所有数据和控件等等
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        findViewsByIds();
    }

    /**
     * 得到所有的控件
     */
    private void findViewsByIds() {
        temperatureStatus = (TextView) rootView.findViewById(R.id.tv_temperature_status);
        electricityStatus = (TextView) rootView.findViewById(R.id.tv_electricity_status);
    }

    @Override
    public void onResume() {
        super.onResume();
        //此时注册广播接收器
        IntentFilter mFilter = new IntentFilter();
        mFilter.setPriority(Integer.MAX_VALUE);

        mFilter.addAction(BLEConstant.ACTION_RECEIVED_TEMPERATURE);
        mFilter.addAction(BLEConstant.ACTION_RECEIVED_VOLTAGE);

        mContext.registerReceiver(mBroadcastReceiver, mFilter);
    }

    /**
     * 广播接收器处理所有的状态广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//取得action
            String message = intent.getStringExtra(BLEConstant.EXTRA_RECEIVED_DATA).trim();//取得内容
            //如果没有传来信息则结束
            if (message == null) {
                return;
            }
            String[] results = message.split(":");
            //如果信息不符合协议则退出
            if (results == null || results.length < 2) {
                return;
            }
            if (TextUtils.equals(action, BLEConstant.ACTION_RECEIVED_TEMPERATURE)) {
                String temperature = results[1];
                temperatureStatus.setText(getString(R.string.status_temperature) + temperature + "(℃)");
            } else if (TextUtils.equals(action, BLEConstant.ACTION_RECEIVED_VOLTAGE)) {
                String voltage = results[1];
                if (voltage != null && voltage.length() > 0) {
                    int length = voltage.length();
                    if (length > 1)
                        voltage = voltage.substring(0, 1) + "." + voltage.substring(1, voltage.length());
                    electricityStatus.setText(getString(R.string.status_electricity) + voltage + "(V)");
                }
                //float voltage = Float.parseFloat(results[2]) / 1000;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        //此生命周期时取消广播接收器
        mContext.unregisterReceiver(mBroadcastReceiver);
    }
}
