package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.Common;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by thinkpad on 2017/3/17.
 */
@Entity
public class B15PSleepBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String deviceCode;
    @NotNull
    private String userId;
    @NotNull
    private String startTime;
    private String endTime;
    private int count;
    private int deepLen;
    private int sleepLen;
    private int shallowLen;
    private int sleepQuality;
    private String sleepCurveS;
    private String sleepCurveP;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDeepLen() {
        return deepLen;
    }

    public void setDeepLen(int deepLen) {
        this.deepLen = deepLen;
    }

    public int getSleepLen() {
        return sleepLen;
    }

    public void setSleepLen(int sleepLen) {
        this.sleepLen = sleepLen;
    }

    public int getShallowLen() {
        return shallowLen;
    }

    public void setShallowLen(int shallowLen) {
        this.shallowLen = shallowLen;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public String getSleepCurveS() {
        return sleepCurveS;
    }

    public void setSleepCurveS(String sleepCurveS) {
        this.sleepCurveS = sleepCurveS;
    }

    public String getSleepCurveP() {
        return sleepCurveP;
    }

    public void setSleepCurveP(String sleepCurveP) {
        this.sleepCurveP = sleepCurveP;
    }

    protected B15PSleepBean(Parcel in) {
        deviceCode = in.readString();
        userId = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        count = in.readInt();
        deepLen = in.readInt();
        sleepLen = in.readInt();
        shallowLen = in.readInt();
        sleepQuality = in.readInt();
        sleepCurveS = in.readString();
        sleepCurveP = in.readString();
    }

    public B15PSleepBean(String startTime, String endTime, int count, int deepLen,
                         int shallowLen, int sleepQuality) {
        this.deviceCode = MyCommandManager.ADDRESS;
        this.userId = Common.customer_id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.count = count;
        this.deepLen = deepLen;
        this.shallowLen = shallowLen;
        this.sleepLen = deepLen + shallowLen;
        this.sleepQuality = sleepQuality;
    }

    @Generated(hash = 1037938592)
    public B15PSleepBean(Long id, @NotNull String deviceCode, @NotNull String userId,
                         @NotNull String startTime, String endTime, int count, int deepLen, int sleepLen,
                         int shallowLen, int sleepQuality, String sleepCurveS, String sleepCurveP) {
        this.id = id;
        this.deviceCode = deviceCode;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.count = count;
        this.deepLen = deepLen;
        this.sleepLen = sleepLen;
        this.shallowLen = shallowLen;
        this.sleepQuality = sleepQuality;
        this.sleepCurveS = sleepCurveS;
        this.sleepCurveP = sleepCurveP;
    }

    @Generated(hash = 764697241)
    public B15PSleepBean() {
    }

    public static final Creator<B15PSleepBean> CREATOR = new Creator<B15PSleepBean>() {
        @Override
        public B15PSleepBean createFromParcel(Parcel in) {
            return new B15PSleepBean(in);
        }

        @Override
        public B15PSleepBean[] newArray(int size) {
            return new B15PSleepBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(deviceCode);
        parcel.writeString(userId);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeInt(count);
        parcel.writeInt(deepLen);
        parcel.writeInt(sleepLen);
        parcel.writeInt(shallowLen);
        parcel.writeInt(sleepQuality);
        parcel.writeString(sleepCurveS);
        parcel.writeString(sleepCurveP);
    }
}
