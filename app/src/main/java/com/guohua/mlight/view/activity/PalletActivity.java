package com.guohua.mlight.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.base.BaseActivity;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.view.fragment.HomeFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @author Leo
 * @detail 主Fragment包括以下功能：开关调光调色延时关
 * @time 2015-10-29
 */
public class PalletActivity extends BaseActivity {
    private ThreadPool pool = null;
    /*绑定控件*/
    @BindView(R.id.tv_value_main)
    TextView valueShow;
    @BindView(R.id.tv_timer_main)
    TextView timerShow;
    @BindView(R.id.sb_brightness_main)
    SeekBar changeBrightness;
    @BindView(R.id.sb_timer_main)
    SeekBar changeTimer;
    @BindView(R.id.iv_color_main)
    ImageView changeColor;
    @BindView(R.id.btn_switch_main)
    ImageButton switcher;
    @BindView(R.id.btn_red_main)
    Button red;
    @BindView(R.id.btn_orange_main)
    Button orange;
    @BindView(R.id.btn_yellow_main)
    Button yellow;
    @BindView(R.id.btn_green_main)
    Button green;
    @BindView(R.id.btn_cyan_main)
    Button cyan;
    @BindView(R.id.btn_blue_main)
    Button blue;
    @BindView(R.id.btn_purple_main)
    Button purple;
    @BindView(R.id.btn_white_main)
    Button white;
    @BindView(R.id.ll_activity_pallet)
    LinearLayout ll_activity_pallet;

    private int currentColor = Color.GREEN;//当前颜色值
    private int currentBrightness = 255;//当前亮度
    private Bitmap bmp = null;//色板

    private Unbinder unbinder;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pallet;
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
        setToolbarTitle(R.string.scene_color_pallet);
        initial();
    }

    /**
     * 初始化数据和控件
     */
    private void initial() {
        pool = ThreadPool.getInstance();
        initViews();
        initValues();
        if (AppContext.getInstance().isLightOn) {
            switcher.setImageResource(R.drawable.light_on);
            ll_activity_pallet.setBackgroundColor(Color.WHITE);
        } else {
            switcher.setImageResource(R.drawable.light_off);
            ll_activity_pallet.setBackgroundColor(Color.BLACK);
        }
    }

    private void initValues() {
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


    private void initViews() {
        changeColor.setOnTouchListener(mOnTouchListener);
        changeBrightness.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        changeBrightness.setEnabled(AppContext.getInstance().isLightOn);
        changeTimer.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        ll_activity_pallet.setBackgroundResource(R.color.greye);
    }

    /**
     * 按钮的单击事件
     */
    @OnClick({R.id.btn_switch_main, R.id.btn_red_main, R.id.btn_orange_main,
            R.id.btn_yellow_main, R.id.btn_green_main, R.id.btn_cyan_main,
            R.id.btn_blue_main, R.id.btn_purple_main, R.id.btn_white_main})
    public void onClick(View v) {
        int id = v.getId();
        int color = Color.WHITE;
        switch (id) {
            case R.id.btn_switch_main: {
                switchLight(AppContext.getInstance().isLightOn);
            }
            return;
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
        currentColor = color;
        ll_activity_pallet.setBackgroundColor(currentColor);
        switcher.setImageResource(R.drawable.light_on);
    }

    /**
     * 开关灯
     */
    private void switchLight(boolean flag) {

    }

    /**
     * SeekBar的滑动值改变事件监听器
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            if (id == R.id.sb_brightness_main) {
//                currentBrightness = progress;
////                changeTheBrightness(progress);
//                changeColorBrightness(progress);
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
            if (id == R.id.sb_brightness_main) {
                currentBrightness = progress;
                changeColorBrightness(progress);
            } else if (id == R.id.sb_timer_main) {
                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_DELAY_CLOSE, new Object[]{progress * 60});
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
        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        if (currentColor == Color.WHITE) {
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL,
                    new Object[]{progress, progress, progress, progress});
            ll_activity_pallet.setBackgroundColor(Color.rgb(progress, progress, progress));
        } else {
            alpha = 0;
            red = Color.red(currentColor) * progress / 255;
            green = Color.green(currentColor) * progress / 255;
            blue = Color.blue(currentColor) * progress / 255;
            data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_CONTROL, new Object[]{alpha, red, green, blue});
            ll_activity_pallet.setBackgroundColor(Color.rgb(red, green, blue));
        }
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
            System.out.println("x: " + x + "; y:" + y);

            currentColor = bmp.getPixel((int) x, (int) y);

            changeBrightness.setEnabled(true);
            switcher.setImageResource(R.drawable.light_on);
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
                    break;
                /*default:
                    Snackbar.make(v, R.string.default_text, Snackbar.LENGTH_SHORT).show();
                    break;*/
            }
            return true;
        }
    };

    private void changeTheColor() {
        ll_activity_pallet.setBackgroundColor(currentColor);
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

        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        double diameter = width < height ? width : height;
        int centerX = width / 2;
        int centerY = height / 2;
        double side = Math.sqrt(Math.pow(Math.abs(x - centerX), 2) + Math.pow(Math.abs(y - centerY), 2));//两点间距离
        double minGap = 40;
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
                changeTheColor();
            }
        }
    };
}
