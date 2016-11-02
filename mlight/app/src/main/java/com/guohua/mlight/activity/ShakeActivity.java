package com.guohua.mlight.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.service.ShakeService;
import com.guohua.mlight.util.Constant;

/**
 * @author Leo
 * @detail 摇一摇界面设计实现 可以切换摇一摇模式 开关或者随机变色
 * @time 2015-11-11
 */
public class ShakeActivity extends AppCompatActivity {
    private ImageView shake;//摇一摇图片
    private TextView show, switcher, color;//模式控件
    private boolean isSwitch = true;//状态变量
    private SeekBar threshold;
    private TextView current;
    private int currentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        /**
         * 绑定服务
         */
        Intent service = new Intent(this, ShakeService.class);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
        init();
    }

    public static final String ACTION_SHAKE_A_SHAKE = "shake_a_shake";

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_SHAKE_A_SHAKE);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);
    }

    /**
     * 初始化数据和控件
     */
    private void init() {
        initValue();
        findViewsByIds();
    }

    /**
     * 初始化状态变量
     */
    private void initValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isSwitch = sp.getBoolean(Constant.KEY_SHAKE_MODE, true);
        currentValue = sp.getInt(Constant.KEY_THRESHOLD, 17);
    }

    /**
     * 保存状态变量
     */
    private void saveValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constant.KEY_SHAKE_MODE, isSwitch).apply();
        sp.edit().putInt(Constant.KEY_THRESHOLD, currentValue).apply();
    }

    /**
     * 获取控件绑定事件
     */
    private void findViewsByIds() {
        threshold = (SeekBar) findViewById(R.id.sb_threshold_shake);
        current = (TextView) findViewById(R.id.tv_current_shake);
        shake = (ImageView) findViewById(R.id.iv_shake_shake);
        show = (TextView) findViewById(R.id.tv_show_shake);
        switcher = (TextView) findViewById(R.id.tv_switch_shake);
        color = (TextView) findViewById(R.id.tv_color_shake);
        color.setOnClickListener(mOnClickListener);
        switcher.setOnClickListener(mOnClickListener);
        changeMode();
        threshold.setProgress(currentValue);
        show.setText(getString(R.string.shake_weak));
        current.setText(getString(R.string.shake_sensitive) + currentValue);
        threshold.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    /**
     * 滑动监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentValue = progress;
            current.setText(getString(R.string.shake_sensitive) + currentValue);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            methods.changeThreshold(currentValue);
            saveValue();
        }
    };

    /**
     * 改变模式
     */
    private void changeMode() {
        if (isSwitch) {
            switcher.setBackgroundColor(getResources().getColor(R.color.greya));
            switcher.setTextColor(getResources().getColor(R.color.main));

            color.setBackgroundColor(Color.WHITE);
            color.setTextColor(Color.BLACK);
        } else {
            color.setBackgroundColor(getResources().getColor(R.color.greya));
            color.setTextColor(getResources().getColor(R.color.main));

            switcher.setBackgroundColor(Color.WHITE);
            switcher.setTextColor(Color.BLACK);
        }
        saveValue();
        if (methods != null) {
            methods.changeMode(isSwitch);
        }
    }

    public void shakeAShake() {
        AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        animationSet.addAnimation(animation);
        animationSet.setDuration(100);
        shake.startAnimation(animationSet);
    }

    /**
     * 监听事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_switch_shake:
                    isSwitch = true;
                    break;
                case R.id.tv_color_shake:
                    isSwitch = false;
                    break;
                default:
                    Toast.makeText(ShakeActivity.this, R.string.default_text, Toast.LENGTH_SHORT).show();
                    break;
            }
            changeMode();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_SHAKE_A_SHAKE)) {
                shakeAShake();
            }
        }
    };

    private ShakeService.IShakeService methods = null;

    /**
     * 服务绑定接口
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            methods = (ShakeService.IShakeService) service;
            threshold.setMax((int) methods.getMaximumRange());
            show.setText(getString(R.string.shake_weak) + (int) methods.getMaximumRange());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (methods != null) {
            methods = null;
        }
        unbindService(mServiceConnection);//解绑服务
    }

    public void back(View v) {
        this.finish();
    }
}
