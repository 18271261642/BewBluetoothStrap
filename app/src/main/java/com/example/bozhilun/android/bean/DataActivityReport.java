package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class DataActivityReport implements Parcelable {

    private BloodPressure bloodPressure;
    private BloodOxygen bloodOxygen;
    private HeartRate heartRate;
    private Sport sport;
    private Sleep sleep;

    public BloodPressure getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(BloodPressure bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public BloodOxygen getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(BloodOxygen bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public HeartRate getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartRate heartRate) {
        this.heartRate = heartRate;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Sleep getSleep() {
        return sleep;
    }

    public void setSleep(Sleep sleep) {
        this.sleep = sleep;
    }

    protected DataActivityReport(Parcel in) {
        bloodPressure = in.readParcelable(BloodPressure.class.getClassLoader());
        bloodOxygen = in.readParcelable(BloodOxygen.class.getClassLoader());
        heartRate = in.readParcelable(HeartRate.class.getClassLoader());
        sport = in.readParcelable(Sport.class.getClassLoader());
        sleep = in.readParcelable(Sleep.class.getClassLoader());
    }

    public static final Creator<DataActivityReport> CREATOR = new Creator<DataActivityReport>() {
        @Override
        public DataActivityReport createFromParcel(Parcel in) {
            return new DataActivityReport(in);
        }

        @Override
        public DataActivityReport[] newArray(int size) {
            return new DataActivityReport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(bloodPressure, i);
        parcel.writeParcelable(bloodOxygen, i);
        parcel.writeParcelable(heartRate, i);
        parcel.writeParcelable(sport, i);
        parcel.writeParcelable(sleep, i);
    }
}
