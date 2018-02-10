package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class StepSum implements Parcelable {

    private int stepNumber;
    private int activityTime;

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(int activityTime) {
        this.activityTime = activityTime;
    }

    protected StepSum(Parcel in) {
        stepNumber = in.readInt();
        activityTime = in.readInt();
    }

    public static final Creator<StepSum> CREATOR = new Creator<StepSum>() {
        @Override
        public StepSum createFromParcel(Parcel in) {
            return new StepSum(in);
        }

        @Override
        public StepSum[] newArray(int size) {
            return new StepSum[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(stepNumber);
        parcel.writeInt(activityTime);
    }
}
