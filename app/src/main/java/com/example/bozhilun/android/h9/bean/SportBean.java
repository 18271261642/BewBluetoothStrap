package com.example.bozhilun.android.h9.bean;

import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/11/3 22:59
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SportBean {


    /**
     * resultCode : 001
     * day : [{"rtc":"2017-10-27","distance":"0","stepNumber":0,"calories":0},{"rtc":"2017-10-28","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2017-10-29","distance":"0","stepNumber":0,"calories":"0"},{"rtc":"2017-10-30","distance":"0.14","stepNumber":200,"calories":2},{"rtc":"2017-10-31","distance":"1.12","stepNumber":1603,"calories":41},{"rtc":"2017-11-01","distance":"0.08","stepNumber":112,"calories":2},{"rtc":"2017-11-02","distance":"1.31","stepNumber":1877,"calories":44},{"rtc":"2017-11-03","distance":"0.14","stepNumber":195,"calories":2}]
     */

    private String resultCode;
    private List<DayBean> day;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<DayBean> getDay() {
        return day;
    }

    public void setDay(List<DayBean> day) {
        this.day = day;
    }

    public static class DayBean {
        /**
         * rtc : 2017-10-27
         * distance : 0
         * stepNumber : 0
         * calories : 0
         */

        private String rtc;
        private String distance;
        private int stepNumber;
        private int calories;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public int getCalories() {
            return calories;
        }

        public void setCalories(int calories) {
            this.calories = calories;
        }
    }
}
