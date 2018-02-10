package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class HeartRateList implements Parcelable {
    private int heartRate;
    private String rtc;
private int weekCount;

    private int  minHeartRate;
    private int         maxHeartRate;

    public int getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(int minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    protected HeartRateList(Parcel in) {
        heartRate = in.readInt();
        rtc = in.readString();
        weekCount= in.readInt();
        minHeartRate= in.readInt();
         maxHeartRate= in.readInt();
    }

    public static final Creator<HeartRateList> CREATOR = new Creator<HeartRateList>() {
        @Override
        public HeartRateList createFromParcel(Parcel in) {
            return new HeartRateList(in);
        }

        @Override
        public HeartRateList[] newArray(int size) {
            return new HeartRateList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(heartRate);
        parcel.writeString(rtc);
        parcel.writeInt(weekCount);
        parcel.writeInt(minHeartRate);
        parcel.writeInt(maxHeartRate);

    }
}
