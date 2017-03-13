package com.guohua.mlight.common.util;

import android.os.Handler;
import android.os.Message;

import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.view.fragment.SceneFragment;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;

/**
 * Created by Aladdin on 2016-9-14.
 */
public class SceneModeSaveDiyGradientRamp {

    private static final String TAG = SceneModeSaveDiyGradientRamp.class.getSimpleName();
    public static boolean isRunning = false;
    public static int diyModeNum = 0;
    public static int MAXMODENUM = 10;

    public static void start(int num) {
        isRunning = true;
        diyModeNum = num;
        /*改成启用底层的模式3
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_NUM, new Object[]{diyModeNum});
        ThreadPool.getInstance().addTask(new SendRunnable(data));*/
        refreshDelay.sendEmptyMessageDelayed(0, Constants.HANDLERDELAY);
//        SceneFragment.mSceneAdapter.setState(true, 3);
    }

    public static void destroy() {
        isRunning = false;
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
//        SceneFragment.mSceneAdapter.setState(false, 3);
    }

    public static Handler refreshDelay = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            /*
            之前App设置的10种颜色，由于通信速率跟不上，效果不佳，暂时去掉
            if(!HomeFragment.isLighting){
                return;
            }
            final int num = msg.what + 1;
            runSceneModeSaveDiyGradientRampTest(msg.what);
            System.out.println(System.currentTimeMillis() + ":  -msg.what--  " + msg.what);
            refreshDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (num <= (MAXMODENUM-diyModeNum)) {
                        refreshDelay.sendEmptyMessage(num);
                        System.out.println(System.currentTimeMillis() + ":  - xxxxxxxx msg.what--  " + msg.what);
                    }
                }
            }, Constants.HANDLERDELAY);*/

            //改成启用底层的模式3
            String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_START, new Object[]{3});
            AppContext.getInstance().sendAll(data);
        }
    };

    public static void runSceneModeSaveDiyGradientRampTest(int num) {
        if (num == (MAXMODENUM-diyModeNum)) {
            String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_SAVE_DIY_START, new Object[]{diyModeNum});
            AppContext.getInstance().sendAll(data);
            System.out.println(System.currentTimeMillis() + ":  ---  " + "; CMD_MODE_SAVE_DIY_START:  =====   " + num + "   _________  =====" +
                    data.toString());
        } else {
            AppContext.getInstance().sendAll(getSceneDatas(Constants.SaveDiyColor[num], num));
        }
    }


    private static final byte[] getSceneDatas(final int[] sceneDiyDatas, int num) {

        int sum = 0;
        for (int i = 2; i < sceneDiyDatas.length; i++) {
            sum += sceneDiyDatas[i];
        }

        int highBit = 0, lowBit = 0;
        while (sum > 255) {
            highBit = (sum & 0xff00) >> 8;
            lowBit = sum & 0x00ff;
            sum = highBit + lowBit;
        }

        final byte[] sceneDatas = new byte[15];
        sceneDatas[0] = (byte) sceneDiyDatas[0];
        sceneDatas[1] = (byte) sceneDiyDatas[1];
        sceneDatas[2] = (byte) sceneDiyDatas[2];
        sceneDatas[3] = (byte) sceneDiyDatas[3];
        sceneDatas[4] = (byte) sceneDiyDatas[4];
        sceneDatas[5] = (byte) sceneDiyDatas[5];
        sceneDatas[6] = (byte) sceneDiyDatas[6];
        sceneDatas[7] = (byte) sceneDiyDatas[7];
        sceneDatas[8] = (byte) sceneDiyDatas[8];
        sceneDatas[9] = (byte) sceneDiyDatas[9];
        sceneDatas[10] = (byte) sceneDiyDatas[10];
        sceneDatas[11] = (byte) sceneDiyDatas[11];
        sceneDatas[12] = (byte) sceneDiyDatas[12];
        sceneDatas[13] = (byte) sceneDiyDatas[13];
        sceneDatas[14] = (byte) sum;

        System.out.println(System.currentTimeMillis() + ":  ---  " + "; getSceneDatas:  =====   " + num + "   _________  =====" +
                sceneDatas[0] + ":" + (sceneDatas[1] & 0x0ff) + ":" + (sceneDatas[2] & 0x0ff) + ":" + (sceneDatas[3] & 0x0ff) + ":" +
                (sceneDatas[4] & 0x0ff) + ":" + (sceneDatas[5] & 0x0ff) + ":" + (sceneDatas[6] & 0x0ff) + ":" + (sceneDatas[7] & 0x0ff) + ":" +
                (sceneDatas[8] & 0x0ff) + ":" + (sceneDatas[9] & 0x0ff) + ":" + (sceneDatas[10] & 0x0ff) + ":" + (sceneDatas[11] & 0x0ff) + ":" +
                (sceneDatas[12] & 0x0ff) + ":" + (sceneDatas[13] & 0x0ff) + ":" + (sceneDatas[14] & 0x0ff));

        return sceneDatas;
    }

}
