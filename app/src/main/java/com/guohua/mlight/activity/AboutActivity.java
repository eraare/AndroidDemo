package com.guohua.mlight.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.upgrade.UpgradeManager;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToolUtils;

/**
 * @author Leo
 * @time 2016-02-23
 * @detail 关于界面主要包括 当前版本信息 版本检测更新 二维码
 */
public class AboutActivity extends AppCompatActivity {
    private TextView softVersion, message;//当前版本信息和更新提示信息
    private TextView firmVersion;//固件版本
    private ImageView upgrade;//升级
    private Handler mHandler;//用于两个类之前的消息传递
    private UpgradeManager upgradeManager;
    private boolean isChecked = false;//检查过了么

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init() {
        mHandler = new Handler() {
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
        };
        upgradeManager = new UpgradeManager(this, mHandler);
        findViewsByIds();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constant.ACTION_FIRMWARE_VERSION);
        mFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, mFilter);
    }

    private void findViewsByIds() {
        softVersion = (TextView) findViewById(R.id.tv_version_about);
        firmVersion = (TextView) findViewById(R.id.tv_firmware_about);
        message = (TextView) findViewById(R.id.tv_message_about);
        upgrade = (ImageView) findViewById(R.id.iv_upgrade_about);

        upgrade.setVisibility(View.INVISIBLE);
        softVersion.setText(getString(R.string.about_software_version) + upgradeManager.getCurrentVersion());
        firmVersion.setText(getString(R.string.about_firmware_version));
        firmVersion.setVisibility(View.INVISIBLE);
        message.setText(getString(R.string.soft_upgrade_check));
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

    /**
     * 返加退出功能
     *
     * @param v
     */
    public void back(View v) {
        this.finish();
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constant.ACTION_FIRMWARE_VERSION)) {
                String message = intent.getStringExtra(Constant.KEY_STATUS_MESSAGE);
                String[] datas = message.split("_");
                if (datas != null && datas.length >= 3) {
                    firmVersion.setVisibility(View.VISIBLE);
                    firmVersion.setText(getString(R.string.about_firmware_version) + datas[2]);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_VERSION, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
