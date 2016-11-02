package com.guohua.mlight.activity;

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

import com.guohua.mlight.R;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.service.GradientRampService;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToastUtill;
import com.guohua.mlight.util.ToolUtils;
import com.guohua.mlight.util.VibrateUtil;

/**
 * @author
 * @detail 摇一摇界面设计实现 可以切换摇一摇模式 开关或者随机变色
 * @time 2015-11-11
 */
public class SceneModeActivity extends AppCompatActivity {
    private static final String TAG = SceneModeActivity.class.getSimpleName();

    private ImageView iv_back_drive;

    ListView mSamrtList;

    private TextView stopGapValueRed;//显示闪烁间隔值
    private TextView gradientGapValueRed;//显示渐变时长值
    private TextView stopGapValueGreen;//显示闪烁间隔值
    private TextView gradientGapValueGreen;//显示渐变时长值
    private TextView stopGapValueBlue;//显示闪烁间隔值
    private TextView gradientGapValueBlue;//显示渐变时长值
    private TextView tv_title_title;//显示渐变时长值
    private TextView tv_savemode;//保存模式


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

    public SceneListInfo.SceneInfo sceneInfo;

    public int curSceneInfoId = 0;
    public String curSceneName = "";
    public int CUR_RED_GRADIENT_DELAY = 0;
    public int CUR_GREEN_GRADIENT_DELAY = 0;
    public int CUR_BLUE_GRADIENT_DELAY = 0;
    public int CURSCENESTOPGAPVALUE = 50;
    public int CURSCENEGRADIENTGAPVALUE = 10;
    public int curIsStartGradientRampService = 3;
    public boolean curSceneGradientGapRedCBChecked = true;
    public boolean curSceneGradientGapGreenCBChecked = true;
    public boolean curSceneGradientGapBlueCBChecked = true;
    public int curSceneCurClickColorImgOnOff[] = {1, 1, 1, 1};
    public int curSceneDatasHead[] = {1, 0, 0, 0, 1, 0, 0, 0};
    public int curSceneDefaultColor[] = {0, 255, 141, 11, 0, 255, 236, 224};
    public int curSceneGradientRampStopGap[] = {CURSCENESTOPGAPVALUE, CURSCENESTOPGAPVALUE * 2, CURSCENESTOPGAPVALUE, CURSCENESTOPGAPVALUE};
    public int curSceneGradientRampGradientGap[] = {CURSCENEGRADIENTGAPVALUE, CURSCENEGRADIENTGAPVALUE, CURSCENEGRADIENTGAPVALUE, CURSCENEGRADIENTGAPVALUE};


    SharedPreferences settings;

    private boolean isInitCheck = false;


    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        init();

        setTitle(curSceneName);
        setContentView(R.layout.gradient_ramp);

        tv_title_title = (TextView) findViewById(R.id.tv_title_title);
        tv_title_title.setText(curSceneName);

        mSamrtList = (ListView) findViewById(R.id.smart_mode_list);
        iv_back_drive = (ImageView) findViewById(R.id.iv_back_drive);
        iv_model_saved = (ImageView) findViewById(R.id.iv_model_saved);
//        tv_show_curColor = (TextView) findViewById(R.id.tv_show_curColor);

        //红色调节
        stopGapRed = (SeekBar) findViewById(R.id.sb_red_stop_freq);
        stopGapValueRed = (TextView) findViewById(R.id.tv_red_show_stop_gap);
        gradientGapRedCB = (CheckBox) findViewById(R.id.cb_gradient_red);
        stopGapRed.setProgress(curSceneGradientRampStopGap[1]);
        stopGapValueRed.setText(getString(R.string.stop_gap) + curSceneGradientRampStopGap[1]);
        if (curSceneGradientGapRedCBChecked) {
            stopGapRed.setEnabled(true);
        } else {
            stopGapRed.setEnabled(false);
        }
        //stopGapRed.setEnabled(false);

        gradientGapRed = (SeekBar) findViewById(R.id.sb_red_gradient_freq);
        gradientGapValueRed = (TextView) findViewById(R.id.tv_red_show_gradient_gap);
        gradientGapRed.setProgress(curSceneGradientRampGradientGap[1]);
        gradientGapValueRed.setText(getString(R.string.red_value) + curSceneGradientRampGradientGap[1]);
        gradientGapRed.setEnabled(true);

        stopGapRed.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapRed.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapRedCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapRedCB.setChecked(curSceneGradientGapRedCBChecked);
        // gradientGapRedCB.setChecked(false);
        isInitCheck = false;
        gradientGapRedCB.setOnTouchListener(checktouch);
        ////////////////////////////////////////////////////

        //绿色调节
        stopGapGreen = (SeekBar) findViewById(R.id.sb_green_stop_freq);
        stopGapValueGreen = (TextView) findViewById(R.id.tv_green_show_stop_gap);
        gradientGapGreenCB = (CheckBox) findViewById(R.id.cb_gradient_green);
        stopGapGreen.setProgress(curSceneGradientRampStopGap[2]);
        stopGapValueGreen.setText(getString(R.string.stop_gap) + curSceneGradientRampStopGap[2]);
        if (curSceneGradientGapGreenCBChecked) {
            stopGapGreen.setEnabled(true);
        } else {
            stopGapGreen.setEnabled(false);
        }
        //stopGapGreen.setEnabled(false);

        gradientGapGreen = (SeekBar) findViewById(R.id.sb_green_gradient_freq);
        gradientGapValueGreen = (TextView) findViewById(R.id.tv_green_show_gradient_gap);
        gradientGapGreen.setProgress(curSceneGradientRampGradientGap[2]);
        gradientGapValueGreen.setText(getString(R.string.green_value) + curSceneGradientRampGradientGap[2]);
        gradientGapGreen.setEnabled(true);

        stopGapGreen.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapGreen.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapGreenCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapGreenCB.setChecked(curSceneGradientGapGreenCBChecked);
//        gradientGapGreenCB.setChecked(false);
        isInitCheck = false;
        gradientGapGreenCB.setOnTouchListener(checktouch);
        ///////////////////////////////////////////

        //蓝色调节
        stopGapBlue = (SeekBar) findViewById(R.id.sb_blue_stop_freq);
        stopGapValueBlue = (TextView) findViewById(R.id.tv_blue_show_stop_gap);
        gradientGapBlueCB = (CheckBox) findViewById(R.id.cb_gradient_blue);
        stopGapBlue.setProgress(curSceneGradientRampStopGap[3]);
        stopGapValueBlue.setText(getString(R.string.stop_gap) + curSceneGradientRampStopGap[3]);
        if (curSceneGradientGapBlueCBChecked) {
            stopGapBlue.setEnabled(true);
        } else {
            stopGapBlue.setEnabled(false);
        }
        //stopGapBlue.setEnabled(false);

        gradientGapBlue = (SeekBar) findViewById(R.id.sb_blue_gradient_freq);
        gradientGapValueBlue = (TextView) findViewById(R.id.tv_blue_show_gradient_gap);
        gradientGapBlue.setProgress(curSceneGradientRampGradientGap[3]);
        gradientGapValueBlue.setText(getString(R.string.blue_value) + curSceneGradientRampGradientGap[3]);
        gradientGapBlue.setEnabled(true);

        stopGapBlue.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapBlue.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        gradientGapBlueCB.setOnCheckedChangeListener(occ);
        isInitCheck = true;
        gradientGapBlueCB.setChecked(curSceneGradientGapBlueCBChecked);
        //  gradientGapBlueCB.setChecked(false);
        isInitCheck = false;
        gradientGapBlueCB.setOnTouchListener(checktouch);
        /////////////////////////////////////////////

//        gradient_white = (TextView) findViewById(R.id.gradient_white);
        gradient_red = (TextView) findViewById(R.id.gradient_red);
        gradient_green = (TextView) findViewById(R.id.gradient_green);
        gradient_blue = (TextView) findViewById(R.id.gradient_blue);

        iv_back_drive.setOnClickListener(mOnClickListener);
        iv_model_saved.setOnClickListener(mOnClickListener);
//        gradient_white.setOnClickListener(mOnClickListener);
        gradient_red.setOnClickListener(mOnClickListener);
        gradient_green.setOnClickListener(mOnClickListener);
        gradient_blue.setOnClickListener(mOnClickListener);

        tv_savemode = (TextView) findViewById(R.id.tv_savemode);
        if(curSceneInfoId == Constant.CALLREMINDERMODEID){
            tv_savemode.setVisibility(View.VISIBLE);
            tv_savemode.setOnClickListener(mOnClickListener);
            gradient_red.setClickable(false);
            gradient_green.setClickable(false);
            gradient_blue.setClickable(false);
        }else{
            tv_savemode.setVisibility(View.GONE);
        }

        /*if(curSceneInfoId == 2){
            gradient_red.setClickable(false);
            gradient_green.setClickable(false);
            gradient_blue.setClickable(false);
        }*/
    }

    private CheckBox.OnTouchListener checktouch = new CheckBox.OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {

            switch (arg0.getId()) {//点击哪个cb
                case R.id.cb_gradient_red:
                    if (!gradientGapRedCB.isClickable()) {
                        ToastUtill.showToast(SceneModeActivity.this, getString(R.string.wait_gradient_run), 200).show();
                    }
                    break;
                case R.id.cb_gradient_green:
                    if (!gradientGapGreenCB.isClickable()) {
                        ToastUtill.showToast(SceneModeActivity.this, getString(R.string.wait_gradient_run), 200).show();
                    }
                    break;
                case R.id.cb_gradient_blue:
                    if (!gradientGapBlueCB.isClickable()) {
                        ToastUtill.showToast(SceneModeActivity.this, getString(R.string.wait_gradient_run), 200).show();
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
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SceneModeActivity.this);
            switch (id) {
                case R.id.cb_gradient_red:
                    if (isChecked) {
                        curClickColorImg = 1;
                        delay = Constant.RED_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
                        curSceneCurClickColorImgOnOff[1] = 1;
                        stopGapRed.setEnabled(true);
                        gradientGapValueRed.setText(getString(R.string.gradient_gap) + curSceneGradientRampGradientGap[1]);
                        /*if(delay != 0){
                            gradientGapRedCB.setClickable(false);
                        }*/
                        msg.arg1 = 1;
                        curSceneGradientGapRedCBChecked = true;
                        if (curIsStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            curIsStartGradientRampService++;
                        }
                    } else {
                        curSceneCurClickColorImgOnOff[1] = 0;
                        stopGapRed.setEnabled(false);
                        gradientGapValueRed.setText(getString(R.string.red_value) + curSceneGradientRampGradientGap[1]);
                        gradient_red.setText("");
                        curSceneGradientGapRedCBChecked = false;
                        if (curIsStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            curIsStartGradientRampService--;
                        }
                    }
                    break;
                case R.id.cb_gradient_green:
                    if (isChecked) {
                        curClickColorImg = 2;
                        delay = Constant.GREEN_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
                        curSceneCurClickColorImgOnOff[2] = 1;
                        stopGapGreen.setEnabled(true);
                        gradientGapValueGreen.setText(getString(R.string.gradient_gap) + curSceneGradientRampGradientGap[2]);
                        /*if(delay != 0){
                            gradientGapGreenCB.setClickable(false);
                        }*/
                        msg.arg1 = 2;
                        curSceneGradientGapGreenCBChecked = true;
                        if (curIsStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            curIsStartGradientRampService++;
                        }
                    } else {
                        curSceneCurClickColorImgOnOff[2] = 0;
                        stopGapGreen.setEnabled(false);
                        gradientGapValueGreen.setText(getString(R.string.green_value) + curSceneGradientRampGradientGap[2]);
                        gradient_green.setText("");
                        curSceneGradientGapGreenCBChecked = false;
                        if (curIsStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            curIsStartGradientRampService--;
                        }
                    }
                    break;
                case R.id.cb_gradient_blue:
                    if (isChecked) {
                        curClickColorImg = 3;
                        delay = Constant.BLUE_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
                        curSceneCurClickColorImgOnOff[3] = 1;
                        stopGapBlue.setEnabled(true);
                        gradientGapValueBlue.setText(getString(R.string.gradient_gap) + curSceneGradientRampGradientGap[3]);
                        /*if(delay != 0){
                            gradientGapBlueCB.setClickable(false);
                        }*/
                        msg.arg1 = 3;
                        curSceneGradientGapBlueCBChecked = true;
                        if (curIsStartGradientRampService == 0) {
                            switchMusicOn();
                        } else {
                            curIsStartGradientRampService++;
                        }
                    } else {
                        curSceneCurClickColorImgOnOff[3] = 0;
                        stopGapBlue.setEnabled(false);
                        gradientGapValueBlue.setText(getString(R.string.blue_value) + curSceneGradientRampGradientGap[3]);
                        gradient_blue.setText("");
                        curSceneGradientGapBlueCBChecked = false;
                        if (curIsStartGradientRampService == 1) {
                            switchMusicOff();
                        } else {
                            curIsStartGradientRampService--;
                        }
                    }
                    break;
            }
            System.out.println("Constant.RED_GRADIENT_DELAY_VALUE: " + Constant.RED_GRADIENT_DELAY_VALUE);
            System.out.println("Constant.GREEN_GRADIENT_DELAY_VALUE: " + Constant.GREEN_GRADIENT_DELAY_VALUE);
            System.out.println("Constant.BLUE_GRADIENT_DELAY_VALUE: " + Constant.BLUE_GRADIENT_DELAY_VALUE);
            System.out.println("delay: " + delay);
            if (!isInitCheck) {
                setCurRgbGradientDelay(361);
                if(curSceneInfoId == 2){
                    setSceneRgbGradientColor(new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                            CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                            curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                            curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
                }else{
                    setSceneCurrentGradientColor(delay, new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                            CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                            curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                            curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
                }
                msg.arg2 = delay;
            } else {
                msg.arg2 = 0;
            }
//            setCurrentGradientColor(delay);
//            refreshDelay.sendMessage(msg);
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int id = seekBar.getId();
            switch (id) {
                case R.id.sb_red_stop_freq:
                    stopGapValueRed.setText(getString(R.string.stop_gap) + progress);
                    curSceneGradientRampStopGap[1] = progress;
                    break;
                case R.id.sb_red_gradient_freq:
                    if (gradientGapRedCB.isChecked()) {
                        gradientGapValueRed.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueRed.setText(getString(R.string.red_value) + progress);
                    }
                    curSceneGradientRampGradientGap[1] = progress;
                    break;
                case R.id.sb_green_stop_freq:
                    stopGapValueGreen.setText(getString(R.string.stop_gap) + progress);
                    curSceneGradientRampStopGap[2] = progress;
                    break;
                case R.id.sb_green_gradient_freq:
                    if (gradientGapGreenCB.isChecked()) {
                        gradientGapValueGreen.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueGreen.setText(getString(R.string.green_value) + progress);
                    }
                    curSceneGradientRampGradientGap[2] = progress;
                    break;
                case R.id.sb_blue_stop_freq:
                    stopGapValueBlue.setText(getString(R.string.stop_gap) + progress);
                    curSceneGradientRampStopGap[3] = progress;
                    break;
                case R.id.sb_blue_gradient_freq:
                    if (gradientGapBlueCB.isChecked()) {
                        gradientGapValueBlue.setText(getString(R.string.gradient_gap) + progress);
                    } else {
                        gradientGapValueBlue.setText(getString(R.string.blue_value) + progress);
                    }
                    curSceneGradientRampGradientGap[3] = progress;
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setCurRgbGradientDelay(423);
            if (curSceneInfoId == 2) {
//                AppContext.isSceneRgbRun = false;
                setSceneRgbGradientColor(new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                        CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                        curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                        curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
            } else {
                setSceneCurrentGradientColor(0, new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                        CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                        curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                        curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
            }
        }
    };

    private void setSceneCurrentGradientColor(int delay, SceneListInfo.SceneInfo ss) {
        mService.setSceneCurrentGradientColor(delay, ss);
    }

    private void setSceneRgbGradientColor(SceneListInfo.SceneInfo ss) {
        mService.setSceneRgbGradientColor(ss);
    }

    // 点击时明暗切换效果
    private Animation Anim_Alpha;

    /**
     * 相关的单击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Anim_Alpha = AnimationUtils.loadAnimation(SceneModeActivity.this,
                    R.anim.alpha_action);
            v.startAnimation(Anim_Alpha);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SceneModeActivity.this);
            int id = v.getId();
            int clickPos = -1;
            VibrateUtil.vibrate(SceneModeActivity.this, 50);
            switch (id) {
                case R.id.iv_back_drive: {
//                    AppContext.isSceneSunRun = false;
//                    AppContext.isSceneRgbRun = false;
//                    Intent intent = new Intent(SceneModeActivity.this, MainActivity.class);
//                    startActivity(intent);
                    finish();
                }
                break;
                case R.id.tv_savemode: {//来电提醒的自定义闪烁模式值保存
                    SceneListInfo.SceneInfo savedMode = new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                            CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                            curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                            curSceneGradientRampStopGap, curSceneGradientRampGradientGap);
                    int[] sendDatas = ToolUtils.getSceneGradientRampByteArray(savedMode);
                    String call_reminder_shinemode_value = "";
                    System.out.println("R.id.tv_savemode sendDatas  -------------------------------");
                    for (int i = 0; i < sendDatas.length; i++) {
                        System.out.print(sendDatas[i] + ";");
                        call_reminder_shinemode_value = call_reminder_shinemode_value + sendDatas[i] + ";";
                    }
                    System.out.println("R.id.tv_savemode sendDatas  ================================  [" + call_reminder_shinemode_value + "]");
                    sp.edit().putInt(Constant.CALL_REMINDER_SHINEMODE, 3).apply();
                    sp.edit().putString(Constant.CALL_REMINDER_SHINEMODE_VALUE, call_reminder_shinemode_value).apply();

                    System.out.println("CALL_REMINDER_SHINEMODE_VALUE  ================================" + sp.getString(Constant.CALL_REMINDER_SHINEMODE_VALUE, "124;111;0;0;10;0;0;121"));
                    ToastUtill.showToast(SceneModeActivity.this, getString(R.string.save_succ), Constant.TOASTLENGTH).show();
                    finish();
                }
                break;
                case R.id.iv_model_saved: {
//                    ToastUtill.showToast(SceneModeActivity.this, getString(R.string.gradient_model_saved), 200).show();
                    saveSceneInfoDialog();
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
                default:
                    break;
            }
        }
    };

    private void saveSceneInfoDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_save_sceneinfo, null);
        new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("Save sceneinfo test").setView(view)
                .setPositiveButton(R.string.confirm_delay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveSceneInfo();
                        Toast.makeText(getApplicationContext(), "Sava Succ test!", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.cancel_delay, null).show();
    }

    private void saveSceneInfo() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SceneModeActivity.this);
        SceneListInfo sceneListInfo = SceneListInfo.getSceneListInfo(sp);

        boolean isAddNew = true;
        for (int i = 0; i < sceneListInfo.sceneListInfo.size(); i++) {
            SceneListInfo.SceneInfo ss = sceneListInfo.sceneListInfo.get(i);
            if (curSceneInfoId == ss.SceneInfoId) {
                sceneListInfo.sceneListInfo.remove(i);
                sceneListInfo.sceneListInfo.add(new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                        CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                        curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                        curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
                isAddNew = false;
                break;
            }
        }
        if (isAddNew) {
            sceneListInfo.sceneListInfo.add(new SceneListInfo.SceneInfo(curSceneInfoId++, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                    CUR_BLUE_GRADIENT_DELAY, CURSCENESTOPGAPVALUE, curIsStartGradientRampService, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                    curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                    curSceneGradientRampStopGap, curSceneGradientRampGradientGap));
        }
        sceneListInfo.saveSceneListInfo(sp, sceneListInfo);
    }

    /**
     * 更改渐变灯的启动时间
     */
    private void changeGradientDelay(final int rgbLed) {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_gradient_delay_settings, null);
        final EditText delayValue = (EditText) view.findViewById(R.id.et_gradient_delay);
//        if (curSceneInfoId == 2) {
//            delayValue.setHint("Input Run Period! Test!");
//        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SceneModeActivity.this);
        int savedDelay = 0;
        switch (rgbLed) {
            case 1:
//                savedDelay = CUR_RED_GRADIENT_DELAY;
                savedDelay = sp.getInt(Constant.RED_GRADIENT_DELAY, Constant.RED_GRADIENT_DELAY_VALUE);
                break;
            case 2:
//                savedDelay = CUR_GREEN_GRADIENT_DELAY;
                savedDelay = sp.getInt(Constant.GREEN_GRADIENT_DELAY, Constant.GREEN_GRADIENT_DELAY_VALUE);
                break;
            case 3:
//                savedDelay = CUR_BLUE_GRADIENT_DELAY;
                savedDelay = sp.getInt(Constant.BLUE_GRADIENT_DELAY, Constant.BLUE_GRADIENT_DELAY_VALUE);
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
                editor.putInt(Constant.RED_GRADIENT_DELAY, delay).apply();
                Constant.RED_GRADIENT_DELAY_VALUE = delay;
                break;
            case 2:
                editor.putInt(Constant.GREEN_GRADIENT_DELAY, delay).apply();
                Constant.GREEN_GRADIENT_DELAY_VALUE = delay;
                break;
            case 3:
                editor.putInt(Constant.BLUE_GRADIENT_DELAY, delay).apply();
                Constant.BLUE_GRADIENT_DELAY_VALUE = delay;
                break;
        }
        setCurRgbGradientDelay(598);
    }


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

        sceneInfo = (SceneListInfo.SceneInfo) getIntent().getSerializableExtra("curSceneInfo");
        curSceneInfoId = sceneInfo.SceneInfoId;
        curSceneName = sceneInfo.SceneName;
//        if(curSceneInfoId == 2){
//            CUR_RED_GRADIENT_DELAY = settings.getInt(Constant.RED_GRADIENT_DELAY, 0)*1000;
//            CUR_GREEN_GRADIENT_DELAY = settings.getInt(Constant.GREEN_GRADIENT_DELAY, 0)*1000;
//            CUR_BLUE_GRADIENT_DELAY = settings.getInt(Constant.BLUE_GRADIENT_DELAY, 0)*1000;
//        }else{
//            CUR_RED_GRADIENT_DELAY = sceneInfo.RED_GRADIENT_DELAY;
//            CUR_GREEN_GRADIENT_DELAY = sceneInfo.GREEN_GRADIENT_DELAY;
//            CUR_BLUE_GRADIENT_DELAY = sceneInfo.BLUE_GRADIENT_DELAY;
//        }
        CURSCENESTOPGAPVALUE = sceneInfo.SCENESTOPGAPVALUE;
        curIsStartGradientRampService = sceneInfo.IsStartGradientRampService;
        CURSCENEGRADIENTGAPVALUE = sceneInfo.SCENEGRADIENTGAPVALUE;
        curSceneGradientGapRedCBChecked = sceneInfo.SceneGradientGapRedCBChecked;
        curSceneGradientGapGreenCBChecked = sceneInfo.SceneGradientGapGreenCBChecked;
        curSceneGradientGapBlueCBChecked = sceneInfo.SceneGradientGapBlueCBChecked;
        curSceneCurClickColorImgOnOff = sceneInfo.SceneCurClickColorImgOnOff;
        curSceneDatasHead = sceneInfo.SceneDatasHead;
        curSceneDefaultColor = sceneInfo.SceneDefaultColor;
        curSceneGradientRampStopGap = sceneInfo.SceneGradientRampStopGap;
        curSceneGradientRampGradientGap = sceneInfo.SceneGradientRampGradientGap;
        setCurRgbGradientDelay(714);

        if(curSceneInfoId == 2) {//红绿蓝波浪模式
            GradientRampService.notification_content_title = getString(R.string.notification_content_GradientRamp_Rgb);
        }else if(curSceneInfoId == -1) {//自定义渐变
            GradientRampService.notification_content_title = getString(R.string.notification_content_GradientRamp);
        }

//        if(AppContext.isStartGradientRampService == 0){
        startGradientRampService();
//        }
    }

    private void switchMusicOn() {
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_ON, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        curIsStartGradientRampService++;
        isSceneModeMusicOn = true;
    }

    private void switchMusicOff() {
//        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
//        ThreadPool.getInstance().addTask(new SendRunnable(data));
        curIsStartGradientRampService--;
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

    public static boolean isSceneModeMusicOn = false;
    public static boolean isSceneRgbModeOn = false;
    public static boolean isSceneDiyModeOn = false;

    private void stopGradientRampService() {
        //stopVisualizerService();
        unbindService(mServiceConnection);
//        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
//        ThreadPool.getInstance().addTask(new SendRunnable(data));
//        Intent service = new Intent(this, GradientRampService.class);
//        stopService(service);
    }

    private GradientRampService.IGradientRampService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (GradientRampService.IGradientRampService) service;
            isSceneModeMusicOn = true;

            Message msgNew = new Message();
            msgNew.arg1 = curSceneInfoId;

            if(curSceneInfoId == 2){//红绿蓝波浪模式
                String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_ON, null);
                ThreadPool.getInstance().addTask(new SendRunnable(data));
                System.out.println("isSceneRgbRun --------------------");
//                AppContext.isSceneRgbRun = true;
                isSceneRgbModeOn = true;
                setCurRgbGradientDelay(783);
                msgNew.arg2 = CUR_RED_GRADIENT_DELAY;
                sceneRunHandler.sendMessageDelayed(msgNew, Constant.HANDLERDELAY);
            }else if(curSceneInfoId == -1){//自定义渐变
                isSceneDiyModeOn = true;
                System.out.println("自定义渐变 curSceneInfoId == -1 --------------------");
            }else{//调色盘
                System.out.println("调色盘--------------------");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public void setCurRgbGradientDelay(int entryline) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SceneModeActivity.this);
        Constant.RED_GRADIENT_DELAY_VALUE = sp.getInt(Constant.RED_GRADIENT_DELAY, curSceneInfoId == 2 ? 0 : 1);
        Constant.GREEN_GRADIENT_DELAY_VALUE = sp.getInt(Constant.GREEN_GRADIENT_DELAY, curSceneInfoId == 2 ? 0 : 1);
        Constant.BLUE_GRADIENT_DELAY_VALUE = sp.getInt(Constant.BLUE_GRADIENT_DELAY, curSceneInfoId == 2 ? 0 : 1);

        int tvRedRunTime = 0, tvGreenRunTime = 0, tvBlueRunTime = 0;
        int redRunTime = 0, greenRunTime = 0, blueRunTime = 0;

        if (curSceneCurClickColorImgOnOff[1] == 0) {
            tvRedRunTime = 0;
            redRunTime = 0;
        } else {
            tvRedRunTime = Constant.RED_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
            redRunTime = ((Constant.COLORMAXVALUE - Constant.HARDWAREINITGRADIENTCOLORGAP) / (curSceneGradientRampGradientGap[1] == 0 ? 1 : curSceneGradientRampGradientGap[1])) * (curSceneGradientRampStopGap[1] * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT) * 2;
            System.out.println("Constant.COLORMAXVALUE: " + Constant.COLORMAXVALUE);
            System.out.println("Constant.HARDWAREINITGRADIENTCOLORGAP: " + Constant.HARDWAREINITGRADIENTCOLORGAP);
            System.out.println("curSceneGradientRampGradientGap[1]: " + curSceneGradientRampGradientGap[1]);
            System.out.println("curSceneGradientRampStopGap[1]: " + curSceneGradientRampStopGap[1]);
            System.out.println("Constant.HARDWARETIMEUNIT: " + Constant.HARDWARETIMEUNIT);
        }
        if (curSceneCurClickColorImgOnOff[2] == 0) {
            tvGreenRunTime = 0;
            greenRunTime = 0;
        } else {
            tvGreenRunTime = Constant.GREEN_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
            greenRunTime = ((Constant.COLORMAXVALUE - Constant.HARDWAREINITGRADIENTCOLORGAP) / (curSceneGradientRampGradientGap[2] == 0 ? 1 : curSceneGradientRampGradientGap[2])) * (curSceneGradientRampStopGap[2] * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT) * 2;
        }
        if (curSceneCurClickColorImgOnOff[3] == 0) {
            tvBlueRunTime = 0;
            blueRunTime = 0;
        } else {
            tvBlueRunTime = Constant.BLUE_GRADIENT_DELAY_VALUE * Constant.SOFTWARETIMEUNIT;
            blueRunTime = ((Constant.COLORMAXVALUE - Constant.HARDWAREINITGRADIENTCOLORGAP) / (curSceneGradientRampGradientGap[3] == 0 ? 1 : curSceneGradientRampGradientGap[3])) * (curSceneGradientRampStopGap[3] * Constant.HARDWARETIMEUNIT + Constant.HARDWARETIMEUNIT) * 2;
        }

        CUR_RED_GRADIENT_DELAY = tvRedRunTime;
        System.out.println(entryline + " CUR_GRADIENT_DELAY++++++++++11111111111111++++++++++++CUR_RED_GRADIENT_DELAY: " + CUR_RED_GRADIENT_DELAY +
                "; CUR_GREEN_GRADIENT_DELAY: " + CUR_GREEN_GRADIENT_DELAY + "; CUR_BLUE_GRADIENT_DELAY:" + CUR_BLUE_GRADIENT_DELAY);

        CUR_GREEN_GRADIENT_DELAY = CUR_RED_GRADIENT_DELAY + redRunTime + tvGreenRunTime;
        System.out.println(entryline + " CUR_GRADIENT_DELAY++++++++++2222222222222++++++++++++CUR_RED_GRADIENT_DELAY: " + CUR_RED_GRADIENT_DELAY +
                "; CUR_GREEN_GRADIENT_DELAY: " + CUR_GREEN_GRADIENT_DELAY + "; CUR_BLUE_GRADIENT_DELAY:" + CUR_BLUE_GRADIENT_DELAY +
                "; redRunTime:" + redRunTime + "; tvGreenRunTime:" + tvGreenRunTime);

        CUR_BLUE_GRADIENT_DELAY = CUR_GREEN_GRADIENT_DELAY + greenRunTime + tvBlueRunTime;
        System.out.println(entryline + " CUR_GRADIENT_DELAY++++++++++333333333333++++++++++++CUR_RED_GRADIENT_DELAY: " + CUR_RED_GRADIENT_DELAY +
                "; CUR_GREEN_GRADIENT_DELAY: " + CUR_GREEN_GRADIENT_DELAY + "; CUR_BLUE_GRADIENT_DELAY:" + CUR_BLUE_GRADIENT_DELAY);

        CUR_RED_GRADIENT_DELAY = CUR_BLUE_GRADIENT_DELAY + blueRunTime + tvRedRunTime;
        System.out.println(entryline + " CUR_GRADIENT_DELAY++++++++++444444444444444++++++++++++CUR_RED_GRADIENT_DELAY: " + CUR_RED_GRADIENT_DELAY +
                "; CUR_GREEN_GRADIENT_DELAY: " + CUR_GREEN_GRADIENT_DELAY + "; CUR_BLUE_GRADIENT_DELAY:" + CUR_BLUE_GRADIENT_DELAY);

    }

    Handler sceneRunHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("sceneRunHandler++++++++++++++++++++++arg1: " + msg.arg1 + "; arg2: " + msg.arg2);
            if(!(curSceneInfoId == 2 && isSceneRgbModeOn) && !(curSceneInfoId == -1 && isSceneDiyModeOn)){
                return;
            }
            final int sceneId = msg.arg1;
            final int delay = msg.arg2;
            final SceneListInfo.SceneInfo ss = new SceneListInfo.SceneInfo(curSceneInfoId, curSceneName, CUR_RED_GRADIENT_DELAY, CUR_GREEN_GRADIENT_DELAY,
                    CUR_BLUE_GRADIENT_DELAY, curIsStartGradientRampService, CURSCENESTOPGAPVALUE, CURSCENEGRADIENTGAPVALUE, curSceneGradientGapRedCBChecked,
                    curSceneGradientGapGreenCBChecked, curSceneGradientGapBlueCBChecked, curSceneCurClickColorImgOnOff, curSceneDatasHead, curSceneDefaultColor,
                    curSceneGradientRampStopGap, curSceneGradientRampGradientGap);
            switch (sceneId) {
                case 2:
                    setSceneRgbGradientColor(ss);
                    break;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msgNew = new Message();
                    msgNew.arg1 = sceneId;
                    if (curSceneInfoId == 2) {
                        msgNew.arg2 = CUR_RED_GRADIENT_DELAY;
                    } else {
                        msgNew.arg2 = delay;
                    }
                    sceneRunHandler.sendMessage(msgNew);
                }
            }, delay);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppContext.isSceneSunRun = false;
//        AppContext.isSceneRgbRun = false;
        stopGradientRampService();
    }

    public void back(View v) {
//        AppContext.isSceneSunRun = false;
//        AppContext.isSceneRgbRun = false;
        this.finish();
    }
}
