package com.guohua.sdk.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.eraare.ble.BLEUtils;
import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseActivity;
import com.guohua.sdk.common.base.BaseFragment;
import com.guohua.sdk.view.fragment.SplashFragment;

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
    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
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
