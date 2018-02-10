package com.example.bozhilun.android.siswatch.utils;

import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.siswatch.bean.CustomBlueDevice;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchConstants {

    //sisi watch 获取当天步数
    public static final byte[] watchSteps = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x11, (byte) 0x03, (byte) 0x08, (byte) 0x01, (byte) 0x01, (byte) 0x00};

    public static byte[] syncwatchtime() {
        byte[] synctimebyte;
        //sis watch 同步时间
        String newDate = WatchUtils.getCurrentDate2();  //时分秒格式时间
        String YearDate = WatchUtils.getCurrentDate();  //年月日格式时间
        //2017-8-2 17:00:17
        String[] timeDate = newDate.split(":");
        String[] yearDate = YearDate.split("-");
        int year = Integer.valueOf(yearDate[0]) % 2000;  //年
        int month = Integer.valueOf(yearDate[1]);  //月
        int daty = Integer.valueOf(yearDate[2]);   //日
        int hour = Integer.valueOf(timeDate[0]);   //时
        int me = Integer.valueOf(timeDate[1]);     //分
        int send = Integer.valueOf(timeDate[2]);   //秒
        synctimebyte = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x44, (byte) 0x08, (byte) 0x01, (byte) 0x02, (byte) (year & 0xff), (byte) (month & 0xff), (byte) (daty & 0xff), (byte) (hour & 0xff), (byte) (me & 0xff), (byte) (send & 0xff), (byte) 0x00};
        SharedPreferencesUtils.setParam(MyApp.getContext(), "sunctime", Arrays.toString(synctimebyte));
        return synctimebyte;
    }

    //获取手表的时间
    public static byte[] getWatchTime() {
        byte[] bytes;
        bytes = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x30, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) 0x00};
        return bytes;
    }

    /**
     * 设置系统类型
     */
    public static byte[] setDeviceType() {
        byte[] typebytes;
        typebytes = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x23, (byte) 0x03, (byte) 0x06, (byte) 0x03, (byte) 0x01, (byte) 0x00};
        return typebytes;
    }

    /**
     * 获取系统类型
     */
    public static byte[] getDeviceType() {
        byte[] gettypes;
        gettypes = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x22, (byte) 0x02, (byte) 0x06, (byte) 0x03, (byte) 0x00};
        return gettypes;
    }

    /**
     * 设置闹钟提醒
     */
    //1.获取1号闹钟时间
    public static final byte[] getWatchAlarmOne = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x77, (byte) 0x03, (byte) 0x05, (byte) 0x02, (byte) 0x01, (byte) 0x00};
    //获取 2 号闹钟时间
    public static final byte[] getWatchAlarmSecond = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x78, (byte) 0x03, (byte) 0x05, (byte) 0x02, (byte) 0x02, (byte) 0x00};
    //获取 3 号闹钟时间
    public static final byte[] getWatchAlarmThird = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x79, (byte) 0x03, (byte) 0x05, (byte) 0x02, (byte) 0x03, (byte) 0x00};

    //设置闹钟
    public static byte[] setWatchAlarm(int alarmnum, int hour, int mine, int onoroff) {
        byte[] alarmdata;
        alarmdata = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x60, (byte) 0x07, (byte) 0x05, (byte) 0x01, (byte) alarmnum, (byte) hour, (byte) mine, (byte) 0xff, (byte) onoroff, (byte) 0x00};
        return alarmdata;
    }

    /**
     * @param alarmnum 闹钟编号
     * @param hour     小时
     * @param mine     分钟
     * @param onoroff  开关
     * @param week     周期
     * @param repeat   是否重复
     * @return
     */
    public static byte[] setWatchAlarm2(int alarmnum, int hour, int mine, int onoroff, int week, int repeat) {
        byte[] alarmdata;
        alarmdata = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x60, (byte) 0x07, (byte) 0x05, (byte) 0x01, (byte) alarmnum, (byte) hour, (byte) mine, (byte) week, (byte) onoroff, (byte) 0x00};
        Log.e("MMM", "----alarmdata---" + Arrays.toString(alarmdata));
        return alarmdata;

    }

    /**
     * 来电提醒
     *
     * @param onoroff
     * @return
     */
    public static byte[] phoneAlert(int onoroff) {
        byte[] phonebyte;
        phonebyte = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x55, (byte) 0x04, (byte) 0x03, (byte) 0x01, (byte) 0x04, (byte) 0x00, (byte) 0x00};

        return phonebyte;
    }

    /**
     * 取消来电提醒
     */
    public static byte[] disPhoneAlert() {
        byte[] disphonebyte;
        disphonebyte = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x50, (byte) 0x04, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00};
        return disphonebyte;
    }

    /**
     * APP提醒
     */
    public static byte[] appalert() {
        byte[] appalert;
        appalert = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x56, (byte) 0x04, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00};

        return appalert;
    }

    /**
     * 短信提醒
     */
    public static byte[] smsAlert() {
        byte[] smsbyte;
        smsbyte = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x35, (byte) 0x04, (byte) 0x03, (byte) 0x01, (byte) 0x08, (byte) 0x01, (byte) 0x00};
        return smsbyte;
    }

    /**
     * 获取节电时间
     */
    public static byte[] getjiedianTime() {
        byte[] getjiediantime;
        getjiediantime = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x66, (byte) 0x02, (byte) 0x04, (byte) 0x02, (byte) 0x00};
        return getjiediantime;
    }

    /**
     * 设置节电时间
     */
    public static byte[] settingJiedianTime(int startHour, int startMinue, int endHour, int endMinue) {
        byte[] setjiediantime;
        setjiediantime = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x67, (byte) 0x06, (byte) 0x04, (byte) 0x01, (byte) startHour, (byte) startMinue, (byte) endHour, (byte) endMinue, (byte) 0x00};

        return setjiediantime;

    }

    /**
     * 设置提醒间隔
     */
    public static byte[] settingJiangeTime(int time) {
        byte[] setjiangetime;
        setjiangetime = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x68, (byte) 0x04, (byte) 0x03, (byte) 0x03, (byte) (byte) time, (byte) 0x00, (byte) 0x00};

        return setjiangetime;
    }

    /**
     * 打开或关闭拍照功能开关
     * 0x01 打开
     * 0x00关闭
     */
    public static byte[] openTakeOphot(int off) {
        byte[] openTakeP;
        openTakeP = new byte[]{(byte) 0x5e, (byte) 0x41, (byte) 0x12, (byte) 0x03, (byte) 0x08, (byte) 0x80, (byte) off, (byte) 0x00};
        return openTakeP;
    }


    public static CustomBlueDevice customBlueDevice = null;
    public static boolean H8ConnectState = false;


}
