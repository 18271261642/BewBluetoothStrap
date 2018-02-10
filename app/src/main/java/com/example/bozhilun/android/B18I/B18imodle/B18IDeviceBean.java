package com.example.bozhilun.android.B18I.B18imodle;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2017/8/24.
 */
//l38i的实体类
public class B18IDeviceBean {
    private BluetoothDevice bluetoothDevice;
    private int ssi;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getSsi() {
        return ssi;
    }

    public void setSsi(int ssi) {
        this.ssi = ssi;
    }

    public B18IDeviceBean(BluetoothDevice bluetoothDevice, int ssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.ssi = ssi;
    }
}
