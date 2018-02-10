package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SumBean;
import com.example.bozhilun.android.util.Common;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2017/3/28.
 */

public class SleepCurveBean implements Parcelable {
    private ArrayList<SumBean> beanList;
    private String sleepCurveS;
    private String userId;
    private String Address;

    public SleepCurveBean(ArrayList<SumBean> beanList, String sleepCurveS) {
        this.beanList = beanList;
        this.sleepCurveS = sleepCurveS;
        this.userId = Common.customer_id;
        this.Address = MyCommandManager.ADDRESS;
    }

    public ArrayList<SumBean> getBeanList() {
        return beanList;
    }

    public void setBeanList(ArrayList<SumBean> beanList) {
        this.beanList = beanList;
    }

    public String getSleepCurveS() {
        return sleepCurveS;
    }

    public void setSleepCurveS(String sleepCurveS) {
        this.sleepCurveS = sleepCurveS;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    protected SleepCurveBean(Parcel in) {
        sleepCurveS = in.readString();
        userId = in.readString();
        Address = in.readString();
    }

    public static final Creator<SleepCurveBean> CREATOR = new Creator<SleepCurveBean>() {
        @Override
        public SleepCurveBean createFromParcel(Parcel in) {
            return new SleepCurveBean(in);
        }

        @Override
        public SleepCurveBean[] newArray(int size) {
            return new SleepCurveBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sleepCurveS);
        parcel.writeString(userId);
        parcel.writeString(Address);
    }
}
