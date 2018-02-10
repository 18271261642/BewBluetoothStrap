package com.example.bozhilun.android.siswatch;

import android.annotation.SuppressLint;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/22.
 */

@SuppressLint("OverrideAbstract")
public class NotifyService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        Log.e("NOTI","-----removed--测试----"+sbn.getId()+"-----"+sbn.getPackageName()+"----"+sbn.getNotification().tickerText);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        Log.e("NOTI","-------测试----"+sbn.getId()+"-----"+sbn.getPackageName()+"----"+sbn.getNotification().tickerText);

    }
}
