package com.example.bozhilun.android.siswatch.utils;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Administrator on 2017/10/31.
 */

public class BlueAdapterUtils {

    private Context mContext;
    private static BlueAdapterUtils blueAdapterUtils;


    public static BlueAdapterUtils getBlueAdapterUtils(Context mContext){
        if(blueAdapterUtils == null){
            blueAdapterUtils = new BlueAdapterUtils(mContext);
        }
        return blueAdapterUtils;
    }

    private  BlueAdapterUtils(Context context){
        this.mContext = context;
    }

    /**
     * 请求打开蓝牙
     * @param activity  activiy
     * @param visiableTime  可见时间
     * @param requestCode   返回码
     */
    public  void turnOnBlue(Activity activity,int visiableTime,int requestCode){
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                visiableTime);
        // 请求开启 Bluetooth
        activity.startActivityForResult(requestBluetoothOn,
                requestCode);
    }

    /**
     * 注册扫描的广播
     * @return
     */
    public IntentFilter scanIntFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        return intentFilter;
    }

}
