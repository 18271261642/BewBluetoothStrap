package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

/**
 * Created by admin on 2017/3/26.
 */


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Administrator on 2016/2/1.
 * 用与监听Aap运行时的网络状态
 *
 * 掉线提醒用户
 */
public class ConnectManages{
    //判断是否连接到网络
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {

            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.i("NetWorkState", "Availabel");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //
    public static boolean onReceive(Context context) {

//获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager)context. getSystemService(context.CONNECTIVITY_SERVICE);
// State state = connManager.getActiveNetworkInfo().getState();
        NetworkInfo.State state = connManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
        if (NetworkInfo.State.CONNECTED == state) { // 判断是否正在使用WIFI网络

            return true;
        }
        state = connManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
        if (NetworkInfo.State.CONNECTED != state) { // 判断是否正在使用GPRS网络

            return true;

        }
        return false;
    }

}