package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/4/10.
 */

public class StepWeekMonth implements Parcelable{

    private String weekCount;
    private int count;
    private String date;
    private int stepNumber;

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    protected StepWeekMonth(Parcel in) {
        weekCount = in.readString();
        count = in.readInt();
        date = in.readString();
        stepNumber = in.readInt();
    }

    public static final Creator<StepWeekMonth> CREATOR = new Creator<StepWeekMonth>() {
        @Override
        public StepWeekMonth createFromParcel(Parcel in) {
            return new StepWeekMonth(in);
        }

        @Override
        public StepWeekMonth[] newArray(int size) {
            return new StepWeekMonth[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(weekCount);
        parcel.writeInt(count);
        parcel.writeString(date);
        parcel.writeInt(stepNumber);
    }
}
