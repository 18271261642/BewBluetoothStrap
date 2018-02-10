package com.example.bozhilun.android.bean;

/**
 * Created by Administrator on 2017/11/3.
 */

public class NewsSleepBean {


    /**
     * rtc : 2017-11-01
     * shallowSleep : 0
     * soberLen : 0
     * sleepLen : 0
     * weekCount : 3
     */

    private String rtc; //日期
    private int shallowSleep;   //浅睡时长
    private int soberLen;   //清醒时长
    private int sleepLen;   //睡眠时长
    private String weekCount;

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getShallowSleep() {
        return shallowSleep;
    }

    public void setShallowSleep(int shallowSleep) {
        this.shallowSleep = shallowSleep;
    }

    public int getSoberLen() {
        return soberLen;
    }

    public void setSoberLen(int soberLen) {
        this.soberLen = soberLen;
    }

    public int getSleepLen() {
        return sleepLen;
    }

    public void setSleepLen(int sleepLen) {
        this.sleepLen = sleepLen;
    }

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }
}
