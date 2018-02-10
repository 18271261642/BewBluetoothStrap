package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by thinkpad on 2017/3/16.
 */
@Entity
public class SleepBean implements Parcelable{
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String startTime;//开始睡眠
    @NotNull
    private String entTime;//结束睡眠
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    @NotNull
    private int sleep_id;//1总 2深睡
    @NotNull
    private int sleep_time;//睡眠时间分钟
    @NotNull
    private String userId;
    @NotNull
    private String addressMac;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEntTime() {
        return entTime;
    }

    public void setEntTime(String entTime) {
        this.entTime = entTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSleep_id() {
        return sleep_id;
    }

    public void setSleep_id(int sleep_id) {
        this.sleep_id = sleep_id;
    }

    public int getSleep_time() {
        return sleep_time;
    }

    public void setSleep_time(int sleep_time) {
        this.sleep_time = sleep_time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressMac() {
        return addressMac;
    }

    public void setAddressMac(String addressMac) {
        this.addressMac = addressMac;
    }

    protected SleepBean(Parcel in) {
        startTime = in.readString();
        entTime = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        sleep_id = in.readInt();
        sleep_time = in.readInt();
        userId = in.readString();
        addressMac = in.readString();
    }

    @Generated(hash = 82550739)
    public SleepBean(Long id, @NotNull String startTime, @NotNull String entTime,
            int year, int month, int day, int hour, int minute, int sleep_id,
            int sleep_time, @NotNull String userId, @NotNull String addressMac) {
        this.id = id;
        this.startTime = startTime;
        this.entTime = entTime;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.sleep_id = sleep_id;
        this.sleep_time = sleep_time;
        this.userId = userId;
        this.addressMac = addressMac;
    }

    @Generated(hash = 266794267)
    public SleepBean() {
    }

    public static final Creator<SleepBean> CREATOR = new Creator<SleepBean>() {
        @Override
        public SleepBean createFromParcel(Parcel in) {
            return new SleepBean(in);
        }

        @Override
        public SleepBean[] newArray(int size) {
            return new SleepBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(startTime);
        parcel.writeString(entTime);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeInt(sleep_id);
        parcel.writeInt(sleep_time);
        parcel.writeString(userId);
        parcel.writeString(addressMac);
    }
}
