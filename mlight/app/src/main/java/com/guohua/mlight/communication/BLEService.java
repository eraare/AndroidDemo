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
import android.text.TextUtils;
import android.widget.Toast;

import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Leo
 * @time 2016-06-27
 * @detail BLE连接通信
 */
@SuppressLint("NewApi")
public class BLEService extends Service {
    public static final String TAG = BLEService.class.getSimpleName();//标志 用语调试等
    private BLEService mContext;// 上下文
    private BluetoothAdapter mBluetoothAdapter = null;// 本地设备
    private static final boolean AUTO_CONNECT = true;// 是否自动连接
    private static final boolean NOTIFICATION_ENABLED = true;
    private boolean isServiceDiscovered = false;//发现服务否
    private HashMap<String, BLEDevice> mBLEDevices;//保存连接设备的Gatt 以device address为key

    //private boolean isConfirmed = false;//密码验证

    /*private IBLEService.Stub mService = new IBLEService.Stub() {
        @Override
        public boolean connect(String deviceAddress) throws RemoteException {
            return mContext.connect(deviceAddress);
        }

        @Override
        public boolean send(String deviceAddress, String message) throws RemoteException {
            return mContext.writeToBLE(deviceAddress, message);
        }


        @Override
        public void disconnect(String deviceAddress) throws RemoteException {
            mContext.disconnect(deviceAddress, false);
        }

        @Override
        public boolean isConnected(String deviceAddress) throws RemoteException {
            return mContext.isConnected(deviceAddress);
        }

        @Override
        public void disconnectAll() throws RemoteException {
            mContext.disconnectAll();
        }

        @Override
        public boolean sendAll(String message) throws RemoteException {
            return mContext.sendAll(message);
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public boolean sendAllByte(byte[] message) throws RemoteException {
            return mContext.sendAll(message);
        }

        @Override
        public boolean sendByte(String deviceAddress, byte[] message) throws RemoteException {
            return mContext.writeToBLE(deviceAddress, message);
        }

    };*/
    /*绑定服务时返回自己*/
    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
//        return mService;
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        init();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        suiside();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        // return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    /**
     * 各种初始化信息
     */
    private void init() {
        if (!initBluetooth()) {
            stopSelf();
        }

        mContext = this;//初始化
        isServiceDiscovered = false;
        mBLEDevices = new HashMap<>();//初始化集合默认大小为10超过会自增
    }

    /**
     * 吐丝
     */
    private void toast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
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

    /**
     * 关闭通信
     */
    public void close(BluetoothGatt mBluetoothGatt) {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
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
        }
        sendBroadcast(intent);
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

    /**
     * GATT通信回調函數
     */
    @SuppressLint("NewApi")
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            // super.onCharacteristicChanged(gatt, characteristic);
            //硬件传来的数据从这里读取
            String deviceAddress = gatt.getDevice().getAddress();
            String rcv = new String(characteristic.getValue());
            sendDataBroadcast(rcv, deviceAddress);
            System.out.println("我收到了" + deviceAddress + "的数据:" + rcv);

//			if(isConfirmed == false && TextUtils.equals(rcv, "goal")) {
//				isConfirmed = true;
//			}
//			if(!isConfirmed) {
//				writeToBLE(deviceAddress, CodeUtils.password);
//			}
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
            //super.onCharacteristicWrite(gatt, characteristic, status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                String deviceAddress = gatt.getDevice().getAddress();
                gatt.disconnect();
//                System.out.println("write failed");
            } else {
//                System.out.println("write succeed");
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            // super.onConnectionStateChange(gatt, status, newState);
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
				System.out.println("state connected");
//				sendStateBroadcast(BLEConstant.ACTION_BLE_CONNECTED, deviceAddress);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // connect(mBluetoothDeviceAddress);
                mDevice.state = BLEDevice.STATE_DISCONNECTED;
                System.out.println("state disconnected");
                sendStateBroadcast(BLEConstant.ACTION_BLE_DISCONNECTED, deviceAddress);
                //isConfirmed = false;
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
            //读到的远程设备的rssi
//			Intent intent1 = new Intent();// 创建Intent对象
//			/*Bundle bundle = new Bundle();
//			// 序列化列表，发送后在本地重建数据
//			bundle.putString(Constant.KEY_DATA_RECEIVED, rev);*/
//			intent1.setAction(BLEConstant.ACTION_RSSI);
//			//intent.putExtras(bundle);
//			intent1.putExtra(BLEConstant.KEY_DATA_RSSI, rssi);
//			sendBroadcast(intent1);// 发送广播
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
            // super.onServicesDiscovered(gatt, status);
//			if (isServiceDiscovered) {
//				return;
//			}
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            String deviceAddress = gatt.getDevice().getAddress();
            if (!mBLEDevices.containsKey(deviceAddress)) {
                gatt.disconnect();
                return;
            }
            BLEDevice mDevice = mBLEDevices.get(deviceAddress);
            BluetoothGatt mGatt = mDevice.gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattCharacteristic mCharacteristic = getCharacteristic(gatt, SampleGattAttributes.UUID_SERVICE, SampleGattAttributes.UUID_CHARACTERISTIC);
                if (mCharacteristic == null) {
                    return;
                }
                mDevice.characteristic = mCharacteristic;
                System.out.println("find services");
//				isServiceDiscovered = true;
                System.out.println("state connected");
                String passport = (Constant.DEFAULT_PASSWORD_HEAD + sp.getString(deviceAddress, CodeUtils.password));
                System.out.println(" BLEService onServicesDiscovered deviceAddress: " + deviceAddress + "; passport:  " + passport);
                writeToBLE(deviceAddress, passport.getBytes());
                sendStateBroadcast(BLEConstant.ACTION_BLE_CONNECTED, deviceAddress);
                setCharacteristicNotification(gatt, mCharacteristic, NOTIFICATION_ENABLED);
            } else {
                gatt.disconnect();
            }
        }

    };

    private BluetoothGattService getService(BluetoothGatt nGatt, UUID nUuid) {
        return nGatt.getService(nUuid);
    }

    private BluetoothGattCharacteristic getCharacteristic(BluetoothGatt nGatt, UUID nServiceUuid, UUID nCharacteristicUuid) {
        BluetoothGattService nService = nGatt.getService(nServiceUuid);
        return nService.getCharacteristic(nCharacteristicUuid);
    }

    /**
     * 设置后可以使用通知 设备给手机发送通知时可触发onCharacteristicChanged()
     *
     * @param characteristic
     * @param enabled
     */

    private void setCharacteristicNotification(BluetoothGatt mBluetoothGatt,
                                               BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothGatt == null || characteristic == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic
                .getDescriptor(SampleGattAttributes.UUID_DESCRIPTOR);
        if (descriptor != null) {
            descriptor
                    .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }


    /**
     * 通信接口 通过此函数即可向BLE设备写入数据
     *
     * @param value
     * @return
     */
    /*private synchronized boolean writeToBLE(String address, String value) {
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

        if (mGatt.writeCharacteristic(mCharacteristic)) {
            return true;
        }

        return false;
    }*/

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

        boolean isSucc = false;
        isSucc = mGatt.writeCharacteristic(mCharacteristic);

		/*synchronized(this)
		{
			try {
				this.wait(50); // 暂停线程
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		synchronized(this)
		{
			this.notify(); // 恢复线程
		}*/

		/*try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/

        return isSucc;

		/*if (isSucc) {
			return true;
		}

		return false;*/
    }

    private boolean sendAll(String message) {
        //遍历所有的连接线程去写数据
        boolean flag = true;//标志位
        Set<Map.Entry<String, BLEDevice>> mEnterySet = mBLEDevices.entrySet();
        for (Map.Entry<String, BLEDevice> entry : mEnterySet) {
            BLEDevice mDevice = entry.getValue();
            if (mDevice != null) {
                if (!writeToBLE(mDevice.address, message.getBytes())) {
                    flag = false;
                }
            }
        }
        //写完就走 别再烦我
        return flag;
    }

    private boolean sendAll(byte[] message) {
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

//	/**
//	 * 通信接口 从BLE设备读数据
//	 *
//	 * @return
//	 */
//	public byte[] readFromBLE() {
//		byte[] value = null;
//
//		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//			return null;
//		}
//
//		if ((mConnectionState != STATE_CONNECTED) || !isServiceDiscovered) {
//			return null;
//		}
//
//		boolean isSuccess = mBluetoothGatt.readCharacteristic(mCharacteristic);
//		if (isSuccess) {
//			value = mCharacteristic.getValue();
//		}
//
//		return value;
//	}
}
