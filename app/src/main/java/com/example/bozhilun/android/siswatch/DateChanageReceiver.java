package com.example.bozhilun.android.siswatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bozhilun.android.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/8/4.
 */

/**
 * 接收时间变化的广播
 */
public class DateChanageReceiver extends BroadcastReceiver {

    private static final String TIME_CHANAGE_ACTION = Intent.ACTION_TIME_CHANGED;
    private static final String DATE_CHANAGE_ACTION = Intent.ACTION_DATE_CHANGED;
    private static final String TIMEZONE_CHANGED_ACTION = Intent.ACTION_TIMEZONE_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            //时间变化
            if(TIME_CHANAGE_ACTION.equals(action)){
                Log.e("TT","-----时间变化了---");
                //同步手表的时间
                EventBus.getDefault().post(new MessageEvent("startsynctime"));

            }
            //日期变化
            if(DATE_CHANAGE_ACTION.equals(action)){
                Log.e("TT","-----日期变化了---");
                EventBus.getDefault().post(new MessageEvent("startsynctime"));
            }

            if(TIMEZONE_CHANGED_ACTION.equals(action)){
                Log.e("TT","----时区变化了----");
                EventBus.getDefault().post(new MessageEvent("startsynctime"));
            }
        }
    }
}
