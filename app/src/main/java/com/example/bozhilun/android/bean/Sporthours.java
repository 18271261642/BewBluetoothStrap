package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thinkpad on 2017/3/18.
 */

public class Sporthours implements Parcelable, Comparable<Sporthours> {

    private int stepNumber;
    private String rtc;

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getRtc() {
        return rtc;
    }

    public void setRtc(String rtc) {
        this.rtc = rtc;
    }

    protected Sporthours(Parcel in) {
        stepNumber = in.readInt();
        rtc = in.readString();
    }

    public static final Creator<Sporthours> CREATOR = new Creator<Sporthours>() {
        @Override
        public Sporthours createFromParcel(Parcel in) {
            return new Sporthours(in);
        }

        @Override
        public Sporthours[] newArray(int size) {
            return new Sporthours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(stepNumber);
        parcel.writeString(rtc);
    }

    @Override
    public int compareTo(Sporthours sporthours) {
        String date = sporthours.getRtc();
        int result = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date new_date = simpleDateFormat.parse(date);
            Date my_date = simpleDateFormat.parse(rtc);
            if (new_date.getTime() < my_date.getTime()) {
                result = -1;
            } else if (new_date.getTime() == my_date.getTime()) {
                result = 0;
            } else {
                result = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
