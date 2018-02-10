package com.example.bozhilun.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator
 * 用户资料实体类
 */
@Entity
public class BuleUser implements Parcelable {
    @Id
    @Index
    private String userId;
    private String email;
    private String nickName;
    private String password;
    private String sex;
    private String image;
    private String height;
    private String weight;
    private String birthday;
    private String deviceCode;

    private int type;


    protected BuleUser(Parcel in) {
        userId = in.readString();
        email = in.readString();
        nickName = in.readString();
        password = in.readString();
        sex = in.readString();
        image = in.readString();
        height = in.readString();
        weight = in.readString();
        birthday = in.readString();
        deviceCode = in.readString();
        type = in.readInt();
    }

    public BuleUser(String userId, String email, String nickName, String password,
            String sex, String image, String height, String weight, String birthday,
            String deviceCode, @NotNull String phone, int type) {
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.sex = sex;
        this.image = image;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.deviceCode = deviceCode;
        this.type = type;
    }

    @Generated(hash = 182650107)
    public BuleUser() {
    }

    @Generated(hash = 1752285986)
    public BuleUser(String userId, String email, String nickName, String password,
            String sex, String image, String height, String weight, String birthday,
            String deviceCode, int type) {
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.sex = sex;
        this.image = image;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.deviceCode = deviceCode;
        this.type = type;
    }

    public static final Creator<BuleUser> CREATOR = new Creator<BuleUser>() {
        @Override
        public BuleUser createFromParcel(Parcel in) {
            return new BuleUser(in);
        }

        @Override
        public BuleUser[] newArray(int size) {
            return new BuleUser[size];
        }
    };

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

  /*  public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }*/

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(email);
        parcel.writeString(nickName);
        parcel.writeString(password);
        parcel.writeString(sex);
        parcel.writeString(image);
        parcel.writeString(height);
        parcel.writeString(weight);
        parcel.writeString(birthday);
        parcel.writeString(deviceCode);

        parcel.writeInt(type);
    }
}
