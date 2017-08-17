package com.guohua.sdk.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.guohua.sdk.R;
import com.guohua.sdk.common.base.BaseActivity;
import com.guohua.sdk.common.base.BaseFragment;

/**
 * @author Leo
 * @time 2016-02-23
 * @detail 关于界面主要包括 当前版本信息 版本检测更新 二维码
 */
public class UsActivity extends BaseActivity {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_us;
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
        setToolbarTitle(getString(R.string.center_about_us));
    }
}
