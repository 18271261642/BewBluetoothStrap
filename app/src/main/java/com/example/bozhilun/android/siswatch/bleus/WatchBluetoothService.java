package com.example.bozhilun.android.siswatch.bleus;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.bleutil.Customdata;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SampleGattAttributes;
import com.example.bozhilun.android.siswatch.DateChanageReceiver;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.siswatch.utils.test.GetWatchStepInterface;
import com.example.bozhilun.android.siswatch.utils.test.JiedianTest;
import com.example.bozhilun.android.siswatch.utils.test.TimeInterface;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/17.
 */

public class WatchBluetoothService extends Service {
    private final static String TAG = "手表服务";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress ;
    private BluetoothGatt mBluetoothGatt;
    public int mConnectionState = STATE_DISCONNECTED;

    public static boolean bleConnect = false;
    public static boolean isInitiative = false;

    public static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    //时间变化的广播
    DateChanageReceiver dateChanageReceiver = new DateChanageReceiver();

    //获取节电时间接口
    private JiedianTest jiedianTest;

    public void setJiedianTest(JiedianTest jiedianTest) {
        this.jiedianTest = jiedianTest;
    }
    //获取手表时间接口
    private TimeInterface timeInterface;

    public void setTimeInterface(TimeInterface timeInterface) {
        this.timeInterface = timeInterface;
    }
    //获取步数接口
    private GetWatchStepInterface getWatchStepInterface;

    public void setGetWatchStepInterface(GetWatchStepInterface getWatchStepInterface) {
        this.getWatchStepInterface = getWatchStepInterface;
    }

    //断开后自动重连线程
    class resultConnectBle extends Thread{

        @Override
        public void run() {
            super.run();
            try {
                boolean isresultconnect = true;
                while (isresultconnect){
                    if(mBluetoothAdapter.isEnabled()){
                        if(mBluetoothDeviceAddress != null && SharedPreferencesUtils.getParam(MyApp.getContext(),"bozlunmac","").equals(mBluetoothDeviceAddress)){
                            if(mBluetoothGatt != null){
                                mBluetoothGatt.close();
                                mBluetoothGatt = null;
                            }
                            Thread.sleep(1000);
                            boolean isResultConnect = connect(mBluetoothDeviceAddress);
                            Log.e(TAG,"-----循环连接-------"+isResultConnect);
                            if(isResultConnect){
                                isresultconnect = false;
                            }else{
                                isresultconnect = true;
                            }
                        }

                    }else{
                        isresultconnect = true;
                    }
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.，所有函数的回调函数
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG,"-----newState----"+newState+"---"+status);
            /**
             * 连接状态：newState
             *    * The profile is in disconnected state   *public static final int STATE_DISCONNECTED  = 0;
             *    * The profile is in connecting state     *public static final int STATE_CONNECTING    = 1;
             *    * The profile is in connected state      *public static final int STATE_CONNECTED    = 2;
             *    * The profile is in disconnecting state  *public static final int STATE_DISCONNECTING = 3;
             *
             */
            String intentAction;
            //收到设备notify值 （设备上报值）
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                bleConnect = true;
                EventBus.getDefault().post(new MessageEvent("STATE_CONNECTED"));
                try {
                    Thread.sleep(500);
                    //发现服务
                    if(null!=mBluetoothGatt){
                        mBluetoothGatt.discoverServices();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                isInitiative = true;
                broadcastUpdate(intentAction);
                Log.e(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                bleConnect = false;
                if(null!=mBluetoothGatt){
                    Log.e(TAG,"-----null!=mBluetoothGatt--------");
                    mBluetoothGatt.close();
                    mBluetoothGatt = null; 
                }
                Log.e(TAG, "Disconnected from GATT server.");
                isInitiative = false;
                String bozlunMac = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"bozlunmac","");
                Log.e(TAG,"--bozlunMac---"+bozlunMac+"---"+gatt.getDevice().getAddress());
                if(!WatchUtils.isEmpty(bozlunMac) && gatt.getDevice().getAddress().equals(bozlunMac)){
                    Log.e(TAG,"----非主动断开----");
                    new resultConnectBle().start();
                }else{
                    Log.e(TAG,"----主动断开----");
                    Intent ints = new Intent();
                    ints.setAction(WatchUtils.WACTH_DISCONNECT_BLE_ACTION);
                    ints.putExtra("bledisconn","bledisconn");
                    sendBroadcast(ints);

                }
                broadcastUpdate(intentAction);
            }
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);


            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
                System.out.println("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            //读取到值，在这里读数据
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };




    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.e(TAG,"----action-----"+action);
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){

        }
        else {
            // For all other profiles, writes the data formatted in HEX.对于所有的文件，写入十六进制格式的文件
            //这里读取到数据
            final byte[] data = characteristic.getValue();
            List<String> recordList = new ArrayList<>();
            Map<String,Integer> steMap = new HashMap<>();
            Log.e(TAG,"---------999----"+ SharedPreferencesUtils.bytesToHexString(characteristic.getValue()));
            for(int n = 0;n<data.length;n++){
                Log.e(TAG,"-------aa--"+n+"---"+data[n]+"--"+ Customdata.byteToHex(data[n]));
            }

            if(Customdata.byteToHex(data[2]).equals("11")){ //获取步数
                steMap.put("today", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6])+ Customdata.byteToHex(data[5])));  //今天的数据
                steMap.put("yestoday", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8])+ Customdata.byteToHex(data[7]))); //昨天
                steMap.put("qiantian", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[10])+ Customdata.byteToHex(data[9])));   //前天
                steMap.put("fourthDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[12])+ Customdata.byteToHex(data[11]))); //大前天
                steMap.put("fiveDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[14])+ Customdata.byteToHex(data[13])));   //前4天
                steMap.put("sixthDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[16])+ Customdata.byteToHex(data[15])));  //前5天
                steMap.put("seventhDay", Customdata.hexStringToAlgorism(Customdata.byteToHex(data[18])+ Customdata.byteToHex(data[17])));    //前6天

                intent.putExtra("bledata", Customdata.byteToHex(data[6])+ Customdata.byteToHex(data[5]));
                EventBus.getDefault().post(new MessageEvent("getwatchsteps", steMap));

                EventBus.getDefault().post(new MessageEvent("startsynctime")); //发送同步手表时间的通知
            }else if(Customdata.byteToHex(data[2]).equals("44")){   //同步时间
                EventBus.getDefault().post(new MessageEvent("syncwatchtime","同步成功"));

            }else if(Customdata.byteToHex(data[2]).equals("30")){   //获取手表返回的时间数据
                EventBus.getDefault().post(new MessageEvent("rebackWatchTime",data));
                if(timeInterface != null){
                    timeInterface.getWatchTime(data);
                }
            }
            else if(Customdata.byteToHex(data[2]).equals("77")){   //获取第一个闹钟的时间
                Log.e(TAG,"----第一个闹钟返回--11--");


                int whour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
                int wmine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
                String newwhour,newwmine;
                if(whour <=9){
                    newwhour = String.valueOf(0)+String.valueOf(whour);
                }else{
                    newwhour = whour+"";
                }
                if(wmine <=9){
                    newwmine = String.valueOf(0)+String.valueOf(wmine);
                }else{
                    newwmine = wmine+"";
                }
                EventBus.getDefault().post(new MessageEvent("getalarmtimesuccessfirst",newwhour+":"+newwmine+"-"+ Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]))));
               // EventBus.getDefault().post(new MessageEvent("settingsecondalarm"));

            }else if(Customdata.byteToHex(data[2]).equals("78")){   //第二个闹钟时间
                Log.e(TAG,"----第2个闹钟返回--22--");
                int whour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
                int wmine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
                String newwhour,newwmine;
                if(whour <=9){
                    newwhour = String.valueOf(0)+String.valueOf(whour);
                }else{
                    newwhour = whour+"";
                }
                if(wmine <=9){
                    newwmine = String.valueOf(0)+String.valueOf(wmine);
                }else{
                    newwmine = wmine+"";
                }
                EventBus.getDefault().post(new MessageEvent("getalarmtimesuccesssecond",newwhour+":"+newwmine+"-"+ Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]))));
                EventBus.getDefault().post(new MessageEvent("settingthirdalarm"));
            }else if(Customdata.byteToHex(data[2]).equals("79")){   //第三个闹钟时间
                Log.e(TAG,"----第3个闹钟返回--33--");
                int whour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
                int wmine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
                String newwhour,newwmine;
                if(whour <=9){
                    newwhour = String.valueOf(0)+String.valueOf(whour);
                }else{
                    newwhour = whour+"";
                }
                if(wmine <=9){
                    newwmine = String.valueOf(0)+String.valueOf(wmine);
                }else{
                    newwmine = wmine+"";
                }
                EventBus.getDefault().post(new MessageEvent("getalarmtimesuccessthird",newwhour+":"+newwmine+"-"+ Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]))));
            }else if(Customdata.byteToHex(data[2]).equals("66")){   //获取节电时间
//                EventBus.getDefault().post(new MessageEvent("returnjiediantime",Customdata.byteToHex(data[6])+":"+Customdata.byteToHex(data[7])+Customdata.byteToHex(data[8])+":"+Customdata.byteToHex(data[9]))+"-"+Customdata.hexStringToAlgorism(Customdata.byteToHex(data[9])));
//                String datass = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]))+":"+Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]))+"-"+Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]))+":"+Customdata.hexStringToAlgorism(Customdata.byteToHex(data[9]));
                //显示节电的开始时间和结束时间，
                String newStartjiedianHour ,newStartjiedianMine,newjiedianEndHour,newjiedianEndMine;
                int startjiedianHour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[6]));
                int startjiedianMine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[7]));
                int endjiedianHour = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[8]));
                int endjiedianMine = Customdata.hexStringToAlgorism(Customdata.byteToHex(data[9]));
                if(startjiedianHour <=9){
                    newStartjiedianHour = String.valueOf(0)+startjiedianHour;
                }else{
                    newStartjiedianHour = startjiedianHour+"";
                }
                if(startjiedianMine <=9){
                    newStartjiedianMine = String.valueOf(0) + startjiedianMine;
                }else{
                    newStartjiedianMine = startjiedianMine+"";
                }

                if(endjiedianHour <=9){
                    newjiedianEndHour = String.valueOf(0)+endjiedianHour;
                }else{
                    newjiedianEndHour = endjiedianHour+"";
                }
                if(endjiedianMine <=9){
                    newjiedianEndMine = String.valueOf(0) + endjiedianMine;
                }else{
                    newjiedianEndMine = endjiedianMine+"";
                }

                String jiedianTimeData =newStartjiedianHour+":"+newStartjiedianMine+"-"+newjiedianEndHour+":"+newjiedianEndMine;
                Log.e(TAG,"---jiedianTimeData--"+jiedianTimeData);
//                if(jiedianTest != null){
//                    jiedianTest.getJiedianData(jiedianTimeData);
//                }
                EventBus.getDefault().post(new MessageEvent("msgJiedian",jiedianTimeData));
            }else if(Customdata.byteToHex(data[2]).equals("60")){   //闹钟设置成功
                EventBus.getDefault().post(new MessageEvent("setalarmsuccess"));
            }else if(Customdata.byteToHex(data[2]).equals("33")){
                EventBus.getDefault().post(new MessageEvent("getWatchTimeSuccess", SharedPreferencesUtils.bytesToHexString(characteristic.getValue())+Arrays.toString(data)));
            }else if(Customdata.byteToHex(data[4]).equals("81")){   //挂断电话的信号
                EventBus.getDefault().post(new MessageEvent("disphone"));
                TelephonyManager tm = (TelephonyManager) MyApp.getContext()
                        .getSystemService(Service.TELEPHONY_SERVICE);
                PhoneUtils.endPhone(MyApp.getContext(),tm);
                PhoneUtils.dPhone();
                PhoneUtils.endCall(MyApp.getContext());
                PhoneUtils.endcall();
            }else if(Customdata.byteToHex(data[3]).equals("03")&& Customdata.byteToHex(data[6]).equals("08")){    //返回拍照的指令
                Log.e(TAG,"-----接收到了拍照的指令----");
                EventBus.getDefault().post(new MessageEvent("tophoto"));
            }

            Log.e(TAG,"-------返回的数据----"+Arrays.toString(data)+"------"+data.toString());
                Log.e(TAG,"-------返回的数据----"+ Customdata.bytes2HexString(characteristic.getValue()));

            for (int i = 0; i < data.length; i++) {
                System.out.println("data......" + data[i]);
            }
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    //以十六进制的形式输出
                    stringBuilder.append(String.format("%02X ", byteChar));
                // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                intent.putExtra(EXTRA_DATA, Customdata.bytes2HexString(data));
             //   intent.putExtra("bledata",Customdata.byteToHex(data[6])+Customdata.byteToHex(data[5]));
            }
        }
        //发送广播，广播接收器在哪？？
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED); //时间变化
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED); //日期变化
        registerReceiver(dateChanageReceiver,new IntentFilter(intentFilter));
        registerReceiver(disBroadCastReceiver,new IntentFilter(WatchUtils.WACTH_DISCONNECT_BLE_ACTION));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        disconnect();   //断开连接
        unregisterReceiver(dateChanageReceiver);
        unregisterReceiver(disBroadCastReceiver);
    }

    private BroadcastReceiver disBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public class LocalBinder extends Binder {
        public WatchBluetoothService getService() {
            return WatchBluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    //判断是否连接
    public boolean isConnected(){
        try {
            String bleAddress = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(),"mylanmac");
            if(!WatchUtils.isEmpty(bleAddress)){
                boolean isConn = connect(bleAddress);
                return isConn;
            }else{
                boolean isCon = connect(MyCommandManager.ADDRESS);
                return isCon;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        Log.e(TAG,"----connect----"+address);
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        mBluetoothAdapter.startDiscovery();

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        if(mBluetoothGatt != null){
            mBluetoothGatt.close();
        }
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.e(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        Log.e(TAG,"device.getBondState==" + device.getBondState());
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        Log.e(TAG,"----disconnect----");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        //mBluetoothGatt = null;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    //读取数据的函数
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    //写入指定的characteristic
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Log.e(TAG,"-----characteristic.getUuid()---="+characteristic.getUuid()+"-="+SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     * 获取已连接设备支持的所有GATT服务集合
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGattService getSupportedGattServices(UUID uuid) {
        BluetoothGattService mBluetoothGattService;
        if (mBluetoothGatt == null) return null;
        mBluetoothGattService = mBluetoothGatt.getService(uuid);
        return mBluetoothGattService;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ServiceMessageEvent event) {

    }

}
