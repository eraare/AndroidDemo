/**
 * Copyright(C)2016-2020
 * 公司：深圳市国华光电科技有限责任公司
 * 作者：李伟（Leo）
 * QQ:532449175
 */

package com.guohua.mlight.common.base;

import android.app.ActivityManager;
import android.app.Application;

import com.guohua.mlight.R;
import com.guohua.mlight.common.util.CrashHandler;
import com.guohua.mlight.model.bean.Device;

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
            startActivity(startMain);
            System.exit(0);*/
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {// android2.1
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(getPackageName());//2.1以下用这个
        }
    }
}
