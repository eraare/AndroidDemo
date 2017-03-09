package com.guohua.mlight.presenter;

/**
 * 登录相关的接口
 */
public interface ILoginPresenter {
    /**
     * 登录
     *
     * @param phone
     * @param password
     */
    void login(String phone, String password);

    /**
     * 注册
     *
     * @param phone
     * @param captcha
     * @param password
     */
    void register(String phone, String captcha, String password);

    /**
     * 重置密码
     *
     * @param captcha
     * @param password
     */

    void reset(String captcha, String password);

    /**
     * 请求验证码
     *
     * @param phone
     */
    void requestSMSCode(String phone);
}
