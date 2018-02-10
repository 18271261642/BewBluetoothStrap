package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class SleepDetail implements Parcelable {

    private int sleepQuality;
    private String startTime;
    private int id;
    private int shallowLen;
    private int sleepLen;
    private int count;
    private String deviceCode;
    private String userId;
    private int deepLen;
    private String endTime;
    private String addTime;

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    protected SleepDetail(Parcel in) {
        sleepQuality = in.readInt();
        startTime = in.readString();
        id = in.readInt();
        shallowLen = in.readInt();
        sleepLen = in.readInt();
        count = in.readInt();
        deviceCode = in.readString();
        userId = in.readString();
        deepLen = in.readInt();
        endTime = in.readString();
        addTime = in.readString();
    }

    public static final Creator<SleepDetail> CREATOR = new Creator<SleepDetail>() {
        @Override
        public SleepDetail createFromParcel(Parcel in) {
            return new SleepDetail(in);
        }

        @Override
        public SleepDetail[] newArray(int size) {
            return new SleepDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sleepQuality);
        parcel.writeString(startTime);
        parcel.writeInt(id);
        parcel.writeInt(shallowLen);
        parcel.writeInt(sleepLen);
        parcel.writeInt(count);
        parcel.writeString(deviceCode);
        parcel.writeString(userId);
        parcel.writeInt(deepLen);
        parcel.writeString(endTime);
        parcel.writeString(addTime);
    }
}
