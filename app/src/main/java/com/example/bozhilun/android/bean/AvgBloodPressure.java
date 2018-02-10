package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class AvgBloodPressure implements Parcelable {

    private int avgSystolic;
    private int avgDiastolic;
    private int minHeartRate;
    private int maxHeartRate;

    public int getAvgSystolic() {
        return avgSystolic;
    }

    public void setAvgSystolic(int avgSystolic) {
        this.avgSystolic = avgSystolic;
    }

    public int getAvgDiastolic() {
        return avgDiastolic;
    }

    public void setAvgDiastolic(int avgDiastolic) {
        this.avgDiastolic = avgDiastolic;
    }

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

    protected AvgBloodPressure(Parcel in) {
        avgSystolic = in.readInt();
        avgDiastolic = in.readInt();
        minHeartRate = in.readInt();
        maxHeartRate = in.readInt();
    }

    public static final Creator<AvgBloodPressure> CREATOR = new Creator<AvgBloodPressure>() {
        @Override
        public AvgBloodPressure createFromParcel(Parcel in) {
            return new AvgBloodPressure(in);
        }

        @Override
        public AvgBloodPressure[] newArray(int size) {
            return new AvgBloodPressure[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(avgSystolic);
        parcel.writeInt(avgDiastolic);
        parcel.writeInt(minHeartRate);
        parcel.writeInt(maxHeartRate);
    }
}
