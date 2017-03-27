package com.guohua.mlight.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.lwble.BLEUtils;
import com.guohua.mlight.view.fragment.SplashFragment;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * @file SplashActivity.java
 * @author Leo
 * @version 1
 * @detail 欢迎界面的Activity
 * @since 2016/12/19 11:47
 */

/**
 * 文件名：SplashActivity.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/19 11:47
 * 描  述：欢迎界面的Activity
 */
public class SplashActivity extends BaseActivity {
    /*Bmob的Application_id*/
    private static final String APPLICATION_ID = "bd5687d8f691de6e530fde446f273582";

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        /*1 初始化Bmob*/
        Bmob.initialize(this, APPLICATION_ID);
        /*使用推送服务时的初始化操作*/
        BmobInstallation.getCurrentInstallation().save();
        /*启动推送服务*/
        BmobPush.startWork(this);

        /*2 检查蓝牙状态*/
        if (!BLEUtils.isSupportBluetoothBLE(this)) {
            toast(R.string.activity_bluetooth_tip_splash);
            finish();
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_container_splash;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return SplashFragment.getInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
