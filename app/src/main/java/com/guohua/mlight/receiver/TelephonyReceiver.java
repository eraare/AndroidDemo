package com.guohua.mlight.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.guohua.mlight.common.config.Constants;
import com.guohua.mlight.model.impl.RxLightService;

/**
 * 处理来电的广播接收器 接收到来电就发送状态 给第二屏，让用户选择操作，返回处理
 *
 * @author r00kie
 */
public class TelephonyReceiver extends BroadcastReceiver {
    /*电话状态的ACTION*/
    public static final String ACTION_TEL = "android.intent.action.PHONE_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();// 得到action
        // 如果为电话状态改变就进行处理
        if (TextUtils.equals(action, ACTION_TEL)) {
            doReceive(context, intent);
        }
    }

    /**
     * 处理电话状态改变
     *
     * @param context
     * @param intent
     */
    private void doReceive(Context context, Intent intent) {
        // 得到来电手机号
        String phoneNumber = intent
                .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        // 获取当前状态
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING: {
                String data = "来电请求";
                if (phoneNumber != null) {
                    data = phoneNumber + data;
                }

                RxLightService.getInstance().musicOn();
                SystemClock.sleep(600);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int mode = sp.getInt(Constants.CALL_REMINDER_SHINEMODE, 0);
                byte[] bytes = {0x7C, (byte) 0x93, 0x00, 0x00, 0x0A, 0x0A, 0x0A, (byte) 0xB1};
                if (mode == 1) {
                    bytes = new byte[]{0x7A, 0x00, (byte) 0xC8, 0x00, 0x0A, 0x0A, 0x0A, (byte) 0xE6};
                } else if (mode == 2) {
                    bytes = new byte[]{0x79, 0x00, 0x00, (byte) 0x96, 0x0A, 0x0A, 0x0A, (byte) 0xB4};
                }
                RxLightService.getInstance().send(bytes);
                RxLightService.getInstance().send(bytes);
            }
            break;
            case TelephonyManager.CALL_STATE_IDLE: {
                String data = "已经挂断";
                if (phoneNumber != null) {
                    data = phoneNumber + data;
                }

                RxLightService.getInstance().musicOff();
                RxLightService.getInstance().adjustColor(Color.BLUE);
            }
            break;
            case TelephonyManager.CALL_STATE_OFFHOOK: {
                String data = "正在接听";
                if (phoneNumber != null) {
                    data = phoneNumber + data;
                }
                RxLightService.getInstance().musicOff();
                RxLightService.getInstance().adjustColor(Color.GREEN);
            }
            break;
            default:
                Log.d("Tel", "飞到火星");
                break;
        }
    }
}

    /*final int[] sendIntDatas;
                if (sp.getInt(Constants.CALL_REMINDER_SHINEMODE, 0) == 3) {
                        String[] s = sp.getString(Constants.CALL_REMINDER_SHINEMODE_VALUE, "124;111;0;0;10;0;0;121").split(";");
                        sendIntDatas = new int[s.length];
                        for (int i = 0; i < s.length; i++) {
        sendIntDatas[i] = Integer.parseInt(s[i]);
        }
        } else {
//                    sendIntDatas = Constants.REMINDERLIGHTSHINEMODE[sp.getInt(Constants.CALL_REMINDER_SHINEMODE, 0)];
        sendIntDatas = null;
        }

        //与上次发数据保持一定时间间隔
        new Handler().postDelayed(new Runnable() {
@Override
public void run() {
        //多发几次
        new Thread(new Runnable() {
@Override
public void run() {
        for (int i = 0; i < 3; i++) {
//                                    ThreadPool.getInstance().addTask(new SendSceneDatasRunnable(0, sendIntDatas));
        try {
        Thread.sleep(100);
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
        }
        }
        }).start();
        }
        }, 300);*/

/*
 * 
 * private ITelephony getITelephony(Context context) { ITelephony iTelephony =
 * null; TelephonyManager mTelephonyManager = (TelephonyManager) context
 * .getSystemService(Context.TELEPHONY_SERVICE); Class<TelephonyManager> c =
 * TelephonyManager.class; Method getITelephonyMethod = null; try {
 * getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
 * // 获取声明的方法 getITelephonyMethod.setAccessible(true); } catch
 * (SecurityException e) { e.printStackTrace(); } catch (NoSuchMethodException
 * e) { e.printStackTrace(); }
 * 
 * try { iTelephony = (ITelephony) getITelephonyMethod.invoke(
 * mTelephonyManager, (Object[]) null); // 获取实例 return iTelephony; } catch
 * (Exception e) { e.printStackTrace(); } return iTelephony; }
 * 
 * 
 * IntentFilter mFilter = new IntentFilter();
 * mFilter.addAction("android.intent.action.PHONE_STATE");
 * mFilter.setPriority(Integer.MAX_VALUE); registerReceiver(mReceiver, mFilter);
 * 
 * try { ITelephony iTelephony = getITelephony(MainActivity.this); // 获取电话接口
 * iTelephony.answerRingingCall(); } catch (Exception e) { e.printStackTrace();
 * }
 */
