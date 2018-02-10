package com.example.bozhilun.android.siswatch.bean;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 自定义的蓝牙搜索实体类
 */
public class CustomBlueDevice implements Parcelable ,Comparable<CustomBlueDevice> {
    //blue 对象
    public BluetoothDevice bluetoothDevice;
    //信号
    public String rssi;
    //CompanyId
    private int companyId;

    public CustomBlueDevice() {
    }

    public CustomBlueDevice(BluetoothDevice bluetoothDevice, String rssi, int companyId) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.companyId = companyId;
    }

    protected CustomBlueDevice(Parcel in) {
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readString();
        companyId = in.readInt();
    }

    public static final Creator<CustomBlueDevice> CREATOR = new Creator<CustomBlueDevice>() {
        @Override
        public CustomBlueDevice createFromParcel(Parcel source) {
            CustomBlueDevice customBlueDevice = new CustomBlueDevice();
            customBlueDevice.bluetoothDevice = source.readParcelable(BluetoothDevice.class.getClassLoader());
            customBlueDevice.rssi = source.readString();
            customBlueDevice.companyId = source.readInt();
            return new CustomBlueDevice(source);
        }

        @Override
        public CustomBlueDevice[] newArray(int size) {
            return new CustomBlueDevice[size];
        }
    };

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(bluetoothDevice, i);
        parcel.writeString(rssi);
        parcel.writeInt(companyId);
    }

    @Override
    public int compareTo(@NonNull CustomBlueDevice o) {
        return o.getRssi().compareTo(o.getRssi());
    }

}
