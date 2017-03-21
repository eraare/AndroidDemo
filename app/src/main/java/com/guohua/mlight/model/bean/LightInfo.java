package com.guohua.mlight.model.bean;

/**
 * Created by china on 2017/3/20.
 */

public class LightInfo {
    public static final String DEFAULT_PASSWORD = "0000";

    public String name; /*设备名*/
    public String address; /*设备地址*/
    public String password; /*设备密码*/
    public int hue; /*色相 0-360*/
    public int saturation; /*饱和度 0-1*/
    public int value; /*明度 0-1*/
    public boolean select; /*选择情况*/
    public boolean connect; /*连接状态*/
    public boolean state; /*开关灯状态*/

    public LightInfo(String name, String address) {
        this(name, address, DEFAULT_PASSWORD, 360, 1, 1, true, false, false);
    }

    public LightInfo(String name, String address, String password, int hue, int saturation, int value, boolean select, boolean connect, boolean state) {
        this.name = name;
        this.address = address;
        this.password = password;
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
        this.select = select;
        this.connect = connect;
        this.state = state;
    }
}
