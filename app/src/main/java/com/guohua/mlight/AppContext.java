/**
 * Copyright(C)2016-2020
 * 公司：深圳市国华光电科技有限责任公司
 * 作者：李伟（Leo）
 * QQ:532449175
 */

package com.guohua.mlight;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.guohua.mlight.bean.Device;
import com.guohua.mlight.communication.BLEService;
import com.guohua.mlight.fragment.MainFragment;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.CrashHandler;

import java.util.ArrayList;

/**
 * @author Leo
 * @detail 程序入口点 处理程序异常和全局内容 与蓝牙通信的服务作为全局对外开放
 * @time 2015-10-29
 */
public class AppContext extends Application {
    public static int currentColor;
    public static int curClickColorImgOnOff[] = {0, 0, 0, 0};
    public static int gradientRampStopGap[] = {Constant.DEFAULTSTOPGAPVALUE, Constant.DEFAULTSTOPGAPVALUE, Constant.DEFAULTSTOPGAPVALUE, Constant.DEFAULTSTOPGAPVALUE};
    public static int gradientRampGradientGap[] = {Constant.DEFAULTGRADIENTGAPVALUE, Constant.DEFAULTGRADIENTGAPVALUE, Constant.DEFAULTGRADIENTGAPVALUE, Constant.DEFAULTGRADIENTGAPVALUE};

    public static int isStartGradientRampService = 0;
    public static boolean isGradientGapRedCBChecked = false;
    public static boolean isGradientGapGreenCBChecked = false;
    public static boolean isGradientGapBlueCBChecked = false;

    /**
     * 以下为单例模式
     */
    private volatile static AppContext mAppContext = null;

    public static AppContext getInstance() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Application start");
        // 开启异常捕捉
        new CrashHandler.Builder(getApplicationContext())
                .debug(false)
                .tip(getString(R.string.error_info))
                .file("log", "crash", ".err")
                .build()
                .catching();
        mAppContext = this;
        openBLEService();
    }

    /**
     * 打开蓝牙串口通信服务 取得通信接口
     */
    private void openBLEService() {
        Intent service = new Intent(this, BLEService.class);
        startService(service);
        isBind = bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private BLEService mBLEService;//提供给外部的服务接口
    public boolean isBind;

    /**
     * 服务连接回调函数
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            mBLEService = IBLEService.Stub.asInterface(service);
            mBLEService = ((BLEService.LocalBinder) service).getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBLEService = null;
            isBind = false;
        }
    };

    /**
     * 关闭蓝牙串口通信服务
     */
    public void closeBLEService() {
        Intent service = new Intent(this, BLEService.class);
        System.out.println(isBind);
        if (isBind) {
            unbindService(mServiceConnection);
            isBind = false;
        }
        stopService(service);
    }

    /**
     * 根据设备地址连接蓝牙设备
     *
     * @param deviceAddress
     */
    public boolean connect(String deviceAddress) {
        if (mBLEService == null) {
            return false;
        }
        return mBLEService.connect(deviceAddress);
    }

    public void connectAll() {
        for (Device device : devices) {
            if (device.isSelected()) {
                connect(device.getDeviceAddress());
            }
        }
    }

    /**
     * 通过地址断开连接设备
     */
    public boolean disonnect(String deviceAddress, boolean remove) {
        if (mBLEService == null) {
            return false;
        }
        mBLEService.disconnect(deviceAddress, remove);
        return true;
    }

    /**
     * 与现有所有连接的蓝牙设备断开连接
     */
    public boolean disonnectAll() {
        if (mBLEService == null) {
            return false;
        }
        mBLEService.disconnectAll();
        return true;
    }

    public boolean isConnect(String deviceAddress) {
        return mBLEService.isConnected(deviceAddress);
    }

    /**
     * 通过蓝牙发送数据
     *
     * @param message
     */
    public boolean send(String deviceAddress, String message) {
        if (message.contains("close") || message.contains("ctl:0:0:0:0:")) {
//            System.out.println("send   R.drawable.icon_light_off");
            MainFragment.isLighting = false;
        } else if (message.contains("de") || message.contains("dl")) {//定时功能

        } else {
            // System.out.println("send  R.drawable.icon_light_on");
            MainFragment.isLighting = true;
        }
        if (mBLEService == null) {
            return false;
        }
        boolean isSucc = mBLEService.send(deviceAddress, message.getBytes());
        /*if(isSucc){//本来发送成功才改变灯为开的状态才是合理的
            if(message.contains("close") || message.contains("ctl:0:0:0:0:")){
                System.out.println("send   R.drawable.icon_light_off");
                MainFragment.isLighting = false;
            }else{
                System.out.println("send  R.drawable.icon_light_on");
                MainFragment.isLighting = true;
            }
        }*/
        return isSucc;
    }

    public boolean send(String deviceAddress, byte[] message) {
        if (mBLEService == null) {
            return false;
        }
        return mBLEService.send(deviceAddress, message);
    }

    /**
     * 通过蓝牙向所有已连接 的设备发送数据
     *
     * @param message
     */
    /*public boolean sendAll(String message) {
        if (mBLEService == null) {
            return false;
        }
        return mBLEService.sendAll(message);
    }*/
    public void sendAll(String message) {
        for (Device device : devices) {
            if (device.isSelected()) {
                send(device.getDeviceAddress(), message);
            }
        }
    }

    public void sendAll(byte[] message) {
        for (Device device : devices) {
            if (device.isSelected()) {
                send(device.getDeviceAddress(), message);
            }
        }
    }

    public ArrayList<Device> devices = new ArrayList<>();

    public void addDevice(Device device) {
        for (Device temp : devices) {
            if (TextUtils.equals(temp.getDeviceAddress(), device.getDeviceAddress())) {
                return;
            }
        }
        this.devices.add(device);
        connect(device.getDeviceAddress());
    }

    /**
     * 彻底退出APP
     */
    public void exitApplication() {
        int currentVersion = android.os.Build.VERSION.SDK_INT;//获取当前版本
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
         /*   Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);*/
            System.exit(0);//退出的重点在这里
//            android.os.Process.killProcess(android.os.Process.myPid());
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());//2.1以下用这个
        }
    }
}
