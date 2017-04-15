package com.guohua.mlight.model.impl;

import android.content.Context;
import android.graphics.Color;

import com.guohua.mlight.lwble.BLECenter;
import com.guohua.mlight.model.IDeviceService;

/**
 * @author Leo
 * @version 1
 * @since 2017-01-03
 * 命令的真正执行者
 */
public class LightService implements IDeviceService {
    private BLECenter mBLECenter;
    private LightProtocol mLightProtocol;

    private volatile static LightService mService = null;

    public static LightService getInstance() {
        if (mService == null) {
            synchronized (LightService.class) {
                if (mService == null) {
                    mService = new LightService();
                }
            }
        }
        return mService;
    }

    private LightService() {
        mBLECenter = BLECenter.getInstance();
        mLightProtocol = LightProtocol.getInstance();
    }

    @Override
    public boolean connect(Context context, String deviceAddress, boolean isAutoConnect) {
        return mBLECenter.connect(context, deviceAddress, isAutoConnect);
    }

    @Override
    public void disconnect(String deviceAddress, boolean isRemove) {
        mBLECenter.disconnect(deviceAddress, isRemove);
    }

    @Override
    public void turnOn(String deviceAddress) {
        String protocol = mLightProtocol.turnOn();
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void turnOff(String deviceAddress) {
        String protocol = mLightProtocol.turnOff();
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void adjustColor(String deviceAddress, int color) {
        /*颜色ARGB*/
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        String protocol = mLightProtocol.control(new Object[]{alpha, red, green, blue});
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void adjustBrightness(String deviceAddress, int color) {
        /*颜色ARGB*/
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        String protocol = mLightProtocol.control(new Object[]{alpha, red, green, blue});
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void validatePassword(String deviceAddress, String password) {
        String protocol = mLightProtocol.validate(password);
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void presetColor(String deviceAddress) {
        String protocol = mLightProtocol.color();
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void delayOff(String deviceAddress, int time) {
        String protocol = mLightProtocol.delayOff(time);
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void password(String deviceAddress, String oldPwd, String newPwd) {
        String protocol = mLightProtocol.password(oldPwd, newPwd);
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void name(String deviceAddress, String name) {
        String protocol = mLightProtocol.name(name);
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void musicOff(String deviceAddress) {
        String protocol = mLightProtocol.musicOff();
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }

    @Override
    public void musicOn(String deviceAddress) {
        String protocol = mLightProtocol.musicOn();
        mBLECenter.send(deviceAddress, protocol.getBytes());
    }
}
