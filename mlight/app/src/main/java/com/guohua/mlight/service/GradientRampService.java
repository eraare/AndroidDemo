package com.guohua.mlight.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.guohua.mlight.AppContext;
import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.bean.SceneListInfo;
import com.guohua.mlight.net.SendSceneDatasRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.util.Constant;
import com.guohua.mlight.util.ToolUtils;

/**
 * @author Leo
 * @detail 音乐律动的服务使用Visualizer实现
 * @time 2015-11-09
 */
public class GradientRampService extends Service{

    private static final String TAG = GradientRampService.class.getSimpleName();

    private ThreadPool pool = null;

    public static String notification_content_title = "";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title)).setContentText(notification_content_title)
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        flags = START_STICKY;//杀不死

        return super.onStartCommand(intent, flags, startId);
    }


    private static final long DELAY = 3000;

    /**
     * 情景模式 和 音乐律动 不能同时运行
     */
    private void stopVisualizerService() {
        Intent service = new Intent(this, VisualizerService.class);
        stopService(service);
        //Toast.makeText(this, R.string.notice_warning, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stopVisualizerService();
        /**
         * 注册广播
         */
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constant.ACTION_EXIT);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

        pool = ThreadPool.getInstance();//得到线程池
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constant.ACTION_EXIT)) {
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopForeground(true);
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        refreshDelay.removeCallbacks(new SendSceneDatasRunnable(0, null));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new GradientRampServiceBinder();
    }

    private class GradientRampServiceBinder extends Binder implements IGradientRampService {

        @Override
        public void setCurrentGradientColor(int delay) {
            runGradientRamp(delay);
        }

        @Override
        public void setSceneCurrentGradientColor(int delay, SceneListInfo.SceneInfo ss) {
            runSceneGradientRamp(delay, ss);
        }


        @Override
        public void setSceneRgbGradientColor(SceneListInfo.SceneInfo ss) {
//            AppContext.isSceneRgbRun = true;
            SceneListInfo.SceneInfo redSS = null, greenSS = null, blueSS = null;
            try {
                redSS = (SceneListInfo.SceneInfo)ss.clone();

                greenSS = (SceneListInfo.SceneInfo)ss.clone();

                blueSS = (SceneListInfo.SceneInfo)ss.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            if(redSS.SceneCurClickColorImgOnOff[1] == 1){
                redSS.SceneCurClickColorImgOnOff[2] = 0;
                redSS.SceneCurClickColorImgOnOff[3] = 0;
                redSS.SceneGradientRampGradientGap[2] = 0;
                redSS.SceneGradientRampGradientGap[3] = 0;
                redSS.SceneGradientRampStopGap[2] = 1;
                redSS.SceneGradientRampStopGap[3] = 1;
                runSceneGradientRamp(0, redSS);
            }

            if(greenSS.SceneCurClickColorImgOnOff[2] == 1){
                greenSS.SceneCurClickColorImgOnOff[1] = 0;
                greenSS.SceneCurClickColorImgOnOff[3] = 0;
                greenSS.SceneGradientRampGradientGap[1] = 0;
                greenSS.SceneGradientRampGradientGap[3] = 0;
                greenSS.SceneGradientRampStopGap[1] = 1;
                greenSS.SceneGradientRampStopGap[3] = 1;
                runSceneGradientRamp(ss.GREEN_GRADIENT_DELAY, greenSS);
            }

            if(blueSS.SceneCurClickColorImgOnOff[3] == 1){
                blueSS.SceneCurClickColorImgOnOff[1] = 0;
                blueSS.SceneCurClickColorImgOnOff[2] = 0;
                blueSS.SceneGradientRampGradientGap[1] = 0;
                blueSS.SceneGradientRampGradientGap[2] = 0;
                blueSS.SceneGradientRampStopGap[1] = 1;
                blueSS.SceneGradientRampStopGap[2] = 1;
                runSceneGradientRamp(ss.BLUE_GRADIENT_DELAY, blueSS);
            }
        }

    }

    public interface IGradientRampService {

        //int curColor[]  [0]:stopGap  [1]:gradientGap  [2]:color  [3]:onOff
        void setCurrentGradientColor(int delay);
        void setSceneCurrentGradientColor(int delay, SceneListInfo.SceneInfo ss);
        void setSceneRgbGradientColor(SceneListInfo.SceneInfo ss);
    }

    Handler refreshDelay = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    /**
     * 历史遗留，之前开发的，配合GradientRampActivity使用
     * @param delay
     */
    public void runGradientRamp(int delay){

            byte ctrMode = (byte) (0x78 + (((AppContext.curClickColorImgOnOff[1]<<2) + (AppContext.curClickColorImgOnOff[2]<<1) + (AppContext.curClickColorImgOnOff[3])) & 0x0ff));
//            ctrMode = (byte)(0xa0 + (1 << 2) + (1 << 1) + 1);

            int deta_red = (AppContext.gradientRampGradientGap[1] & 0x0ff);
            int deta_green = (AppContext.gradientRampGradientGap[2] & 0x0ff);
            int deta_blue = (AppContext.gradientRampGradientGap[3] & 0x0ff);
            int deta_red_time = (AppContext.gradientRampStopGap[1] & 0x0ff);
            int deta_green_time = (AppContext.gradientRampStopGap[2] & 0x0ff);
            int deta_blue_time =  (AppContext.gradientRampStopGap[3] & 0x0ff);

            int sum = deta_red + deta_green + deta_blue + deta_red_time + deta_green_time + deta_blue_time;

            System.out.println("datas:  ===== 000000 =====  " + ctrMode + ":" + (deta_red & 0x0ff) + ":"  + (deta_green & 0x0ff) + ":"  + (deta_blue & 0x0ff) + ":"
                + (deta_red_time & 0x0ff) + ":"  +
                (deta_green_time & 0x0ff) + ":" + (deta_blue_time & 0x0ff) + ":" + (sum & 0x0ff));

            int highBit = 0, lowBit = 0;
            while(sum > 255){
                System.out.println("1 sum: " + sum + "; highBit: " + highBit + "; lowBit: " + lowBit);
                highBit = (sum & 0xff00) >> 8;
                lowBit = sum & 0x00ff;
                sum =  highBit + lowBit;
                System.out.println("2 sum: " + sum + "; highBit: " + highBit + "; lowBit: " + lowBit);
            }
            final int[] datas = new int[8];
            datas[0] = ctrMode;
            datas[1] = deta_red;
            datas[2] = deta_green;
            datas[3] = deta_blue;
            datas[4] = deta_red_time;
            datas[5] = deta_green_time;
            datas[6] = deta_blue_time;
            datas[7] = sum;
            System.out.println("datas:  ==========  " + datas.length);

            refreshDelay.postDelayed(new SendSceneDatasRunnable(0,datas), delay);
            System.out.println("dataCmd:  ===== 111111 =====  " + ctrMode + ":" + (deta_red & 0x0ff) + ":"  + (deta_green & 0x0ff) + ":"  + (deta_blue & 0x0ff) + ":"
                        + (deta_red_time & 0x0ff) + ":"  +
                        (deta_green_time & 0x0ff) + ":" + (deta_blue_time & 0x0ff) + ":" + (sum & 0x0ff));

    }

    /**
     * 现在使用的，配合SceneModeActivity使用
     * @param delay
     */
    public void runSceneGradientRamp(int delay, SceneListInfo.SceneInfo ss){
        refreshDelay.postDelayed(new SendSceneDatasRunnable(0, (ToolUtils.getSceneGradientRampByteArray(ss))), delay);
    }

}

