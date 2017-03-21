package com.guohua.mlight.lwble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;

/**
 * @author Leo
 * @version 1
 * @since 2016-12-30
 * 蓝牙设备扫描器
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEScanner {
    private static final long SCAN_PERIOD = 10000;// 扫描10s
    private volatile static BLEScanner singleton = null;
    /*蓝牙适配器*/
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();// 用于postDelay
    private boolean mScanning = false;// 循环标志位
    /**
     * 扫描到的设备的回调接口
     */
    private DeviceDiscoveredListener mDeviceDiscoveredListener;
    /**
     * 扫描蓝牙BLE设备的回掉函数
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            System.out.println(bluetoothDevice.getName());
            if (mDeviceDiscoveredListener != null) {
                mDeviceDiscoveredListener.onDiscovered(bluetoothDevice, rssi, bytes);
            }
        }
    };
    /*Section: 设备扫描*/
    /**
     * 扫描到的设备的回调接口
     */
    private StateCallback mStateCallback;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            /*扫描状态回调出去*/
            if (mStateCallback != null) mStateCallback.onStateChanged(mScanning);
        }
    };

    private BLEScanner() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BLEScanner getInstance() {
        if (singleton == null) {
            synchronized (BLEScanner.class) {
                if (singleton == null) {
                    singleton = new BLEScanner();
                }
            }
        }
        return singleton;
    }

    /**
     * 扫描BLE设备
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(mRunnable, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mHandler.removeCallbacks(mRunnable);
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        /*扫描状态回调出去*/
        if (mStateCallback != null) mStateCallback.onStateChanged(mScanning);
    }

    /*设置接收器*/
    public void setDeviceDiscoveredListener(DeviceDiscoveredListener deviceDiscoveredListener) {
        this.mDeviceDiscoveredListener = deviceDiscoveredListener;
    }

    public void setStateCallback(StateCallback stateCallback) {
        this.mStateCallback = stateCallback;
    }

    /**
     * 是否正在扫描
     *
     * @return
     */
    public boolean isScanning() {
        return this.mScanning;
    }

    /**
     * 扫描或者停止
     */
    public void startScan() {
        scanLeDevice(true);
    }

    public void stopScan() {
        scanLeDevice(false);
    }

    public interface DeviceDiscoveredListener {
        void onDiscovered(BluetoothDevice device, int rssi, byte[] bytes);
    }

    public interface StateCallback {
        void onStateChanged(boolean state);
    }
}
