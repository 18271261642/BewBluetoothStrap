package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class SleepTotal implements Parcelable {

    private int shallowLen;
    private int sleepLen;
    private int deepLen;

    public int getShallowLen() {
        return shallowLen;
    }

    public void setShallowLen(int shallowLen) {
        this.shallowLen = shallowLen;
    }

    public int getSleepLen() {
        return sleepLen;
    }

    public void setSleepLen(int sleepLen) {
        this.sleepLen = sleepLen;
    }

    public int getDeepLen() {
        return deepLen;
    }

    public void setDeepLen(int deepLen) {
        this.deepLen = deepLen;
    }

    protected SleepTotal(Parcel in) {
        shallowLen = in.readInt();
        sleepLen = in.readInt();
        deepLen = in.readInt();
    }

    public static final Creator<SleepTotal> CREATOR = new Creator<SleepTotal>() {
        @Override
        public SleepTotal createFromParcel(Parcel in) {
            return new SleepTotal(in);
        }

        @Override
        public SleepTotal[] newArray(int size) {
            return new SleepTotal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(shallowLen);
        parcel.writeInt(sleepLen);
        parcel.writeInt(deepLen);
    }
}
