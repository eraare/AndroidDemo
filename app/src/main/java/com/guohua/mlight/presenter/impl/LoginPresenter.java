package com.guohua.mlight.presenter.impl;

import com.guohua.mlight.presenter.ILoginPresenter;
import com.guohua.mlight.view.ILoginView;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author Leo
 * @version 1
 * @since 2017-01-11
 * 登录注册等用到的Presenter
 */
public class LoginPresenter implements ILoginPresenter {
    private static final String SMS_CODE_TEMPLATE = "BAladdin";
    /*由于Bmob已经封装好了Model所以无需重复创建*/
    private ILoginView mLoginView;

    public LoginPresenter(ILoginView loginView) {
        this.mLoginView = loginView;
    }

    @Override
    public void login(String phone, String password) {
        BmobUser.loginByAccount(phone, password, mLoginListener);
    }

    /*登录的回调函数*/
    private LogInListener<BmobUser> mLoginListener = new LogInListener<BmobUser>() {
        @Override
        public void done(BmobUser bmobUser, BmobException e) {
            // 如果错误就输出错误并返回
            if (e != null) {
                mLoginView.onFailed(e.getMessage());
                return;
            }
            // 否则进行判断
            if (bmobUser != null) {
                mLoginView.onSucceed(bmobUser);
            } else {
                mLoginView.onFailed("用户名或密码错误");
            }
        }
    };

    @Override
    public void register(String phone, String captcha, String password) {
        BmobUser user = new BmobUser();
        user.setMobilePhoneNumber(phone);
        user.setPassword(password);
        user.signOrLogin(captcha, mSaveListener);
    }

    /*注册和登录用到的监听器*/
    private SaveListener<BmobUser> mSaveListener = new SaveListener<BmobUser>() {
        @Override
        public void done(BmobUser bmobUser, BmobException e) {
            if (e == null) {
                mLoginView.onSucceed(bmobUser);
            } else {
                mLoginView.onFailed(e.getMessage());
            }
        }
    };

    @Override
    public void reset(String captcha, String password) {
        BmobUser.resetPasswordBySMSCode(captcha, password, mUpdateListener);
    }

    /*重置密码用到的监听器*/
    private UpdateListener mUpdateListener = new UpdateListener() {
        @Override
        public void done(BmobException e) {
            if (e == null) {
                mLoginView.onSucceed(null);
            } else {
                mLoginView.onFailed(e.getMessage());
            }
        }
    };

    @Override
    public void requestSMSCode(String phone) {
        BmobSMS.requestSMSCode(phone, SMS_CODE_TEMPLATE, mQueryListener);
    }

    /*验证码需要的监听器*/
    private QueryListener<Integer> mQueryListener = new QueryListener<Integer>() {
        @Override
        public void done(Integer integer, BmobException e) {
            if (e == null) {
                mLoginView.onCaptchaSucceed(integer);
            } else {
                mLoginView.onCaptchaFailed(e.getMessage());
            }
        }
    };
}
