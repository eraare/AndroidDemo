/**
 * Copyright(C)2016-2020
 * 公司：深圳市国华光电科技有限责任公司
 * 作者：李伟（Leo）
 * QQ:532449175
 */

package com.guohua.mlight.common.base;

import android.app.ActivityManager;
import android.app.Application;
import android.graphics.Color;
import android.text.TextUtils;

import com.guohua.mlight.R;
import com.guohua.mlight.common.util.CrashHandler;
import com.guohua.mlight.model.bean.Device;
import com.guohua.mlight.model.bean.LightInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Leo
 * @detail 程序入口点 处理程序异常和全局内容 与蓝牙通信的服务作为全局对外开放
 * @time 2015-10-29
 */
public class AppContext extends Application {
    private volatile static AppContext mAppContext = null;

    public static AppContext getInstance() {
        return mAppContext;
    }

    /*缓存设备列表*/
    public List<LightInfo> lights = new CopyOnWriteArrayList<>();
    public List<Device> devices = new CopyOnWriteArrayList<>();
    /*所有设备的通用状态*/
    public boolean isLightOn = true; /*灯是否打开状态*/
    /*色相 0-360 饱和度 0-1 明度 0-1*/
    public float[] currentHSV = {0F, 0F, 1F};
    public int currentAlpha = 255;
//    public int currentColor = Color.WHITE;

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
        mAppContext = this;
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

    /**
     * 添加设备
     */
    public void addLight(LightInfo light) {
        for (LightInfo temp : lights) {
            if (TextUtils.equals(temp.address, light.address)) {
                return;
            }
        }
        lights.add(light);
    }

    /**
     * 查找设备
     *
     * @param address
     * @return
     */
    public LightInfo findLight(String address) {
        int size = lights.size();
        for (int i = 0; i < size; i++) {
            LightInfo light = lights.get(i);
            if (TextUtils.equals(address, light.address)) {
                return light;
            }
        }
        return null;
    }
}
