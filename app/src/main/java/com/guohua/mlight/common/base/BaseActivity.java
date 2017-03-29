package com.guohua.mlight.common.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;

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
    protected Toolbar mToolbar; /*标题导航栏*/
    private Unbinder mUnbinder; // 取消绑定
    private boolean showBack;
    private TextView mForwardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideNavigationBar();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        mUnbinder = ButterKnife.bind(this); /*使用ButterKnife*/
        setupToolbar(); /*配置Toolbar*/
        init(getIntent(), savedInstanceState); /*处理Intent及初始化*/
        setupFirstFragment(); /*加载第一个Fragment*/
        initAfterCreate(); /*之后的初始化操作*/
    }

    /**
     * 隐藏底部的NavigationBar
     */
    public void hideNavigationBar() {
        int uiFlags = /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | */View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; // hide nav bar
                /*| View.SYSTEM_UI_FLAG_FULLSCREEN;*/ // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    /**
     * 获取布局文件的id
     *
     * @return
     */
    protected abstract int getContentViewId();

    /**
     * 配置Toolbar
     */
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(getToolbarId());
        mForwardView = (TextView) findViewById(getForwardId());
        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(mToolbar);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeFragment();
        }
    };

    protected int getToolbarId() {
        return R.id.t_toolbar_base;
    }

    protected int getForwardId() {
        return R.id.iv_forward_base;
    }

    /**
     * onCreate()阶段的一些初始化
     */
    protected void init(Intent intent, Bundle savedInstanceState) {
        showBack = true;
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
     * 获取第一个Fragment
     *
     * @return
     */
    protected abstract BaseFragment getFirstFragment();

    /**
     * onCreate()后onStart()前进行的只有一次的初始化
     */
    protected void initAfterCreate() {
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
     * 获取fragment容器id
     *
     * @return
     */
    protected abstract int getFragmentContainerId();

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

    @Override
    protected void onStart() {
        super.onStart();
        if (mToolbar != null && showBack) {
            mToolbar.setNavigationIcon(R.drawable.icon_back_white);
            mToolbar.setNavigationOnClickListener(mOnClickListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁一些东西
        suicide();
        // 取消控件绑定
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            hideNavigationBar();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    /*接口*/
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    public void setToolbarTitle(int id) {
        mToolbar.setTitle(id);
    }

    public void setShowBack(boolean showBack) {
        this.showBack = showBack;
    }

    public void setForwardVisibility(int visibility) {
        mForwardView.setVisibility(visibility);
    }

    public void setForwardTitle(String title) {
        mForwardView.setText(title);
    }

    public void setForwardTitle(int id) {
        mForwardView.setText(id);
    }

    public void setOnForwardClickListener(View.OnClickListener onClickListener) {
        mForwardView.setOnClickListener(onClickListener);
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
        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
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
        mToast = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /*连接对话框*/
    private ProgressDialog mProgressDialog;

    /**
     * 显示进度对话框
     *
     * @param title
     * @param message
     */
    public void showProgressDialog(String title, String message) {
        mProgressDialog = ProgressDialog.show(this, title, message, true, false);
    }

    /**
     * dismiss进度对话框
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
