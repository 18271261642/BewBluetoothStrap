package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class BloodOxygen implements Parcelable {
    private ArrayList<BloodOxygenList> BloodOxygen;
    private AvgBloodOxygen avgBloodOxygen;

    public ArrayList<BloodOxygenList> getBloodOxygen() {
        return BloodOxygen;
    }

    public void setBloodOxygen(ArrayList<BloodOxygenList> bloodOxygen) {
        BloodOxygen = bloodOxygen;
    }

    public AvgBloodOxygen getAvgBloodOxygen() {
        return avgBloodOxygen;
    }

    public void setAvgBloodOxygen(AvgBloodOxygen avgBloodOxygen) {
        this.avgBloodOxygen = avgBloodOxygen;
    }

    protected BloodOxygen(Parcel in) {
        BloodOxygen = in.createTypedArrayList(BloodOxygenList.CREATOR);
        avgBloodOxygen = in.readParcelable(AvgBloodOxygen.class.getClassLoader());
    }

    public static final Creator<BloodOxygen> CREATOR = new Creator<BloodOxygen>() {
        @Override
        public BloodOxygen createFromParcel(Parcel in) {
            return new BloodOxygen(in);
        }

        @Override
        public BloodOxygen[] newArray(int size) {
            return new BloodOxygen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(BloodOxygen);
        parcel.writeParcelable(avgBloodOxygen, i);
    }
}
