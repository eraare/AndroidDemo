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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.guohua.mlight.R;
import com.guohua.mlight.common.base.AppContext;
import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.model.impl.RxLightService;
import com.guohua.mlight.view.activity.MainActivity;
import com.guohua.mlight.view.activity.ShakeActivity;

import java.util.Random;

public class ShakeService extends Service {
    public int VALUE = 17;//阀值
    private SensorManager manager = null;//传感器
    private Vibrator vibrator = null;//震动
    private boolean isBind = false; //是否绑定
    private boolean shakeMode = true; //摇一摇开关还是变色

    private Handler mHandler;
    private boolean allowShake = true;

    private float maxRange;

    public ShakeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, mIntent, 0);
        Notification notification = new Notification.Builder(this).setTicker(getString(R.string.ticker_text))
                .setWhen(System.currentTimeMillis()).setContentInfo(getString(R.string.notification_info)).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notification_title)).setContentText(getString(R.string.notification_content_shake))
                .setContentIntent(mPendingIntent).setPriority(Notification.PRIORITY_MAX).build();
        startForeground(-1213, notification);
        //foregroundCompat.startForegroundCompat(-1213, notification);
        flags = Service.START_FLAG_RETRY;//杀不死
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        isBind = true;
        return new ShakeServiceBinder();
    }

    /**
     * 摇一摇 和 音乐律动 不能同时运行
     */
    private void stopVisualizerService() {
        Intent service = new Intent(this, VisualizerService.class);
        stopService(service);
        Toast.makeText(this, R.string.notice_warning, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        stopVisualizerService();
        initTheValue();
        mHandler = new Handler();
        allowShake = true;
        /**
         * 注册退出广播
         */
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(Constants.ACTION_EXIT);
        mFilter.setPriority(Integer.MAX_VALUE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mFilter);

        shakeMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_SHAKE_MODE, true);

        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动服务

        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        maxRange = sensor.getMaximumRange();
        manager.registerListener(mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);//注册
    }

    private void initTheValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        VALUE = sp.getInt(Constants.KEY_THRESHOLD, 17);
    }

    private static final long SHAKE_DELAY = 800;

    //传感器事件处理器
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            if (type == Sensor.TYPE_ACCELEROMETER) {
                if (x >= VALUE || x <= -VALUE || y >= VALUE || y <= -VALUE
                        || z >= VALUE || z <= -VALUE)
                    doShake();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     */
    private void doShake() {
        if (!allowShake) {
            return;
        }

        if (isBind) {
            /**
             * 晃动图片
             */
            Intent intent = new Intent(ShakeActivity.ACTION_SHAKE_A_SHAKE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        //震动
        vibrator.vibrate(300);
        //vibrator.vibrate(new long[]{200, 5, 200, 5, 200}, -1);

        /**
         * 控制灯
         */
        String data;
        if (shakeMode) {
            if (AppContext.getInstance().isLightOn) {
                RxLightService.getInstance().turnOff();
                AppContext.getInstance().isLightOn = false;
            } else {
                RxLightService.getInstance().turnOn();
                AppContext.getInstance().isLightOn = true;
            }
        } else {
            Random r = new Random();
            //int alpha = r.nextInt(256);关闭白光
            int alpha = 255;
            int red = r.nextInt(256);
            int green = r.nextInt(256);
            int blue = r.nextInt(256);
            int color = Color.argb(alpha, red, green, blue);
            RxLightService.getInstance().adjustColor(color);
        }

        allowShake = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                allowShake = true;
            }
        }, SHAKE_DELAY);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBind = false;
        return super.onUnbind(intent);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constants.ACTION_EXIT)) {
                stopSelf();
            }
        }
    };

    /**
     * 取消
     */
    private void suiside() {
        manager.unregisterListener(mSensorEventListener);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        suiside();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private class ShakeServiceBinder extends Binder implements IShakeService {
        @Override
        public void changeMode(boolean mode) {
            shakeMode = mode;
        }

        @Override
        public float getMaximumRange() {
            return maxRange;
        }

        @Override
        public void changeThreshold(int value) {
            VALUE = value;
        }
    }

    public interface IShakeService {
        void changeMode(boolean mode);

        float getMaximumRange();

        void changeThreshold(int value);
    }

}
