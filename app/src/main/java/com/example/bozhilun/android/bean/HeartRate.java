package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class HeartRate implements Parcelable {
    private ArrayList<HeartRatePointTime> heartRate;

    public ArrayList<HeartRatePointTime> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(ArrayList<HeartRatePointTime> heartRate) {
        this.heartRate = heartRate;
    }



    protected HeartRate(Parcel in) {
        heartRate = in.createTypedArrayList(HeartRatePointTime.CREATOR);

    }

    public static final Creator<HeartRate> CREATOR = new Creator<HeartRate>() {
        @Override
        public HeartRate createFromParcel(Parcel in) {
            return new HeartRate(in);
        }

        @Override
        public HeartRate[] newArray(int size) {
            return new HeartRate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(heartRate);

    }
}
