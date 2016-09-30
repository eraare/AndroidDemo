package com.guohua.mlight.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.activity.SceneModeActivity;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.service.GradientRampService;
import com.guohua.mlight.service.SceneSunGradientRampService;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.SceneModeSaveDiyGradientRamp;
import com.guohua.mlight.view.TimerView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class MainFragment extends Fragment {
    private MainActivity mContext = null;
    private ThreadPool pool = null;//线程池 向蓝牙设备发送控制数据等

    public static final String TAG = MainFragment.class.getSimpleName();
    public static String TITLE = "";
    /**
     * 单例模式
     */
    private volatile static MainFragment mainFragment = null;

    public static MainFragment newInstance() {
        if (mainFragment == null) {
            synchronized (MainFragment.class) {
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
            }
        }
        return mainFragment;
    }

    /**
     * 布局中的控件
     */
    private View rootView;//主布局
    public  ImageView switcher;//开关
    private SeekBar lightness;//亮度调节
    private SeekBar colorTemp;//色温
    private TextView timerOn;//定时开灯
    private TimerView timerFive;// 5分钟
    private TimerView timerFifteen;// 15分钟
    private TimerView timerThirty;//30分钟
    private TimerView timerSixty;//60分钟

    public static volatile boolean isLighting;//灯的状态
    private int currentBrightness;//当前亮度
    private int currentColor;//当前颜色值
    private int currentTemp;//当前色温

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return rootView;
    }

    /**
     * 当界面重新展示时（fragment.show）,调用onrequest刷新界面
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if(isLighting){
            System.out.println("Mainfagment R.drawable.icon_light_on");
            switcher.setImageResource(R.drawable.icon_light_on);
        }else{
            System.out.println("Mainfagment R.drawable.icon_light_off");
            switcher.setImageResource(R.drawable.icon_light_off);
        }
    }

    /**
     * 初始化数据和控件
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        pool = ThreadPool.getInstance();
        findViewsByIds();
        initData();
    }

    private String ISLIGHTINGTAG = "MainFragmentisLighting";

    /**
     * 初始化数据
     */
    private void initData() {
        TITLE = getString(R.string.app_name);
        currentBrightness = 255;
        currentTemp = 255;
        currentColor = Color.WHITE;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        isLighting = sp.getBoolean(ISLIGHTINGTAG, true);
        System.out.println("MainFragment initData MainFragmentisLighting: " + isLighting);
        if(isLighting){
            switcher.setImageResource(R.drawable.icon_light_on);
        }else{
            switcher.setImageResource(R.drawable.icon_light_off);
        }
        lightness.setProgress(currentBrightness);
        colorTemp.setProgress(currentTemp);

        initTimer();
    }

    public void initTimer(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        objectOpenTime = sp.getLong(Constant.KEY_TIMER_OPEN, 0);
        long now = System.currentTimeMillis();

        if (now - objectOpenTime < 0) {//还没到定时任务时间
            Toast.makeText(mContext, getString(R.string.unfinised_open_light_timer), Toast.LENGTH_SHORT).show();
            currentOpenTime = (int) ((objectOpenTime - now)/ (1000 * 60))+1;

            System.out.println(String.format("%d",objectOpenTime) + "; 111timer; " + String.format("%d",objectCloseTime) + "; timer; " +  String.format("%d",now) +
                    "; timer; " +  currentOpenTime + "; timer; " +  currentCloseTime);
            startTimer(WHAT_TIMER_OPEN);
        }else{
            if(objectOpenTime != 0){//说明有定时任务，但是已经超过时间了，那么把当前状态设置为定时后的状态
                Toast.makeText(mContext, getString(R.string.finised_open_light_timer), Toast.LENGTH_SHORT).show();

                //开灯
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
                //pool.addTask(new SendRunnable(data));
                isLighting = true;
                switcher.setImageResource(R.drawable.icon_light_on);
            }
            //没有定时任务，设置值为初始
            System.out.println(String.format("%d",objectOpenTime) + "; 222timer; " + String.format("%d",objectCloseTime) + "; timer; " +  String.format("%d",now) +
                    "; timer; " +  currentOpenTime + "; timer; " +  currentCloseTime);
            objectOpenTime = 0;
            currentOpenTime = 0;
            sp.edit().putLong(Constant.KEY_TIMER_OPEN, 0).apply();
        }

        objectCloseTime = sp.getLong(Constant.KEY_TIMER_CLOSE, 0);

        if (now - objectCloseTime < 0) {//还没到定时任务时间

            Toast.makeText(mContext, getString(R.string.unfinised_close_light_timer), Toast.LENGTH_SHORT).show();
            switch (sp.getInt(Constant.KEY_TIMER_MODE, 0)){
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

            currentCloseTime = (int) ((objectCloseTime - now)/ (1000 * 60))+1;

            System.out.println(String.format("%d",objectOpenTime) + "; 33322222timer; " + String.format("%d",objectCloseTime) + "; timer; " +  String.format("%d",now) +
                    "; timer; " +  currentOpenTime + "; timer; " +  currentCloseTime);
            startTimer(currentCloseTime);
        }else{
            if(objectCloseTime != 0){//说明有定时任务，但是已经超过时间了，那么把当前状态设置为定时后的状态
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
            sp.edit().putLong(Constant.KEY_TIMER_CLOSE, 0).apply();
            sp.edit().putInt(Constant.KEY_TIMER_MODE, 0).apply();
        }

        System.out.println(String.format("%d",objectOpenTime) + "; 333timer; " + String.format("%d",objectCloseTime) + "; timer; " +  String.format("%d",now) +
                "; timer; " +  currentOpenTime + "; timer; " +  currentCloseTime);
    }


    /**
     * 得到所有控件 并绑定相应的事件
     */
    private void findViewsByIds() {
        switcher = (ImageView) rootView.findViewById(R.id.iv_switch_main);
        lightness = (SeekBar) rootView.findViewById(R.id.sb_lightness_main);
        colorTemp = (SeekBar) rootView.findViewById(R.id.sb_color_temperature_main);
        timerOn = (TextView) rootView.findViewById(R.id.tv_timer_on_main);
        timerFive = (TimerView) rootView.findViewById(R.id.tv_timer_five_main);
        timerFifteen = (TimerView) rootView.findViewById(R.id.tv_timer_fifteen_main);
        timerThirty = (TimerView) rootView.findViewById(R.id.tv_timer_thirty_main);
        timerSixty = (TimerView) rootView.findViewById(R.id.tv_timer_sixty_main);
        // 绑定监听器
        switcher.setOnClickListener(mOnClickListener);
        timerOn.setOnClickListener(mOnClickListener);
        timerFive.setOnClickListener(mOnClickListener);
        timerFifteen.setOnClickListener(mOnClickListener);
        timerThirty.setOnClickListener(mOnClickListener);
        timerSixty.setOnClickListener(mOnClickListener);
        // 绑定滑动监听器
        lightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        colorTemp.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        if(getResources().getConfiguration().locale.getCountry().equals("CN")){
            lightness.setThumb(getResources().getDrawable(R.drawable.icon_lightness_thumb));
            colorTemp.setThumb(getResources().getDrawable(R.drawable.icon_color_thumb));
        }
    }

    /**
     * 相关的单击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.iv_switch_main: {
                    switchLight();
                }
                break;
                case R.id.tv_timer_on_main: {
                    mContext.showDialogFragment(TimerFragment.TAG);
                    startTimer(WHAT_TIMER_OPEN);
                }
                break;
                case R.id.tv_timer_five_main: {
                    timerFive.changeState();
                    if (timerFive.isSelected()) {
                        timerFifteen.selected(false);
                        timerThirty.selected(false);
                        timerSixty.selected(false);
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{TIMER_DELAY_1 * 60})));

                        saveCloseTimer(TIMER_DELAY_1);
                    } else {
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, null)));
                    }
                }
                break;
                case R.id.tv_timer_fifteen_main: {
                    timerFifteen.changeState();
                    if (timerFifteen.isSelected()) {
                        timerFive.selected(false);
                        timerThirty.selected(false);
                        timerSixty.selected(false);
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{TIMER_DELAY_2 * 60})));

                        saveCloseTimer(TIMER_DELAY_2);
                    } else {
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, null)));
                    }
                }
                break;
                case R.id.tv_timer_thirty_main: {
                    timerThirty.changeState();
                    if (timerThirty.isSelected()) {
                        timerFive.selected(false);
                        timerFifteen.selected(false);
                        timerSixty.selected(false);
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{TIMER_DELAY_3 * 60})));

                        saveCloseTimer(TIMER_DELAY_3);
                    } else {
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, null)));
                    }
                }
                break;
                case R.id.tv_timer_sixty_main: {
                    timerSixty.changeState();
                    if (timerSixty.isSelected()) {
                        timerFive.selected(false);
                        timerFifteen.selected(false);
                        timerThirty.selected(false);
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{TIMER_DELAY_4 * 60})));

                        saveCloseTimer(TIMER_DELAY_4);
                    } else {
                        pool.addTask(new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, null)));
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

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

    private void saveCloseTimer(long timer){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        objectCloseTime = timer * 60 * 1000 + System.currentTimeMillis();
        currentCloseTime = (int) (timer);
        editor.putLong(Constant.KEY_TIMER_CLOSE, objectCloseTime).apply();
        editor.putInt(Constant.KEY_TIMER_MODE, currentCloseTime).apply();
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

            if(currentOpenTime <= 0){//TimerFragment 没设置时间的话， 是 < 0 的
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
                editor.putLong(Constant.KEY_TIMER_OPEN, 0).apply();
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
                timerFive.selected(false);
                timerFifteen.selected(false);
                timerThirty.selected(false);
                timerSixty.selected(false);
                //String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_CLOSE});
               // pool.addTask(new SendRunnable(data));
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putLong(Constant.KEY_TIMER_CLOSE, 0).apply();
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
                if(whatTimer == WHAT_TIMER_OPEN){
                    timerOpenHandler.sendEmptyMessage(whatTimer);
                }else{
                    timerCloseHandler.sendEmptyMessage(whatTimer);
                }
            }
        };
        if(whatTimer == WHAT_TIMER_OPEN){
            System.out.println(objectOpenTime + " WHAT_TIMER_OPEN " + currentOpenTime);
            if (timerOpen != null) {
                timerOpen.cancel();
                timerOpen = null;
            }
            timerOpen = new Timer();
            timerOpen.schedule(timerTask, 1000 * 60, 60 * 1000);
        }else{
            System.out.println(objectCloseTime + " WHAT_TIMER_CLOSE " + currentCloseTime);
            if (timerClose != null) {
                timerClose.cancel();
                timerClose = null;
            }
            timerClose = new Timer();
            timerClose.schedule(timerTask, 1000 * 60, 60 * 1000);
        }
    }


    /**
     * SeekBar的滑动值改变事件监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            /*int id = seekBar.getId();
            if (id == R.id.sb_lightness_main) {
                currentBrightness = progress;
                changeColorBrightness(progress);
            } else if (id == R.id.sb_color_temperature_main) {
                //currentBrightness = progress;
                changeColorTemperature(progress);
            }*/
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

            //关闭小夜灯
            SceneFragment.mSceneAdapter.setState(false, 1);

            //关闭日出日落模式
            if(SceneSunGradientRampService.isRunning){
                Intent intent = new Intent(mContext, SceneSunGradientRampService.class);
                mContext.stopService(intent);
                SceneSunGradientRampService.isRunning = false;
                SceneFragment.mSceneAdapter.setState(false, 2);
            }

            //关闭十面埋伏模式
            if(SceneModeSaveDiyGradientRamp.isRunning){
                SceneModeSaveDiyGradientRamp.destroy();
            }

            if(SceneModeActivity.isSceneModeMusicOn){
                Intent intent = new Intent(mContext, GradientRampService.class);
                mContext.stopService(intent);
                SceneModeActivity.isSceneModeMusicOn = false;
                SceneModeActivity.isSceneRgbModeOn = false;
                SceneModeActivity.isSceneDiyModeOn = false;
                //关闭红绿蓝水波纹
                SceneFragment.mSceneAdapter.setState(false, 4);

                //关闭炫彩渐变
                SceneFragment.mSceneAdapter.setState(false, 5);
            }

        } else {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{CodeUtils.SWITCH_OPEN});
            isLighting = true;
            switcher.setImageResource(R.drawable.icon_light_on);
        }
        //多发几次，保证开关
        new Thread(new Runnable(){
            @Override
            public void run() {
                for(int i = 0; i < 5; i++){
//                  pool.addTask(new SendRunnable(data));
                    AppContext.getInstance().sendAll(data);
                    try {
                        Thread.sleep(Constant.HANDLERDELAY/3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

/*
        for(int i = 0; i < 5; i++){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pool.addTask(new SendRunnable(data));
                }
            }, Constant.HANDLERDELAY*i);
        }
*/
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
        currentColor = Color.argb(alpha, temperatureRed,temperatureGreen,temperatureBlue);
        int brightnessRed = Color.red(currentColor) * progress / 255;
        int brightnessGreen = Color.green(currentColor) * progress / 255;
        int brightnessBlue = Color.blue(currentColor) * progress / 255;
        data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, brightnessRed, brightnessGreen, brightnessBlue});
        System.out.println("brightnessRed: " + brightnessRed + "; brightnessGreen: " + brightnessGreen + "; brightnessBlue: " + brightnessBlue);
//    }
        pool.addTask(new SendRunnable(data));
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
        currentColor = Color.argb(alpha, brightnessRed,brightnessGreen,brightnessBlue);
        temperatureRed = Color.red(currentColor) ;
        temperatureGreen = Color.green(currentColor) * progress / 255;
        temperatureBlue = Color.blue(currentColor) * progress / 255;
        data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, temperatureRed, temperatureGreen, temperatureBlue});
        System.out.println("temperatureRed: " + temperatureRed + "; temperatureGreen: " + temperatureGreen + "; temperatureBlue: " + temperatureBlue);
//    }

        pool.addTask(new SendRunnable(data));
        if (!isLighting) {
            isLighting = true;
            switcher.setImageResource(R.drawable.icon_light_on);
        }
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(ISLIGHTINGTAG, MainFragment.isLighting).commit();
        System.out.println("MainActivity onDestroy MainFragmentisLighting: " +
                PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(ISLIGHTINGTAG, MainFragment.isLighting));
        super.onDestroy();

    }
}
