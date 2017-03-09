package com.guohua.mlight.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.view.activity.MainActivity;


/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class PalletFragment extends Fragment {
    private MainActivity mContext = null;
    private ThreadPool pool = null;
    public static final String TAG = PalletFragment.class.getSimpleName();
    /**
     * 音例模式
     */
    private volatile static PalletFragment palletFragment = null;

    public static PalletFragment getInstance() {
        if (palletFragment == null) {
            synchronized (MainFragment1.class) {
                if (palletFragment == null) {
                    palletFragment = new PalletFragment();
                }
            }
        }
        return palletFragment;
    }

    /**
     * 布局中的控件
     */
    private View rootView;//主布局
    private TextView valueShow, timerShow;//当前颜色值和定时关灯值
    private SeekBar changeBrightness, changeTimer;//改变值的SeekBar
    private ImageView changeColor;//颜色选择器
    private ImageButton switcher;//开关按钮

    private int currentColor = Color.GREEN;//当前颜色值
    private int currentBrightness = 255;//当前亮度
    private Bitmap bmp = null;//色板

    private LinearLayout ll_fragment_pallet;

    /**
     * 独立颜色值
     */
    private Button red, orange, yellow, green, cyan, blue, purple, white;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pallet, container, false);
        init();
        return rootView;
    }

    /**
     * 初始化数据和控件
     */
    private void init() {
        mContext = (MainActivity) getActivity();
        pool = ThreadPool.getInstance();
        findViewsByIds();
        initValues();
    }

    private void initValues() {
        switchLight(false);
        bmp = ((BitmapDrawable) changeColor.getDrawable()).getBitmap();
        changeBrightness.setProgress(currentBrightness);
        valueShow.setText("当前颜色");
        valueShow.setBackgroundColor(currentColor);
        changeTimer.setProgress(0);
        timerShow.setText("定时关灯未开启");
    }

    /**
     * 得到所有控件 并绑定相应的事件
     */
    private void findViewsByIds() {
        valueShow = (TextView) rootView.findViewById(R.id.tv_value_main);
        timerShow = (TextView) rootView.findViewById(R.id.tv_timer_main);
        changeBrightness = (SeekBar) rootView.findViewById(R.id.sb_brightness_main);
        changeTimer = (SeekBar) rootView.findViewById(R.id.sb_timer_main);
        changeColor = (ImageView) rootView.findViewById(R.id.iv_color_main);
        switcher = (ImageButton) rootView.findViewById(R.id.btn_switch_main);

        switcher.setOnClickListener(mOnClickListener);
        changeColor.setOnTouchListener(mOnTouchListener);
        changeBrightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        changeTimer.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        red = (Button) rootView.findViewById(R.id.btn_red_main);
        orange = (Button) rootView.findViewById(R.id.btn_orange_main);
        yellow = (Button) rootView.findViewById(R.id.btn_yellow_main);
        green = (Button) rootView.findViewById(R.id.btn_green_main);
        cyan = (Button) rootView.findViewById(R.id.btn_cyan_main);
        blue = (Button) rootView.findViewById(R.id.btn_blue_main);
        purple = (Button) rootView.findViewById(R.id.btn_purple_main);
        white = (Button) rootView.findViewById(R.id.btn_white_main);

        ll_fragment_pallet = (LinearLayout) rootView.findViewById(R.id.ll_fragment_pallet);

        red.setOnClickListener(mOnClickListener);
        orange.setOnClickListener(mOnClickListener);
        yellow.setOnClickListener(mOnClickListener);
        green.setOnClickListener(mOnClickListener);
        cyan.setOnClickListener(mOnClickListener);
        blue.setOnClickListener(mOnClickListener);
        purple.setOnClickListener(mOnClickListener);
        white.setOnClickListener(mOnClickListener);
    }

    /**
     * 按钮的单击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_switch_main) {
                switchLight(HomeFragment.isLighting);
                return;
            }
            int color = Color.WHITE;
            switch (id) {
                case R.id.btn_red_main:
                    color = Color.RED;
                    break;
                case R.id.btn_orange_main:
                    color = Color.argb(255, 255, 79, 0);
                    break;
                case R.id.btn_yellow_main:
                    color = Color.YELLOW;
                    break;
                case R.id.btn_green_main:
                    color = Color.GREEN;
                    break;
                case R.id.btn_cyan_main:
                    color = Color.CYAN;
                    break;
                case R.id.btn_blue_main:
                    color = Color.BLUE;
                    break;
                case R.id.btn_purple_main:
                    color = Color.argb(255, 255, 0, 255);
                    break;
                case R.id.btn_white_main:
                    color = Color.WHITE;
                    break;
                default:
                    break;
            }
            String data = CodeUtils.transARGB2Protocol(color);
            pool.addTask(new SendRunnable(data));
        }
    };

    /**
     * 开关灯
     */
    private void switchLight(boolean flag) {
        String data;
        if (!flag) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{Constants.CMD_OPEN_LIGHT});
            HomeFragment.isLighting = true;
            switcher.setImageResource(R.drawable.light_on);
        } else {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{Constants.CMD_CLOSE_LIGHT});
            HomeFragment.isLighting = false;
            switcher.setImageResource(R.drawable.light_off);
        }
        pool.addOtherTask(new SendRunnable(data));
    }

    /**
     * SeekBar的滑动值改变事件监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            if (id == R.id.sb_brightness_main) {
                currentBrightness = progress;
//                changeTheBrightness(progress);
                changeColorBrightness(progress);
            } else if (id == R.id.sb_timer_main) {
                if (progress == 0) {
                    timerShow.setText("定时关灯未开启");
                } else {
                    timerShow.setText(progress + "分钟后自动关灯");
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int id = seekBar.getId();
            int progress = seekBar.getProgress();
            if (id == R.id.sb_timer_main) {
                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{progress * 60});
                pool.addTask(new SendRunnable(data));
            }
        }
    };

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

        pool.addOtherTask(new SendRunnable(data));
    }

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

        currentColor = Color.argb(alpha, red, green, blue);
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        pool.addTask(new SendRunnable(data));
        valueShow.setText("当前颜色 a:" + alpha + "r:" + red + "g:" + green + "b:" + blue);
        valueShow.setBackgroundColor(currentColor);
        /**
         * 暂定的方法
         *//*
        int alpha = 255;
        int red = Color.red(currentColor) * progress / 255;
        int green = Color.green(currentColor) * progress / 255;
        int blue = Color.blue(currentColor) * progress / 255;
        System.out.println("MainFragment1:argb-" + alpha + red + green + blue);

        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        pool.addTask(new SendRunnable(data));
        valueShow.setText("当前颜色 a:" + alpha + "r:" + red + "g:" + green + "b:" + blue);
        valueShow.setBackgroundColor(currentColor);*/
    }

    /**
     * 图片上的滑动事件监听器
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (!validate(x, y)) {
                return false;
            }

            currentColor = bmp.getPixel((int) x, (int) y);
            changeTheColor();

            int action = event.getAction();
            switch (action) {
                /*case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentColor = bmp.getPixel((int) x, (int) y);
                    changeTheColor();
                    break;*/
                case MotionEvent.ACTION_UP:
                    AppContext.getInstance().currentColor = currentColor;
                    break;
                /*default:
                    Snackbar.make(v, R.string.default_text, Snackbar.LENGTH_SHORT).show();
                    break;*/
            }
            return true;
        }
    };

    private void changeTheColor() {
        int alpha = Color.alpha(currentColor) * currentBrightness / 255;
        int red = Color.red(currentColor) * currentBrightness / 255;
        int green = Color.green(currentColor) * currentBrightness / 255;
        int blue = Color.blue(currentColor) * currentBrightness / 255;
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
        pool.addTask(new SendRunnable(data));
        valueShow.setText("当前颜色 a:" + alpha + "r:" + red + "g:" + green + "b:" + blue);
        //valueShow.setTextColor(currentColor);

//        valueShow.setBackgroundColor(currentColor);
        ll_fragment_pallet.setBackgroundColor(currentColor);
    }

    /**
     * 验证滑动的坐标是否有效
     *
     * @param x
     * @param y
     * @return
     */
    private boolean validate(float x, float y) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        System.out.println("bmp.getWidth(): " + width + ";bmp.getHeight():" + height);

        if (x < 0 || y < 0 || x > width || y > height) {
            return false;
        }
        double diameter = width < height ? width : height;
        int centerX = width / 2;
        int centerY = height / 2;
        double side = Math.sqrt(Math.pow(Math.abs(x - centerX), 2) + Math.pow(Math.abs(y - centerY), 2));//两点间距离
        double minGap = 75;
        if (side > minGap && side < diameter) {
            return true;
        }
        return false;
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.ACTION_RECEIVED_STATUS)) {
                //String message = intent.getStringExtra(Constants.KEY_RECEIVED_MESSAGE);
                //switchLight(AppContext.getInstance().isLightOn);
                currentColor = AppContext.getInstance().currentColor;
                changeTheColor();
            }
        }
    };
}
