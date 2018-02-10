package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thinkpad on 2017/3/16.
 */
@Entity
public class B15PSleepHeartRateStepBean implements Parcelable, Comparable<B15PSleepHeartRateStepBean> {
    @Id(autoincrement = true)
    private Long id;
    private int systolic;//  收缩压
    private int diastolic;//舒张压
    private int stepNumber;// 步数
    private String date;// data_time
    private int heartRate;// 当前的心率
    @NotNull
    private String userId;
    @NotNull
    private String deviceCode;
    private int status;//0自动，1手动
    private int bloodOxygen;


    protected B15PSleepHeartRateStepBean(Parcel in) {
        systolic = in.readInt();
        diastolic = in.readInt();
        stepNumber = in.readInt();
        date = in.readString();
        heartRate = in.readInt();
        userId = in.readString();
        deviceCode = in.readString();
        status = in.readInt();
        bloodOxygen = in.readInt();
    }

    @Generated(hash = 1384972592)
    public B15PSleepHeartRateStepBean(Long id, int systolic, int diastolic, int stepNumber, String date,
            int heartRate, @NotNull String userId, @NotNull String deviceCode, int status, int bloodOxygen) {
        this.id = id;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.stepNumber = stepNumber;
        this.date = date;
        this.heartRate = heartRate;
        this.userId = userId;
        this.deviceCode = deviceCode;
        this.status = status;
        this.bloodOxygen = bloodOxygen;
    }

    @Generated(hash = 1143959857)
    public B15PSleepHeartRateStepBean() {
    }

    public static final Creator<B15PSleepHeartRateStepBean> CREATOR = new Creator<B15PSleepHeartRateStepBean>() {
        @Override
        public B15PSleepHeartRateStepBean createFromParcel(Parcel in) {
            return new B15PSleepHeartRateStepBean(in);
        }

        @Override
        public B15PSleepHeartRateStepBean[] newArray(int size) {
            return new B15PSleepHeartRateStepBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(systolic);
        parcel.writeInt(diastolic);
        parcel.writeInt(stepNumber);
        parcel.writeString(date);
        parcel.writeInt(heartRate);
        parcel.writeString(userId);
        parcel.writeString(deviceCode);
        parcel.writeInt(status);
        parcel.writeInt(bloodOxygen);
    }

    public B15PSleepHeartRateStepBean(int systolic, int diastolic, int stepNumber, String date,
                                      int heartRate, int bloodOxygen, int status, String userId, String addressMac) {
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.stepNumber = stepNumber;
        this.date = date;
        this.heartRate = heartRate;
        this.bloodOxygen = bloodOxygen;
        this.status = status;
        this.userId = userId;
        this.deviceCode = addressMac;
    }
    @Override
    public int compareTo(B15PSleepHeartRateStepBean b15PSleepHeartRateStepBean) {
        String b15_date = b15PSleepHeartRateStepBean.getDate();
        int result = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date new_date = simpleDateFormat.parse(b15_date);
            Date my_date = simpleDateFormat.parse(date);
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSystolic() {
        return this.systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return this.diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return this.deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBloodOxygen() {
        return this.bloodOxygen;
    }

    public void setBloodOxygen(int bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }
}
