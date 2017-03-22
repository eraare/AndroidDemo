package com.guohua.mlight.lwble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @file BLEController.java
 * @author Leo
 * @version 1
 * @detail 蓝牙通信控制器
 * @since 2016/12/30 17:25
 */

/**
 * 文件名：BLEController.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/30 17:25
 * 描  述：蓝牙通信控制器
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEController {
    /*连接断开状态码*/
    public static final int STATE_INITIAL = -1;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTING = 3;
    public static final int STATE_DISCONNECTED = 4;
    public static final int STATE_SERVICING = 5;

    private volatile static BLEController singleton = null;
    /*蓝牙适配器和蓝牙GATT*/
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, BluetoothGatt> mGatts;
    /*Section: BLE回调类*/
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            int currentState = STATE_INITIAL;
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                currentState = STATE_CONNECTED;
                System.out.println("RxBLE: Connected");
                if (!gatt.discoverServices()) {
                    System.out.println("RxBLE: Cannot Discovery Services");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                currentState = STATE_DISCONNECTED;
                System.out.println("RxBLE: Disconnected");
            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                currentState = STATE_CONNECTING;
                System.out.println("RxBLE: Connecting");
            } else if (newState == BluetoothGatt.STATE_DISCONNECTING) {
                currentState = STATE_DISCONNECTING;
                System.out.println("RxBLE: Disconnecting");
            }
            String deviceAddress = gatt.getDevice().getAddress();
            /*用EventBus把状态POST出去*/
            EventBus.getDefault().post(new MessageEvent(deviceAddress, currentState));
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("RxBLE: Fond Services");
            /*设置可通知*/
            System.out.println("RxBLE: Set Notification");
            String deviceAddress = gatt.getDevice().getAddress();
            /*用EventBus把状态POST出去*/
            EventBus.getDefault().post(new MessageEvent(deviceAddress, STATE_SERVICING));
            new Thread(new ConfigRunnable(gatt, true)).start();/*通知配置和密码验证必须由事件间隔*/
            super.onServicesDiscovered(gatt, status);
        }

        /*密码校验线程*/
        final class ConfigRunnable implements Runnable {
            private BluetoothGatt gatt;
            private boolean enable;

            public ConfigRunnable(BluetoothGatt gatt, boolean enable) {
                this.gatt = gatt;
                this.enable = enable;
            }

            @Override
            public void run() {
                SystemClock.sleep(150);
                setCharacteristicNotification(this.gatt, this.enable);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
                /*哪个设备传来的数据*/
            String deviceAddress = gatt.getDevice().getAddress();
            byte[] data = characteristic.getValue();
            System.out.println("RxBLE: Received Data-[" + new String(data) + "]");
            /*用EventBus把状态POST出去*/
            EventBus.getDefault().post(new MessageEvent(deviceAddress, new String(data)));
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    private BLEController() {
        /*初始化数据*/
        init();
    }

    public static BLEController getInstance() {
        if (singleton == null) {
            synchronized (BLEController.class) {
                if (singleton == null) {
                    singleton = new BLEController();
                }
            }
        }
        return singleton;
    }

    /*初始化*/
    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mGatts = new ConcurrentHashMap<>();
    }

    /**
     * 结束的一些操作动作
     */
    public void suicide() {
        if (mGatts == null) return;
        /*通过Key遍历进行关闭所有的设备*/
        Set<String> keySet = mGatts.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            disconnect(iterator.next(), true);
        }
        mGatts.clear();
    }

    /**
     * 设置蓝牙设备可接受通知
     *
     * @param gatt
     * @param enabled
     */
    private void setCharacteristicNotification(BluetoothGatt gatt, boolean enabled) {
        /*gatt为空或则characteristic为空则退出*/
        if (gatt == null) {
            return;
        }
        BluetoothGattCharacteristic characteristic = getCharacteristic(gatt);
        if (characteristic == null) {
            return;
        }
        /*设置属性并通知*/
        gatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BLEUUID.UUID_DESCRIPTOR);
        // 查看是否带有可通知属性notify 查看是否带有indecation属性
        if (0 != (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else if (0 != (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        }
        gatt.writeDescriptor(descriptor);
    }

    /**
     * 获取BluetoothGattCharacteristic
     *
     * @param gatt
     * @return
     */
    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt gatt) {
        BluetoothGattService bluetoothGattService = gatt.getService(BLEUUID.UUID_SERVICE);
        if (bluetoothGattService == null) {
            return null;
        }
        return bluetoothGattService.getCharacteristic(BLEUUID.UUID_CHARACTERISTIC);
    }

    /**
     * 根据地址连接设备
     *
     * @param context
     * @param deviceAddress
     * @param isAutoConnect
     * @return
     */
    public boolean connect(Context context, String deviceAddress, boolean isAutoConnect) {
        /*地址为空则退出*/
        if (TextUtils.isEmpty(deviceAddress)) return false;
        // 从集合中取得gatt
        BluetoothGatt gatt = mGatts.get(deviceAddress);
        if (gatt == null) {
            /*第一次连接*/
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            if (device == null) return false;
            gatt = device.connectGatt(context, isAutoConnect, mBluetoothGattCallback);
            if (gatt == null) return false;
            mGatts.put(deviceAddress, gatt);
            return true;
        } else {
            /*非第一次只是重新连接*/
            return gatt.connect();
        }
    }

    /**
     * 断开连接
     *
     * @param deviceAddress
     * @param isRemove
     */
    public void disconnect(String deviceAddress, boolean isRemove) {
        BluetoothGatt gatt = mGatts.get(deviceAddress);
        // 为空则返回
        if (gatt == null) return;
        // 断开连接
        gatt.disconnect();
        // 移除并关闭
        if (isRemove) mGatts.remove(deviceAddress).close();
    }

    /*Section: 对外接口*/

    /**
     * 发送数据
     *
     * @param deviceAddress
     * @param data
     * @return
     */
    public boolean send(String deviceAddress, byte[] data) {
        /*判空*/
        BluetoothGatt gatt = mGatts.get(deviceAddress);
        if (gatt == null) return false;
        BluetoothGattCharacteristic character = getCharacteristic(gatt);
        if (character == null) return false;
        /*向设置发送数据*/
        character.setValue(data);
        return gatt.writeCharacteristic(character);
    }
}
