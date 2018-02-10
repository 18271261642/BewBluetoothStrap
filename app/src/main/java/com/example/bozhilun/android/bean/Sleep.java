package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class Sleep implements Parcelable{
    private ArrayList<B15PSleepBean> sleep;
    private SleepTotal sleepTotal;

    protected Sleep(Parcel in) {
        sleep = in.createTypedArrayList(B15PSleepBean.CREATOR);
        sleepTotal = in.readParcelable(SleepTotal.class.getClassLoader());
    }

    public static final Creator<Sleep> CREATOR = new Creator<Sleep>() {
        @Override
        public Sleep createFromParcel(Parcel in) {
            return new Sleep(in);
        }

        @Override
        public Sleep[] newArray(int size) {
            return new Sleep[size];
        }
    };

    public ArrayList<B15PSleepBean> getSleep() {
        return sleep;
    }

    public void setSleep(ArrayList<B15PSleepBean> sleep) {
        this.sleep = sleep;
    }

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
        parcel.writeTypedList(sleep);
        parcel.writeParcelable(sleepTotal, i);
    }
}
