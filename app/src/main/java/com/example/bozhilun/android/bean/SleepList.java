package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class SleepList implements Parcelable{

    private int deepSleep;
    private String rtc;
    private int shallowSleep;
    private int weekCount;

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public int getShallowSleep() {
        return shallowSleep;
    }

    public void setShallowSleep(int shallowSleep) {
        this.shallowSleep = shallowSleep;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }



    protected SleepList(Parcel in) {
        deepSleep = in.readInt();
        rtc = in.readString();
        shallowSleep = in.readInt();
        weekCount= in.readInt();
    }

    public static final Creator<SleepList> CREATOR = new Creator<SleepList>() {
        @Override
        public SleepList createFromParcel(Parcel in) {
            return new SleepList(in);
        }

        @Override
        public SleepList[] newArray(int size) {
            return new SleepList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(deepSleep);
        parcel.writeString(rtc);
        parcel.writeInt(shallowSleep);
        parcel.writeInt(weekCount);

    }
}
