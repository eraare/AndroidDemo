package com.guohua.mlight.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
        setContentView(getContentViewId());
        /*1 绑定控件*/
        mUnbinder = ButterKnife.bind(this);
        /*2 处理传递的Intent数据*/
        handleIntent(getIntent());
        /*3 Activity的相关初始化*/
        init(savedInstanceState);
        /*4 配置第一个Fragment*/
        setupFirstFragment();
        /*5 一些特殊需要在onCreate完成后进行*/
        initAfterCreate();
    }

    /**
     * onCreate()后onStart()前进行的只有一次的初始化
     */
    protected void initAfterCreate() {
    }

    /**
     * 配置第一个Fragment
     */
    private void setupFirstFragment() {
        if (null == getSupportFragmentManager().getFragments()) {
            BaseFragment firstFragment = getFirstFragment();
            if (null != firstFragment) {
                addFragment(firstFragment);
            }
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
    protected void init(Bundle savedInstanceState) {
    }

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
