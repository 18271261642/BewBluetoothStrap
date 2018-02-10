package com.example.bozhilun.android.h9.bean;

import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/18 12:22
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class HeartDataBean {


    /**
     * heartRate : [{"rtc":"2017-11-01 00:00","heartRate":78},{"rtc":"2017-11-01 01:00","heartRate":73},{"rtc":"2017-11-01 02:00","heartRate":58},{"rtc":"2017-11-01 03:00","heartRate":58},{"rtc":"2017-11-01 04:00","heartRate":60},{"rtc":"2017-11-01 05:00","heartRate":58},{"rtc":"2017-11-01 06:00","heartRate":60},{"rtc":"2017-11-01 07:00","heartRate":64},{"rtc":"2017-11-01 08:00","heartRate":77},{"rtc":"2017-11-01 09:00","heartRate":77},{"rtc":"2017-11-01 10:00","heartRate":86},{"rtc":"2017-11-01 11:00","heartRate":77},{"rtc":"2017-11-01 12:00","heartRate":95},{"rtc":"2017-11-01 13:00","heartRate":86},{"rtc":"2017-11-01 14:00","heartRate":77},{"rtc":"2017-11-01 15:00","heartRate":73}]
     * resultCode : 001
     * manual : [{"rtc":"2017-11-01 00:00","heartRate":84},{"rtc":"2017-11-01 14:40","heartRate":74},{"rtc":"2017-11-01 15:40","heartRate":73}]
     * avgHeartRate : {"minHeartRate":58,"avgHeartRate":73,"maxHeartRate":95}
     */

    private String resultCode;
    private AvgHeartRateBean avgHeartRate;
    private List<HeartRateBean> heartRate;
    private List<ManualBean> manual;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public AvgHeartRateBean getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(AvgHeartRateBean avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public List<HeartRateBean> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(List<HeartRateBean> heartRate) {
        this.heartRate = heartRate;
    }

    public List<ManualBean> getManual() {
        return manual;
    }

    public void setManual(List<ManualBean> manual) {
        this.manual = manual;
    }

    public static class AvgHeartRateBean {
        /**
         * minHeartRate : 58
         * avgHeartRate : 73
         * maxHeartRate : 95
         */

        private int minHeartRate;
        private int avgHeartRate;
        private int maxHeartRate;

        public int getMinHeartRate() {
            return minHeartRate;
        }

        public void setMinHeartRate(int minHeartRate) {
            this.minHeartRate = minHeartRate;
        }

        public int getAvgHeartRate() {
            return avgHeartRate;
        }

        public void setAvgHeartRate(int avgHeartRate) {
            this.avgHeartRate = avgHeartRate;
        }

        public int getMaxHeartRate() {
            return maxHeartRate;
        }

        public void setMaxHeartRate(int maxHeartRate) {
            this.maxHeartRate = maxHeartRate;
        }
    }

    public static class HeartRateBean {
        /**
         * rtc : 2017-11-01 00:00
         * heartRate : 78
         */

        private String rtc;
        private int heartRate;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }
    }

    public static class ManualBean {
        /**
         * rtc : 2017-11-01 00:00
         * heartRate : 84
         */

        private String rtc;
        private int heartRate;

        public String getRtc() {
            return rtc;
        }

        public void setRtc(String rtc) {
            this.rtc = rtc;
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }
    }
}
