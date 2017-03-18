package com.guohua.mlight.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.lwble.BLEAdapter;
import com.guohua.mlight.lwble.BLEConstant;
import com.guohua.mlight.lwble.BLEUtils;

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
    private static final long SCAN_PERIOD = 10000;// 扫描10s
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

    @BindView(R.id.rv_device_scan)
    RecyclerView mDeviceView;

    private BLEAdapter mAdapter; /* 设备适配器*/
    private BluetoothAdapter mBluetoothAdapter;/*蓝牙适配器*/
    private Handler mHandler = new Handler();// 用于postDelay
    private boolean mScanning = false;// 循环标志位

    /**
     * 初始化数据
     */
    private void initial() {
        setupDeviceView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BLEAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceView.setAdapter(mAdapter);
    }

    private void doScan() {
        if (mScanning) {
            scanLeDevice(false);
        } else {
            mAdapter.clear();
            scanLeDevice(true);
        }
    }

    private BLEAdapter.OnItemClickListener mOnItemClickListener = new BLEAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            setResultToConnect(position);
        }
    };


    private void setResultToConnect(int position) {
        scanLeDevice(false);
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
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            scanLeDevice(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    @Override
    protected void suicide() {
        super.suicide();
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    /*扫描蓝牙BLE设备的回掉函数*/
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addDevice(bluetoothDevice);
                }
            });
        }
    };
}
