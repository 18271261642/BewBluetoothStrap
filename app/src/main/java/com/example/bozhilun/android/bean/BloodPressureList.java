package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class BloodPressureList implements Parcelable{
    private int  weekCount;
    private int systolic;
    private String rtc;
    private int diastolic;

    private int maxDiastolic;
    private int       minSystolic;

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getMaxDiastolic() {

        return maxDiastolic;
    }

    public void setMaxDiastolic(int maxDiastolic) {
        this.maxDiastolic = maxDiastolic;
    }

    public int getMinSystolic() {
        return minSystolic;
    }

    public void setMinSystolic(int minSystolic) {
        this.minSystolic = minSystolic;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    protected BloodPressureList(Parcel in) {
        systolic = in.readInt();
        rtc = in.readString();
        diastolic = in.readInt();
        maxDiastolic = in.readInt();
         minSystolic = in.readInt();
        weekCount= in.readInt();
    }

    public static final Creator<BloodPressureList> CREATOR = new Creator<BloodPressureList>() {
        @Override
        public BloodPressureList createFromParcel(Parcel in) {
            return new BloodPressureList(in);
        }

        @Override
        public BloodPressureList[] newArray(int size) {
            return new BloodPressureList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(systolic);
        parcel.writeString(rtc);
        parcel.writeInt(diastolic);
        parcel.writeInt(minSystolic);
        parcel.writeInt(maxDiastolic);
        parcel.writeInt(weekCount);
    }
}
