package com.example.bozhilun.android.activity.wylactivity.wyl_util;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/1/13.
 */
public class GPSutils implements Serializable {

    Double JINDU;//精度
    Double WEIDU;//纬度

    public Double getJINDU() {
        return JINDU;
    }

    public void setJINDU(Double JINDU) {
        this.JINDU = JINDU;
    }

    public Double getWEIDU() {
        return WEIDU;
    }

    public void setWEIDU(Double WEIDU) {
        this.WEIDU = WEIDU;
    }

    @Override
    public String toString() {
        return "GPSutils{" +
                "JINDU=" + JINDU +
                ", WEIDU=" + WEIDU +
                '}';
    }

    /**
     * 将String类型的时间转换成long,如：12:01:08
     * @param strTime String类型的时间
     * @return long类型的时间
     * */
    protected long convertStrTimeToLong(String strTime) {
        // TODO Auto-generated method stub
        String []timeArry=strTime.split(":");
        long longTime=0;
        if (timeArry.length==2) {//如果时间是MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*1000*60+Integer.parseInt(timeArry[1])*1000;
        }else if (timeArry.length==3){//如果时间是HH:MM:SS格式
            longTime=Integer.parseInt(timeArry[0])*1000*60*60+Integer.parseInt(timeArry[1])
                    *1000*60+Integer.parseInt(timeArry[0])*1000;
        }
        return SystemClock.elapsedRealtime()-longTime;
    }




    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss){
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;
    }

    /**
     * 打开 GPs获取当前的卫星个数
     *
     * @param activity
     */
    public static void openGPSSettings(Activity activity ) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

        } else {
            //调转GPS设置界面
            //开启gps
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            activity. startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
        }
    }

    /**
     * 根据两点的经纬度，计算出其之间的距离（返回单位为km）
     * @param lat1 纬度1
     * @param lng1 经度1
     * @param lat2 纬度2
     * @param lng2 经度2
     * @return */
    private static double EARTH_RADIUS = 6378.137;//地球半径
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = rad(lat1) - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000.0;
        return s;
    }


}
