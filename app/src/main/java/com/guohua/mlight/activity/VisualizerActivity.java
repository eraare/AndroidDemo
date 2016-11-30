package com.guohua.mlight.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.ai.IObserver;
import com.guohua.mlight.service.VisualizerService;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToolUtils;
import com.guohua.mlight.view.VisualizerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * @author Leo
 * @detail 音乐律动时频谱的显示
 * @time 2015-11-17
 */
public class VisualizerActivity extends AppCompatActivity implements IObserver {
    /*Section: 绑定控件*/
    @BindView(R.id.vv_show_visualizer)
    VisualizerView mVisualizerView;
    @BindView(R.id.tv_show_visualizer)
    TextView valueShow;
    @BindView(R.id.sb_personal_visualizer)
    SeekBar personal;
    @BindView(R.id.s_background_visualizer)
    Switch background;
    //private Unbinder unbinder;
    /*Section: 属性*/
    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化控件及数据
     */
    private void init() {
        initValues();
        initViews();
        Intent service = new Intent(this, VisualizerService.class);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        value = sp.getInt(Constant.KEY_PERSONAL_FEEL, 1);
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        personal.setProgress(value);
        valueShow.setText(getString(R.string.visualizer_feel) + value);
        personal.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        if (!ToolUtils.isServiceRunning(getApplicationContext(), VisualizerService.class.getName())) {
            System.out.println("false");
            background.setChecked(false);
        }
    }

    @OnCheckedChanged(R.id.s_background_visualizer)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startTheService();
        } else {
            stopTheService();
        }
        System.out.println("Service" + isChecked);
    }

    /**
     * 后台运行开启服务
     */
    private void startTheService() {
        Intent service = new Intent(this, VisualizerService.class);
        startService(service);
    }

    /**
     * 关闭后台运行
     */
    private void stopTheService() {
        Intent service = new Intent(this, VisualizerService.class);
        stopService(service);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            changeFeel(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void changeFeel(int progress) {
        saveValues(progress);
        valueShow.setText(getString(R.string.visualizer_feel) + progress);
        mService.changeTheFeel(progress);
    }

    private void saveValues(int progress) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt(Constant.KEY_PERSONAL_FEEL, progress).apply();
    }

    private VisualizerService.IVisualizerService mService;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (VisualizerService.IVisualizerService) service;
            if (mService != null) {
                mService.registerTheObserver(VisualizerActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mService != null) {
                mService.unregisterTheObserver(VisualizerActivity.this);
                mService = null;
            }
        }
    };

    @Override
    public void update(byte[] bytes) {
        mVisualizerView.updateVisualizer(bytes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        /*if (unbinder != null) {
            unbinder.unbind();
        }*/
    }

    public void back(View v) {
        this.finish();
    }
}
