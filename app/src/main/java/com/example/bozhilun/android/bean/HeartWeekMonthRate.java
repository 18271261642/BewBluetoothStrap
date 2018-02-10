package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/4/10.
 */

public class HeartWeekMonthRate implements Parcelable {

    private String weekCount;
    private int minHeartRate;
    private int avgHeartRate;
    private String rtc;
    private int maxHeartRate;

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }

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

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    protected HeartWeekMonthRate(Parcel in) {
        weekCount = in.readString();
        minHeartRate = in.readInt();
        avgHeartRate = in.readInt();
        rtc = in.readString();
        maxHeartRate = in.readInt();
    }

    public static final Creator<HeartWeekMonthRate> CREATOR = new Creator<HeartWeekMonthRate>() {
        @Override
        public HeartWeekMonthRate createFromParcel(Parcel in) {
            return new HeartWeekMonthRate(in);
        }

        @Override
        public HeartWeekMonthRate[] newArray(int size) {
            return new HeartWeekMonthRate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(weekCount);
        parcel.writeInt(minHeartRate);
        parcel.writeInt(avgHeartRate);
        parcel.writeString(rtc);
        parcel.writeInt(maxHeartRate);
    }
}
