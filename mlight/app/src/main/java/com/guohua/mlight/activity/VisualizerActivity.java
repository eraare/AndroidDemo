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
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.ai.IObserver;
import com.guohua.mlight.service.VisualizerService;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.view.VisualizerView;

/**
 * @author Leo
 * @detail 音乐律动时频谱的显示
 * @time 2015-11-17
 */
public class VisualizerActivity extends AppCompatActivity implements IObserver {
    private VisualizerView mVisualizerView;//频谱视图
    private TextView valueShow;//显示值
    private SeekBar personal;//随身感
    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        init();
    }

    /**
     * 初始化控件及数据
     */
    private void init() {
        initValues();
        findViewsByIds();
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
    private void findViewsByIds() {
        mVisualizerView = (VisualizerView) findViewById(R.id.vv_show_visualizer);
        valueShow = (TextView) findViewById(R.id.tv_show_visualizer);
        personal = (SeekBar) findViewById(R.id.sb_personal_visualizer);
        personal.setProgress(value);
        valueShow.setText(getString(R.string.visualizer_feel) + value);
        personal.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
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
    }

    public void back(View v) {
        this.finish();
    }
}
