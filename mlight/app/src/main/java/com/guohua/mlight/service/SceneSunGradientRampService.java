package com.guohua.mlight.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.SendSceneDatasRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.util.CodeUtils;
import com.guohua.mlight.util.Constant;

/**
 * @author Leo
 * @detail 音乐律动的服务使用Visualizer实现
 * @time 2015-11-09
 */
public class SceneSunGradientRampService extends Service {

    private static final String TAG = SceneSunGradientRampService.class.getSimpleName();
    public static boolean isRunning = false;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        isRunning = true;
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_ON, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        refreshDelay.sendEmptyMessageDelayed(0, Constant.HANDLERDELAY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.notification_content_GradientRamp_Sun))
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        flags = START_STICKY;//杀不死

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopForeground(true);
        super.onDestroy();
        isRunning = false;
        String data = CodeUtils.transARGB2Protocol(CodeUtils.CMD_MODE_MUSIC_OFF, null);
        ThreadPool.getInstance().addTask(new SendRunnable(data));
        refreshDelay.removeCallbacks(new SendSceneDatasRunnable(0, null));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler refreshDelay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            runSceneSunGradientRamp();
            runSceneSunGradientRampTest();
            refreshDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isRunning) {
                        refreshDelay.sendEmptyMessage(0);
                    }else{
                        refreshDelay.removeCallbacks(new SendSceneDatasRunnable(0, null));
                    }
                }
            }, Constant.SUN_DOWN_DELAY1 * 2 + Constant.SUN_DOWN_DELAY2 * 2 + Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2);
        }
    };



    public void runSceneSunGradientRampTest() {

        final SceneListInfo.SceneInfo ss = Constant.sunScene;
        System.out.println(System.currentTimeMillis() + ":  ---  " +
                "SceneListInfo.SceneInfo ss.SceneDatasHead:  ===== 0000 =====  " + ss.SceneDatasHead[0] + ":" + (ss.SceneDatasHead[1] & 0x0ff) + ":" + (ss.SceneDatasHead[2] & 0x0ff) + ":" + (ss.SceneDatasHead[3] & 0x0ff) + ":"
                + (ss.SceneDatasHead[4] & 0x0ff) + ":" + (ss.SceneDatasHead[5] & 0x0ff) + ":" + (ss.SceneDatasHead[6] & 0x0ff) + ":" + (ss.SceneDatasHead[7] & 0x0ff));

        //上午
        //rgb(0,0,0)->rgb(100,0,0) detaV(1,0,0) detaT(15,1,1)                      1阶段
        byte ctrMode = (byte) (((ss.SceneDatasHead[0] << 7) + (ss.SceneDatasHead[1] << 6) + (ss.SceneDatasHead[2] << 5) + (ss.SceneDatasHead[3] << 4) +
                (ss.SceneDatasHead[4] << 3) + (ss.SceneDatasHead[5] << 2) + (ss.SceneDatasHead[6] << 1) + (ss.SceneDatasHead[7])) & 0x0ff);

        final int[] start_datas = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[12] & 0x0ff), (Constant.SunSceneDefaultGradientColor[13] & 0x0ff), (Constant.SunSceneDefaultGradientColor[14] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff)
        );

        ThreadPool.getInstance().addTask(new SendSceneDatasRunnable(0, start_datas));
        System.out.println(System.currentTimeMillis() + ":  ---  " + (Constant.SUN_DOWN_DELAY1) + " ++ " + (Constant.SUN_DOWN_DELAY2) + " ++ " + (Constant.SUN_DOWN_DELAY3) + " ++ " + (Constant.SUN_DOWN_DELAY4) + " ++ " +
                " runSceneSunGradientRamp:  ===== 111111 =====  " + start_datas[0] + ":" + (start_datas[1] & 0x0ff) + ":" + (start_datas[2] & 0x0ff) + ":" + (start_datas[3] & 0x0ff) + ":"
                + (start_datas[4] & 0x0ff) + ":" + (start_datas[5] & 0x0ff) + ":" + (start_datas[6] & 0x0ff) + ":" + (start_datas[7] & 0x0ff) + ":" +
                (start_datas[8] & 0x0ff) + ":" + (start_datas[9] & 0x0ff) + ":" + (start_datas[10] & 0x0ff) + ":" + (start_datas[11] & 0x0ff) + ":" +
                (start_datas[12] & 0x0ff) + ":" + (start_datas[13] & 0x0ff));
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //rgb(100,0,0)->rgb(165,20,0)  detaV(1,1,0) detaT(20,65,0)                      2阶段
        final int[] end_datas2 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas2), Constant.SUN_DOWN_DELAY1);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(165,20,0)->rgb(255,200,80)  detaV(1,1,1) detaT(16,8,16)                      3阶段
        final int[] end_datas3 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas3), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(255,200,80)->rgb(255,255,255)  detaV(0,1,1) detaT(1,25,8)                      4阶段
        final int[] end_datas4 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[9] & 0x0ff), (Constant.SunSceneDefaultGradientColor[10] & 0x0ff), (Constant.SunSceneDefaultGradientColor[11] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas4), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //下午------------------------------------------------------------------
        ctrMode = (byte) (((ss.SceneDatasHead[0] << 7) + (0 << 6) + (0 << 5) + (0 << 4) +
                (ss.SceneDatasHead[4] << 3) + (ss.SceneDatasHead[5] << 2) + (ss.SceneDatasHead[6] << 1) + (ss.SceneDatasHead[7])) & 0x0ff);

        //rgb(255,255,255)->rgb(255,200,80)  detaV(0,1,1) detaT(1,25,8)                      5阶段
        final int[] end_datas5 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[9] & 0x0ff), (Constant.SunSceneDefaultGradientColor[10] & 0x0ff), (Constant.SunSceneDefaultGradientColor[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas5), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 +
                Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(255,200,80)->rgb(165,20,0)  detaV(1,1,1) detaT(16,8,16)                      6阶段
        final int[] end_datas6 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas6), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 +
                Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4 * 2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(165,20,0)->rgb(100,0,0) detaV(1,1,0) detaT(20,65,0)                      7阶段
        final int[] end_datas7 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas7), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 +
                Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(100,0,0)->rgb(0,0,0) detaV(1,0,0) detaT(15,1,1)                      8阶段
        final int[] end_datas8 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[12] & 0x0ff), (Constant.SunSceneDefaultGradientColor[13] & 0x0ff), (Constant.SunSceneDefaultGradientColor[14] & 0x0ff)
        );

        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, end_datas8), Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 * 2 +
                Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////
    }
/*
    public void runSceneSunGradientRampTest() {

        final SceneListInfo.SceneInfo ss = Constant.sunScene;
        System.out.println(System.currentTimeMillis() + ":  ---  " +
                "SceneListInfo.SceneInfo ss.SceneDatasHead:  ===== 0000 =====  " + ss.SceneDatasHead[0] + ":" + (ss.SceneDatasHead[1] & 0x0ff) + ":" + (ss.SceneDatasHead[2] & 0x0ff) + ":" + (ss.SceneDatasHead[3] & 0x0ff) + ":"
                + (ss.SceneDatasHead[4] & 0x0ff) + ":" + (ss.SceneDatasHead[5] & 0x0ff) + ":" + (ss.SceneDatasHead[6] & 0x0ff) + ":" + (ss.SceneDatasHead[7] & 0x0ff));

        //上午
        //rgb(0,0,0)->rgb(100,0,0) detaV(1,0,0) detaT(15,1,1)                      1阶段
        byte ctrMode = (byte) (((ss.SceneDatasHead[0] << 7) + (ss.SceneDatasHead[1] << 6) + (ss.SceneDatasHead[2] << 5) + (ss.SceneDatasHead[3] << 4) +
                (ss.SceneDatasHead[4] << 3) + (ss.SceneDatasHead[5] << 2) + (ss.SceneDatasHead[6] << 1) + (ss.SceneDatasHead[7])) & 0x0ff);

        final byte[] start_datas = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[12] & 0x0ff), (Constant.SunSceneDefaultGradientColor[13] & 0x0ff), (Constant.SunSceneDefaultGradientColor[14] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff)
        );

        AppContext.getInstance().sendAll(start_datas);
        System.out.println(System.currentTimeMillis() + ":  ---  " + (Constant.SUN_DOWN_DELAY1) + " ++ " + (Constant.SUN_DOWN_DELAY2) + " ++ " + (Constant.SUN_DOWN_DELAY3) + " ++ " + (Constant.SUN_DOWN_DELAY4) + " ++ " +
                " runSceneSunGradientRamp:  ===== 111111 =====  " + start_datas[0] + ":" + (start_datas[1] & 0x0ff) + ":" + (start_datas[2] & 0x0ff) + ":" + (start_datas[3] & 0x0ff) + ":"
                + (start_datas[4] & 0x0ff) + ":" + (start_datas[5] & 0x0ff) + ":" + (start_datas[6] & 0x0ff) + ":" + (start_datas[7] & 0x0ff) + ":" +
                (start_datas[8] & 0x0ff) + ":" + (start_datas[9] & 0x0ff) + ":" + (start_datas[10] & 0x0ff) + ":" + (start_datas[11] & 0x0ff) + ":" +
                (start_datas[12] & 0x0ff) + ":" + (start_datas[13] & 0x0ff));
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        //rgb(100,0,0)->rgb(165,20,0)  detaV(1,1,0) detaT(20,65,0)                      2阶段
        final byte[] end_datas2 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas2);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        Constant.SUN_DOWN_DELAY1 + "; runSceneSunGradientRamp:  ===== 2222222 =====  " + end_datas2[0] + ":" + (end_datas2[1] & 0x0ff) + ":" + (end_datas2[2] & 0x0ff) + ":" + (end_datas2[3] & 0x0ff) + ":"
                        + (end_datas2[4] & 0x0ff) + ":" + (end_datas2[5] & 0x0ff) + ":" + (end_datas2[6] & 0x0ff) + ":" + (end_datas2[7] & 0x0ff) + ":" +
                        (end_datas2[8] & 0x0ff) + ":" + (end_datas2[9] & 0x0ff) + ":" + (end_datas2[10] & 0x0ff) + ":" + (end_datas2[11] & 0x0ff) + ":" +
                        (end_datas2[12] & 0x0ff) + ":" + (end_datas2[13] & 0x0ff));
            }
        }, Constant.SUN_DOWN_DELAY1);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(165,20,0)->rgb(255,200,80)  detaV(1,1,1) detaT(16,8,16)                      3阶段
        final byte[] end_datas3 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas3);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2) + "; runSceneSunGradientRamp:  ===== 333333333 =====  " + end_datas3[0] + ":" + (end_datas3[1] & 0x0ff) + ":" + (end_datas3[2] & 0x0ff) + ":" + (end_datas3[3] & 0x0ff) + ":"
                        + (end_datas3[4] & 0x0ff) + ":" + (end_datas3[5] & 0x0ff) + ":" + (end_datas3[6] & 0x0ff) + ":" + (end_datas3[7] & 0x0ff) + ":" +
                        (end_datas3[8] & 0x0ff) + ":" + (end_datas3[9] & 0x0ff) + ":" + (end_datas3[10] & 0x0ff) + ":" + (end_datas3[11] & 0x0ff) + ":" +
                        (end_datas3[12] & 0x0ff) + ":" + (end_datas3[13] & 0x0ff));
            }
        }, (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2));
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(255,200,80)->rgb(255,255,255)  detaV(0,1,1) detaT(1,25,8)                      4阶段
        final byte[] end_datas4 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[9] & 0x0ff), (Constant.SunSceneDefaultGradientColor[10] & 0x0ff), (Constant.SunSceneDefaultGradientColor[11] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas4);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3) + "; runSceneSunGradientRamp:  ===== 444444444444 =====  " + end_datas4[0] + ":" + (end_datas4[1] & 0x0ff) + ":" + (end_datas4[2] & 0x0ff) + ":" + (end_datas4[3] & 0x0ff) + ":"
                        + (end_datas4[4] & 0x0ff) + ":" + (end_datas4[5] & 0x0ff) + ":" + (end_datas4[6] & 0x0ff) + ":" + (end_datas4[7] & 0x0ff) + ":" +
                        (end_datas4[8] & 0x0ff) + ":" + (end_datas4[9] & 0x0ff) + ":" + (end_datas4[10] & 0x0ff) + ":" + (end_datas4[11] & 0x0ff) + ":" +
                        (end_datas4[12] & 0x0ff) + ":" + (end_datas4[13] & 0x0ff));
            }
        }, (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3));
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //下午------------------------------------------------------------------
        ctrMode = (byte) (((ss.SceneDatasHead[0] << 7) + (0 << 6) + (0 << 5) + (0 << 4) +
                (ss.SceneDatasHead[4] << 3) + (ss.SceneDatasHead[5] << 2) + (ss.SceneDatasHead[6] << 1) + (ss.SceneDatasHead[7])) & 0x0ff);

        //rgb(255,255,255)->rgb(255,200,80)  detaV(0,1,1) detaT(1,25,8)                      5阶段
        final byte[] end_datas5 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[9] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[10] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[9] & 0x0ff), (Constant.SunSceneDefaultGradientColor[10] & 0x0ff), (Constant.SunSceneDefaultGradientColor[11] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas5);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4) + "; runSceneSunGradientRamp:  ===== 555555555555555555555 =====  " + end_datas5[0] + ":" + (end_datas5[1] & 0x0ff) + ":" + (end_datas5[2] & 0x0ff) + ":" + (end_datas5[3] & 0x0ff) + ":"
                        + (end_datas5[4] & 0x0ff) + ":" + (end_datas5[5] & 0x0ff) + ":" + (end_datas5[6] & 0x0ff) + ":" + (end_datas5[7] & 0x0ff) + ":" +
                        (end_datas5[8] & 0x0ff) + ":" + (end_datas5[9] & 0x0ff) + ":" + (end_datas5[10] & 0x0ff) + ":" + (end_datas5[11] & 0x0ff) + ":" +
                        (end_datas5[12] & 0x0ff) + ":" + (end_datas5[13] & 0x0ff));
            }
        }, (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4));
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(255,200,80)->rgb(165,20,0)  detaV(1,1,1) detaT(16,8,16)                      6阶段
        final byte[] end_datas6 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[6] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[7] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[6] & 0x0ff), (Constant.SunSceneDefaultGradientColor[7] & 0x0ff), (Constant.SunSceneDefaultGradientColor[8] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas6);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4 * 2) + "; runSceneSunGradientRamp:  ===== 66666666666666666666 =====  " + end_datas6[0] + ":" + (end_datas6[1] & 0x0ff) + ":" + (end_datas6[2] & 0x0ff) + ":" + (end_datas6[3] & 0x0ff) + ":"
                        + (end_datas6[4] & 0x0ff) + ":" + (end_datas6[5] & 0x0ff) + ":" + (end_datas6[6] & 0x0ff) + ":" + (end_datas6[7] & 0x0ff) + ":" +
                        (end_datas6[8] & 0x0ff) + ":" + (end_datas6[9] & 0x0ff) + ":" + (end_datas6[10] & 0x0ff) + ":" + (end_datas6[11] & 0x0ff) + ":" +
                        (end_datas6[12] & 0x0ff) + ":" + (end_datas6[13] & 0x0ff));
            }
        }, (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 + Constant.SUN_DOWN_DELAY4 * 2));
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(165,20,0)->rgb(100,0,0) detaV(1,1,0) detaT(20,65,0)                      7阶段
        final byte[] end_datas7 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[3] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[4] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[3] & 0x0ff), (Constant.SunSceneDefaultGradientColor[4] & 0x0ff), (Constant.SunSceneDefaultGradientColor[5] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas7);
                System.out.println(System.currentTimeMillis() + ":  ---  " +
                        (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2) + "; runSceneSunGradientRamp:  ===== 7777777777777777 =====  " + end_datas7[0] + ":" + (end_datas7[1] & 0x0ff) + ":" + (end_datas7[2] & 0x0ff) + ":" + (end_datas7[3] & 0x0ff) + ":"
                        + (end_datas7[4] & 0x0ff) + ":" + (end_datas7[5] & 0x0ff) + ":" + (end_datas7[6] & 0x0ff) + ":" + (end_datas7[7] & 0x0ff) + ":" +
                        (end_datas7[8] & 0x0ff) + ":" + (end_datas7[9] & 0x0ff) + ":" + (end_datas7[10] & 0x0ff) + ":" + (end_datas7[11] & 0x0ff) + ":" +
                        (end_datas7[12] & 0x0ff) + ":" + (end_datas7[13] & 0x0ff));
            }
        }, Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 + Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////


        //rgb(100,0,0)->rgb(0,0,0) detaV(1,0,0) detaT(15,1,1)                      8阶段
        final byte[] end_datas8 = getSceneDatas(ctrMode,
                (Constant.SunSceneDefaultGradientColorDeta[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorDeta[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColorTime[0] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[1] & 0x0ff), (Constant.SunSceneDefaultGradientColorTime[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[0] & 0x0ff), (Constant.SunSceneDefaultGradientColor[1] & 0x0ff), (Constant.SunSceneDefaultGradientColor[2] & 0x0ff),
                (Constant.SunSceneDefaultGradientColor[12] & 0x0ff), (Constant.SunSceneDefaultGradientColor[13] & 0x0ff), (Constant.SunSceneDefaultGradientColor[14] & 0x0ff)
        );

        refreshDelay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppContext.getInstance().sendAll(end_datas8);
                System.out.println(System.currentTimeMillis() + ":  ---  " + (Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 * 2 + Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2) + " ++ " + (Constant.SUN_DOWN_DELAY2) + " ++ " + (Constant.SUN_DOWN_DELAY3) + " ++ " + (Constant.SUN_DOWN_DELAY4) + " ++ " +
                        " runSceneSunGradientRamp:  ===== 888888888888 =====  " + end_datas8[0] + ":" + (end_datas8[1] & 0x0ff) + ":" + (end_datas8[2] & 0x0ff) + ":" + (end_datas8[3] & 0x0ff) + ":"
                        + (end_datas8[4] & 0x0ff) + ":" + (end_datas8[5] & 0x0ff) + ":" + (end_datas8[6] & 0x0ff) + ":" + (end_datas8[7] & 0x0ff) + ":" +
                        (end_datas8[8] & 0x0ff) + ":" + (end_datas8[9] & 0x0ff) + ":" + (end_datas8[10] & 0x0ff) + ":" + (end_datas8[11] & 0x0ff) + ":" +
                        (end_datas8[12] & 0x0ff) + ":" + (end_datas8[13] & 0x0ff));
            }
        }, Constant.SUN_DOWN_DELAY1 + Constant.SUN_DOWN_DELAY2 * 2 + Constant.SUN_DOWN_DELAY3 * 2 + Constant.SUN_DOWN_DELAY4 * 2);
        //////////////////////////////////////////////////////////////////////////////////////////////////////
    }
*/


    private final int[] getSceneDatas(byte ctrMode, int deta_red, int deta_green, int deta_blue, int deta_red_time, int deta_green_time, int deta_blue_time,
                                       int start_deta_red, int start_deta_green, int start_deta_blue, int end_deta_red, int end_deta_green, int end_deta_blue) {

        int sum = deta_red + deta_green + deta_blue + deta_red_time + deta_green_time + deta_blue_time +
                start_deta_red + start_deta_green + start_deta_blue + end_deta_red + end_deta_green + end_deta_blue;
        int highBit = 0, lowBit = 0;
        while (sum > 255) {
            highBit = (sum & 0xff00) >> 8;
            lowBit = sum & 0x00ff;
            sum = highBit + lowBit;
        }

        final int[] sceneDatas = new int[14];
        sceneDatas[0] = ctrMode;
        sceneDatas[1] = deta_red;
        sceneDatas[2] = deta_green;
        sceneDatas[3] = deta_blue;
        sceneDatas[4] = deta_red_time;
        sceneDatas[5] = deta_green_time;
        sceneDatas[6] = deta_blue_time;
        sceneDatas[7] = start_deta_red;
        sceneDatas[8] = start_deta_green;
        sceneDatas[9] = start_deta_blue;
        sceneDatas[10] = end_deta_red;
        sceneDatas[11] = end_deta_green;
        sceneDatas[12] = end_deta_blue;
        sceneDatas[13] = sum;

        return sceneDatas;
    }

}

