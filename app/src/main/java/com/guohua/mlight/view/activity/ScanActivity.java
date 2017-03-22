package com.guohua.mlight.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.lwble.BLEConstant;
import com.guohua.mlight.lwble.BLEFilter;
import com.guohua.mlight.lwble.BLEScanner;
import com.guohua.mlight.lwble.BLEUtils;
import com.guohua.mlight.view.adapter.BLEAdapter;

import butterknife.BindView;

/**
 * @file ScanActivity.java
 * @author Leo
 * @version 1
 * @detail 蓝牙设备扫描
 * @since 2016/12/29 17:13
 */

/**
 * 文件名：ScanActivity.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/29 17:13
 * 描  述：蓝牙设备扫描
 */
public class ScanActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 1;// 请求码

    @Override
    protected int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @BindView(R.id.rv_device_scan)
    RecyclerView mDeviceView; /*设备列表*/

    private BLEAdapter mAdapter; /* 设备适配器*/
    private BluetoothAdapter mBluetoothAdapter;/*蓝牙适配器*/
    private BLEScanner mBleScanner; /*蓝牙扫描器*/

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        /*如果支持蓝牙BLE则进行初始化否则退出*/
        if (BLEUtils.isSupportBluetoothBLE(this)) {
            initial();
        } else {
            Toast.makeText(this, "您的手机不支持蓝牙BLE", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 初始化数据
     */
    private void initial() {
        setupToolbar();
        setupDeviceView();
        setupBleScanner();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setupToolbar() {
        setToolbarTitle("搜索设备");
        setForwardVisibility(View.VISIBLE);
        setForwardTitle("搜索");
        setOnForwardClickListener(mOnClickListener);
    }

    /**
     * 扫描断开扫描
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBleScanner.isScanning()) {
                mBleScanner.stopScan();
            } else {
                mAdapter.clear();
                mBleScanner.startScan(10000);
            }
        }
    };

    /**
     * 配置蓝牙扫描
     */
    private void setupBleScanner() {
        mBleScanner = BLEScanner.getInstance();
        mBleScanner.setStateCallback(mStateCallback);
        mBleScanner.setDeviceDiscoveredListener(mDeviceDiscoveredListener);
    }

    /**
     * 扫描到设备后加入适配器
     */
    private BLEScanner.DeviceDiscoveredListener mDeviceDiscoveredListener = new BLEScanner.DeviceDiscoveredListener() {
        @Override
        public void onDiscovered(final BluetoothDevice device, int rssi, byte[] bytes) {
            if (!BLEFilter.filter(bytes)) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addDevice(device);
                }
            });
        }
    };

    /**
     * 扫描状态回调
     */
    private BLEScanner.StateCallback mStateCallback = new BLEScanner.StateCallback() {
        @Override
        public void onStateChanged(boolean state) {
            if (state) {
                setForwardTitle("搜索中...");
            } else {
                setForwardTitle("搜索");
            }
        }
    };

    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BLEAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceView.setAdapter(mAdapter);
    }

    private BLEAdapter.OnItemClickListener mOnItemClickListener = new BLEAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            setResultToConnect(position);
        }
    };


    private void setResultToConnect(int position) {
        mBleScanner.stopScan();
        BluetoothDevice device = mAdapter.getDevice(position);
        String name = device.getName().trim();
        String address = device.getAddress().trim();
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(BLEConstant.EXTRA_DEVICE_NAME, name);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mAdapter.clear();
            mBleScanner.startScan(1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            mAdapter.clear();
            mBleScanner.startScan(1000);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBleScanner.stopScan();
    }

    @Override
    protected void suicide() {
        super.suicide();
        mBleScanner.stopScan();
    }

}
