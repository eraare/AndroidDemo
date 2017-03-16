package com.guohua.mlight.view.activity;

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
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.ToolUtils;
import com.guohua.mlight.service.ShakeService;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Leo
 * @detail 摇一摇界面设计实现 可以切换摇一摇模式 开关或者随机变色
 * @time 2015-11-11
 */
public class ShakeActivity extends BaseActivity {
    public static final String ACTION_SHAKE_A_SHAKE = "shake_a_shake";
    /*Section: 绑定控件*/
    @BindView(R.id.sb_threshold_shake)
    SeekBar threshold;
    @BindView(R.id.tv_current_shake)
    TextView current;
    @BindView(R.id.iv_shake_shake)
    ImageView shake;
    @BindView(R.id.tv_show_shake)
    TextView show;
    @BindView(R.id.tv_switch_shake)
    TextView switcher;
    @BindView(R.id.tv_color_shake)
    TextView color;
    @BindView(R.id.s_background_shake)
    Switch background;

    private boolean isSwitch = true;//状态变量
    private int currentValue;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_shake;
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
        setToolbarTitle(getString(R.string.scene_shake_shake));
        initValue();
        initViews();

        Intent service = new Intent(this, ShakeService.class);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ACTION_SHAKE_A_SHAKE);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);
    }

    /**
     * 初始化状态变量
     */
    private void initValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        isSwitch = sp.getBoolean(Constants.KEY_SHAKE_MODE, true);
        currentValue = sp.getInt(Constants.KEY_THRESHOLD, 17);
    }

    /**
     * 保存状态变量
     */
    private void saveValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constants.KEY_SHAKE_MODE, isSwitch).apply();
        sp.edit().putInt(Constants.KEY_THRESHOLD, currentValue).apply();
    }

    /**
     * 获取控件绑定事件
     */
    private void initViews() {
        changeMode();
        threshold.setProgress(currentValue);
        show.setText(getString(R.string.shake_weak));
        current.setText(getString(R.string.shake_sensitive) + currentValue);
        threshold.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        if (!ToolUtils.isServiceRunning(getApplicationContext(), ShakeService.class.getName())) {
            System.out.println("ShakeService: " + ShakeService.class.getName());
            background.setChecked(false);
        }
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

    @OnCheckedChanged(R.id.s_background_shake)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            startTheService();
        } else {
            stopTheService();
        }
        System.out.println("Service" + isChecked);
    }

    private void startTheService() {
        Intent service = new Intent(this, ShakeService.class);
        startService(service);
    }

    private void stopTheService() {
        Intent service = new Intent(this, ShakeService.class);
        stopService(service);
    }

    /**
     * 监听事件
     */
    @OnClick({R.id.tv_switch_shake, R.id.tv_color_shake})
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
    protected void suicide() {
        super.suicide();
        if (methods != null) {
            methods = null;
        }
        unbindService(mServiceConnection);//解绑服务
    }

    public void back(View v) {
        this.finish();
    }
}
