package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class AvgHeartRate implements Parcelable {
    private int minHeartRate;
    private int avgHeartRate;
    private int maxHeartRate;
    private String rtc; //data_time

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

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    protected AvgHeartRate(Parcel in) {
        minHeartRate = in.readInt();
        avgHeartRate = in.readInt();
        maxHeartRate = in.readInt();
        rtc = in.readString();
    }

    public static final Creator<AvgHeartRate> CREATOR = new Creator<AvgHeartRate>() {
        @Override
        public AvgHeartRate createFromParcel(Parcel in) {
            return new AvgHeartRate(in);
        }

        @Override
        public AvgHeartRate[] newArray(int size) {
            return new AvgHeartRate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(minHeartRate);
        parcel.writeInt(avgHeartRate);
        parcel.writeInt(maxHeartRate);
        parcel.writeString(rtc);
    }
}
