package com.guohua.mlight.net;

import com.guohua.mlight.common.base.AppContext;

import java.util.ArrayList;

/**
 * @author Leo
 * @detail 发送数据的线程 为了使用线程池而设立 防止主线程阻塞
 * @time 2015-11-04
 */
public class SendSceneDatasRunnable implements Runnable {
    private ArrayList<String> addresses;//地址数组
    private byte[] data;//要发送的数据
    private int delay;

    public SendSceneDatasRunnable(int delay, int[] intData) {
        this.delay = delay;
        if(intData != null){
            data = new byte[intData.length];
            for (int i = 0; i < intData.length; i++) {
                data[i] = (byte)intData[i];
            }
        }
    }

    public SendSceneDatasRunnable(ArrayList<String> addresses, byte[] data) {
        this.addresses = addresses;
        this.data = data;
    }

    @Override
    public void run() {
        /*try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //如果数据为空就结束啦啦啦
        if (data != null && data.length > 0) {
            //如果地址为空就向所有发送啦啦啦
            if (addresses == null || addresses.size() <= 0) {
                AppContext.getInstance().sendAll(data);
            } else {
                //否则就向地址发送数据
                for (String address : addresses) {
                    AppContext.getInstance().send(address, data);
                }
            }
        }
    }
}
