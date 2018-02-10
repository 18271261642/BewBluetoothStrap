package com.example.bozhilun.android.siswatch.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

@SuppressLint("NewApi")
public class HidUtil {
	
	protected String TAG = "HidUtil";
	Context context;
	private BluetoothAdapter mBtAdapter;
	public static HidUtil instance;
	private BluetoothProfile mBluetoothProfile;
	private final int INPUT_DEVICE = 4;
	public static HidUtil getInstance(Context context){
		Log.e("配对工具","---66----");
		if(null == instance){
			Log.e("配对工具","---77----");
			instance = new HidUtil(context);
		}
		return instance;
	}

	private HidUtil(Context context) {
		this.context = context;
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBtAdapter != null){
			Log.e("配对工具","---111----");
			if(mBluetoothProfile == null){
				Log.e("配对工具","---222----");
				try {
					mBtAdapter.getProfileProxy(context,
							mListener, INPUT_DEVICE);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("配对工具","---333----");
				}
			}

		}

	}
	
	public void close(){
		if(null != mBtAdapter && null != mBluetoothProfile){
			try {
				mBtAdapter.closeProfileProxy(INPUT_DEVICE,mBluetoothProfile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private BluetoothProfile.ServiceListener mListener = new BluetoothProfile.ServiceListener() {
		@Override
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			Log.e(TAG, "mConnectListener onServiceConnected"+profile+"--"+proxy);
			//BluetoothProfile proxy这个已经是BluetoothInputDevice类型了
			try {
				if (profile == INPUT_DEVICE) {
					mBluetoothProfile = proxy;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(int profile) {
			Log.e(TAG, "mConnectListener onServiceConnected");
		}
	};
	
	/**
	 * 获取BluetoothProfile中hid的profile，"INPUT_DEVICE"类型隐藏，需反射获取
	 * @return
     */
	@SuppressLint("NewApi")
	public static int getInputDeviceHiddenConstant() {
		Class<BluetoothProfile> clazz = BluetoothProfile.class;
		for (Field f : clazz.getFields()) {
			int mod = f.getModifiers();
			if (Modifier.isStatic(mod) && Modifier.isPublic(mod)
					&& Modifier.isFinal(mod)) {
				try {
					if (f.getName().equals("INPUT_DEVICE")) {
						return f.getInt(null);
					}
				} catch (Exception e) {
				}
			}
		}
		return -1;
	}

	//判断是否绑定
	public boolean isBonded(BluetoothDevice device){
		int state =  device.getBondState();
		if(state == BluetoothDevice.BOND_BONDED){
			return true;
		}
		return false;
	}

	//判断设备是连接
	public boolean isConnected(BluetoothDevice device){
		Log.i(TAG,"isconneted device:"+device+",mBluetoothProfile:"+mBluetoothProfile);
		if(mBluetoothProfile == null)return false;
		List<BluetoothDevice> connectedDevices = mBluetoothProfile
				.getConnectedDevices();
		if(connectedDevices.contains(device)){
			return true;
		}
		return false;
	}

	//获取连接的设备
	public BluetoothDevice getConnectedDevice(String address){
		Log.i(TAG,"getConnectedDevice mac address:"+address);
		if(mBluetoothProfile == null || address == null)return null;
		List<BluetoothDevice> connectedDevices = mBluetoothProfile
				.getConnectedDevices();
		for(BluetoothDevice device:connectedDevices){
			String addr = device.getAddress();
			if(addr !=null && addr.equals(address)){
				return device;
			}
		}
		return null;
	}

	/**
	 * 根据mac地址获取是否有配对的设备
	 * @param bluetoothAdapter
	 * @param address
	 * @return
	 */
	public BluetoothDevice getPairDeviceByAddress(BluetoothAdapter bluetoothAdapter,String address){

		Set<BluetoothDevice> pariList = bluetoothAdapter.getBondedDevices();
		for(BluetoothDevice bd : pariList){
			String addr = bd.getAddress();
			if(addr != null && addr.equals(address)){
				return bd;
			}
		}
		return null;
	}

	/**
	 * 连接设备
	 * @parambluetoothDevice
     */
	public void connect(final BluetoothDevice device) {
		Log.e(TAG, "connect device:"+device);
		if(mBluetoothProfile != null){
			Log.e(TAG,"----conn--mBluetoothProfile!=null--");
			try {
				//得到BluetoothInputDevice然后反射connect连接设备
				Method method = mBluetoothProfile.getClass().getMethod("connect",
						new Class[] { BluetoothDevice.class });
				method.invoke(mBluetoothProfile, device);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Log.e(TAG,"----conn--mBluetoothProfile==null--");
			new HidUtil(context);
		}

	}

	/**
	 * 断开连接
	 * @parambluetoothDevice
     */
	public void disConnect(BluetoothDevice device) {
		Log.i(TAG, "disConnect device:"+device);
		try {
			if (device != null && mBluetoothProfile != null) {
				Method method = mBluetoothProfile.getClass().getMethod("disconnect",
						new Class[] { BluetoothDevice.class });
				method.invoke(mBluetoothProfile, device);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//设置优先级
	public void setPriority(BluetoothDevice device,int priority){
		Log.i(TAG, "setPriority device:"+device);
		try {
			if (device != null) {
				Method method = mBluetoothProfile.getClass().getMethod("setPriority",
						new Class[] { BluetoothDevice.class ,int.class});
				method.invoke(mBluetoothProfile, device,priority);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 配对
	 * @parambluetoothDevice
     */
	public void pair(BluetoothDevice device) {
		Log.i(TAG, "pair device:"+device);
		Method createBondMethod;
		try {
			createBondMethod = BluetoothDevice.class.getMethod("createBond");
			createBondMethod.invoke(device);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 取消配对
	 * @parambluetoothDevice
     */
	public void unPair(BluetoothDevice device) {
		Method createBondMethod;
		try {
			createBondMethod = BluetoothDevice.class.getMethod("removeBond");
			createBondMethod.invoke(device);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
