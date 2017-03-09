package com.guohua.mlight.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.ToastUtill;
import com.guohua.mlight.view.activity.MainActivity;
import com.guohua.mlight.view.widget.holocolorpicker.ColorPicker;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class MainFragment1 extends Fragment {
    private MainActivity mContext = null;
    private ThreadPool pool = null;//线程池 向蓝牙设备发送控制数据等

    public static final String TAG = MainFragment1.class.getSimpleName();
    public static final String TITLE = "魔小灯";
    /**
     * 单例模式
     */
    private volatile static MainFragment1 mainFragment = null;

    public static MainFragment1 newInstance() {
        if (mainFragment == null) {
            synchronized (MainFragment1.class) {
                if (mainFragment == null) {
                    mainFragment = new MainFragment1();
                }
            }
        }
        return mainFragment;
    }

    public static final int WHAT_TIMER = 1;

    /**
     * 布局中的控件
     */
    private View rootView;//主布局
    private SeekBar changeBrightness, changeTimer;//改变值的SeekBar
    private ColorPicker colorPicker;//颜色选择器
    private TextView showBrightness, showTimer;
    private ImageView iv_timer_scene;

    private float objectTime;//目标时间
    private boolean isLighting;//是否开灯了
    private int currentBrightness;//当前亮度
    private int currentColor;//当前颜色值
    private int currentTime;//当前时间

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int what = msg.what;
            if (what == WHAT_TIMER) {
                currentTime--;
                if (currentTime <= 0) {
                    currentTime = 0;
                    if (timer != null) {
                        timer.cancel();
                        objectTime = 0;
                    }
                    //关灯
                    String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
                    pool.addTask(new SendRunnable(data));
                }
                changeTimer.setProgress(currentTime);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main2, container, false);
        init();
        return rootView;
    }

    /**
     * 初始化数据和控件
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        pool = ThreadPool.getInstance();
        initValues();//初始化SharedPreference
        initData();//初始化数据
        findViewsByIds();
        registerReceiver();
        isLighting = true;
    }

    private void initData() {
        currentBrightness = 0;
        float now = System.currentTimeMillis();
        if (now - objectTime < 0) {
            currentTime = (int) ((objectTime - now) / (1000 * 60));
            startTimer();
        } else {
            currentTime = 0;
        }
    }

    /**
     * 从SharedPreference中读取相关数据
     */
    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        isLighting = sp.getBoolean(Constants.KEY_DEVICE_SWITCH, true);
        currentColor = sp.getInt(Constants.KEY_DEVICE_COLOR, Color.WHITE);
        objectTime = sp.getFloat(Constants.KEY_TIMER, 0);
    }

    private void saveValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.KEY_DEVICE_COLOR, currentColor).apply();
        editor.putBoolean(Constants.KEY_DEVICE_SWITCH, isLighting).apply();
        editor.putFloat(Constants.KEY_TIMER, objectTime).apply();
    }

    /**
     * 得到所有控件 并绑定相应的事件
     */
    private void findViewsByIds() {
        changeBrightness = (SeekBar) rootView.findViewById(R.id.sb_brightness_main);
        changeTimer = (SeekBar) rootView.findViewById(R.id.sb_timer_main);
        colorPicker = (ColorPicker) rootView.findViewById(R.id.cp_picker_main);
        showBrightness = (TextView) rootView.findViewById(R.id.tv_brightness_main);
        showTimer = (TextView) rootView.findViewById(R.id.tv_timer_main);
        iv_timer_scene = (ImageView) rootView.findViewById(R.id.iv_timer_scene);

        changeBrightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        changeTimer.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        colorPicker.setOnColorChangedListener(mOnColorChangedListener);
        colorPicker.setOnCenterClickListener(mOnCenterClickListener);
        iv_timer_scene.setOnClickListener(mOnClickListener);

        changeTimer.setProgress(currentTime);
        changeBrightness.setProgress(currentBrightness);

        showBrightness.setText(getString(R.string.main_brightness_tip) + currentBrightness);
        showTimerInfo(currentTime);
        colorPicker.setColor(currentColor); //设置颜色时会触发发送数据
        colorPicker.setOldCenterColor(getResources().getColor(R.color.main));
    }

    private void showTimerInfo(int progress) {
        if (progress == 0) {
            showTimer.setText(getString(R.string.main_timer_tip_zero));
        } else {
            showTimer.setText(progress + getString(R.string.main_timer_tip));
        }
    }

    private ColorPicker.OnCenterClickListener mOnCenterClickListener = new ColorPicker.OnCenterClickListener() {
        @Override
        public void onCenterClick() {
            switchLight();
            //Toast.makeText(mContext, "ColorPicker.OnCenterClickListener", Toast.LENGTH_SHORT).show();
            //System.out.println(isLighting + "   ColorPicker.OnCenterClickListener---------------------------------");
        }
    };

    private ColorPicker.OnColorChangedListener mOnColorChangedListener = new ColorPicker.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            changeTheColor(color);
        }
    };

    /**
     * 相关的单击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.iv_timer_scene: {
//                    showTimerDialog();
                    ToastUtill.showToast(mContext, "定时功能正在完善中...", Constants.TOASTLENGTH).show();
                }
                break;
                default:
                    break;
            }
        }
    };

    private void showTimerDialog() {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(TimerFragment.TAG);
        if (fragment == null) {
            fragmentTransaction.add(TimerFragment.getInstance(), TimerFragment.TAG);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 开关灯
     */
    private void switchLight() {
        String data;
        if (isLighting) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
            isLighting = false;
            colorPicker.setOldCenterColor(Color.BLACK);
            //colorPicker.setNewCenterColor(Color.BLACK);

//            Toast.makeText(mContext, "guandeng", Toast.LENGTH_SHORT).show();
            System.out.println("guandeng SendRunnable data = " + data);

        } else {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
            isLighting = true;
            colorPicker.setOldCenterColor(getResources().getColor(R.color.main));
//            Toast.makeText(mContext, "kaideng", Toast.LENGTH_SHORT).show();
            System.out.println("kaideng SendRunnable data = " + data);
            //colorPicker.setNewCenterColor(getResources().getColor(R.color.main));
        }
//        AppContext.getInstance().sendAll(data);
        System.out.println("SendRunnable data = " + data);

        if (!isLighting && (AppContext.isGradientGapRedCBChecked || AppContext.isGradientGapGreenCBChecked || AppContext.isGradientGapBlueCBChecked)) {

//            AppContext.getInstance().sendAll(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null));
            String stopRamp = new String(new byte[]{0x78, 0, 0, 0, 0, 0, 0, 0});
            String musicOff = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
//            pool.addTask(new SendRunnable(stopRamp));
            pool.addMusicOffTask(new SendRunnable(stopRamp));

            /*pool.addMusicOffTask(new SendRunnable(musicOff));
            pool.addMusicOffTask(new SendRunnable(data));*/

            AppContext.isGradientGapRedCBChecked = false;
            AppContext.isGradientGapGreenCBChecked = false;
            AppContext.isGradientGapBlueCBChecked = false;
            System.out.println("stop ramp SendRunnable stopRamp = " + stopRamp);
            System.out.println("musicOff SendRunnable musicOff = " + musicOff);
//            Intent service = new Intent(mContext, GradientRampService.class);
//            mContext.stopService(service);
        } else {
            pool.addTask(new SendRunnable(data));
        }

    }

    /**
     * SeekBar的滑动值改变事件监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            if (id == R.id.sb_brightness_main) {
                showBrightness.setText(getString(R.string.main_brightness_tip) + progress);
                currentBrightness = progress;
//                changeTheBrightness(progress);
                changeColorBrightness(progress);
            } else if (id == R.id.sb_timer_main) {
                currentTime = progress;
                showTimerInfo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int id = seekBar.getId();

            if (id == R.id.sb_timer_main) {
                int progress = seekBar.getProgress();
                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{progress * 60});
                pool.addTask(new SendRunnable(data));
                objectTime = System.currentTimeMillis() + progress * 60 * 1000;
                startTimer();
            }
        }
    };

    private Timer timer;
    private TimerTask timerTask;

    /**
     * 启动计时器来倒计时
     */
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(WHAT_TIMER);
            }
        };
        timer.schedule(timerTask, 1000 * 60, 60 * 1000);
    }

    /**
     * 改变程序的亮度
     *
     * @param progress
     *//*
    private void changeTheBrightness(int progress) {
        *//**
     * 正确的方法
     *//*
        String data;
        //如果是白色就调所有灯珠
        if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{progress, progress, progress, progress});
        } else {
            //其它颜色就把白色灯珠设为0只调其它灯珠
            int alpha = 0;
            int red = Color.red(currentColor) * progress / 255;
            int green = Color.green(currentColor) * progress / 255;
            int blue = Color.blue(currentColor) * progress / 255;
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        }
        pool.addTask(new SendRunnable(data));
    }*/

    /**
     * 改变程序的亮度
     *
     * @param progress
     */
    private void changeTheBrightness(int progress) {
        /**
         * 正确的方法
         */
        int alpha = progress;
        int red = Color.red(currentColor);
        int green = Color.green(currentColor);
        int blue = Color.blue(currentColor);
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});

        pool.addTask(new SendRunnable(data));
    }

    /**
     * 改变当前彩色亮度
     *
     * @param progress
     */
    private void changeColorBrightness(int progress) {

        String data;
        if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{progress, progress, progress, progress});
        } else {
            int alpha = 0;
            int red = Color.red(currentColor) * progress / 255;
            int green = Color.green(currentColor) * progress / 255;
            int blue = Color.blue(currentColor) * progress / 255;
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        }

        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            colorPicker.setOldCenterColor(getResources().getColor(R.color.main));
            //colorPicker.setNewCenterColor(getResources().getColor(R.color.main));
        }
    }


    /**
     * 改变当前颜色
     *
     * @param color
     *//*
    private void changeTheColor(int color) {
        currentColor = color;

        String data;
        if (color == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{currentBrightness, currentBrightness, currentBrightness, currentBrightness});
        } else {
            int alpha = 0;
            int red = Color.red(color) * currentBrightness / 255;
            int green = Color.green(color) * currentBrightness / 255;
            int blue = Color.blue(color) * currentBrightness / 255;
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        }

        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            colorPicker.setOldCenterColor(getResources().getColor(R.color.main));
            //colorPicker.setNewCenterColor(getResources().getColor(R.color.main));
        }
    }*/

    /**
     * 改变当前颜色
     *
     * @param color
     */
    private void changeTheColor(int color) {
        currentColor = color;

        int alpha = currentBrightness;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});

        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            colorPicker.setOldCenterColor(getResources().getColor(R.color.main));
            //colorPicker.setNewCenterColor(getResources().getColor(R.color.main));
        }
    }

    /**
     * 初始化状态
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.ACTION_INIT_STATUS)) {
                initValues();
            }
        }
    };

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.setPriority(Integer.MAX_VALUE);

        mFilter.addAction(Constants.ACTION_INIT_STATUS);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, mFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        saveValues();
    }
}
