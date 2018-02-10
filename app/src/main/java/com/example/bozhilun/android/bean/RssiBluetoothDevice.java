package com.example.bozhilun.android.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by thinkpad on 2017/3/15.
 */

public class RssiBluetoothDevice implements Comparable<RssiBluetoothDevice> {

    private BluetoothDevice bluetoothDevice;
    private int ressi;

    public RssiBluetoothDevice(BluetoothDevice bluetoothDevice, int ressi) {
        this.bluetoothDevice = bluetoothDevice;
        this.ressi = ressi;
    }
    public RssiBluetoothDevice() {

    }
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRessi() {
        return ressi;
    }

    public void setRessi(int ressi) {
        this.ressi = ressi;
    }

    @Override
    public int compareTo(RssiBluetoothDevice rssiBluetoothDevice) {
        if (this.ressi < rssiBluetoothDevice.getRessi()) return -1;
        else if (this.ressi == rssiBluetoothDevice.getRessi()) return 0;
        else return 1;
    }
}
