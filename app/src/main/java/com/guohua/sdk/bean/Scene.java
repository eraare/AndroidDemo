package com.guohua.sdk.bean;

/**
 * @author Leo
 * @version 1
 * @since 2016-08-25
 * 情景
 */
public class Scene {
    public Object tag;
    public String title;
    public String detail;
    public int picture;
    public boolean isRunning;

    public Scene(Object tag, String title, String detail, int picture) {
        this.tag = tag;
        this.title = title;
        this.detail = detail;
        this.picture = picture;
        isRunning = false;
    }
}
