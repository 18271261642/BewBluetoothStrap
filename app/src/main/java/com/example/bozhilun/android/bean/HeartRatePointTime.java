package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class HeartRatePointTime implements Parcelable {
    private int heartRate;
    private String rtc;

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

    protected HeartRatePointTime(Parcel in) {
        heartRate = in.readInt();
        rtc = in.readString();
    }

    public static final Creator<HeartRatePointTime> CREATOR = new Creator<HeartRatePointTime>() {
        @Override
        public HeartRatePointTime createFromParcel(Parcel in) {
            return new HeartRatePointTime(in);
        }

        @Override
        public HeartRatePointTime[] newArray(int size) {
            return new HeartRatePointTime[size];
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
    }
}
