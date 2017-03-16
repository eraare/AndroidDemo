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
public class AppActivity extends BaseActivity {
    @BindView(R.id.tv_version_about)
    TextView softVersion;
    @BindView(R.id.tv_firmware_about)
    TextView firmVersion;
    @BindView(R.id.tv_message_about)
    TextView message;
    @BindView(R.id.iv_upgrade_about)
    ImageView upgrade;

    //用于两个类之前的消息传递
    private final Handler mHandler = new LocalHandler();
    private UpgradeManager upgradeManager;
    private boolean isChecked = false;//检查过了么
    private String mFirmVersion;

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
        upgradeManager = new UpgradeManager(this, mHandler);
        initViews();
    }

    /**
     * 初始化控件显示内容
     */
    private void initViews() {
        upgrade.setVisibility(View.INVISIBLE);
        softVersion.setText(getString(R.string.about_software_version) + upgradeManager.getCurrentVersion());
        firmVersion.setText(getString(R.string.about_firmware_version));
        firmVersion.setVisibility(View.INVISIBLE);
        message.setText(getString(R.string.soft_upgrade_check));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerTheReceiver();
        /*请求固件版本*/
        if (mFirmVersion == null) {
            String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_VERSION, null);
            ThreadPool.getInstance().addTask(new SendRunnable(data));
        }
    }

    private void registerTheReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.ACTION_FIRMWARE_VERSION);
        mFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.ACTION_FIRMWARE_VERSION)) {
                String message = intent.getStringExtra(Constants.KEY_STATUS_MESSAGE);
                String[] datas = message.split("_");
                if (datas != null && datas.length >= 3) {
                    mFirmVersion = getString(R.string.about_firmware_version) + datas[2];
                    firmVersion.setVisibility(View.VISIBLE);
                    firmVersion.setText(mFirmVersion);
                }
            }
        }
    };

    private class LocalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case UpgradeManager.WHAT_UPDATE_TRUE: {
                    message.setText(getString(R.string.soft_upgrade_yes));
                    upgrade.setVisibility(View.VISIBLE);
                }
                break;
                case UpgradeManager.WHAT_UPDATE_FALSE: {
                    message.setText(getString(R.string.soft_upgrade_no));
                    upgrade.setVisibility(View.INVISIBLE);
                }
                break;
                case UpgradeManager.WHAT_UPDATE_DOWNLOAD: {
                    if (upgradeManager != null) {
                        upgradeManager.setProgress();
                    }
                }
                break;
                case UpgradeManager.WHAT_UPDATE_FINISH: {
                    if (upgradeManager != null) {
                        upgradeManager.installApk();
                    }
                }
                default:
                    break;
            }
        }
    }

    /**
     * 检查是否要更新
     *
     * @param v
     */
    public void check(View v) {
        if (!ToolUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.about_no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isChecked) {
            return;
        }
        if (upgradeManager != null) {
            upgradeManager.check();
            message.setText(R.string.soft_upgrade_checking);
            isChecked = true;
        }
    }

    public void upgrade(View v) {
        if (!ToolUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.about_no_network, Toast.LENGTH_SHORT).show();
            return;
        }
        if (upgradeManager != null) {
            upgradeManager.update();
        }
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
