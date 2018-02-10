package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class SleepData implements Parcelable{
    private ArrayList<SleepList> sleepData;
    private SleepTotal sleepTotal;

    public ArrayList<SleepList> getSleepData() {
        return sleepData;
    }

    public void setSleepData(ArrayList<SleepList> sleepData) {
        this.sleepData = sleepData;
    }

    protected SleepData(Parcel in) {
        sleepData = in.createTypedArrayList(SleepList.CREATOR);
        sleepTotal = in.readParcelable(SleepTotal.class.getClassLoader());
    }

    public static final Creator<SleepData> CREATOR = new Creator<SleepData>() {
        @Override
        public SleepData createFromParcel(Parcel in) {
            return new SleepData(in);
        }

        @Override
        public SleepData[] newArray(int size) {
            return new SleepData[size];
        }
    };



    public SleepTotal getSleepTotal() {
        return sleepTotal;
    }

    public void setSleepTotal(SleepTotal sleepTotal) {
        this.sleepTotal = sleepTotal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(sleepData);
        parcel.writeParcelable(sleepTotal, i);
    }
}
