package com.guohua.mlight.net;

import android.util.Log;

import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.common.util.CodeUtils;

/**
 * @author Leo
 * @detail 发送数据的线程 为了使用线程池而设立 防止主线程阻塞
 * @time 2015-11-04
 */
public class DriveModeSendThread extends Thread {
    private long threadDelay;//要发送的数据
    private int[] colors;//情景模式标识码
    public boolean isRunning = false;

    public DriveModeSendThread(long delay, int driveModeCode) {
        threadDelay = delay;
        Log.e("DriveModeSendThread", "DriveModeSendThread delay 1111111111-------------------  " + delay);
        isRunning = true;
        switch (driveModeCode) {
            case Constants.DRIVEMODE_RED_CODE: {
                colors = new int[]{Constants.RED};
            }
            break;
            case Constants.DRIVEMODE_GREEN_CODE: {
                colors = new int[]{Constants.GREEN};
            }
            break;
            case Constants.DRIVEMODE_BLUE_CODE: {
                colors = new int[]{Constants.BLUE};
            }
            break;
            case Constants.DRIVEMODE_REDGREEN_CODE: {
                colors = new int[]{Constants.RED, Constants.GREEN};
            }
            break;
            case Constants.DRIVEMODE_REDBLUE_CODE: {
                colors = new int[]{Constants.RED, Constants.BLUE};
            }
            break;
            case Constants.DRIVEMODE_BLUEGREEN_CODE: {
                colors = new int[]{Constants.BLUE, Constants.GREEN};
            }
            break;
            case Constants.DRIVEMODE_MIX_CODE: {
                colors = new int[]{Constants.RED, Constants.GREEN, Constants.BLUE};
            }
            break;
            case Constants.DRIVEMODE_DIY_CODE: {

            }
            break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        while (isRunning){
            for (int i = 0; i < colors.length; i++) {
                String data = CodeUtils.transARGB2Protocol(colors[i]);
                ThreadPool.getInstance().addTask(new SendRunnable(data));
                Log.e("DriveModeSendThread", "DriveModeSendThread delay 222222-------------------  " + threadDelay);
                try {
                    Thread.sleep(threadDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ThreadPool.getInstance().addTask(
                        new SendRunnable(CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SWITCH, new Object[]{Constants.CMD_CLOSE_LIGHT})));
                try {
                    Thread.sleep(threadDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void destroyThread() {
        isRunning = false;
    }
}
