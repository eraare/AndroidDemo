package com.guohua.mlight.communication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Leo
 * @version 2.0
 *          #detail BLE连接通信
 * @since 2016-06-27
 */
@SuppressLint("NewApi")
public class BLEService extends Service {
    /*Section: 属性区域*/
    public static final String TAG = BLEService1.class.getSimpleName();//标志 用于调试等
    private BLEService mContext;// 上下文
    private BluetoothAdapter mBluetoothAdapter = null;// 本地设备
    private static final boolean AUTO_CONNECT = true;// 是否自动连接
    private static final boolean NOTIFICATION_ENABLED = true;
    private ConcurrentHashMap<String, BLEDevice> mBLEDevices;//保存连接设备的Gatt 以device address为key
    private SharedPreferences sp;

    /*Section: 生命周期*/
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        init();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        // return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        suiside();
    }

    /*Section: onCreate()*/

    /**
     * 各种初始化信息
     */
    private void init() {
        if (!initBluetooth()) {
            stopSelf();
        }
        mContext = this;//初始化
        mBLEDevices = new ConcurrentHashMap<>();//初始化集合默认大小为10超过会自增
    }

    /**
     * 初始化BluetoothManager和BluetoothAdapter
     *
     * @return
     */
    private boolean initBluetooth() {
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
//		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    /*Section: onBind()*/
    /*绑定服务时返回自己*/
    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    /*Section: onDestroy()*/

    /**
     * 自殺
     */
    private void suiside() {
        disconnectAll();
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter = null;
            }
        } catch (Exception e) {
            toast("关闭蓝牙失败，请手动关闭");
        }
    }

    /*Section: common methods*/

    /**
     * 吐丝
     */
    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 向外发送收到的数据广播
     *
     * @param rcv
     */
    private void sendDataBroadcast(String rcv, String address) {
        if (rcv == null || TextUtils.equals(rcv, ""))
            return;
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_RECEIVED_DATA, rcv);
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        if (rcv.startsWith("T:") || rcv.startsWith("t:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_TEMPERATURE);
        } else if (rcv.startsWith("V:") || rcv.startsWith("v:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_VOLTAGE);
        } else if (rcv.startsWith("VER:") || rcv.startsWith("ver:")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_VERSION);
        } else if (rcv.startsWith("pres")) {
            intent.setAction(BLEConstant.ACTION_RECEIVED_SELFIE);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * 向外发送状态广播
     *
     * @param action
     * @param address
     */
    private void sendStateBroadcast(String action, String address) {
        if (action == null || TextUtils.equals(action, ""))
            return;
        Intent intent = new Intent();
        intent.putExtra(BLEConstant.EXTRA_DEVICE_ADDRESS, address);
        intent.setAction(action);
        sendBroadcast(intent);
    }

    /*Section: BLE连接通信的回调函数*/
    /**
     * GATT通信回調函數
     */
    @SuppressLint("NewApi")
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            super.onCharacteristicChanged(gatt, characteristic);
            String deviceAddress = gatt.getDevice().getAddress();
            String received = new String(characteristic.getValue());
            sendDataBroadcast(received, deviceAddress);
            System.out.println("我收到了" + deviceAddress + "的数据:" + received);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicRead(gatt, characteristic, status);
            System.out.println("onCharacteristicRead()");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("onCharacteristicWrite()");
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            super.onConnectionStateChange(gatt, status, newState);
            String deviceAddress = gatt.getDevice().getAddress();
            if (!mBLEDevices.containsKey(deviceAddress)) {
                return;
            }
            BLEDevice mDevice = mBLEDevices.get(deviceAddress);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BluetoothGatt mGatt = mDevice.gatt;
                if (mGatt != null)
                    mGatt.discoverServices();
                mDevice.state = BLEDevice.STATE_CONNECTED;
                sendStateBroadcast(BLEConstant.ACTION_BLE_CONNECTED, deviceAddress);
                System.out.println("state connected");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mDevice.state = BLEDevice.STATE_DISCONNECTED;
                sendStateBroadcast(BLEConstant.ACTION_BLE_DISCONNECTED, deviceAddress);
                System.out.println("state disconnected");
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorRead(gatt, descriptor, status);
            System.out.println("onDescriptorRead()");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorWrite(gatt, descriptor, status);
            System.out.println("onDescriptorWrite()");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            super.onReadRemoteRssi(gatt, rssi, status);
            System.out.println("onReadRemoteRssi()");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onReliableWriteCompleted(gatt, status);
            System.out.println("onReliableWriteCompleted()");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onServicesDiscovered(gatt, status);
            System.out.println("found service----------------------------");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(SampleGattAttributes.UUID_SERVICE);
                if (service == null) {
                    return;
                }
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(SampleGattAttributes.UUID_CHARACTERISTIC);
                if (characteristic == null) {
                    return;
                }

                String deviceAddress = gatt.getDevice().getAddress();
                BLEDevice device = mBLEDevices.get(deviceAddress);
                if (device == null) {
                    return;
                }
                device.characteristic = characteristic;
                if (sp == null) {
                    sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                }
                String passport = (Constants.DEFAULT_PASSWORD_HEAD + sp.getString(deviceAddress, CodeUtils.password));
                new Thread(new ConfigRunnable(deviceAddress, passport)).start();
            }
        }
    };

    /**
     * 设置后可以使用通知 设备给手机发送通知时可触发onCharacteristicChanged()
     *
     * @param mBluetoothGatt
     * @param characteristic
     * @param enabled
     */
    private void setCharacteristicNotification(final BluetoothGatt mBluetoothGatt, final BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SampleGattAttributes.UUID_DESCRIPTOR);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private synchronized void setCharacteristicNotification(String address, boolean enabled) {
        BLEDevice device = mBLEDevices.get(address);
        BluetoothGatt gatt = device.gatt;
        BluetoothGattCharacteristic characteristic = device.characteristic;
        if (mBluetoothAdapter == null || gatt == null) {
            return;
        }
        gatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SampleGattAttributes.UUID_DESCRIPTOR);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    /**
     * 关闭通信
     */
    private void close(BluetoothGatt mBluetoothGatt) {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
    }

    /**
     * 通信接口 通过此函数即可向BLE设备写入数据
     *
     * @param value
     * @return
     */
    private synchronized boolean writeToBLE(String address, byte[] value) {
        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return false;
        }
        if (!mBLEDevices.containsKey(address)) {
            return false;
        }

        BLEDevice mDevice = mBLEDevices.get(address);
        if (mDevice.state != BLEDevice.STATE_CONNECTED) {
            return false;
        }

        BluetoothGatt mGatt = mDevice.gatt;
        BluetoothGattCharacteristic mCharacteristic = mDevice.characteristic;

        if (mGatt == null || mCharacteristic == null) {
            return false;
        }

        mCharacteristic.setValue(value);
        boolean isSucc = mGatt.writeCharacteristic(mCharacteristic);

        return isSucc;
    }

    public boolean sendAll(String message) {
        return sendAll(message.getBytes());
    }

    public boolean sendAll(byte[] message) {
        //遍历所有的连接线程去写数据
        boolean flag = true;//标志位
        Set<Map.Entry<String, BLEDevice>> mEnterySet = mBLEDevices.entrySet();
        for (Map.Entry<String, BLEDevice> entry : mEnterySet) {
            BLEDevice mDevice = entry.getValue();
            if (mDevice != null) {
                if (!writeToBLE(mDevice.address, message)) {
                    flag = false;
                }
            }
        }
        //写完就走 别再烦我
        return flag;
    }

    public boolean send(String deviceAddress, byte[] data) {
        return writeToBLE(deviceAddress, data);
    }

    public boolean isConnected(String deviceAddress) {
        //你是敢给我空的地址我就敢给你false
        if (deviceAddress == null || TextUtils.equals("", deviceAddress)) {
            return false;
        }
        BLEDevice mDevice = mBLEDevices.get(deviceAddress);
        if (mDevice != null) {
            int state = mDevice.state;
            if (state == BLEDevice.STATE_CONNECTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 建立通信連接
     *
     * @param address
     * @return
     */
    public boolean connect(final String address) {
        //没有工具 没有地址 叫我怎么好 只好返回false了
        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return false;
        }

        boolean isHave = mBLEDevices.containsKey(address);//判断是否已经有了gatt
        if (isHave) {
            //取得BLE设备
            BLEDevice mDevice = mBLEDevices.get(address);
            if (mDevice != null) {
                int state = mDevice.state;
                BluetoothGatt mGatt = mDevice.gatt;
                //判断gatt和state
                if (mGatt != null && state == BLEDevice.STATE_DISCONNECTED) {
                    if (mGatt.connect()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        BluetoothGatt mGatt = device.connectGatt(this, AUTO_CONNECT, mGattCallback);
        mBLEDevices.put(address, new BLEDevice(address, mGatt));
        return true;
    }

    /**
     * 根据地址斷開GATT連接
     */
    public void disconnect(String address, boolean isRemove) {
        if (mBluetoothAdapter == null || address == null || TextUtils.equals(address, "")) {
            return;
        }
        if (!mBLEDevices.containsKey(address)) {
            return;
        }
        BLEDevice mDevice = mBLEDevices.get(address);
        if (mDevice != null) {
            BluetoothGatt mGatt = mDevice.gatt;
            int state = mDevice.state;
            if (mGatt != null && state == BLEDevice.STATE_CONNECTED) {
                mGatt.disconnect();
            }
        }
        if (isRemove) {
            close(mDevice.gatt);
            mBLEDevices.remove(address);
        }
    }

    /**
     * 关闭所有设备并清空map
     */
    public void disconnectAll() {
        Set<Map.Entry<String, BLEDevice>> mEnterySet = mBLEDevices.entrySet();
        for (Map.Entry<String, BLEDevice> entry : mEnterySet) {
            BLEDevice mDevice = entry.getValue();
            disconnect(mDevice.address, false);
            close(mDevice.gatt);
        }
        //断开完我再清理
        mBLEDevices.clear();
    }

    private class ConfigRunnable implements Runnable {
        private String mAddress;
        private String mPassword;

        public ConfigRunnable(String address, String password) {
            this.mAddress = address;
            this.mPassword = password;
        }

        @Override
        public void run() {
            writeToBLE(mAddress, mPassword.getBytes());
            delay(150);
            setCharacteristicNotification(mAddress, NOTIFICATION_ENABLED);
        }

        private void delay(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
