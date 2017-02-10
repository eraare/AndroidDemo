package com.guohua.mlight.view.widget;

/**
 * @file CountDownTimerView.java
 * @author Leo
 * @version 1
 * @detail 倒计时控件
 * @since 2017/1/12 10:29
 */

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.widget.Button;

import com.guohua.mlight.R;

/**
 * 文件名：CountDownTimerView.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2017/1/12 10:29
 * 描  述：倒计时控件
 */
public class CountDownTimerView extends CountDownTimer {
    /*需要控制的控件*/
    private Button mButton;

    public CountDownTimerView(long millisInFuture, long countDownInterval, @NonNull Button button) {
        super(millisInFuture, countDownInterval);
        this.mButton = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setEnabled(false);
        long time = millisUntilFinished / 1000;
        /*为了国际化牺牲了一行代码*/
        mButton.setText(R.string.fragment_captcha_retry_login);
        mButton.setText(time + mButton.getText().toString());
    }

    @Override
    public void onFinish() {
        mButton.setEnabled(true);
        mButton.setText(R.string.fragment_captcha_login);
    }
}
