package com.guohua.mlight.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.upgrade.UpgradeManager;

import butterknife.BindView;

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
}
