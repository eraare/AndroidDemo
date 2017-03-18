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
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.view.activity.MainActivity;
import com.guohua.mlight.view.widget.TimerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class HomeFragment extends BaseFragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    /**
     * 单例模式
     */
    private volatile static HomeFragment mainFragment = null;

    public static HomeFragment newInstance() {
        if (mainFragment == null) {
            synchronized (HomeFragment.class) {
                if (mainFragment == null) {
                    mainFragment = new HomeFragment();
                }
            }
        }
        return mainFragment;
    }

    /**
     * 布局中的控件
     */
    @BindView(R.id.iv_switch_main)
    ImageView switcher;//开关
    @BindView(R.id.sb_lightness_main)
    SeekBar lightness;//亮度调节
    @BindView(R.id.sb_color_temperature_main)
    SeekBar colorTemp;//色温
    @BindView(R.id.tv_timer_on_main)
    TextView timerOn;//定时开灯
    @BindView(R.id.tv_timer_off_main)
    TextView timerOff;//定时关灯
    @BindView(R.id.tv_timer_five_main)
    TimerView timerFive;// 5分钟
    @BindView(R.id.tv_timer_fifteen_main)
    TimerView timerFifteen;// 15分钟
    @BindView(R.id.tv_timer_thirty_main)
    TimerView timerThirty;//30分钟
    @BindView(R.id.tv_timer_sixty_main)
    TimerView timerSixty;//60分钟

    public volatile static boolean isLighting;//灯的状态
    private int currentBrightness;//当前亮度
    private int currentColor;//当前颜色值
    private int currentTemp;//当前色温

    private ThreadPool pool = ThreadPool.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        super.init(view, savedInstanceState);
        setupView();
        initData();
    }

    /**
     * 当界面重新展示时（fragment.show）,调用onrequest刷新界面
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (isLighting) {
            System.out.println("Mainfagment R.drawable.icon_light_on");
            switcher.setImageResource(R.drawable.icon_light_on);
        } else {
            System.out.println("Mainfagment R.drawable.icon_light_off");
            switcher.setImageResource(R.drawable.icon_light_off);
        }
    }

    private String ISLIGHTINGTAG = "MainFragmentisLighting";

    /**
     * 初始化数据
     */
    private void initData() {
        currentBrightness = 255;
        currentTemp = 255;
        currentColor = Color.WHITE;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        isLighting = sp.getBoolean(ISLIGHTINGTAG, true);
        System.out.println("HomeFragment initData MainFragmentisLighting: " + isLighting);
        if (isLighting) {
            switcher.setImageResource(R.drawable.icon_light_on);
        } else {
            switcher.setImageResource(R.drawable.icon_light_off);
        }
        lightness.setProgress(currentBrightness);
        colorTemp.setProgress(currentTemp);

        registerTimerReceiver();
        initTimer();
    }

    public void initTimer() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        objectOpenTime = sp.getLong(Constants.KEY_TIMER_OPEN, 0);
        long now = System.currentTimeMillis();

        if (now - objectOpenTime < 0) {//还没到定时任务时间

            timerOn.setText(getString(R.string.cancel_open_light_timer) + "   " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(objectOpenTime)));

            Toast.makeText(mContext, getString(R.string.unfinised_open_light_timer), Toast.LENGTH_SHORT).show();
            currentOpenTime = (int) ((objectOpenTime - now) / (1000 * 60)) + 1;

            System.out.println(String.format("%d", objectOpenTime) + "; 111timer; " + String.format("%d", objectCloseTime) + "; timer; " + String.format("%d", now) +
                    "; timer; " + currentOpenTime + "; timer; " + currentCloseTime);
            startTimer(WHAT_TIMER_OPEN);
        } else {
            if (objectOpenTime != 0) {//说明有定时任务，但是已经超过时间了，那么把当前状态设置为定时后的状态
                Toast.makeText(mContext, getString(R.string.finised_open_light_timer), Toast.LENGTH_SHORT).show();

                //开灯
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
                //pool.addTask(new SendRunnable(data));
                isLighting = true;
                switcher.setImageResource(R.drawable.icon_light_on);
            }
            //没有定时任务，设置值为初始
            System.out.println(String.format("%d", objectOpenTime) + "; 222timer; " + String.format("%d", objectCloseTime) + "; timer; " + String.format("%d", now) +
                    "; timer; " + currentOpenTime + "; timer; " + currentCloseTime);
            objectOpenTime = 0;
            currentOpenTime = 0;
            sp.edit().putLong(Constants.KEY_TIMER_OPEN, 0).apply();
            sp.edit().putBoolean(Constants.EXIST_TIMER_OPEN, false).apply();
            timerOn.setText(R.string.open_light_timer);
        }

        objectCloseTime = sp.getLong(Constants.KEY_TIMER_CLOSE, 0);

        if (now - objectCloseTime < 0) {//还没到定时任务时间

            Toast.makeText(mContext, getString(R.string.unfinised_close_light_timer), Toast.LENGTH_SHORT).show();
            switch (sp.getInt(Constants.KEY_TIMER_MODE, 0)) {
                case TIMER_DELAY_1:
                    timerFive.selected(true);
                    break;
                case TIMER_DELAY_2:
                    timerFifteen.selected(true);
                    break;
                case TIMER_DELAY_3:
                    timerThirty.selected(true);
                    break;
                case TIMER_DELAY_4:
                    timerSixty.selected(true);
                    break;
            }

            currentCloseTime = (int) ((objectCloseTime - now) / (1000 * 60)) + 1;

            timerOff.setText(getString(R.string.close_light_timer) + "   " +
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(objectCloseTime)));


            System.out.println(String.format("%d", objectOpenTime) + "; 33322222timer; " + String.format("%d", objectCloseTime) + "; timer; " + String.format("%d", now) +
                    "; timer; " + currentOpenTime + "; timer; " + currentCloseTime);
            startTimer(currentCloseTime);
        } else {
            if (objectCloseTime != 0) {//说明有定时任务，但是已经超过时间了，那么把当前状态设置为定时后的状态
                Toast.makeText(mContext, getString(R.string.finised_close_light_timer), Toast.LENGTH_SHORT).show();

                //关灯
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
                //pool.addTask(new SendRunnable(data));
                isLighting = false;
                switcher.setImageResource(R.drawable.icon_light_off);

                timerFive.selected(false);
                timerFifteen.selected(false);
                timerThirty.selected(false);
                timerSixty.selected(false);

            }
            //没有定时任务，设置值为初始
            objectCloseTime = 0;
            currentCloseTime = 0;
            sp.edit().putLong(Constants.KEY_TIMER_CLOSE, 0).apply();
            sp.edit().putInt(Constants.KEY_TIMER_MODE, 0).apply();
            timerOff.setText(getString(R.string.close_light_timer));
        }

        System.out.println(String.format("%d", objectOpenTime) + "; 333timer; " + String.format("%d", objectCloseTime) + "; timer; " + String.format("%d", now) +
                "; timer; " + currentOpenTime + "; timer; " + currentCloseTime);
    }


    /**
     * 得到所有控件 并绑定相应的事件
     */
    private void setupView() {
        // 绑定滑动监听器
        lightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        colorTemp.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            lightness.setThumb(getResources().getDrawable(R.drawable.icon_lightness_thumb));
            colorTemp.setThumb(getResources().getDrawable(R.drawable.icon_color_thumb));
        }
    }

    /**
     * 相关的单击事件监听器
     */
    @OnClick({R.id.iv_switch_main, R.id.tv_timer_on_main, R.id.tv_timer_off_main, R.id.tv_timer_five_main,
            R.id.tv_timer_fifteen_main, R.id.tv_timer_thirty_main, R.id.tv_timer_sixty_main})
    public void onClick(View v) {
        int id = v.getId();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (id) {
            case R.id.iv_switch_main: {
                switchLight();
            }
            break;
            case R.id.tv_timer_on_main: {
                if (sp.getBoolean(Constants.EXIST_TIMER_OPEN, false)) {//存在定时开任务,则关闭定时开
                    deleteLightTimer(true);
                } else {
//                    ((MainActivity) mContext).showDialogFragment(TimerFragment.TAG);
                }
            }
            break;
            case R.id.tv_timer_five_main: {
                timerFive.changeState();
                if (timerFive.isSelected()) {
                    openCloseLightTimer(TIMER_DELAY_1);
                } else {
                    deleteLightTimer(false);
                }
            }
            break;
            case R.id.tv_timer_fifteen_main: {
                timerFifteen.changeState();
                if (timerFifteen.isSelected()) {
                    openCloseLightTimer(TIMER_DELAY_2);
                } else {
                    deleteLightTimer(false);
                }
            }
            break;
            case R.id.tv_timer_thirty_main: {
                timerThirty.changeState();
                if (timerThirty.isSelected()) {
                    openCloseLightTimer(TIMER_DELAY_3);
                } else {
                    deleteLightTimer(false);
                }
            }
            break;
            case R.id.tv_timer_sixty_main: {
                timerSixty.changeState();
                if (timerSixty.isSelected()) {
                    openCloseLightTimer(TIMER_DELAY_4);
                } else {
                    deleteLightTimer(false);
                }
            }
            break;
            default:
                break;
        }
    }

    private void openCloseLightTimer(int timeDelay) {
        timerFive.selected(false);
        timerFifteen.selected(false);
        timerThirty.selected(false);
        timerSixty.selected(false);

        switch (timeDelay) {
            case TIMER_DELAY_1: {
                timerFive.selected(true);
            }
            break;
            case TIMER_DELAY_2: {
                timerFifteen.selected(true);
            }
            break;
            case TIMER_DELAY_3: {
                timerThirty.selected(true);
            }
            break;
            case TIMER_DELAY_4: {
                timerSixty.selected(true);
            }
            break;
        }

//        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{timeDelay * 60})));
        timerOff.setText(getString(R.string.close_light_timer) + "   " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis() + timeDelay * 60 * 1000)));
        saveCloseTimer(timeDelay);
    }

    private void deleteLightTimer(boolean isOnOff) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (isOnOff) {//删除开灯定时器
            mContext.toast(getString(R.string.cancel_open_light_timer) + "   "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(sp.getLong(Constants.KEY_TIMER_OPEN, 0))));

//            pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_OPEN, null)));
            objectOpenTime = 0;
            currentOpenTime = 0;
            sp.edit().putLong(Constants.KEY_TIMER_OPEN, 0).apply();
            sp.edit().putBoolean(Constants.EXIST_TIMER_OPEN, false).apply();
            timerOn.setText(R.string.open_light_timer);
        } else {//删除关灯定时器
            sp.edit().putLong(Constants.KEY_TIMER_CLOSE, 0).apply();
            sp.edit().putInt(Constants.KEY_TIMER_MODE, 0).apply();
            timerOff.setText(getString(R.string.close_light_timer));
//            pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, null)));
        }
    }

    public long objectOpenTime = 0;//目标开时间
    public long objectCloseTime = 0;//目标关时间
    public static int currentOpenTime = 0;//当前开剩余时间
    private int currentCloseTime = 0;//当前关剩余时间
    private final int WHAT_TIMER_OPEN = -1;
    //    private final int WHAT_TIMER_CLOSE = 1;
    private final int TIMER_DELAY_1 = 5;
    private final int TIMER_DELAY_2 = 15;
    private final int TIMER_DELAY_3 = 30;
    private final int TIMER_DELAY_4 = 60;

    private void saveCloseTimer(long timer) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        objectCloseTime = timer * 60 * 1000 + System.currentTimeMillis();
        currentCloseTime = (int) (timer);
        editor.putLong(Constants.KEY_TIMER_CLOSE, objectCloseTime).apply();
        editor.putInt(Constants.KEY_TIMER_MODE, currentCloseTime).apply();
        startTimer(currentCloseTime);
    }

    private Handler timerOpenHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int what = msg.what;
            System.out.println("timer icon_light xxxxxxx " + what);
            currentOpenTime--;
            System.out.println(currentOpenTime + " timer icon_light xxx11111111111xxxx " + what);

            if (currentOpenTime <= 0) {//TimerFragment 没设置时间的话， 是 < 0 的
                System.out.println(currentOpenTime + "timer icon_light xxx22222222222xxxx " + what);
                if (timerOpen != null) {
                    timerOpen.cancel();
                    timerOpen = null;
                }
            }
            if (currentOpenTime == 0) {//递减下来的，说明有定时任务
                System.out.println(currentOpenTime + "timer icon_light_on " + what);

                //开灯
                isLighting = true;
                switcher.setImageResource(R.drawable.icon_light_on);
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
                //pool.addTask(new SendRunnable(data));

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putLong(Constants.KEY_TIMER_OPEN, 0).apply();

                objectOpenTime = 0;
                sp.edit().putBoolean(Constants.EXIST_TIMER_OPEN, false).apply();
                timerOn.setText(R.string.open_light_timer);
            }
        }
    };

    private Handler timerCloseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            int what = msg.what;
            System.out.println("timer icon_light xxxxxxx " + what);
            currentCloseTime--;
            System.out.println(currentCloseTime + "timer icon_light xxx333333333xxxx " + what);
            if (currentCloseTime <= 0) {
                System.out.println(currentCloseTime + "timer icon_light xxx44444444444xxxx " + what);
                if (timerClose != null) {
                    timerClose.cancel();
                    timerClose = null;
                }

                //关灯
                isLighting = false;
                switcher.setImageResource(R.drawable.icon_light_off);
                timerOff.setText(getString(R.string.close_light_timer));
                timerFive.selected(false);
                timerFifteen.selected(false);
                timerThirty.selected(false);
                timerSixty.selected(false);
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
                // pool.addTask(new SendRunnable(data));
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putLong(Constants.KEY_TIMER_CLOSE, 0).apply();
            }
        }
    };

    private Timer timerOpen;
    private Timer timerClose;
    private TimerTask timerTask;

    /**
     * 启动计时器来倒计时
     */
    private void startTimer(final int whatTimer) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (whatTimer == WHAT_TIMER_OPEN) {
                    timerOpenHandler.sendEmptyMessage(whatTimer);
                } else {
                    timerCloseHandler.sendEmptyMessage(whatTimer);
                }
            }
        };
        if (whatTimer == WHAT_TIMER_OPEN) {
            System.out.println(objectOpenTime + " WHAT_TIMER_OPEN " + currentOpenTime);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (sp.getBoolean(Constants.EXIST_TIMER_OPEN, false)) {
                timerOn.setText(getString(R.string.cancel_open_light_timer) + "   " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(sp.getLong(Constants.KEY_TIMER_OPEN, 0))));
            } else {
                timerOn.setText(getString(R.string.open_light_timer));
            }

            if (timerOpen != null) {
                timerOpen.cancel();
                timerOpen = null;
            }
            timerOpen = new Timer();
            timerOpen.schedule(timerTask, 1000 * 60, 60 * 1000);
        } else {
            System.out.println(objectCloseTime + " WHAT_TIMER_CLOSE " + currentCloseTime);
            if (timerClose != null) {
                timerClose.cancel();
                timerClose = null;
            }
            timerClose = new Timer();
            timerClose.schedule(timerTask, 1000 * 60, 60 * 1000);
        }
    }

    private void registerTimerReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.ACTION_OPENLIGHT_TIMER);
        mFilter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(mBroadcastReceiver, mFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.ACTION_OPENLIGHT_TIMER)) {
                System.out.println("TimerFragment mBroadcastReceiver recieve: " + Constants.ACTION_OPENLIGHT_TIMER);
                startTimer(WHAT_TIMER_OPEN);
            }
        }
    };


    /**
     * SeekBar的滑动值改变事件监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            if (id == R.id.sb_lightness_main) {
                currentBrightness = seekBar.getProgress();
                changeColorBrightness(seekBar.getProgress());
            } else if (id == R.id.sb_color_temperature_main) {
                changeColorTemperature(seekBar.getProgress());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            /*int id = seekBar.getId();

            if (id == R.id.sb_lightness_main) {

            } else if (id == R.id.sb_color_temperature_main) {

            }*/
            int id = seekBar.getId();
            if (id == R.id.sb_lightness_main) {
                currentBrightness = seekBar.getProgress();
                changeColorBrightness(seekBar.getProgress());
            } else if (id == R.id.sb_color_temperature_main) {
                changeColorTemperature(seekBar.getProgress());
            }
        }
    };

    /**
     * 开关灯
     */
    private void switchLight() {
        final String data;

        if (isLighting) {
            isLighting = false;
            switcher.setImageResource(R.drawable.icon_light_off);
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
        } else {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
            isLighting = true;
            switcher.setImageResource(R.drawable.icon_light_on);
        }
        //多发几次，保证开关
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
//                    AppContext.getInstance().sendAll(data);
                    try {
                        Thread.sleep(Constants.HANDLERDELAY / 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int brightnessRed = Color.red(Color.WHITE);
    private int brightnessGreen = Color.green(Color.WHITE);
    private int brightnessBlue = Color.blue(Color.WHITE);

    /**
     * 改变当前彩色亮度
     *
     * @param progress
     */
    private void changeColorBrightness(int progress) {
        String data;
        /*if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{progress, progress, progress, progress});
        } else {*/
        int alpha = 255;
        currentColor = Color.argb(alpha, temperatureRed, temperatureGreen, temperatureBlue);
        int brightnessRed = Color.red(currentColor) * progress / 255;
        int brightnessGreen = Color.green(currentColor) * progress / 255;
        int brightnessBlue = Color.blue(currentColor) * progress / 255;
        data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, brightnessRed, brightnessGreen, brightnessBlue});
        System.out.println("brightnessRed: " + brightnessRed + "; brightnessGreen: " + brightnessGreen + "; brightnessBlue: " + brightnessBlue);
//    }
//        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            switcher.setImageResource(R.drawable.icon_light_on);
        }
    }

    private int temperatureRed = Color.red(Color.WHITE);
    private int temperatureGreen = Color.green(Color.WHITE);
    private int temperatureBlue = Color.blue(Color.WHITE);

    private void changeColorTemperature(int progress) {
        String data;
        /*if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{progress, progress, progress, progress});
        } else {*/
        int alpha = 255;
        currentColor = Color.argb(alpha, brightnessRed, brightnessGreen, brightnessBlue);
        temperatureRed = Color.red(currentColor);
        temperatureGreen = Color.green(currentColor) * progress / 255;
        temperatureBlue = Color.blue(currentColor) * progress / 255;
        data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, temperatureRed, temperatureGreen, temperatureBlue});
        System.out.println("temperatureRed: " + temperatureRed + "; temperatureGreen: " + temperatureGreen + "; temperatureBlue: " + temperatureBlue);
//    }

//        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            switcher.setImageResource(R.drawable.icon_light_on);
        }
    }

    @Override
    protected void suicide() {
        super.suicide();
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(ISLIGHTINGTAG, HomeFragment.isLighting).apply();
        mContext.unregisterReceiver(mBroadcastReceiver);
    }
}
