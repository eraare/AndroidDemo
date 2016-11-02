package com.guohua.mlight.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * @author r00kie 让用户授权 Intent intent = new
 *         Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
 *         startActivity(intent);
 * @describe 接收手机的所有通知
 * @time 2015-08-20
 */
@SuppressLint("NewApi")
public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Notification mNotification = sbn.getNotification();
        if (mNotification != null) {
            Bundle extras = mNotification.extras;
            handleData(extras);
            System.out.println("lililililllllllllllllllllllllllllllllllllllllllllll");
            /*
             * Notification.Action[] mActions = mNotification.actions; if
			 * (mActions != null) { for (Notification.Action mAction : mActions)
			 * { int icon = mAction.icon; CharSequence actionTitle =
			 * mAction.title; PendingIntent pendingIntent =
			 * mAction.actionIntent; } }
			 */
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 处理收到的通知数据
     *
     * @param extras
     */
    private void handleData(Bundle extras) {
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
		/*
		 * int notificationIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
		 * Bitmap notificationLargeIcon = ((Bitmap) extras
		 * .getParcelable(Notification.EXTRA_LARGE_ICON));
		 */
        CharSequence notificationText = extras
                .getCharSequence(Notification.EXTRA_TEXT);
        CharSequence notificationSubText = extras
                .getCharSequence(Notification.EXTRA_SUB_TEXT);
        String data = "通知";
        if (notificationTitle != null) {
            data += notificationTitle;
        }
        if (notificationText != null) {
            data += ":" + notificationText;
        }
        if (notificationSubText != null) {
            data += "(" + notificationSubText + ")";
        }
    }
}
