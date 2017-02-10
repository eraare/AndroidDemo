package com.guohua.mlight.model.bean;

/**
 * 设备类
 */
public class Device {
    private String deviceName;//保存到数据库
    private String deviceAddress;//保存到数据库
    private boolean connected;//内存中 需要随时读写状态
    private boolean selected;//内存中 需要随时读写状态

    public Device() {
        this.connected = false;
        this.selected = true;
    }

    public Device(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = false;
        this.selected = false;
    }

    public Device(String deviceName, String deviceAddress, boolean selected) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = false;
        this.selected = selected;
    }

    public Device(String deviceName, String deviceAddress, boolean connected, boolean selected) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.connected = connected;
        this.selected = selected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
