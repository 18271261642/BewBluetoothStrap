package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class HeartRateone implements Parcelable {

    private ArrayList<HeartRateList> heartRate;


    public ArrayList<HeartRateList> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(ArrayList<HeartRateList> heartRate) {
        this.heartRate = heartRate;
    }

    protected HeartRateone(Parcel in) {
        heartRate = in.createTypedArrayList(HeartRateList.CREATOR);

    }

    public static final Creator<HeartRateone> CREATOR = new Creator<HeartRateone>() {
        @Override
        public HeartRateone createFromParcel(Parcel in) {
            return new HeartRateone(in);
        }

        @Override
        public HeartRateone[] newArray(int size) {
            return new HeartRateone[size];
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
