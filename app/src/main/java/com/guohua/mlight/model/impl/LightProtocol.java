package com.guohua.mlight.model.impl;

import android.text.TextUtils;

import com.guohua.mlight.model.IDeviceProtocol;

/**
 * 灯的控制协议
 */
public class LightProtocol implements IDeviceProtocol {
    private LightProtocol() {
    }

    private volatile static LightProtocol singleton = null;

    public static LightProtocol getInstance() {
        if (singleton == null) {
            synchronized (LightProtocol.class) {
                if (singleton == null) {
                    singleton = new LightProtocol();
                }
            }
        }
        return singleton;
    }

    @Override
    public String turnOn() {
        /*开灯*/
        return "open";
    }

    @Override
    public String turnOff() {
        /*关灯*/
        return "close";
    }

    @Override
    public String control(Object[] values) {
        StringBuilder sb = new StringBuilder("ctl:");

        for (Object value : values) {
            sb.append(value);
            sb.append(":");
        }

        return sb.toString();
    }

    @Override
    public String delayOn(int millis) {
        /*定时开灯*/
        if (millis <= 0) return "dl off";
        return "dl:" + millis;
    }

    @Override
    public String delayOff(int millis) {
        /*延时关灯*/
        if (millis <= 0) return "de off";
        return "de:" + millis;
    }

    @Override
    public String password(String oldPwd, String newPwd) {
        /*重置密码*/
        if (TextUtils.isEmpty(oldPwd)) return null;
        if (TextUtils.isEmpty(newPwd)) return null;
        return "mg:" + oldPwd + ":" + newPwd;
    }

    @Override
    public String name(String name) {
        /*设置灯名*/
        if (TextUtils.isEmpty(name)) return null;
        return "sn:" + name;
    }

    @Override
    public String color() {
        return "save";
    }

    @Override
    public String version() {
        return "version";
    }

    @Override
    public String musicOn() {
        return "music on";
    }

    @Override
    public String musicOff() {
        return "music off";
    }

    @Override
    public String diyStart(String value) {
        return "device start" + value;
    }

    @Override
    public String diyNumber(String value) {
        return "rec:" + value;
    }

    @Override
    public String validate(String password) {
        return "mk:" + password;
    }
}
