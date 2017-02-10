package com.guohua.mlight.model.bean;

/**
 * Created by Aladdin on 2016-8-16.
 */
public class Timer {

    public int deviceId;
    public String timerTime;
    public int delayCloseSeconds;
    public int delayCloseSecondsRest;
    public int delayOpenSeconds;
    public int delayOpenSecondsRest;

    @Override
    public String toString() {
        return "Timer{" +
                "deviceId=" + deviceId +
                ", timerTime='" + timerTime + '\'' +
                ", delayCloseSeconds=" + delayCloseSeconds +
                ", delayCloseSecondsRest=" + delayCloseSecondsRest +
                ", delayOpenSeconds=" + delayOpenSeconds +
                ", delayOpenSecondsRest=" + delayOpenSecondsRest +
                '}';
    }

    public Timer(int deviceId, String timerTime, int delayCloseSeconds, int delayCloseSecondsRest, int delayOpenSeconds, int delayOpenSecondsRest) {
        this.deviceId = deviceId;
        this.timerTime = timerTime;
        this.delayCloseSeconds = delayCloseSeconds;
        this.delayCloseSecondsRest = delayCloseSecondsRest;
        this.delayOpenSeconds = delayOpenSeconds;
        this.delayOpenSecondsRest = delayOpenSecondsRest;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimerTime() {
        return timerTime;
    }

    public void setTimerTime(String timerTime) {
        this.timerTime = timerTime;
    }

    public int getDelayCloseSeconds() {
        return delayCloseSeconds;
    }

    public void setDelayCloseSeconds(int delayCloseSeconds) {
        this.delayCloseSeconds = delayCloseSeconds;
    }

    public int getDelayCloseSecondsRest() {
        return delayCloseSecondsRest;
    }

    public void setDelayCloseSecondsRest(int delayCloseSecondsRest) {
        this.delayCloseSecondsRest = delayCloseSecondsRest;
    }

    public int getDelayOpenSeconds() {
        return delayOpenSeconds;
    }

    public void setDelayOpenSeconds(int delayOpenSeconds) {
        this.delayOpenSeconds = delayOpenSeconds;
    }

    public int getDelayOpenSecondsRest() {
        return delayOpenSecondsRest;
    }

    public void setDelayOpenSecondsRest(int delayOpenSecondsRest) {
        this.delayOpenSecondsRest = delayOpenSecondsRest;
    }
}
