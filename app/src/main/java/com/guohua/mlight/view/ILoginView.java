package com.guohua.mlight.view;

import cn.bmob.v3.BmobUser;

/**
 * 登录相关的接口
 */
public interface ILoginView {
    void onSucceed(BmobUser bmobUser);

    void onFailed(String error);

    void onCaptchaSucceed(Integer integer);

    void onCaptchaFailed(String error);
}
