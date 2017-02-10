package com.guohua.mlight.view.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.R;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.service.GradientRampService;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.ToastUtill;
import com.guohua.mlight.common.util.VibrateUtil;

/**
 * @author Leo
 * @detail 摇一摇界面设计实现 可以切换摇一摇模式 开关或者随机变色
 * @time 2015-11-11
 */
public class GradientRampActivity extends AppCompatActivity {
    private static final String TAG = GradientRampActivity.class.getSimpleName();

    //private ImageView iv_back_drive;

    public static final int REQUEST_SELECT_LAMP = 1;
    public static final int REQUEST_SELECT_COLOR = 2;

    /**
     * 骑行颜色模式
     */
    private static final int POSITION_OF_DRIVERED = 0;
    private static final int POSITION_OF_DRIVEGREEN = 1;
    private static final int POSITION_OF_DRIVEBLUE = 2;
    private static final int POSITION_OF_DRIVEMIX = 3;
    private static final int POSITION_OF_DRIVEDIY = 4;

    private boolean isDiveDiyChecked = false;
    ListView mSamrtList;

    private TextView stopGapValueRed;//显示闪烁间隔值
    private TextView gradientGapValueRed;//显示渐变时长值
    private TextView stopGapValueGreen;//显示闪烁间隔值
    private TextView gradientGapValueGreen;//显示渐变时长值
    private TextView stopGapValueBlue;//显示闪烁间隔值
    private TextView gradientGapValueBlue;//显示渐变时长值


    //    private TextView gradient_white;//
    private TextView gradient_red;//
    private TextView gradient_green;//
    private TextView gradient_blue;//
    //    private TextView tv_show_curColor;//
    private SeekBar stopGapRed;//闪烁间隔
    private SeekBar gradientGapRed;//渐变时长
    private SeekBar stopGapGreen;//闪烁间隔
    private SeekBar gradientGapGreen;//渐变时长
    private SeekBar stopGapBlue;//闪烁间隔
    private SeekBar gradientGapBlue;//渐变时长

    private CheckBox gradientGapRedCB;//渐变是否选中
    private CheckBox gradientGapGreenCB;//渐变是否选中
    private CheckBox gradientGapBlueCB;//渐变是否选中

    private ImageView iv_model_saved;

    private int curClickColorImg = -1;


    SharedPreferences settings;


    private String data;

    private boolean isInitCheck = false;

    //何种情景模式的广播
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (curClickColorImg == -1) {
                return;
            }
        }
    };


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        init();

        setTitle(R.string.gradient_title);
        setContentView(R.layout.gradient_ramp);

        mSamrtList = (ListView) findViewById(R.id.smart_mode_list);
        //iv_back_drive = (ImageView) findViewById(R.id.iv_back_drive);
        iv_model_saved = (ImageView) findViewById(R.id.iv_model_saved);
//        tv_show_curColor = (TextView) findViewById(R.id.tv_show_curColor);

        //红色调节
        stopGapRed = (SeekBar) findViewById(R.id.sb_red_stop_freq);
        stopGapValueRed = (TextView) findViewById(R.id.tv_red_show_stop_gap);
        gradientGapRedCB = (CheckBox) findViewById(R.id.cb_gradient_red);
        stopGapRed.setProgress(AppContext.gradientRampStopGap[1]);
        stopGapValueRed.setText(getString(R.string.stop_gap) + AppContext.gradientRampStopGap[1]);
        if (AppContext.isGradientGapRedCBChecked) {
            stopGapRed.setEnabled(true);
        } else {
            stopGapRed.setEnabled(false);
        }
        //stopGapRed.setEnabled(false);

        gradientGapRed = (SeekBar) findViewById(R.id.sb_red_gradient_freq);
        gradientGapValueRed = (TextView) findViewById(R.id.tv_red_show_gradient_gap);
        gradientGapRed.setProgress(AppContext.gradientRampGradientGap[1]);
        gradientGapValueRed.setText(getString(R.string.red_value) + AppContext.gradientRampGradientGap[1]);
        gradientGapRed.setEnabled(true);

        stopGapRed.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapRed.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapRedCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapRedCB.setChecked(AppContext.isGradientGapRedCBChecked);
        // gradientGapRedCB.setChecked(false);
        isInitCheck = false;
        gradientGapRedCB.setOnTouchListener(checktouch);
        ////////////////////////////////////////////////////

        //绿色调节
        stopGapGreen = (SeekBar) findViewById(R.id.sb_green_stop_freq);
        stopGapValueGreen = (TextView) findViewById(R.id.tv_green_show_stop_gap);
        gradientGapGreenCB = (CheckBox) findViewById(R.id.cb_gradient_green);
        stopGapGreen.setProgress(AppContext.gradientRampStopGap[2]);
        stopGapValueGreen.setText(getString(R.string.stop_gap) + AppContext.gradientRampStopGap[2]);
        if (AppContext.isGradientGapGreenCBChecked) {
            stopGapGreen.setEnabled(true);
        } else {
            stopGapGreen.setEnabled(false);
        }
        //stopGapGreen.setEnabled(false);

        gradientGapGreen = (SeekBar) findViewById(R.id.sb_green_gradient_freq);
        gradientGapValueGreen = (TextView) findViewById(R.id.tv_green_show_gradient_gap);
        gradientGapGreen.setProgress(AppContext.gradientRampGradientGap[2]);
        gradientGapValueGreen.setText(getString(R.string.green_value) + AppContext.gradientRampGradientGap[2]);
        gradientGapGreen.setEnabled(true);

        stopGapGreen.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapGreen.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapGreenCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapGreenCB.setChecked(AppContext.isGradientGapGreenCBChecked);
//        gradientGapGreenCB.setChecked(false);
        isInitCheck = false;
        gradientGapGreenCB.setOnTouchListener(checktouch);
        ///////////////////////////////////////////

        //蓝色调节
        stopGapBlue = (SeekBar) findViewById(R.id.sb_blue_stop_freq);
        stopGapValueBlue = (TextView) findViewById(R.id.tv_blue_show_stop_gap);
        gradientGapBlueCB = (CheckBox) findViewById(R.id.cb_gradient_blue);
        stopGapBlue.setProgress(AppContext.gradientRampStopGap[3]);
        stopGapValueBlue.setText(getString(R.string.stop_gap) + AppContext.gradientRampStopGap[3]);
        if (AppContext.isGradientGapBlueCBChecked) {
            stopGapBlue.setEnabled(true);
        } else {
            stopGapBlue.setEnabled(false);
        }
        //stopGapBlue.setEnabled(false);

        gradientGapBlue = (SeekBar) findViewById(R.id.sb_blue_gradient_freq);
        gradientGapValueBlue = (TextView) findViewById(R.id.tv_blue_show_gradient_gap);
        gradientGapBlue.setProgress(AppContext.gradientRampGradientGap[3]);
        gradientGapValueBlue.setText(getString(R.string.blue_value) + AppContext.gradientRampGradientGap[3]);
        gradientGapBlue.setEnabled(true);

        stopGapBlue.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapBlue.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapBlueCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapBlueCB.setChecked(AppContext.isGradientGapBlueCBChecked);
        //  gradientGapBlueCB.setChecked(false);
        isInitCheck = false;
        gradientGapBlueCB.setOnTouchListener(checktouch);
        /////////////////////////////////////////////

//        gradient_white = (TextView) findViewById(R.id.gradient_white);
        gradient_red = (TextView) findViewById(R.id.gradient_red);
        gradient_green = (TextView) findViewById(R.id.gradient_green);
        gradient_blue = (TextView) findViewById(R.id.gradient_blue);

        //iv_back_drive.setOnClickListener(mOnClickListener);
        iv_model_saved.setOnClickListener(mOnClickListener);
//        gradient_white.setOnClickListener(mOnClickListener);
        gradient_red.setOnClickListener(mOnClickListener);
        gradient_green.setOnClickListener(mOnClickListener);
        gradient_blue.setOnClickListener(mOnClickListener);
    }

    private CheckBox.OnTouchListener checktouch = new CheckBox.OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {

            switch (arg0.getId()) {//点击哪个cb
                case R.id.cb_gradient_red:
                    if (!gradientGapRedCB.isClickable()) {
                        ToastUtill.showToast(GradientRampActivity.this, getString(R.string.wait_gradient_run), Constants.TOASTLENGTH).show();
                    }
                    break;
                case R.id.cb_gradient_green:
                    if (!gradientGapGreenCB.isClickable()) {
                        ToastUtill.showToast(GradientRampActivity.this, getString(R.string.wait_gradient_run), Constants.TOASTLENGTH).show();
                    }
                    break;
                case R.id.cb_gradient_blue:
                    if (!gradientGapBlueCB.isClickable()) {
                        ToastUtill.showToast(GradientRampActivity.this, getString(R.string.wait_gradient_run), Constants.TOASTLENGTH).show();
                    }
                    break;
            }
            return false;
        }

    };


    private CheckBox.OnCheckedChangeListener occ = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            int delay = 0;
            Message msg = new Message();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GradientRampActivity.this);
            switch (id) {
                case R.id.cb_gradient_red:
                    if (isChecked) {
                        curClickColorImg = 1;
                        delay = sp.getInt(Constants.RED_GRADIENT_DELAY, 0);
                        AppContext.curClickColorImgOnOff[1] = 1;
                        stopGapRed.setEnabled(true);
                        gradientGapValueRed.setText(getString(R.string.gradient_gap) + AppContext.gradientRampGradientGap[1]);
                        if (delay != 0) {
                            gradientGapRedCB.setClickable(false);
                        }
                        msg.arg1 = 1;
                        AppContext.isGradientGapRedCBChecked = true;
                        if (AppContext.isStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            AppContext.isStartGradientRampService++;
                        }
                    } else {
                        AppContext.curClickColorImgOnOff[1] = 0;
                        stopGapRed.setEnabled(false);
                        gradientGapValueRed.setText(getString(R.string.red_value) + AppContext.gradientRampGradientGap[1]);
                        gradient_red.setText("");
                        AppContext.isGradientGapRedCBChecked = false;
                        if (AppContext.isStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            AppContext.isStartGradientRampService--;
                        }
                    }
                    break;
                case R.id.cb_gradient_green:
                    if (isChecked) {
                        curClickColorImg = 2;
                        delay = sp.getInt(Constants.GREEN_GRADIENT_DELAY, 0);
                        AppContext.curClickColorImgOnOff[2] = 1;
                        stopGapGreen.setEnabled(true);
                        gradientGapValueGreen.setText(getString(R.string.gradient_gap) + AppContext.gradientRampGradientGap[2]);
                        if (delay != 0) {
                            gradientGapGreenCB.setClickable(false);
                        }
                        msg.arg1 = 2;
                        AppContext.isGradientGapGreenCBChecked = true;
                        if (AppContext.isStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            AppContext.isStartGradientRampService++;
                        }
                    } else {
                        AppContext.curClickColorImgOnOff[2] = 0;
                        stopGapGreen.setEnabled(false);
                        gradientGapValueGreen.setText(getString(R.string.green_value) + AppContext.gradientRampGradientGap[2]);
                        gradient_green.setText("");
                        AppContext.isGradientGapGreenCBChecked = false;
                        if (AppContext.isStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            AppContext.isStartGradientRampService--;
                        }
                    }
                    break;
                case R.id.cb_gradient_blue:
                    if (isChecked) {
                        curClickColorImg = 3;
                        delay = sp.getInt(Constants.BLUE_GRADIENT_DELAY, 0);
                        AppContext.curClickColorImgOnOff[3] = 1;
                        stopGapBlue.setEnabled(true);
                        gradientGapValueBlue.setText(getString(R.string.gradient_gap) + AppContext.gradientRampGradientGap[3]);
                        if (delay != 0) {
                            gradientGapBlueCB.setClickable(false);
                        }
                        msg.arg1 = 3;
                        AppContext.isGradientGapBlueCBChecked = true;
                        if (AppContext.isStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            AppContext.isStartGradientRampService++;
                        }
                    } else {
                        AppContext.curClickColorImgOnOff[3] = 0;
                        stopGapBlue.setEnabled(false);
                        gradientGapValueBlue.setText(getString(R.string.blue_value) + AppContext.gradientRampGradientGap[3]);
                        gradient_blue.setText("");
                        AppContext.isGradientGapBlueCBChecked = false;
                        if (AppContext.isStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            AppContext.isStartGradientRampService--;
                        }
                    }
                    break;
            }
            if (!isInitCheck) {

                setCurrentGradientColor(delay);
                msg.arg2 = delay;
            } else {
                msg.arg2 = 0;
            }
//            setCurrentGradientColor(delay);
            refreshDelay.sendMessage(msg);
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            switch (id) {
                case R.id.sb_red_stop_freq:
                    stopGapValueRed.setText(getString(R.string.stop_gap) + progress);
                    AppContext.gradientRampStopGap[1] = progress;
                    break;
                case R.id.sb_red_gradient_freq:
                    if (gradientGapRedCB.isChecked()) {
                        gradientGapValueRed.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueRed.setText(getString(R.string.red_value) + progress);
                    }
                    AppContext.gradientRampGradientGap[1] = progress;
                    break;
                case R.id.sb_green_stop_freq:
                    stopGapValueGreen.setText(getString(R.string.stop_gap) + progress);
                    AppContext.gradientRampStopGap[2] = progress;
                    break;
                case R.id.sb_green_gradient_freq:
                    if (gradientGapGreenCB.isChecked()) {
                        gradientGapValueGreen.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueGreen.setText(getString(R.string.green_value) + progress);
                    }
                    AppContext.gradientRampGradientGap[2] = progress;
                    break;
                case R.id.sb_blue_stop_freq:
                    stopGapValueBlue.setText(getString(R.string.stop_gap) + progress);
                    AppContext.gradientRampStopGap[3] = progress;
                    break;
                case R.id.sb_blue_gradient_freq:
                    if (gradientGapBlueCB.isChecked()) {
                        gradientGapValueBlue.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueBlue.setText(getString(R.string.blue_value) + progress);
                    }
                    AppContext.gradientRampGradientGap[3] = progress;
                    break;
                default:
                    break;
            }
            setCurrentGradientColor(0);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setCurrentGradientColor(0);
        }
    };

    private void setCurrentGradientColor(int delay) {
        mService.setCurrentGradientColor(delay);
    }

    // 点击时明暗切换效果
    private Animation Anim_Alpha;

    /**
     * 相关的单击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Anim_Alpha = AnimationUtils.loadAnimation(GradientRampActivity.this,
                    R.anim.alpha_action);
            v.startAnimation(Anim_Alpha);

            int id = v.getId();
            int clickPos = -1;
            VibrateUtil.vibrate(GradientRampActivity.this, 50);
            switch (id) {
                /*case R.id.iv_back_drive: {
                    Intent intent = new Intent(GradientRampActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;*/
                case R.id.iv_model_saved: {
                    ToastUtill.showToast(GradientRampActivity.this, getString(R.string.gradient_model_saved), Constants.TOASTLENGTH).show();
                }
                break;
                case R.id.gradient_red: {
                    changeGradientDelay(1);
                }
                break;
                case R.id.gradient_green: {
                    changeGradientDelay(2);
                }
                break;
                case R.id.gradient_blue: {
                    changeGradientDelay(3);
                }
                break;
               /* case R.id.gradient_white: {
                    clickPos = 0;
                    if(curClickColorImg != clickPos) {
                        curClickColorImg = clickPos;
                        clearChoosedColor();
                        gradient_white.setBackgroundResource(R.drawable.design_white_choose_point);
                    }else{
                        if(AppContext.curClickColorImgOnOff[clickPos] == 0){
                            gradient_white.setText("√");
                            AppContext.curClickColorImgOnOff[clickPos] = 1;
                        }else{
                            gradient_white.setText("X");
                            AppContext.curClickColorImgOnOff[clickPos] = 0;
                        }
                    }
                }
                break;
                case R.id.gradient_red: {
                    clickPos = 1;
                    if(curClickColorImg != clickPos) {
                        curClickColorImg = clickPos;
                        clearChoosedColor();
                        gradient_red.setBackgroundResource(R.drawable.design_red_choose_point);
                    }else{
                        if(AppContext.curClickColorImgOnOff[clickPos] ==0){
                            gradient_red.setText("√");
                            AppContext.curClickColorImgOnOff[clickPos] = 1;
                        }else{
                            gradient_red.setText("X");
                            AppContext.curClickColorImgOnOff[clickPos] = 0;
                        }
                    }
                }
                break;
                case R.id.gradient_green: {
                    clickPos = 2;
                    if(curClickColorImg != clickPos) {
                        curClickColorImg = clickPos;
                        clearChoosedColor();
                        gradient_green.setBackgroundResource(R.drawable.design_green_choose_point);
                    }else{
                        if(AppContext.curClickColorImgOnOff[clickPos] == 0){
                            gradient_green.setText("√");
                            AppContext.curClickColorImgOnOff[clickPos] = 1;
                        }else{
                            gradient_green.setText("X");
                            AppContext.curClickColorImgOnOff[clickPos] = 0;
                        }
                    }
                }
                break;
                case R.id.gradient_blue: {
                    clickPos = 3;
                    if(curClickColorImg != clickPos) {
                        curClickColorImg = clickPos;
                        clearChoosedColor();
                        gradient_blue.setBackgroundResource(R.drawable.design_blue_choose_point);
                    }else{
                        if(AppContext.curClickColorImgOnOff[clickPos] == 0){
                            gradient_blue.setText("√");
                            AppContext.curClickColorImgOnOff[clickPos] = 1;
                        }else{
                            gradient_blue.setText("X");
                            AppContext.curClickColorImgOnOff[clickPos] = 0;
                        }
                    }
                }
                break;*/
                default:
                    break;
            }
            /*if(clickPos != -1){
                choosedColor(AppContext.curClickColorImgOnOff[clickPos] == 1 ? true : false);
                stopGap.setProgress(AppContext.gradientRampStopGap[clickPos]);
                stopGapValue.setText(getString(R.string.stop_gap) + AppContext.gradientRampStopGap[clickPos]);
                gradientGap.setProgress(AppContext.gradientRampGradientGap[clickPos]);
                gradientGapValue.setText(getString(R.string.gradient_gap) + AppContext.gradientRampGradientGap[clickPos]);
            }*/
        }
    };


 /*   public void choosedColor(boolean seekbarOk) {

        switch (curClickColorImg){
            case 0:
                tv_show_curColor.setText("白光");
                tv_show_curColor.setBackgroundColor(Color.WHITE);
                break;
            case 1:
                tv_show_curColor.setText("红光");
                tv_show_curColor.setBackgroundColor(Color.RED);
                break;
            case 2:
                tv_show_curColor.setText("绿光");
                tv_show_curColor.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                tv_show_curColor.setText("蓝光");
                tv_show_curColor.setBackgroundColor(Color.BLUE);
                break;
        }
        stopGap.setEnabled(seekbarOk);
        gradientGap.setEnabled(seekbarOk);
        setCurrentGradientColor();
    }*/


    /**
     * 更改渐变灯的启动时间
     */
    private void changeGradientDelay(final int rgbLed) {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_gradient_delay_settings, null);
        final EditText delayValue = (EditText) view.findViewById(R.id.et_gradient_delay);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GradientRampActivity.this);
        int savedDelay = 0;
        switch (rgbLed) {
            case 1:
                savedDelay = sp.getInt(Constants.RED_GRADIENT_DELAY, 0);
                break;
            case 2:
                savedDelay = sp.getInt(Constants.GREEN_GRADIENT_DELAY, 0);
                break;
            case 3:
                savedDelay = sp.getInt(Constants.BLUE_GRADIENT_DELAY, 0);
                break;
        }
        if (savedDelay != 0) {
//            delayValue.setText(getString(R.string.hint_value_saved) + savedDelay);
            delayValue.setHint(savedDelay + "");
        }
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle(R.string.change_gradient_delay).setView(view)
                .setPositiveButton(R.string.confirm_delay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String delayValueString = delayValue.getText().toString().trim();
                        if (delayValueString == null || delayValueString.length() <= 0 || !delayValueString.matches("[0-9]+")) {
                            Toast.makeText(getApplicationContext(), R.string.settings_delay_tip, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveGradientDelay(rgbLed, Integer.valueOf(delayValueString));
                        //Toast.makeText(getApplicationContext(), R.string.settings_warning, Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.cancel_delay, null).show();
    }

    private final static int curGradientDelay = 0;

    private void saveGradientDelay(int rgbLed, int delay) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        switch (rgbLed) {
            case 1:
                editor.putInt(Constants.RED_GRADIENT_DELAY, delay).apply();
                break;
            case 2:
                editor.putInt(Constants.GREEN_GRADIENT_DELAY, delay).apply();
                break;
            case 3:
                editor.putInt(Constants.BLUE_GRADIENT_DELAY, delay).apply();
                break;
        }
    }

    Handler refreshDelay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("arg1: " + msg.arg1 + "; arg2: " + msg.arg2);
            final int rgbLed = msg.arg1;
            final int delay = msg.arg2;
            switch (rgbLed) {
                case 1:
                    if (AppContext.curClickColorImgOnOff[1] == 1) {
                        if (delay == 0) {
                            gradient_red.setTextSize(12);
                            gradient_red.setText("Run..");
                            gradientGapRedCB.setClickable(true);
                        } else {
                            gradient_red.setText(delay + "");
                        }
                    } else {
                        gradient_red.setText("");
                    }
                    break;
                case 2:
                    if (AppContext.curClickColorImgOnOff[2] == 1) {
                        if (delay == 0) {
                            gradient_green.setTextSize(12);
                            gradient_green.setText("Run..");
                            gradientGapGreenCB.setClickable(true);
                        } else {
                            gradient_green.setText(delay + "");
                        }
                    } else {
                        gradient_green.setText("");
                    }
                    break;
                case 3:
                    if (AppContext.curClickColorImgOnOff[3] == 1) {
                        if (delay == 0) {
                            gradient_blue.setTextSize(12);
                            gradient_blue.setText("Run..");
                            gradientGapBlueCB.setClickable(true);
                        } else {
                            gradient_blue.setText(delay + "");
                        }
                    } else {
                        gradient_blue.setText("");
                    }
                    break;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (delay > 0) {
                        Message msgNew = new Message();
                        msgNew.arg1 = rgbLed;
                        msgNew.arg2 = delay - 1;
                        refreshDelay.sendMessage(msgNew);
                    }
                }
            }, 1000);

        }
    };

    public void clearChoosedColor() {
//        gradient_white.setBackgroundResource(R.drawable.design_white_point);
        gradient_red.setBackgroundResource(R.drawable.design_red_point);
        gradient_green.setBackgroundResource(R.drawable.design_green_point);
        gradient_blue.setBackgroundResource(R.drawable.design_blue_point);
    }

    @Override
    public void finish() {
        super.finish();
    }


    private ThreadPool pool = null;//线程池 向蓝牙设备发送控制数据等

    /**
     * 初始化数据和控件
     */
    private void init() {
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        pool = ThreadPool.getInstance();
//        if(AppContext.isStartGradientRampService == 0){
        startGradientRampService();
//        }
    }

    private void switchMusicOn() {
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_ON, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        AppContext.isStartGradientRampService++;
    }

    private void switchMusicOff() {
//        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
//        ThreadPool.getInstance().addTask(new SendRunnable(data));
        AppContext.isStartGradientRampService--;
    }

    private void startGradientRampService() {
        //stopVisualizerService();
        Intent service = new Intent(this, GradientRampService.class);
        //stopService(service);
        bindService(service, mServiceConnection, BIND_AUTO_CREATE);
        startService(service);

//        if(AppContext.isStartGradientRampService == 0){

//        }
    }

    private void stopGradientRampService() {
        //stopVisualizerService();
        unbindService(mServiceConnection);
//        Intent service = new Intent(this, GradientRampService.class);
//        stopService(service);
    }

    private GradientRampService.IGradientRampService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (GradientRampService.IGradientRampService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGradientRampService();
    }

    public void back(View v) {
        this.finish();
    }

}
