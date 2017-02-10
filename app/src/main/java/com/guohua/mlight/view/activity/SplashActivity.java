package com.guohua.mlight.view.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.util.BLEUtil;
import com.guohua.mlight.view.fragment.SplashFragment;

import cn.bmob.v3.Bmob;

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
    private static final String APPLICATION_ID = "7d6c82d807e2db420de5c76a0fb0912e";

    @Override
    protected void init(Bundle savedInstanceState) {
        /*1 初始化Bmob*/
        Bmob.initialize(this, APPLICATION_ID);
        /*2 检查蓝牙状态*/
        if (!BLEUtil.isSupportBluetoothBLE(this)) {
            toast("您的设备不支持BLE");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*如果蓝牙未打开则打开蓝牙*/
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
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
}
