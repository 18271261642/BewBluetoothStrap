package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/4/10.
 */

public class HeartMobthWeek implements Parcelable {
    private HeartMaxMin max_min;
    private HeartWeekMonthRate heartRate;

    public HeartMaxMin getMax_min() {
        return max_min;
    }

    public void setMax_min(HeartMaxMin max_min) {
        this.max_min = max_min;
    }

    public HeartWeekMonthRate getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartWeekMonthRate heartRate) {
        this.heartRate = heartRate;
    }

    protected HeartMobthWeek(Parcel in) {
        max_min = in.readParcelable(HeartMaxMin.class.getClassLoader());
        heartRate = in.readParcelable(HeartWeekMonthRate.class.getClassLoader());
    }

    public static final Creator<HeartMobthWeek> CREATOR = new Creator<HeartMobthWeek>() {
        @Override
        public HeartMobthWeek createFromParcel(Parcel in) {
            return new HeartMobthWeek(in);
        }

        @Override
        public HeartMobthWeek[] newArray(int size) {
            return new HeartMobthWeek[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(max_min, i);
        parcel.writeParcelable(heartRate, i);
    }
}
