package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.LoginUtil;
import com.guohua.mlight.common.util.ToolUtil;
import com.guohua.mlight.presenter.ILoginPresenter;
import com.guohua.mlight.presenter.impl.LoginPresenter;
import com.guohua.mlight.view.ILoginView;
import com.guohua.mlight.view.widget.CountDownTimerView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * @file ForgetFragment.java
 * @author Leo
 * @version 1
 * @detail 忘记密码页面的设计
 * @since 2016/12/26 17:54
 */

/**
 * 文件名：ForgetFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/26 17:54
 * 描  述：忘记密码页面的设计
 */
public class ForgetFragment extends BaseFragment implements ILoginView {
    private volatile static ForgetFragment singleton = null;

    public static ForgetFragment getInstance() {
        if (singleton == null) {
            synchronized (ForgetFragment.class) {
                if (singleton == null) {
                    singleton = new ForgetFragment();
                }
            }
        }
        return singleton;
    }

    /*绑定视图控件*/
    @BindView(R.id.et_phone_forget)
    EditText mPhoneView;
    @BindView(R.id.et_captcha_forget)
    EditText mCaptchaView;
    @BindView(R.id.et_password_forget)
    EditText mPasswordView;
    @BindView(R.id.btn_captcha_forget)
    Button mRequestView;
    @BindView(R.id.btn_reset_forget)
    Button mResetView;
    /*处理业务逻辑的Presenter*/
    private ILoginPresenter mLoginPresenter;
    /*验证码倒计时*/
    private CountDownTimerView mCountDownTimer;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        mContext.setToolbarTitle(R.string.fragment_forget_login);
        /*1 初始化Presenter*/
        mLoginPresenter = new LoginPresenter(this);
        /*2 初始化验证码倒计时*/
        mCountDownTimer = new CountDownTimerView(Constants.CAPTCHA_TIMER_DELAY,
                Constants.CAPTCHA_TIMER_INTERVAL, mRequestView);
    }

    @OnClick({R.id.btn_reset_forget, R.id.btn_captcha_forget, R.id.iv_clear_forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_forget: {
                reset();
            }
            break;
            case R.id.btn_captcha_forget: {
                requestCaptcha();
            }
            break;
            case R.id.iv_clear_forget: {
                switchVisibility();
            }
            break;
            default:
                break;
        }
    }

    /*改变密码可见性*/
    private void switchVisibility() {
        /*当前的Transformation方法以及显示隐藏方法*/
        TransformationMethod currentMethod = mPasswordView.getTransformationMethod();
        TransformationMethod hideMethod = PasswordTransformationMethod.getInstance();
        TransformationMethod showMethod = HideReturnsTransformationMethod.getInstance();
        /*若为隐藏则显示 否则隐藏*/
        if (currentMethod instanceof PasswordTransformationMethod) {
            mPasswordView.setTransformationMethod(showMethod);
        } else {
            mPasswordView.setTransformationMethod(hideMethod);
        }
    }

    /*请求验证码*/
    private void requestCaptcha() {
        String phone = mPhoneView.getText().toString().trim();
        if (!LoginUtil.checkPhoneNumber(phone)) {
            mContext.toast(R.string.fragment_invalid_phone_login);
        } else {
            mRequestView.setEnabled(false);
            mLoginPresenter.requestSMSCode(phone);
        }
    }

    /**
     * 重置密码
     */
    private void reset() {
        /*获取用户输入*/
        String phone = mPhoneView.getText().toString().trim();
        String captcha = mCaptchaView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        /*校验数据的有效性*/
        if (!LoginUtil.checkPhoneNumber(phone)) {
            mContext.toast(R.string.fragment_invalid_phone_login);
            return;
        }
        if (!LoginUtil.checkCaptcha(captcha)) {
            mContext.toast(R.string.fragment_invalid_captcha_login);
            return;
        }
        if (!LoginUtil.checkPassword(password)) {
            mContext.toast(R.string.fragment_invalid_password_login);
            return;
        }
        /*可以重置密码了*/
        mResetView.setEnabled(false);
        String md5Password = ToolUtil.encryptByMD5(password);
        mLoginPresenter.reset(captcha, md5Password);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forget;
    }

    @Override
    protected void suicide() {
        super.suicide();
        /*退出页面时销毁*/
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @Override
    public void onSucceed(BmobUser bmobUser) {
        mResetView.setEnabled(true);
        mContext.toast(R.string.fragment_reset_succeed_login);
        removeFragment();
    }

    @Override
    public void onFailed(String error) {
        mResetView.setEnabled(false);
        mContext.toast(error);
    }

    @Override
    public void onCaptchaSucceed(Integer integer) {
        mCountDownTimer.start();
    }

    @Override
    public void onCaptchaFailed(String error) {
        mRequestView.setEnabled(true);
        mContext.toast(error);
    }
}
