package com.example.bozhilun.android.bleutil;

import android.os.Parcel;
import android.os.Parcelable;

public class SumBean implements Parcelable {
    private int type;//0睡眠 1深睡
    private int sum;//5分钟

    protected SumBean(Parcel in) {
        type = in.readInt();
        sum = in.readInt();
    }

    public static final Creator<SumBean> CREATOR = new Creator<SumBean>() {
        @Override
        public SumBean createFromParcel(Parcel in) {
            return new SumBean(in);
        }

        @Override
        public SumBean[] newArray(int size) {
            return new SumBean[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public SumBean(int type, int sum) {
        this.type = type;
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "SumBean [sum=" + sum + ", type=" + type + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeInt(sum);
    }


}
