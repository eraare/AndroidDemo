package com.guohua.sdk.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseActivity;
import com.guohua.sdk.common.base.BaseFragment;

import butterknife.BindView;

/**
 * @author Leo
 * @version 1
 * @since 2016-02-23
 * 关于界面主要包括 当前版本信息 版本检测更新 二维码
 */
public class AppActivity extends BaseActivity {
    @BindView(R.id.tv_version_about)
    TextView softVersion;
    @BindView(R.id.tv_firmware_about)
    TextView firmVersion;
    @BindView(R.id.tv_message_about)
    TextView message;
    @BindView(R.id.iv_upgrade_about)
    ImageView upgrade;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_app;
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        super.init(intent, savedInstanceState);
        setToolbarTitle(getString(R.string.center_about_app));
        initViews();
    }

    /**
     * 初始化控件显示内容
     */
    private void initViews() {
        upgrade.setVisibility(View.INVISIBLE);
        softVersion.setText(getString(R.string.about_software_version) + getVersionName());
        firmVersion.setText(getString(R.string.about_firmware_version));
        firmVersion.setVisibility(View.INVISIBLE);
        message.setText(getString(R.string.soft_upgrade_check));
    }

    /**
     * 获取系统的当前版本名称
     *
     * @return
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返加退出功能
     *
     * @param v
     */
    public void back(View v) {
        this.finish();
    }
}
