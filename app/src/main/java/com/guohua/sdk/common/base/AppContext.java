/**
 * Copyright(C)2016-2020
 * 公司：深圳市国华光电科技有限公司
 * 作者：Leo
 * QQ: 2110694775
 */

package com.guohua.sdk.common.base;

import android.app.ActivityManager;
import android.app.Application;
import android.text.TextUtils;

import com.eraare.ble.BLEUtils;
import com.guohua.sdk.R;
import com.guohua.sdk.bean.Device;
import com.guohua.sdk.common.util.CrashHandler;
import com.guohua.socket.DeviceManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Leo
 * @detail 程序入口点 处理程序异常和全局内容
 * @time 2015-10-29
 */
public class AppContext extends Application {
    private volatile static AppContext mContext;

    public static AppContext getInstance() {
        if (mContext == null) {
            synchronized (AppContext.class) {
                if (mContext == null) {
                    mContext = new AppContext();
                }
            }
        }
        return mContext;
    }

    /*保存开启APP时蓝牙的开关状态*/
    public boolean isBluetoothEnabled = BLEUtils.isBluetoothEnabled();
    /*缓存设备信息*/
    public List<Device> devices = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // 开启异常捕捉
        new CrashHandler.Builder(getApplicationContext())
                .debug(true)
                .tip(getString(R.string.error_info))
                .file("log", "crash", ".err")
                .build()
                .catching();
        mContext = this;

        /*SDK   初始化SDK中的DeviceManager*/
        DeviceManager.getInstance().initial(this);
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
            /*android.os.Process.killProcess(android.os.Process.myPid());*/
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());//2.1以下用这个
        }
    }

    public Device findDevice(String address) {
        for (Device device : devices) {
            if (TextUtils.equals(device.address, address)) {
                return device;
            }
        }
        return null;
    }
}
