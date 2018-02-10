package com.example.bozhilun.android.siswatch.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.util.SharedPreferencesUtils;


/**
 * Created by sunjianhua on 2017/11/7.
 */

/**
 * 监听来电状态
 */
public class CustomPhoneStateListener extends PhoneStateListener {

    private static final String TAG = "CustomPhoneStateListene";
    private static final String H8_NAME_TAG = "bozlun";

    private PhoneStateListenerInterface phoneStateListenerInterface;

    public void setPhoneStateListenerInterface(PhoneStateListenerInterface phoneStateListenerInterface) {
        this.phoneStateListenerInterface = phoneStateListenerInterface;
    }

    String bleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanya");

    private Context mContext;

    private static CustomPhoneStateListener customPhoneStateListener;

//    public static CustomPhoneStateListener getCustomPhoneStateListener(Context mContext) {
//        if (customPhoneStateListener == null) {
//            customPhoneStateListener = new CustomPhoneStateListener(mContext);
//        }
//        return customPhoneStateListener;
//    }

    public CustomPhoneStateListener(Context context) {
        mContext = context;
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        Log.e(TAG, "---------CustomPhoneStateListener onServiceStateChanged: " + serviceState);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.e(TAG, "---------CustomPhoneStateListener state: "
                + state + " incomingNumber: " + incomingNumber + "---" + bleName);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                if(phoneStateListenerInterface != null){
                    phoneStateListenerInterface.callPhoneData(TelephonyManager.CALL_STATE_IDLE);
                }
                Log.e(TAG, "------电话挂断---");
                break;
            case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                Log.e(TAG, "------电话响铃---" + Build.BRAND+"---开关状态="+SharedPreferencesUtils.getParam(MyApp.getContext(), "laidianphone", ""));
                if ("on".equals(SharedPreferencesUtils.getParam(MyApp.getContext(), "laidianphone", ""))) {
                    Log.e("PhoneBroadcastReceiver", "------2222222---" + bleName);
                    //EventBus.getDefault().post(new MessageEvent("laidianphone"));
                    if(phoneStateListenerInterface != null){
                        phoneStateListenerInterface.callPhoneData(TelephonyManager.CALL_STATE_RINGING);
                    }
                }

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通  但是没法区分
                Log.e(TAG, "------电话来电，去电---");
                if(phoneStateListenerInterface != null){
                    phoneStateListenerInterface.callPhoneData(TelephonyManager.CALL_STATE_IDLE);
                }
                break;
        }
    }

}
