package com.example.bozhilun.android.h9.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/20 16:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SleepDataBean {


    /**
     * resultMsg :
     * sleepTotal : {"shallowLen":500,"sleepLen":810,"deepLen":310}
     * resultCode : 001
     * sleep : [{"sleepQuality":3,"startTim e":"2016-10-08 01:05","id":2,"shallowLen":250,"sleepLen":405,"count":1,"deviceCode":null,"userId":"e5060f58-561a-4cab-aef8-f38091e99e88","deepLen":155,"endTim e":"2016-10-08 07:50","addTim e":"2016-10-08 18:37:42"}]
     */

    private String resultMsg;
    private SleepTotalBean sleepTotal;
    private String resultCode;
    private List<SleepBean> sleep;

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public SleepTotalBean getSleepTotal() {
        return sleepTotal;
    }

    public void setSleepTotal(SleepTotalBean sleepTotal) {
        this.sleepTotal = sleepTotal;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<SleepBean> getSleep() {
        return sleep;
    }

    public void setSleep(List<SleepBean> sleep) {
        this.sleep = sleep;
    }

    public static class SleepTotalBean {
        /**
         * shallowLen : 500
         * sleepLen : 810
         * deepLen : 310
         */

        private int shallowLen;
        private int sleepLen;
        private int deepLen;

        public int getShallowLen() {
            return shallowLen;
        }

        public void setShallowLen(int shallowLen) {
            this.shallowLen = shallowLen;
        }

        public int getSleepLen() {
            return sleepLen;
        }

        public void setSleepLen(int sleepLen) {
            this.sleepLen = sleepLen;
        }

        public int getDeepLen() {
            return deepLen;
        }

        public void setDeepLen(int deepLen) {
            this.deepLen = deepLen;
        }
    }

    public static class SleepBean {
        /**
         * sleepQuality : 3
         * startTim e : 2016-10-08 01:05
         * id : 2
         * shallowLen : 250
         * sleepLen : 405
         * count : 1
         * deviceCode : null
         * userId : e5060f58-561a-4cab-aef8-f38091e99e88
         * deepLen : 155
         * endTim e : 2016-10-08 07:50
         * addTim e : 2016-10-08 18:37:42
         */

        private int sleepQuality;
        @SerializedName("startTim e")
        private String _$StartTimE130; // FIXME check this code
        private int id;
        private int shallowLen;
        private int sleepLen;
        private int count;
        private Object deviceCode;
        private String userId;
        private int deepLen;
        @SerializedName("endTim e")
        private String _$EndTimE199; // FIXME check this code
        @SerializedName("addTim e")
        private String _$AddTimE160; // FIXME check this code

        public int getSleepQuality() {
            return sleepQuality;
        }

        public void setSleepQuality(int sleepQuality) {
            this.sleepQuality = sleepQuality;
        }

        public String get_$StartTimE130() {
            return _$StartTimE130;
        }

        public void set_$StartTimE130(String _$StartTimE130) {
            this._$StartTimE130 = _$StartTimE130;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getShallowLen() {
            return shallowLen;
        }

        public void setShallowLen(int shallowLen) {
            this.shallowLen = shallowLen;
        }

        public int getSleepLen() {
            return sleepLen;
        }

        public void setSleepLen(int sleepLen) {
            this.sleepLen = sleepLen;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Object getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(Object deviceCode) {
            this.deviceCode = deviceCode;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getDeepLen() {
            return deepLen;
        }

        public void setDeepLen(int deepLen) {
            this.deepLen = deepLen;
        }

        public String get_$EndTimE199() {
            return _$EndTimE199;
        }

        public void set_$EndTimE199(String _$EndTimE199) {
            this._$EndTimE199 = _$EndTimE199;
        }

        public String get_$AddTimE160() {
            return _$AddTimE160;
        }

        public void set_$AddTimE160(String _$AddTimE160) {
            this._$AddTimE160 = _$AddTimE160;
        }
    }
}
