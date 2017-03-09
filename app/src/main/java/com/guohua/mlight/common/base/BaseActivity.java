package com.guohua.mlight.common.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @file BaseActivity.java
 * @author Leo
 * @version 1
 * @detail 所有Activity的基类 用于处理公共的操作
 * @since 2016/12/14 8:53
 */

/**
 * 文件名：BaseActivity.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/14 8:53
 * 描  述：所有Activity的基类 用于处理公共的操作
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder; // 取消绑定

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*1 处理全屏或其他窗口*/
        //steepStatusBar();
        //setStatusBarTransparent();
        setContentView(getContentViewId());
        //StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main), true);
        /*2 添加Activity到Application*/
        //AppContext.getInstance().addActivity(this);
        /*3 绑定控件*/
        mUnbinder = ButterKnife.bind(this);
        /*4 处理传递的Intent数据*/
        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        /*5 Activity的相关初始化*/
        init(savedInstanceState);
        /*6 配置第一个Fragment*/
        setupFirstFragment();
        /*7 一些特殊需要在onCreate完成后进行*/
        initAfterCreate();
    }

    /**
     * onCreate()后onStart()前进行的只有一次的初始化
     */
    public void initAfterCreate() {
    }

    /**
     * 配置第一个Fragment
     */
    private void setupFirstFragment() {
        if (null == getSupportFragmentManager().getFragments()) {
            BaseFragment firstFragment = getFirstFragment();
            if (null != firstFragment) {
                addFragment(firstFragment);
                /*String tag = firstFragment.getClass().getSimpleName();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(getFragmentContainerId(), firstFragment, tag);
                transaction.commit();*/
            }
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 将状态栏透明化
     */
    public void setStatusBarTransparent() {
        //5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    /**
     * 获取布局文件的id
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 处理接收到的Intent
     *
     * @param intent
     */
    protected void handleIntent(Intent intent) {
        // override to handle intent
    }

    /**
     * onCreate()阶段的一些初始化
     */
    protected abstract void init(Bundle savedInstanceState);

    /**
     * 添加fragment到activity
     *
     * @param fragment
     */
    protected void addFragment(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String tag = fragment.getClass().getSimpleName();
        transaction.replace(getFragmentContainerId(), fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 获取第一个Fragment
     *
     * @return
     */
    protected abstract BaseFragment getFirstFragment();

    /**
     * 移除会退栈中的fragment
     */
    protected void removeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            manager.popBackStack();
        } else {
            finish();
        }
    }

    /**
     * 获取fragment容器id
     *
     * @return
     */
    protected abstract int getFragmentContainerId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁一些东西
        suicide();
        // 取消控件绑定
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        //AppContext.getInstance().removeActivity(this);
    }

    /**
     * onDestroy()阶段的一些销毁操作
     */
    protected void suicide() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Toast mToast;

    /**
     * 页面消息提示
     *
     * @param message
     */
    public void toast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 页面消息提示
     *
     * @param id
     */
    public void toast(int id) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, id, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
