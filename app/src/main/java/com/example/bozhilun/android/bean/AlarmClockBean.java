package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by thinkpad on 2017/3/15.
 */
@Entity
public class AlarmClockBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String lanyaneme;
    @NotNull
    private String addressMac;
    @NotNull
    private String BeginHour;
    @NotNull
    private String Beginminte;
    @NotNull
    private String userId;
    @NotNull
    private int isOpen;
    private int number;
    private int every;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanyaneme() {
        return lanyaneme;
    }

    public void setLanyaneme(String lanyaneme) {
        this.lanyaneme = lanyaneme;
    }

    public String getAddressMac() {
        return addressMac;
    }

    public void setAddressMac(String addressMac) {
        this.addressMac = addressMac;
    }

    public String getBeginHour() {
        return BeginHour;
    }

    public void setBeginHour(String beginHour) {
        BeginHour = beginHour;
    }

    public String getBeginminte() {
        return Beginminte;
    }

    public void setBeginminte(String beginminte) {
        Beginminte = beginminte;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getEvery() {
        return every;
    }

    public void setEvery(int every) {
        this.every = every;
    }

    protected AlarmClockBean(Parcel in) {
        lanyaneme = in.readString();
        addressMac = in.readString();
        BeginHour = in.readString();
        Beginminte = in.readString();
        userId = in.readString();
        isOpen = in.readInt();
        number = in.readInt();
        every = in.readInt();
    }

    public AlarmClockBean(String lanyaneme, String userId, String addressMac, String BeginHour,
                          String Beginminte, int isOpen, int number) {
        this.userId = userId;
        this.lanyaneme = lanyaneme;
        this.addressMac = addressMac;
        this.BeginHour = BeginHour;
        this.Beginminte = Beginminte;
        this.number = number;
        this.isOpen = isOpen;
    }

    public AlarmClockBean(String lanyaneme, String userId, String addressMac, String BeginHour,
                          String Beginminte, int isOpen, int number, int every) {
        this.userId = userId;
        this.lanyaneme = lanyaneme;
        this.addressMac = addressMac;
        this.BeginHour = BeginHour;
        this.Beginminte = Beginminte;
        this.isOpen = isOpen;
        this.number = number;
        this.every = every;
    }

    @Generated(hash = 1529746916)
    public AlarmClockBean(Long id, @NotNull String lanyaneme, @NotNull String addressMac,
            @NotNull String BeginHour, @NotNull String Beginminte, @NotNull String userId,
            int isOpen, int number, int every) {
        this.id = id;
        this.lanyaneme = lanyaneme;
        this.addressMac = addressMac;
        this.BeginHour = BeginHour;
        this.Beginminte = Beginminte;
        this.userId = userId;
        this.isOpen = isOpen;
        this.number = number;
        this.every = every;
    }

    @Generated(hash = 1521996584)
    public AlarmClockBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lanyaneme);
        dest.writeString(addressMac);
        dest.writeString(BeginHour);
        dest.writeString(Beginminte);
        dest.writeString(userId);
        dest.writeInt(isOpen);
        dest.writeInt(number);
        dest.writeInt(every);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlarmClockBean> CREATOR = new Creator<AlarmClockBean>() {
        @Override
        public AlarmClockBean createFromParcel(Parcel in) {
            return new AlarmClockBean(in);
        }

        @Override
        public AlarmClockBean[] newArray(int size) {
            return new AlarmClockBean[size];
        }
    };
}
