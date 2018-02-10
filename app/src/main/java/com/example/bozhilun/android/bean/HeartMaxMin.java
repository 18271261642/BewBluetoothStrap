package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/4/10.
 */

public class HeartMaxMin implements Parcelable{

    private int minHeartRate;
    private int maxHeartRate;

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

    protected HeartMaxMin(Parcel in) {
        minHeartRate = in.readInt();
        maxHeartRate = in.readInt();
    }

    public static final Creator<HeartMaxMin> CREATOR = new Creator<HeartMaxMin>() {
        @Override
        public HeartMaxMin createFromParcel(Parcel in) {
            return new HeartMaxMin(in);
        }

        @Override
        public HeartMaxMin[] newArray(int size) {
            return new HeartMaxMin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(minHeartRate);
        parcel.writeInt(maxHeartRate);
    }
}
