package com.guohua.mlight.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.guohua.mlight.MainActivity;
import com.guohua.mlight.R;
import com.guohua.mlight.model.ai.IObserver;
import com.guohua.mlight.model.ai.ISubject;
import com.guohua.mlight.net.SendRunnable;
import com.guohua.mlight.net.ThreadPool;
import com.guohua.mlight.common.util.CodeUtils;
import com.guohua.mlight.common.util.Constant;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Leo
 * @detail 音乐律动的服务使用Visualizer实现
 * @time 2015-11-09
 */
public class VisualizerService extends Service implements ISubject {
    // 音乐分析
    private Visualizer mVisualizer = null;
    // 通信Handle理
    private ThreadPool pool = null;
    // 常量
    //private static final int RATE = 20000;// 多长时间输出一次
    //private IColorStrategy colorStrategy;
    private int size;
    private int color;
    private Handler mHandler = null;
    private Timer mTimer;
    private static final int LEVEL = 0;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case Constant.WHAT_CHANGE_COLOR: {
                        Random random = new Random();
                        int r = random.nextInt(7);
                        color = getAutoModeColor(r, LEVEL);
                    }
                    break;
                    default:
                        break;
                }
            }
        };
        initData();
    }

    private int getAutoModeColor(int which, int level) {
        int tcolor;
        switch (which) {
            case 0:
                tcolor = Color.argb(level, 0, 0, 255);
                break;
            case 1:
                tcolor = Color.argb(level, 255, 0, 0);
                break;
            case 2:
                tcolor = Color.argb(level, 0, 255, 0);
                break;
            case 3:
                tcolor = Color.argb(level, 255, 0, 255);
                break;
            case 4:
                tcolor = Color.argb(level, 0, 255, 255);
                break;
            case 5:
                tcolor = Color.argb(level, 255, 255, 0);
                break;
            case 6:
                tcolor = Color.argb(level, 255, 255, 255);
                break;
            default:
                tcolor = Color.argb(level, 255, 255, 255);
                break;
        }

        return tcolor;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.notification_content_visualizer))
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        //foregroundCompat.startForegroundCompat(-1213, notification);
        flags = START_STICKY;//杀不死
        return super.onStartCommand(intent, flags, startId);
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(Constant.WHAT_CHANGE_COLOR);
        }
    };

    private static final long DELAY = 5000;

    /**
     * 摇一摇 和 音乐律动 不能同时运行
     */
    private void stopShakeService() {
        Intent service = new Intent(this, ShakeService.class);
        stopService(service);
        //Toast.makeText(this, R.string.notice_warning, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stopShakeService();
        /**
         * 注册退出广播
         */
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constant.ACTION_EXIT);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

        initValues();
        //colorStrategy = new ColourStrategy();
        IObservers = new ArrayList<>();
        pool = ThreadPool.getInstance();//得到线程池
        initVisualizer();//初始化Visualizer
        mTimer = new Timer();
        mTimer.schedule(timerTask, DELAY, DELAY);
    }

    private void initValues() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        size = sp.getInt(Constant.KEY_PERSONAL_FEEL, 2);
        color = sp.getInt(Constant.KEY_COLOR, Color.argb(LEVEL, 255, 255, 255));
    }

    /**
     * 配置Visualizer对象
     */
    private void initVisualizer() {
        /* 对音频处理函数进行初始化和配置 */
        //先判断Visualizer是不是处理采集状态 如果是采集状态则先关闭
        if (mVisualizer != null) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }

            mVisualizer.release();
        }
        mVisualizer = new Visualizer(0);
        if (mVisualizer == null) {
            Toast.makeText(this, R.string.default_text, Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        if (mVisualizer.getEnabled()) {
            mVisualizer.setEnabled(false);
        }
//        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);// 128
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[Visualizer.getCaptureSizeRange().length - 1]);
        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, Visualizer.getMaxCaptureRate(), false, true);//Meizu手机为20000
//        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, 20000, false, true);
        if (!mVisualizer.getEnabled()) {
            mVisualizer.setEnabled(true);
        }
    }

    private int colorValue;
    private String data;


    private OnDataCaptureListener mOnDataCaptureListener = new OnDataCaptureListener() {

        @Override
        public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFftDataCapture(Visualizer arg0, byte[] fft, int arg2) {
            // TODO Auto-generated method stub
            if (fft == null || fft.length <= 0) {
                return;
            }
            notifyObserver(fft);
            /*if (colorStrategy == null) {
                return;
            }
            colorValue = colorStrategy.getColorByFft(fft, size, color);*/
            colorValue = getColorByFft(fft, size, color);

            data = CodeUtils.transARGB2Protocol(colorValue);
            pool.addTask(new SendRunnable(data));
        }
    };

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
        mTimer.cancel();
        if (mVisualizer != null) {
            if (mVisualizer.getEnabled()) {
                mVisualizer.setEnabled(false);
            }
            mVisualizer.release();
        }
        if (IObservers != null) {
            IObservers.clear();
            IObservers = null;
        }

        /*if (colorStrategy != null) {
            colorStrategy = null;
        }*/
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new VisualizerServiceBinder();
    }

    private class VisualizerServiceBinder extends Binder implements IVisualizerService {
        @Override
        public void registerTheObserver(IObserver IObserver) {
            registerObserver(IObserver);
        }

        @Override
        public void unregisterTheObserver(IObserver IObserver) {
            unregisterObserver(IObserver);
        }

        @Override
        public void changeTheFeel(int progress) {
            size = progress;
        }
    }

    public interface IVisualizerService {
        void registerTheObserver(IObserver IObserver);

        void unregisterTheObserver(IObserver IObserver);

        void changeTheFeel(int progress);
    }

    private ArrayList<IObserver> IObservers = null;//存储观察者

    @Override
    public void registerObserver(IObserver IObserver) {
        IObservers.add(IObserver);
    }

    @Override
    public void unregisterObserver(IObserver IObserver) {
        IObservers.remove(IObserver);
    }

    @Override
    public void notifyObserver(byte[] bytes) {
        if (IObservers == null || IObservers.size() <= 0) {
            return;
        }
        for (IObserver IObserver : IObservers) {
            IObserver.update(bytes);
        }
    }

    /////////////////////////////////处理fft数据的算法//////////////////////////////////
    private int[] model;
    private double brightStandard, sb, brightness;
    int alpha, red, green, blue;

    public int getColorByFft(byte[] fft, int size, int color) {
        model = fft2Model(fft);
        brightStandard = size * Math.sqrt(2) * 128;//亮度的标准面积
        sb = 0;//亮度的实际面积
        int i;
//        int max = model[0];
        for (i = 1; i < size + 1; i++) {
//            if(model[i] > max) {
//                max = model[i];
//            }
            sb += model[i];
        }
//        sb *= Math.sqrt(2) * 128 / max;
        brightness = (sb / brightStandard) * 255;
        //int alpha = (int) (Color.alpha(color) * brightness / 255);
        ///////////////////////////////关闭了白光////////////////////////////////////
        alpha = 0;//关闭白灯
        red = (int) (Color.red(color) * brightness / 255);
        green = (int) (Color.green(color) * brightness / 255);
        blue = (int) (Color.blue(color) * brightness / 255);
        color = Color.argb(alpha, red, green, blue);
//        if (red == 0 && green == 0 && blue == 0) {
//            color = Color.argb(alpha, 255, 255, 255);
//        }
        return color;
    }

//    private static final int COUNT = 3;//颜色基准数,比如要把频谱的不同频率对应到三种颜色 count就是3
//    private int[] model;
//    public int getColorByFft(byte[] fft, int size, int color) {
//        model = fft2Model(fft);
//
//        int part = size / COUNT;
//        double standard = part * Math.sqrt(2) * 128;
//
//        double[] reals = new double[COUNT];
//
//        int max = model[0];
//        int i;
//        double s = 0;
//        for(i = 1; i < part * COUNT + 1; i++) {
//            if(model[i] > max) {
//                max = model[i];
//            }
//            s += model[i];
//            if(i % part == 0) {
//                reals[(i - 1) / part] = s;
//                s = 0;
//            }
//        }
//
//        int red = (int) (reals[1] / standard * 255);
//        int green = (int) (reals[2] / standard * 255);
//        int blue = (int) (reals[0] / standard * 255);
//
//        int result = Color.argb(0, red, 0, 0);
//
//
//        return result;
//    }

    /**
     * fft转成振幅
     *
     * @param fft
     * @return
     */
    private byte[] modelByte;
    private int[] unsignedModel;

    private int[] fft2Model(byte[] fft) {
        modelByte = new byte[fft.length / 2 + 1];
        unsignedModel = new int[modelByte.length];
        modelByte[0] = (byte) Math.abs(fft[0]);
        int i, j;
        for (i = 2, j = 1; i < fft.length - 1; i += 2, j++) {
            modelByte[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            unsignedModel[j] = getUnsignedByte(modelByte[j]);
        }
        return unsignedModel;
    }

    /**
     * 转换成无符号
     *
     * @param b
     * @return
     */
    private int result;

    private int getUnsignedByte(byte b) {
        result = b & 0xff;
        return result;
    }
}

