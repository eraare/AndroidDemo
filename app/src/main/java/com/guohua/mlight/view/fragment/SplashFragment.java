package com.guohua.mlight.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.view.activity.LoginActivity;
import com.guohua.mlight.view.activity.MainActivity;

import cn.bmob.v3.BmobUser;

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
    protected void init(View view, Bundle savedInstanceState) {
        // 开启自动跳转
        mHandler.postDelayed(mRunnable, DELAY);
    }

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
        /*获取本地缓存的用户信息*/
        BmobUser currentUser = BmobUser.getCurrentUser();
        if (currentUser == null) {
            /*去登陆*/
            startActivity(new Intent(mContext, LoginActivity.class));
        } else {
            /*可以使用*/
            startActivity(new Intent(mContext, MainActivity.class));
        }
        /*退出当前页面*/
        removeFragment();
    }
}
