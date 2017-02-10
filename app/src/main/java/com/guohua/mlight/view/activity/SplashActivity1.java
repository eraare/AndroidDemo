package com.guohua.mlight.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.ToolUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Leo
 * @detail 程序进入时的首页面主要做公司宣传程序版本数据加载操作
 * @time 2015-10-30
 */
public class SplashActivity1 extends AppCompatActivity {
    public static final long DELAY = 1500;//等待时延
    private Handler mHandler = new Handler();//控制Handler
    /*Section: 加载控件*/
    @BindView(R.id.tv_title_splash)
    TextView title;//标题 魔小灯
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);
        mUnbinder = ButterKnife.bind(this);

        //android6.0 运行时申请蓝牙权限
        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH, Constants.MY_PERMISSIONS_REQUEST_BLUETOOTH);

        //android6.0 运行时申请操作蓝牙权限
        ToolUtils.requestPermissions(this, Manifest.permission.BLUETOOTH_ADMIN, Constants.MY_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN);

        //android6.0 运行时申请定位权限（android6.0 下，要使用蓝牙需同时开启定位权限）
        ToolUtils.requestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION, Constants.MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        /*设置Splash里的内容文字为特定字体*/
        setFont();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.default_text, Toast.LENGTH_LONG).show();
            AppContext.getInstance().closeBLEService();
            finish();
        } else {
            mHandler.postDelayed(mRunnable, DELAY);//DELAY后发出线程
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                saveBluetoothState(false);
            } else {
                saveBluetoothState(true);
            }
        }

        showTitleAnimation();
    }

    /**
     * 显示动画效果
     */
    private void showTitleAnimation() {
//        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation animation = new AlphaAnimation(0f, 1.0f);
        animation.setDuration(DELAY);
        title.startAnimation(animation);
    }

    /**
     * 存储蓝牙的初始状态
     *
     * @param isStart
     */
    private void saveBluetoothState(boolean isStart) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constants.KEY_BLUETOOTH_INIT_STATE, isStart).apply();
    }

    /**
     * 设置显示的字体为特定字体
     */
    private void setFont() {
        TextView info = (TextView) findViewById(R.id.tv_info_splash);
        info.setTypeface(Typeface.createFromAsset(getAssets(), "font/zjjgbxs.ttf"));
    }

    /**
     * 要执行的线程
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startTheActivity();
        }
    };

    /**
     * 启动主Activity
     */

    private void startTheActivity() {
//        Intent intent = new Intent(this, ConnectActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
//        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 扑捉触屏事件 用户可以点击屏幕快速进入
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            mHandler.removeCallbacks(mRunnable);
            startTheActivity();
        }
        return true;
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
