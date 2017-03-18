package com.r00kie.lwble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @file BLEActivity.java
 * @author Leo
 * @version 1
 * @detail 蓝牙设备扫描
 * @since 2016/12/29 17:13
 */

/**
 * 文件名：BLEActivity.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/29 17:13
 * 描  述：蓝牙设备扫描
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEActivity extends AppCompatActivity {
    private static final long SCAN_PERIOD = 10000;// 扫描10s
    private static final int REQUEST_ENABLE_BT = 1;// 请求码

    private Toolbar mToolbar; /*标题栏*/
    private TextView mScanView; /*扫描视图控件*/
    private RecyclerView mDeviceView; /* 设备列表*/
    private BLEAdapter mAdapter; /* 设备适配器*/

    /*蓝牙适配器*/
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();// 用于postDelay
    private boolean mScanning = false;// 循环标志位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ble);
        /*如果支持蓝牙BLE则进行初始化否则退出*/
        if (BLEUtils.isSupportBluetoothBLE(this)) {
            initial();
        } else {
            Toast.makeText(this, R.string.ble_not_support, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 初始化相应的数据
     */
    private void initial() {
        loadViews();
        setupToolbar();
        setupDeviceView();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void loadViews() {
        mToolbar = (Toolbar) findViewById(R.id.t_toolbar_ble);
        mScanView = (TextView) findViewById(R.id.tv_scan_ble);
        mDeviceView = (RecyclerView) findViewById(R.id.rv_device_ble);
    }

    private void setupToolbar() {
        mToolbar.setNavigationIcon(R.drawable.icon_back);
        mToolbar.setTitle(R.string.ble_title);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(mOnClickListener);
        mScanView.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_scan_ble) {
                doScan();
            } else {
                finish();
            }
        }
    };

    private void doScan() {
        if (mScanning) {
            scanLeDevice(false);
        } else {
            scanLeDevice(true);
        }
    }

    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BLEAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mDeviceView.setAdapter(mAdapter);
    }

    private BLEAdapter.OnItemClickListener mOnItemClickListener = new BLEAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, Object position) {
            setResultToConnect((Integer) position);
        }
    };

    /**
     * 返回结果到发起扫描的地方
     *
     * @param position
     */
    private void setResultToConnect(int position) {
        /*停止扫描*/
        scanLeDevice(false);
        /*获取设备地址和设备名*/
        BluetoothDevice device = mAdapter.getDevice(position);
        String name = device.getName().trim();
        String address = device.getAddress().trim();
        /*把地址和设备名封装到Intent里*/
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(BLEConstant.EXTRA_DEVICE_NAME, name);
        /*返回结果并退出*/
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用*/
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
    protected void onDestroy() {
        super.onDestroy();
        suicide();
    }

    /**
     * 结束销毁
     */
    private void suicide() {
        scanLeDevice(false);
    }

    /*Section: 设备扫描*/

    /**
     * 扫描BLE设备
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanView.setText(R.string.ble_scan);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mScanView.setText(R.string.ble_scanning);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanView.setText(R.string.ble_scan);
        }
    }

    /**
     * 扫描蓝牙BLE设备的回掉函数
     */
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
