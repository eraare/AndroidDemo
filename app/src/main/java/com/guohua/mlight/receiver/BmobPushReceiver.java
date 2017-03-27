package com.guohua.mlight.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.guohua.mlight.R;
import com.guohua.mlight.model.bean.PushInfo;

import cn.bmob.push.PushConstants;

/**
 * @author Leo
 * @version 1
 * @since 2017-03-27
 * 接收Bmob推送来的消息
 */
public class BmobPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, PushConstants.ACTION_MESSAGE)) {
            String msg = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            System.out.println(msg);
            showNotification(context, msg);
//            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 弹出通知
     *
     * @param msg
     */
    private void showNotification(Context context, String msg) {
        /*准备数据*/
        PushInfo info = parseJsonString(msg);
        if (info == null) return; /*推送内容为空*/
        if (TextUtils.isEmpty(info.title)) info.title = "柠檬李先生";
        if (TextUtils.isEmpty(info.text)) info.text = "欢迎光临柠檬李先生的小站";
        if (TextUtils.isEmpty(info.action)) info.action = "http://www.eraare.com";
        /*配置Action*/
        Uri uri = Uri.parse(info.action);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        /*构建Notification*/
        Notification notification = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle(info.title)
                .setContentText(info.text)
                .setTicker(info.title)
                .setAutoCancel(true)
                .build();
        /*显示通知*/
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(520, notification);
    }

    /**
     * 解析json数据
     *
     * @param json
     * @return
     */
    private PushInfo parseJsonString(String json) {
        Gson gson = new Gson();
        PushInfo info = gson.fromJson(json, PushInfo.class);
        return info;
    }

}
