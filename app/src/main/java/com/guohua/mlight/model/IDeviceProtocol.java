package com.guohua.mlight.model;

/**
 * 设备控制协议
 */
public interface IDeviceProtocol {
    /*开灯*/
    String turnOn();

    /*关灯*/
    String turnOff();

    /*控制颜色和亮度*/
    String control(Object[] values);

    /*延时开灯*/
    String delayOn(int millis);

    /*延时关灯*/
    String delayOff(int millis);

    /*设置密码*/
    String password(String oldPwd, String newPwd);

    /*更改灯名*/
    String name(String name);

    /*预置灯色*/
    String color();

    /*获取版本*/
    String version();

    /*开启底层律动*/
    String musicOn();

    /*关闭底层律动*/
    String musicOff();

    /*DIY*/
    String diyNumber(String value);

    /*DIY*/
    String diyStart(String value);

    /*密码校验*/
    String validate(String password);
}
