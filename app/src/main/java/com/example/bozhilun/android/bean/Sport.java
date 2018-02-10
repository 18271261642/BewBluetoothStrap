package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class Sport implements Parcelable {
    private ArrayList<Sporthours> hours;
    private StepSum stepSum;

    public ArrayList<Sporthours> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Sporthours> hours) {
        this.hours = hours;
    }

    public StepSum getStepSum() {
        return stepSum;
    }

    public void setStepSum(StepSum stepSum) {
        this.stepSum = stepSum;
    }

    protected Sport(Parcel in) {
        hours = in.createTypedArrayList(Sporthours.CREATOR);
        stepSum = in.readParcelable(StepSum.class.getClassLoader());
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel in) {
            return new Sport(in);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(hours);
        parcel.writeParcelable(stepSum, i);
    }
}
