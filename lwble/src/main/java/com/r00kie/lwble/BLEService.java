package com.r00kie.lwble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @file BLEService.java
 * @author Leo
 * @version 1
 * @detail 蓝牙通信核心服务
 * @since 2016/12/29 10:10
 */

/**
 * 文件名：BLEService.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/29 10:10
 * 描  述：蓝牙通信核心服务
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEService extends Service {
    private static final String TAG = BLEService.class.getSimpleName();
    private BLEService mContext; //上下文

    /*蓝牙适配器和蓝牙GATT*/
    private BluetoothAdapter mBluetoothAdapter;
    private Map<String, BluetoothGatt> mGatts;

    /*Section: 服务生命周期*/
    @Override
    public void onCreate() {
        super.onCreate();
        /*若支持蓝牙和BLE就进行初始化*/
        /*如果支持蓝牙BLE则进行初始化否则退出*/
        if (BLEUtils.isSupportBluetoothBLE(this)) {
            init();
        } else {
            Toast.makeText(this, R.string.ble_not_support, Toast.LENGTH_SHORT).show();
            stopSelf();
        }
    }

    /*初始化*/
    private void init() {
        mContext = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mGatts = new ConcurrentHashMap<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return new LocalBinder();
    }

    /**
     * 服务接口
     */
    private class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        suicide();
    }

    /**
     * 结束的一些操作动作
     */
    private void suicide() {
        if (mGatts == null) return;
        /*通过Key遍历进行关闭所有的设备*/
        Set<String> keySet = mGatts.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            disconnect(iterator.next(), true);
        }
        mGatts.clear();
    }

    /*Section: BLE回调类*/
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                System.out.println("RxBLE: Connected");
                if (!gatt.discoverServices()) {
                    System.out.println("RxBLE: Cannot Discovery Services");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                System.out.println("RxBLE: Disconnected");
            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                System.out.println("RxBLE: Connecting");
            } else if (newState == BluetoothGatt.STATE_DISCONNECTING) {
                System.out.println("RxBLE: Disconnecting");
            }
            /*把状态传递出去*/
            if (mOnConnectStateChangedListener != null) {
                String deviceAddress = gatt.getDevice().getAddress();
                mOnConnectStateChangedListener.onStateChanged(deviceAddress, newState);
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("RxBLE: Fond Services");
            /*设置可通知*/
            setCharacteristicNotification(gatt, true);
            System.out.println("RxBLE: Set Notification");
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*数据通过回调接口传递出去*/
            if (mOnDataReceivedListener != null) {
                /*哪个设备传来的数据*/
                String deviceAddress = gatt.getDevice().getAddress();
                byte[] data = characteristic.getValue();
                mOnDataReceivedListener.onDataReceived(deviceAddress, data);
                System.out.println("RxBLE: Received Data-[" + new String(data) + "]");
            }
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

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

    /*Section: 回调接口*/
    private OnConnectStateChangedListener mOnConnectStateChangedListener;
    private OnDataReceivedListener mOnDataReceivedListener;

    /**
     * 设置接收状态改变的接口
     *
     * @param onConnectStateChangedListener
     */
    public void setOnConnectStateChangedListener(OnConnectStateChangedListener onConnectStateChangedListener) {
        this.mOnConnectStateChangedListener = onConnectStateChangedListener;
    }

    /**
     * 设置数据接收的接口
     *
     * @param onDataReceivedListener
     */
    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
        this.mOnDataReceivedListener = onDataReceivedListener;
    }

    /**
     * 连接状态改变接口
     */
    public interface OnConnectStateChangedListener {
        void onStateChanged(String deviceAddress, int state);
    }

    /**
     * 数据接收数据
     */
    public interface OnDataReceivedListener {
        void onDataReceived(String deviceAddress, byte[] data);
    }

    /*Section: 对外接口*/

    /**
     * 根据地址连接设备
     *
     * @param deviceAddress
     * @param isAutoConnect
     * @return
     */
    public boolean connect(String deviceAddress, boolean isAutoConnect) {
        /*地址为空则退出*/
        if (TextUtils.isEmpty(deviceAddress)) return false;
        // 从集合中取得gatt
        BluetoothGatt gatt = mGatts.get(deviceAddress);
        if (gatt == null) {
            /*第一次连接*/
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            if (device == null) return false;
            gatt = device.connectGatt(mContext, isAutoConnect, mBluetoothGattCallback);
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
