package com.guohua.mlight.common.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @file BLEUtil.java
 * @author Leo
 * @version 1
 * @detail BLE用到的工具类，包括检查是否支持蓝牙及BLE
 * @since 2016/12/29 8:57
 */

/**
 * 文件名：BLEUtil.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/29 8:57
 * 描  述：BLE用到的工具类，包括检查是否支持蓝牙及BLE
 */
public final class BLEUtil {

    /**
     * 检测设备是否支持蓝牙BLE
     *
     * @param context
     * @return
     */
    public static boolean isSupportBluetoothBLE(Context context) {
        if (isSupportBluetooth()) {
            if (isSupportBLE(context)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否支持BLE
     *
     * @return
     */
    public static boolean isSupportBLE(Context context) {
        // 判断是否支持BLE
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        return true;
    }

    /**
     * 是否支持蓝牙
     *
     * @return
     */
    public static boolean isSupportBluetooth() {
        // 初始化BluetoothAdapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 检查设备上是否支持蓝牙不支 持就退出程序
        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }
}
