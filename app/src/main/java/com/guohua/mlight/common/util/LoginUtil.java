package com.guohua.mlight.common.util;

import android.text.TextUtils;

/**
 * 和登录相关操作用到的工具类集合
 */
public final class LoginUtil {
    /**
     * 校验手机号码的有效性
     *
     * @param phone
     * @return
     */
    public static boolean checkPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        int length = phone.length();
        if (length != 11) {
            return false;
        }
        return true;
    }

    /**
     * 校验密码的有效性
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        int length = password.length();
        if (length < 6) {
            return false;
        }
        return true;
    }

    /**
     * 校验密码的有效性
     *
     * @param captcha
     * @return
     */
    public static boolean checkCaptcha(String captcha) {
        if (TextUtils.isEmpty(captcha)) {
            return false;
        }
        int length = captcha.length();
        if (length != 6) {
            return false;
        }
        return true;
    }
}
