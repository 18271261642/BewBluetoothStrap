package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by thinkpad on 2017/3/16.
 */
@Entity
public class StepBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private int stepNumber;//步数
    private String distance;//路程
    private int calories;//能耗
    @NotNull
    private String userId;
    @NotNull
    private String deviceCode;
    @NotNull
    private String date;
    private int status;//0达标 1不达标



    public Long getId() {
        return id;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    protected StepBean(Parcel in) {
        id = in.readLong();
        stepNumber = in.readInt();
        distance = in.readString();
        calories = in.readInt();
        userId = in.readString();
        deviceCode = in.readString();
        date = in.readString();
        status = in.readInt();

    }

    public StepBean(int stepNumber,
                    @NotNull String date, int status,String deviceCode) {
        this.stepNumber = stepNumber;
        this.distance = getDistance(75, stepNumber);
        this.calories = getCalories(this.distance);
        this.userId = Common.customer_id;
        this.deviceCode = deviceCode;
        this.date = date;
        this.status = status;


    }



    @Generated(hash = 781306117)
    public StepBean() {
    }

    @Generated(hash = 153816641)
    public StepBean(Long id, int stepNumber, String distance, int calories,
            @NotNull String userId, @NotNull String deviceCode,
            @NotNull String date, int status) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.distance = distance;
        this.calories = calories;
        this.userId = userId;
        this.deviceCode = deviceCode;
        this.date = date;
        this.status = status;
    }


    public static final Creator<StepBean> CREATOR = new Creator<StepBean>() {
        @Override
        public StepBean createFromParcel(Parcel in) {
            return new StepBean(in);
        }

        @Override
        public StepBean[] newArray(int size) {
            return new StepBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(stepNumber);
        parcel.writeString(distance);
        parcel.writeInt(calories);
        parcel.writeString(userId);
        parcel.writeString(deviceCode);
        parcel.writeString(date);
        parcel.writeInt(status);
    }

    //计算路程
    public String getDistance(int height, int step) {
        String distance = "";
        double stepLength = 0;
        if (height < 155) {
            stepLength = height * 20 / 4200d;
        } else if (155 < height && height < 174) {
            stepLength = height * 13 / 2800d;
        } else if (height >= 174) {
            stepLength = height * 19 / 4200d;
        }
        distance = String.valueOf((step * stepLength) / 1000d);
        distance = distance.substring(0, distance.indexOf(".") + 2);
        return distance;
    }

    //计算卡路里
    public int getCalories(String distance) {
        return (int) (Double.valueOf(distance) * 65.4);
    }

    public void setId(Long id) {
        this.id = id;
    }
}
