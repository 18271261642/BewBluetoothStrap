package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thinkpad on 2017/4/10.
 */

public class SportWeekMonth implements Parcelable {
    private ArrayList<StepWeekMonth> day;
    private int avgSport;
    private int avgActivity;

    public ArrayList<StepWeekMonth> getDay() {
        return day;
    }

    public void setDay(ArrayList<StepWeekMonth> day) {
        this.day = day;
    }

    protected SportWeekMonth(Parcel in) {
        day = in.createTypedArrayList(StepWeekMonth.CREATOR);
        avgSport = in.readInt();
        avgActivity = in.readInt();
    }

    public static final Creator<SportWeekMonth> CREATOR = new Creator<SportWeekMonth>() {
        @Override
        public SportWeekMonth createFromParcel(Parcel in) {
            return new SportWeekMonth(in);
        }

        @Override
        public SportWeekMonth[] newArray(int size) {
            return new SportWeekMonth[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(day);
        parcel.writeInt(avgSport);
        parcel.writeInt(avgActivity);
    }
}
