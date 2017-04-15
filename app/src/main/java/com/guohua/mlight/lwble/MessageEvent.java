package com.guohua.mlight.lwble;

/**
 * Created by china on 2017/3/20.
 */
public final class MessageEvent {
    /*传输内容的控制*/
    public static final int WHAT_STATE = 1;
    public static final int WHAT_DATA = 2;
    public static final int WHAT_ALL = 3;

    public int what; /*传输内容*/
    public String address; /*MAC地址*/
    public int state; /*连接状态*/
    public String data; /*接收到的数据*/

    public MessageEvent(String address, int state) {
        this.address = address;
        this.state = state;
        this.what = WHAT_STATE;
    }

    public MessageEvent(String address, String data) {
        this.address = address;
        this.data = data;
        this.what = WHAT_DATA;
    }

    public MessageEvent(String address, int state, String data) {
        this.address = address;
        this.state = state;
        this.data = data;
        this.what = WHAT_ALL;
    }
}
