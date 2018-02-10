package com.example.bozhilun.android.siswatch.bean;

/**
 * Created by Administrator on 2017/7/20.
 */

public class WatchDataDatyBean {


    /**
     * distance : 0
     * calories : 0
     * rtc : 2017-07-13
     * stepNumber : 0
     */

    private String distance;
    private String calories;
    private String rtc;
    private int stepNumber;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
}
