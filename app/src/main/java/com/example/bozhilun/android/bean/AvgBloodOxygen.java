package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class AvgBloodOxygen implements Parcelable {
    private int minBloodOxygen;
    private int avgBloodOxygen;
    private int maxBloodOxygen;

    public int getMinBloodOxygen() {
        return minBloodOxygen;
    }

    public void setMinBloodOxygen(int minBloodOxygen) {
        this.minBloodOxygen = minBloodOxygen;
    }

    public int getAvgBloodOxygen() {
        return avgBloodOxygen;
    }

    public void setAvgBloodOxygen(int avgBloodOxygen) {
        this.avgBloodOxygen = avgBloodOxygen;
    }

    public int getMaxBloodOxygen() {
        return maxBloodOxygen;
    }

    public void setMaxBloodOxygen(int maxBloodOxygen) {
        this.maxBloodOxygen = maxBloodOxygen;
    }

    protected AvgBloodOxygen(Parcel in) {
        minBloodOxygen = in.readInt();
        avgBloodOxygen = in.readInt();
        maxBloodOxygen = in.readInt();
    }

    public static final Creator<AvgBloodOxygen> CREATOR = new Creator<AvgBloodOxygen>() {
        @Override
        public AvgBloodOxygen createFromParcel(Parcel in) {
            return new AvgBloodOxygen(in);
        }

        @Override
        public AvgBloodOxygen[] newArray(int size) {
            return new AvgBloodOxygen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(minBloodOxygen);
        parcel.writeInt(avgBloodOxygen);
        parcel.writeInt(maxBloodOxygen);
    }
}
