package com.guohua.mlight.view.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.communication.BLEConstant;
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
        loadDevice(); /*加载设备列表*/
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
            mRefreshView.setRefreshing(false);
        }
    };

    /**
     * 设备显示视图配置
     */
    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(mContext));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mDeviceAdapter = new DeviceAdapter();
        mDeviceAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceView.setAdapter(mDeviceAdapter);
    }

    private DeviceAdapter.OnItemClickListener mOnItemClickListener = new DeviceAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("Hello World", "mOnItemClickListener");
            final Device device = mDeviceAdapter.getDevice(position);
            if (device.isConnected()) {
//                device.setConnected(false);
                AppContext.getInstance().disonnect(device.getDeviceAddress(), false);
            } else {
//                device.setConnected(true);
                AppContext.getInstance().connect(device.getDeviceAddress());
            }
        }
    };

    /**
     * 加载设备列表
     */
    private void loadDevice() {
        /*mDeviceAdapter.addDevice(new Device("魔小灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("全彩照明", "33:93:A6:26:2B:0A"));
        mDeviceAdapter.addDevice(new Device("智能插排", "A6:88:22:33:93:A6"));
        mDeviceAdapter.addDevice(new Device("卧室灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("餐厅灯", "00:23:93:A6:88:22"));
        mDeviceAdapter.addDevice(new Device("运动智能", "00:23:93:A6:88:22"));*/
        mDeviceAdapter.setData(AppContext.getInstance().devices);
    }

    /**
     * @param device
     */
    public void onResult(Device device) {
        if (mDeviceAdapter.addDevice(device)) {
            /*添加后进行自动连接*/
            AppContext.getInstance().connect(device.getDeviceAddress());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerTheReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }

    /*连接对话框*/
    private ProgressDialog mProgressDialog;

    /*初始化连接对话框*/
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            initProgressDialog();
        }
//        mProgressDialog.setTitle(R.string.connect_dialog_title);
        mProgressDialog.setMessage(message);
    }

    /*注册广播接收蓝牙灯的连接状态改变*/
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, BLEConstant.ACTION_BLE_CONNECTED)) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_DISCONNECTED)) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_CONNECTING)) {
                showProgressDialog("拼命连接中...");
            } else if (TextUtils.equals(action, BLEConstant.ACTION_BLE_DISCONNECTED)) {
                showProgressDialog("正在断开...");
            }
            mDeviceAdapter.notifyDataSetChanged();
        }
    };

    private void registerTheReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLEConstant.ACTION_BLE_CONNECTED);
        filter.addAction(BLEConstant.ACTION_BLE_DISCONNECTED);
        filter.addAction(BLEConstant.ACTION_BLE_CONNECTING);
        filter.addAction(BLEConstant.ACTION_BLE_DISCONNECTED);
        filter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, filter);
    }
}
