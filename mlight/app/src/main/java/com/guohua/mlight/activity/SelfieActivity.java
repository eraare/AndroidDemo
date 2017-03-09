package com.guohua.mlight.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.util.Constant;
import com.guohua.mlight.common.util.ShellUtils;
import com.guohua.mlight.common.util.ToolUtils;

/**
 * Created by Leo on 2016/3/18.
 */
public class SelfieActivity extends AppCompatActivity {
    private TextView open;//开启蓝牙自拍
    private TextView close;//关闭蓝牙自拍
    private TextView go;//去授权
    private boolean isSelfieRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);
        init();
    }

    private void init() {
        initValues();
        findViewsByIds();
        initControls();
    }

    private void findViewsByIds() {
        open = (TextView) findViewById(R.id.tv_open_selfie);
        close = (TextView) findViewById(R.id.tv_close_selfie);
        go = (TextView) findViewById(R.id.tv_go_selfie);
        open.setOnClickListener(mOnClickListener);
        close.setOnClickListener(mOnClickListener);
        go.setOnClickListener(mOnClickListener);
    }

    private void switchSelfie() {
//        if (isSelfieRunning) {
//            String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SELFIE, new String[]{CodeUtils.MODE_SELFIE_CLOSE});
//            ThreadPool.getInstance().addTask(new SendRunnable(data));
//            isSelfieRunning = false;
//            Toast.makeText(this, R.string.selfie_close_tip, Toast.LENGTH_SHORT).show();
//        } else {
//            String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SELFIE, new String[]{CodeUtils.MODE_SELFIE_OPEN});
//            ThreadPool.getInstance().addTask(new SendRunnable(data));
//            isSelfieRunning = true;
//            Toast.makeText(this, R.string.selfie_open_tip, Toast.LENGTH_SHORT).show();
//        }
//        initControls();
//        saveValues();
    }

    private void initControls() {
        if (isSelfieRunning) {
            open.setTextColor(getResources().getColor(R.color.main));
            open.setBackgroundColor(getResources().getColor(R.color.greya));

            close.setTextColor(getResources().getColor(R.color.black));
            close.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            close.setTextColor(getResources().getColor(R.color.main));
            close.setBackgroundColor(getResources().getColor(R.color.greya));

            open.setTextColor(getResources().getColor(R.color.black));
            open.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_open_selfie: {
                    switchSelfie();
                }
                break;
                case R.id.tv_close_selfie: {
                    switchSelfie();
                }
                break;
                case R.id.tv_go_selfie: {
                    if (!ShellUtils.checkRootPermission()) {
                        Toast.makeText(getApplicationContext(), R.string.selfie_require_root, Toast.LENGTH_SHORT).show();
                        if (ToolUtils.upgradeRootPermission(getPackageCodePath())) {
                            Toast.makeText(getApplicationContext(), R.string.selfie_root_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.selfie_root_fail, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.selfie_already_root, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isSelfieRunning = sp.getBoolean(Constant.KEY_SELFIE_RUN, false);
    }

    private void saveValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constant.KEY_SELFIE_RUN, isSelfieRunning).apply();
    }

    /**
     * 返回上一级结束自己
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }
}
