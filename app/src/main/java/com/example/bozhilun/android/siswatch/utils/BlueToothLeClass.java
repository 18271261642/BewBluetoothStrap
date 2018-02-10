package com.example.bozhilun.android.siswatch.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2017/7/20.
 */

public class BlueToothLeClass {

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;

    public interface OnConnectListener{
        void onConnect(BluetoothGatt gatt);
    }

    public interface OnDisconnectListener{
        void onDisconnect(BluetoothGatt gatt);
    }

    public interface OnServiceDiscoverListener{
        void onServiceDiscover(BluetoothGatt gatt);
    }

    public interface OnDataAvailableListener{
        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic,int status);
    }

    private OnConnectListener mOnConnectListener;
    private OnDisconnectListener mOnDisconnectListener;
    private OnServiceDiscoverListener mOnServiceDiscoverListener;
    private OnDataAvailableListener mOnDataAvailableListener;
    private Context mContext;

    public void setOnConnectListener(OnConnectListener listener){
        mOnConnectListener=listener;
    }

    public void setOnDisconnectListener(OnDisconnectListener listener){
        mOnDisconnectListener=listener;
    }

    public void setOnDataAvailableListener(OnDataAvailableListener listener){
        mOnDataAvailableListener=listener;
    }

    public void setOnServiceDiscoverListener(OnServiceDiscoverListener listener){
        mOnServiceDiscoverListener=listener;
    }

    public BlueToothLeClass(Context context){
        mContext=context;
    }

    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState== BluetoothProfile.STATE_CONNECTED){
                if(mOnConnectListener!=null){
                    mOnConnectListener.onConnect(gatt);
                }
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                if(mOnConnectListener!=null){
                    mOnDisconnectListener.onDisconnect(gatt);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(mOnDataAvailableListener!=null){
                mOnDataAvailableListener.onCharacteristicWrite(gatt, characteristic, status);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(mOnDataAvailableListener!=null){
                mOnDataAvailableListener.onCharacteristicRead(gatt,characteristic,status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS && mOnServiceDiscoverListener!=null) {
                mOnServiceDiscoverListener.onServiceDiscover(gatt);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("bluetoothdevice","onCharacteristicChanged");
        }
    };

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        return true;
    }

    public boolean connect(final String address){
        if(mBluetoothAdapter==null||address==null){
            return false;
        }
        if(mBluetoothGatt!=null&&mBluetoothDeviceAddress!=null&&address.equals(mBluetoothDeviceAddress)){
            if(mBluetoothGatt.connect()){
                return true;
            }else{
                return false;
            }
        }
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);
        if(null==remoteDevice){
            return false;
        }
        mBluetoothGatt=remoteDevice.connectGatt(mContext,true,mGattCallback);

        mBluetoothDeviceAddress=address;
        return true;
    }

    public void disconnect(){
        if(mBluetoothAdapter==null||mBluetoothGatt==null){
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close(){
        if(mBluetoothGatt==null){
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt=null;
    }

}
