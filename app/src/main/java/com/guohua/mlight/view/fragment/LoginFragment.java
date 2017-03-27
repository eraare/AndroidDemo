package com.guohua.mlight.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.BaseFragment;
import com.guohua.mlight.common.util.LoginUtil;
import com.guohua.mlight.common.util.ToolUtil;
import com.guohua.mlight.presenter.ILoginPresenter;
import com.guohua.mlight.presenter.impl.LoginPresenter;
import com.guohua.mlight.view.ILoginView;
import com.guohua.mlight.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * @file LoginFragment.java
 * @author Leo
 * @version 1
 * @detail 登录页面，提供登录接口和注册忘记密码接口
 * @since 2016/12/17 11:14
 */

/**
 * 文件名：LoginFragment.java
 * 作  者：Leo
 * 版  本：1
 * 日  期：2016/12/17 11:14
 * 描  述：登录页面，提供登录接口和注册忘记密码接口
 */
public class LoginFragment extends BaseFragment implements ILoginView {
    private volatile static LoginFragment singleton = null;

    public static LoginFragment getInstance() {
        if (singleton == null) {
            synchronized (LoginFragment.class) {
                if (singleton == null) {
                    singleton = new LoginFragment();
                }
            }
        }
        return singleton;
    }

    /*登录用到的逻辑Presenter*/
    private ILoginPresenter mLoginPresenter;
    /*绑定视图控件*/
    @BindView(R.id.et_phone_login)
    EditText mPhoneView;
    @BindView(R.id.et_password_login)
    EditText mPasswordView;
    @BindView(R.id.btn_login_login)
    Button mLoginView;

    @Override
    protected void init(View view, Bundle savedInstanceState) {
        mContext.setToolbarTitle(R.string.fragment_login_login);
        mLoginPresenter = new LoginPresenter(this);
    }

    @OnClick({R.id.btn_login_login, R.id.tv_register_login, R.id.tv_forget_login})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login_login: {

                login();
            }
            break;
            case R.id.tv_register_login: {
                addFragment(RegisterFragment.getInstance());
            }
            break;
            case R.id.tv_forget_login: {
                addFragment(ForgetFragment.getInstance());
            }
            break;
            default:
                break;
        }
    }

    /**
     * 登录操作
     */
    private void login() {
        /*获取用户输入的手机号和密码*/
        String phone = mPhoneView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        /*校验数据的有效性*/
        if (!LoginUtil.checkPhoneNumber(phone)) {
            mContext.toast(R.string.fragment_invalid_phone_login);
            return;
        }
        if (!LoginUtil.checkPassword(password)) {
            mContext.toast(R.string.fragment_invalid_password_login);
            return;
        }
        mLoginView.setEnabled(false);
        String md5Password = ToolUtil.encryptByMD5(password);
        mLoginPresenter.login(phone, md5Password);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onSucceed(BmobUser bmobUser) {
        mLoginView.setEnabled(true);
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        removeFragment();
    }

    @Override
    public void onFailed(String error) {
        mLoginView.setEnabled(true);
        mContext.toast(error);
    }

    @Override
    public void onCaptchaSucceed(Integer integer) {

    }

    @Override
    public void onCaptchaFailed(String error) {

    }
}
