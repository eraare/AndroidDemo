package com.guohua.mlight.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import butterknife.OnTextChanged;
import cn.bmob.v3.BmobUser;

/**
 * @file RegisterFragment.java
 * @author Leo
 * @version 1
 * @detail 新用户注册
 * @since 2016/12/26 17:05
 */

/**
 * 文件名：RegisterFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/26 17:05
 * 描  述：新用户注册
 */
public class RegisterFragment extends BaseFragment implements ILoginView {
    private volatile static RegisterFragment singleton = null;

    public static RegisterFragment getInstance() {
        if (singleton == null) {
            synchronized (RegisterFragment.class) {
                if (singleton == null) {
                    singleton = new RegisterFragment();
                }
            }
        }
        return singleton;
    }

    /*绑定视图*/
    @BindView(R.id.et_phone_register)
    EditText mPhoneView;
    @BindView(R.id.et_captcha_register)
    EditText mCaptchaView;
    @BindView(R.id.et_password_register)
    EditText mPasswordView;
    @BindView(R.id.btn_captcha_register)
    Button mRequestView;
    @BindView(R.id.btn_register_register)
    Button mRegisterView;
    @BindView(R.id.iv_clear_register)
    ImageView mClearView;

    /*业务逻辑的Presenter*/
    ILoginPresenter mLoginPresenter;

    /*计时器*/
    private CountDownTimerView mCountDownTimer;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        mContext.setToolbarTitle(R.string.fragment_register_login);
        mLoginPresenter = new LoginPresenter(this);
        /*验证码倒计时*/
        mCountDownTimer = new CountDownTimerView(Constants.CAPTCHA_TIMER_DELAY,
                Constants.CAPTCHA_TIMER_INTERVAL, mRequestView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @OnClick({R.id.btn_register_register, R.id.btn_captcha_register, R.id.iv_clear_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register_register: {
                register();
            }
            break;
            case R.id.btn_captcha_register: {
                requestCaptcha();
            }
            break;
            case R.id.iv_clear_register: {
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
            mClearView.setImageResource(R.drawable.icon_invisible_login);
        } else {
            mPasswordView.setTransformationMethod(hideMethod);
            mClearView.setImageResource(R.drawable.icon_visible_login);
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
     * 注册
     */
    private void register() {
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
        /*可以注册了*/
        mRegisterView.setEnabled(false);
        String md5Password = ToolUtil.encryptByMD5(password);
        mLoginPresenter.register(phone, captcha, md5Password);
    }

    @OnTextChanged(value = R.id.et_phone_register, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPhoneTextChanged(Editable s) {
        String phone = s.toString();
        int length = phone.length();
        if (length == 11) {
            mCaptchaView.requestFocus();
        }
    }

    @OnTextChanged(value = R.id.et_captcha_register, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterCaptchaTextChanged(Editable s) {
        String sms = s.toString();
        int length = sms.length();
        if (length == 6) {
            mPasswordView.requestFocus();
        }
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
        mRegisterView.setEnabled(true);
        mContext.toast(R.string.fragment_register_succeed_login);
        removeFragment();
    }

    @Override
    public void onFailed(String error) {
        mRegisterView.setEnabled(true);
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
