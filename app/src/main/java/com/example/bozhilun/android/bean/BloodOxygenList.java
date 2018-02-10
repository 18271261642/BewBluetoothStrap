package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class BloodOxygenList implements Parcelable{
    private int bloodOxygen;
    private String rtc;
    private int maxBloodOxygen;
    private int weekCount;

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getMaxBloodOxygen() {
        return maxBloodOxygen;
    }

    public void setMaxBloodOxygen(int maxBloodOxygen) {
        this.maxBloodOxygen = maxBloodOxygen;
    }

    public int getMinBloodOxygen() {
        return minBloodOxygen;
    }

    public void setMinBloodOxygen(int minBloodOxygen) {
        this.minBloodOxygen = minBloodOxygen;
    }

    private int        minBloodOxygen;
    public int getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    protected BloodOxygenList(Parcel in) {
        bloodOxygen = in.readInt();
        rtc = in.readString();
        maxBloodOxygen= in.readInt();
        minBloodOxygen= in.readInt();
        weekCount= in.readInt();
    }

    public static final Creator<BloodOxygenList> CREATOR = new Creator<BloodOxygenList>() {
        @Override
        public BloodOxygenList createFromParcel(Parcel in) {
            return new BloodOxygenList(in);
        }

        @Override
        public BloodOxygenList[] newArray(int size) {
            return new BloodOxygenList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bloodOxygen);
        parcel.writeString(rtc);
        parcel.writeInt(maxBloodOxygen);
        parcel.writeInt(minBloodOxygen);
        parcel.writeInt(weekCount);
    }
}
