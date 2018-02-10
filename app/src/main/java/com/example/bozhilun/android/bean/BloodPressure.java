package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class BloodPressure implements Parcelable {

    private ArrayList<BloodPressureList> bloodPressure;
    private AvgBloodPressure avgBloodPressure;

    public ArrayList<BloodPressureList> getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(ArrayList<BloodPressureList> bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public AvgBloodPressure getAvgBloodPressure() {
        return avgBloodPressure;
    }

    public void setAvgBloodPressure(AvgBloodPressure avgBloodPressure) {
        this.avgBloodPressure = avgBloodPressure;
    }

    protected BloodPressure(Parcel in) {
        bloodPressure = in.createTypedArrayList(BloodPressureList.CREATOR);
        avgBloodPressure = in.readParcelable(AvgBloodPressure.class.getClassLoader());
    }

    public static final Creator<BloodPressure> CREATOR = new Creator<BloodPressure>() {
        @Override
        public BloodPressure createFromParcel(Parcel in) {
            return new BloodPressure(in);
        }

        @Override
        public BloodPressure[] newArray(int size) {
            return new BloodPressure[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(bloodPressure);
        parcel.writeParcelable(avgBloodPressure, i);
    }
}
