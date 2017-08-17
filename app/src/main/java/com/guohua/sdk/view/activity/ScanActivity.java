package com.guohua.sdk.view.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eraare.ble.BLEScanner;
import com.eraare.ble.BLEUtils;
import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseActivity;
import com.guohua.sdk.common.base.BaseFragment;
import com.guohua.sdk.common.permission.PermissionListener;
import com.guohua.sdk.common.permission.PermissionManager;
import com.guohua.sdk.common.util.FilterUtils;
import com.guohua.sdk.view.adapter.BLEAdapter;
import com.guohua.sdk.view.widget.LocalRecyclerView;

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
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";

    private static final int REQUEST_ENABLE_BT = 1;// 请求码
    private static final long DEFAULT_SCAN_DURATION = 10000;

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
    LocalRecyclerView mDeviceView; /*设备列表*/
    @BindView(R.id.tv_empty_view_scan)
    TextView mEmptyView; /*空设备*/

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
        setToolbarTitle(R.string.activity_title_scan);
        setForwardVisibility(View.VISIBLE);
        setForwardTitle(R.string.activity_scan_scan);
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
                /*请求定位权限*/
                requestPermission();
            }
        }
    };

    /**
     * 配置蓝牙扫描
     */
    private void setupBleScanner() {
        mBleScanner = BLEScanner.getInstance();
        mBleScanner.setCallback(mCallback);
    }

    private BLEScanner.Callback mCallback = new BLEScanner.Callback() {
        @Override
        public void onStateChanged(boolean state) {
            if (state) {
                setForwardTitle(R.string.activity_scanning_scan);
            } else {
                setForwardTitle(R.string.activity_scan_scan);
            }
        }

        @Override
        public void onDeviceDiscovered(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (!FilterUtils.filter(bytes)) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addDevice(bluetoothDevice);
                }
            });
        }
    };

    private void setupDeviceView() {
        mDeviceView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceView.setItemAnimator(new DefaultItemAnimator());
        mDeviceView.setEmptyView(mEmptyView);
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
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(EXTRA_DEVICE_NAME, name);
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
            requestPermission();
        }
    }

    /*Section: 权限管理*/
    private PermissionManager mHelper; /*权限管理类*/
    public static final int PERMISSION_REQUEST_CODE = 102;

    private void requestPermission() {
        /*是否有定位权限使用蓝牙操作进行扫描*/
        if (PermissionManager.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mAdapter.clear();
            BLEScanner.getInstance().startScan(DEFAULT_SCAN_DURATION);
        } else {
            mHelper = PermissionManager.with(this)
                    .permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .setPermissionsListener(mPermissionListener)
                    .addRequestCode(PERMISSION_REQUEST_CODE)
                    .request();
        }
    }

    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onGranted() {
            mAdapter.clear();
            mBleScanner.startScan(DEFAULT_SCAN_DURATION);
        }

        @Override
        public void onDenied() {
            toast("需要模糊定位权限使用蓝牙");
        }

        @Override
        public void onShowRationale(String[] permissions) {
            Snackbar.make(mEmptyView, "需要模糊定位权限使用蓝牙", Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHelper.setIsPositive(true);
                            mHelper.request();
                        }
                    }).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            mHelper.onPermissionResult(permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            /*请求定位权限*/
            requestPermission();
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
