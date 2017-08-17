package com.guohua.sdk.bean;

/**
 * @author Leo
 * @version 1
 * @since 2017-08-15
 * 设备Bean类
 */
public class Device {
    public String name;
    public String address;
    public boolean select;
    public boolean connect;
    public String password;

    public Device(String name, String address) {
        this(name, address, "0000", true, false);
    }

    public Device(String name, String address, String password, boolean select, boolean connect) {
        this.name = name;
        this.address = address;
        this.password = password;
        this.select = select;
        this.connect = connect;
    }
}
