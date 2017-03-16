package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.view.adapter.DeviceAdapter;

import butterknife.BindView;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-15
 * 设备管理界面
 */
public class DeviceFragment extends BaseFragment {
    private volatile static DeviceFragment deviceFragment = null;

    public static DeviceFragment getInstance() {
        if (deviceFragment == null) {
            synchronized (DeviceFragment.class) {
                if (deviceFragment == null) {
                    deviceFragment = new DeviceFragment();
                }
            }
        }
        return deviceFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device;
    }

    @BindView(R.id.srl_refresh_device)
    SwipeRefreshLayout mRefreshView;
    @BindView(R.id.rv_device_device)
    RecyclerView mDeviceView;

    private DeviceAdapter mDeviceAdapter;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        setupRefreshView(); /*配置刷新控件*/
        setupDeviceView(); /*配置设备显示控件*/
        loadDevice();
    }

    /**
     * 下拉刷新控件配置
     */
    private void setupRefreshView() {
        mRefreshView.setOnRefreshListener(mOnRefreshListener);
    }

    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

        }
    };

    /**
     * 设备显示视图配置
     */
    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(mContext));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mDeviceAdapter = new DeviceAdapter();
        mDeviceView.setAdapter(mDeviceAdapter);
    }

    /**
     * 加载设备列表
     */
    private void loadDevice() {
        mDeviceAdapter.addDevice(new Device("魔小灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("全彩照明", "33:93:A6:26:2B:0A"));
        mDeviceAdapter.addDevice(new Device("智能插排", "A6:88:22:33:93:A6"));
        mDeviceAdapter.addDevice(new Device("卧室灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("餐厅灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("运动智能", "00:23:93:A6:88:22"));
    }
}
