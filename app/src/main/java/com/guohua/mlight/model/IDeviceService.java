package com.guohua.mlight.model;

import android.content.Context;

/**
 * 命令模式中的具体执行者Receiver的抽象角色
 */
public interface IDeviceService {
    /**
     * 连接设备
     *
     * @param context
     * @param deviceAddress
     * @param isAutoConnect
     */
    boolean connect(Context context, String deviceAddress, boolean isAutoConnect);

    /**
     * 断开连接
     *
     * @param deviceAddress
     * @param isRemove
     */
    void disconnect(String deviceAddress, boolean isRemove);

    /**
     * 开灯
     */
    void turnOn(String deviceAddress);

    /**
     * 关灯
     */
    void turnOff(String deviceAddress);

    /**
     * 调色
     * 将改变后的颜色值封装到color里
     */
    void adjustColor(String deviceAddress, int color);

    /**
     * 调光
     * 将改变后的亮度值封装到color里
     */
    void adjustBrightness(String deviceAddress, int color);

    /**
     * 验证密码
     *
     * @param deviceAddress
     * @param password
     */
    void validatePassword(String deviceAddress, String password);

    /**
     * 预置灯色
     *
     * @param deviceAddress
     */
    void presetColor(String deviceAddress);

    /**
     * 延时关灯
     *
     * @param deviceAddress
     */
    void delayOff(String deviceAddress, int time);

    /**
     * 设置密码
     *
     * @param oldPwd
     * @param newPwd
     */
    void password(String deviceAddress, String oldPwd, String newPwd);

    /**
     * 更改灯名
     *
     * @param name
     */
    void name(String deviceAddress, String name);

    /**
     * 炫彩渐变关闭
     *
     * @param deviceAddress
     */
    void musicOff(String deviceAddress);

    /**
     * 炫彩渐变开启
     *
     * @param deviceAddress
     */
    void musicOn(String deviceAddress);
}
