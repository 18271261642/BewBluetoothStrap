package com.example.bozhilun.android.B18I.b18ireceiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * @aboutContent: 监听蓝牙状态的广播
 * @author： 安
 * @crateTime: 2017/9/12 10:17
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18IBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "--B18IBroadcastReceiver--";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, intent.getAction());
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                Log.e(TAG, blueState + "");
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON://13
                        Log.e(TAG, "onReceive----本地蓝牙打开-----STATE_TURNING_ON");
                        EventBus.getDefault().post(new B18iEventBus("STATE_TURNING_ON"));
                        break;
                    case BluetoothAdapter.STATE_ON://13
                        Log.e(TAG, "onReceive----本地蓝牙是打开这的-----STATE_ON");
                        EventBus.getDefault().post(new B18iEventBus("STATE_ON"));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF://11
                        Log.e(TAG, "onReceive----本地蓝牙关闭-----STATE_TURNING_OFF");
                        EventBus.getDefault().post(new B18iEventBus("STATE_TURNING_OFF"));
                        break;
                    case BluetoothAdapter.STATE_OFF://10
                        Log.e(TAG, "onReceive----本地蓝牙是关闭着的-----STATE_OFF");
                        EventBus.getDefault().post(new B18iEventBus("STATE_OFF"));
                        break;
                }
                break;
        }
    }
}
