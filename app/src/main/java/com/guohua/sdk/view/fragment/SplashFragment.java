package com.guohua.sdk.view.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;

import com.eraare.ble.BLEUtils;
import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseFragment;
import com.guohua.sdk.view.activity.MainActivity;

/**
 * @file SplashFragment.java
 * @author Leo
 * @version 1
 * @detail 默认的欢迎界面
 * @since 2016/12/19 16:17
 */

/**
 * 文件名：SplashFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/19 16:17
 * 描  述：默认的欢迎界面
 */
public class SplashFragment extends BaseFragment {
    /*单例模式*/
    private volatile static SplashFragment singleton = null;

    public static SplashFragment getInstance() {
        if (singleton == null) {
            synchronized (SplashFragment.class) {
                if (singleton == null) {
                    singleton = new SplashFragment();
                }
            }
        }
        return singleton;
    }

    /*处理自动跳转*/
    private static final long DELAY = 1500;
    private Handler mHandler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_splash;
    }

    /**
     * 要执行的线程
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startTheActivity();
        }
    };

    /**
     * 启动主Activity
     */
    private void startTheActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        removeFragment();
    }

    /*请求打开蓝牙*/
    private static final int REQUEST_BLUETOOTH = 1;

    @Override
    public void onResume() {
        super.onResume();
        if (!BLEUtils.isBluetoothEnabled()) {
            /*如果蓝牙未打开就打开蓝牙*/
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH);
        } else {
            /*如果蓝牙已打开则进行延时展示*/
            mHandler.postDelayed(mRunnable, DELAY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                mContext.toast(R.string.fragment_denied_tip_splash);
            }
            startTheActivity();
        }
    }
}
