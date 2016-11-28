package com.guohua.mlight.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;

/**
 * Created by HHH on 2016/11/28.
 */
public final class BluetoothUtil {
    /**
     * java反射技术取消蓝牙配对
     *
     * @param device
     */
    public static void unpairDevice(BluetoothDevice device) {
        try {
            Class c = BluetoothDevice.class;
            Method m = c.getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unpairDevice(String address) {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothDevice device = mAdapter.getRemoteDevice(address);
            Class c = BluetoothDevice.class;
            Method m = c.getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}