/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bozhilun.android.bleutil;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.afa.tourism.greendao.gen.B15PSleepBeanDao;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.activity.HeathActivity;
import com.example.bozhilun.android.activity.wylactivity.OutdoorCyclingActivityStar;
import com.example.bozhilun.android.alock.LogUtil;
import com.example.bozhilun.android.bean.B15PSleepBean;
import com.example.bozhilun.android.bean.B15PSleepHeartRateStepBean;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.bean.SleepCurveBean;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.VerifyUtil;
import com.lidroid.xutils.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.example.bozhilun.android.bleutil.Customdata.byteToHex;
import static com.example.bozhilun.android.bleutil.Customdata.huansuan;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@SuppressLint("NewApi")
public class BluetoothLeService extends Service {

    public BluetoothLeService() {
        super();
    }

    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public  static BluetoothGatt mBluetoothGatt;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String ACTION_DATA = "com.chara.data";

    public final static String batterylevel = "batterylevel";//电池电量
    public final static String ReadSteps = "ReadSteps";//步数
    public final static String Shakethecamera = "Shakethecamera";//相机
    public final static String HeartRate = "HeartRate";//心率单次测量
    public final static String OnekeyMeasurement = "OnekeyMeasurement";//一键测量通用模式
   public final static String OnekeyMeasurementsiren = "OnekeyMeasurement";//一键测量私人模式
    public final static String Findphone = "Findphone";//查找手机中
    public final static String Currentversionnumber = "Currentversionnumber";//手环版本号
    public final static String DailyActivity = "DailyActivity";//活动量
    public final static String Sleep = "Sleep";//睡眠质量

    public final static String XieYa = "XieYa";//血压
    public final static String XieYng = "XieYng";//血氧
    public static boolean isService=false;//是否有服务
    public JSONArray jsonArray = new JSONArray();
    public Calendar cendar = Calendar.getInstance();
    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_SERVER);//b15p
    public final static UUID UUID_from_b15s = UUID.fromString(SampleGattAttributes.HEART_RATE_SERVER);//b15ps
    private Handler handler = new Handler();
    private Handler handlerb  = new Handler();

    private Timer timer = null;
    private TimerTask task = null;
    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 8888:
                    EventBus.getDefault().post(new MessageEvent("all_day_acyivity", alldayActivity));
                    myhandler.removeCallbacksAndMessages(null);
                    if (null != timer) {
                        timer.cancel();
                        timer = null;
                    }
                    if (null != task) {
                        task.cancel();
                        task = null;
                    }
                    break;
            }
        }
    };


    boolean idfasong=false;
    private static boolean status;

    private Integer jishu2 = 0;

    private static String[] array = new String[16], array1 = new String[16], array2 = new String[16], array3 = new String[16], array4 = new String[16], array5 = new String[16];//b15p睡眠六段数据
    //计算结束时间
    public static String jieshuhour, dateb;
    //b15s 活动量的数据
    private int jinru = 0;
    private int zhuangtaizhi = 0;


    private BluetoothGattCharacteristic data;
    private BluetoothGattService RxService;
    private BluetoothGattCharacteristic RxChar;
    private List<Sleeptime> sleeptimeList = new ArrayList<>();
    private SimpleDateFormat sdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static ArrayList<B15PSleepHeartRateStepBean> alldayActivity = new ArrayList<>();
    public HashMap<String, Object> map;
    public  int stepcount=0;
    List AAA=new ArrayList();
    List BBB=new ArrayList();

    String pakege="";
    String pakege2="";
    String RI,sleepRI; //天
    public static   boolean isSendSteps=false;//是否发送过数据（步数详情）
    /**
     * 发现蓝牙UUID
     */
    private Runnable runnable = new Runnable() {
        public void run() {
            mBluetoothGatt.discoverServices();
        }
    };
    /**
     * b15s的睡眠数据
     */
    private Runnable runnableb = new Runnable() {
        public void run() {
            MyLogUtil.i("responseqsq"+sleeptimeList.toString());
               EventBus.getDefault().post(new MessageEvent("sleep15s_data_service", sleeptimeList));
           sleeptimeList.clear();
            idfasong=false;
        }
    };

    // Implements callback methods for GATT events that the app cares about.  For zook,
    // connection change and services discovered.，所有函数的回调函数
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            //收到设备notify值 （设备上报值）
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                MyCommandManager.deviceDisconnState = false;
                try {
                    Thread.sleep(500);
                    if(null!=mBluetoothGatt){
                        mBluetoothGatt.discoverServices();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

              /*  //延时发现服务
                    handler.postDelayed(runnable, 400L);*/
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*清除数据*/
                if(null!=mBluetoothGatt){
                    mBluetoothGatt.close();
                }
                isService=false;
                Log.i(TAG, "Disconnected from GATT server.");
                /**
                 * 非正常断开设备
                 */

                if((!MyCommandManager.deviceDisconnState)&&MyCommandManager.deviceAddress.equals(gatt.getDevice().getAddress())){
                    LogUtils.e("非正常断开设备");
                    new disconnNewConnTh().start();
                   // EventBus.getDefault().post(new ServiceMessageEvent("notfinddisvice"));
                } else {
                    LogUtils.e("正常断开设备");
                }
               // EventBus.getDefault().post(new MessageEvent("connect_fail"));
            }
        }
        /** 断连自动重连 */
        class disconnNewConnTh extends Thread {
            @Override
            public void run() {
                try {
                    boolean isContinue = true;
                    while(isContinue){
                        Thread.sleep(1000);
                        if(mBluetoothAdapter.isEnabled() ){
                            if ( mBluetoothDeviceAddress != null && MyCommandManager.deviceAddress.equals(mBluetoothDeviceAddress)) {
                                if (null != mBluetoothGatt) {
                                    mBluetoothGatt.close();
                                    mBluetoothGatt = null;
                                }
                             boolean isconnect=   connect(mBluetoothDeviceAddress);
                                if(isconnect){
                                    isContinue = false;
                                }else{
                                    isContinue = true;
                                }

                            }
                        }else{isContinue = true;}}
                } catch (InterruptedException e) {e.printStackTrace();}}
        }


        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            try {
             //  EventBus.getDefault().post(new MessageEvent("update_service_sussess"));
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    EventBus.getDefault().post(new MessageEvent("connect_success", gatt));
                    System.out.println("------------>发现服务成功");
                    MyCommandManager.deviceConnctState = true;
                    if("B15P".equals(SharedPreferencesUtils.readObject(MyApp.context,"mylanya").toString())){
                        startNotification(SampleGattAttributes.HEART_RATE_SERVER, SampleGattAttributes.CLIENT_CHARACTERISTIC_DATA);
                    }else{
                        startNotification(SampleGattAttributes.HEART_RATE_SERVERp, SampleGattAttributes.CLIENT_CHARACTERISTIC_DATAp);
                    }
                    EventBus.getDefault().post(new MessageEvent("update_data_service"));
                } else {
                    // Log.e("error", "没有发现可服务");
                    // 发现服务失败重新连接，可有可无
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                    connect(mBluetoothDeviceAddress);
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        /** 开启通知 */
        private void startNotification(String serviceUUID, String charaterUUID) {
            BluetoothGattCharacteristic c1 = getCharacter(serviceUUID, charaterUUID);
            if (c1 != null) {
                Log.e("taa", "获取到了通知");
                final int cx1 = c1.getProperties();
                if ((cx1 | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {// 如果有一个活跃的通知上的特点,明确第一所以不更新用户界面上的数据字段。
                    if (c1 != null) {
                        Log.e("error","设置0为false");
                        setCharacteristicNotification(c1, false);
                    }
                    readCharacteristic(serviceUUID, charaterUUID);
                    if ((cx1 | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        Log.e("error","继续清理1");
                        setCharacteristicNotification(c1, true);
                        isService=true;
                    }}}}

        /**
         * 获取特征值
         */
        public BluetoothGattCharacteristic getCharacter(String serviceUUID, String characterUUID) {
            // Log.e("error","设备名称："+mBluetoothGatt.getDevice().getAddress());
            if (mBluetoothGatt == null) {
                return null;
            }
            BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            if (service != null) {return service.getCharacteristic(UUID.fromString(characterUUID));}
            return null;
        }
        /** 读取特征值的Values */
        public void readCharacteristic(String service_uuid, String cha_uuid) {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                return;
            }
            BluetoothGattCharacteristic c1 = getCharacter(service_uuid, cha_uuid);
            if (c1 != null) {
                mBluetoothGatt.readCharacteristic(c1);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //手读取到值，在这里读数据
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.e(TAG,"-------b15p---"+Customdata.bytesToHexString(characteristic.getValue()));
           Log.e(TAG,"----b15P转换--"+Customdata.bytesToHexString(characteristic.getValue()));
            //环发送给APP的数据，(广播形式)
           // broadCast(ACTION_DATA, characteristic.getValue());
            //智能防丢
            final String[] da = new String[characteristic.getValue().length];
            for (int i = 0; i < characteristic.getValue().length; i++) {
                da[i] = byteToHex(characteristic.getValue()[i]);
             //  Log.e("TAG","---bbb----"+da[i]);
            }

            //EventBus.getDefault().post(new ResultMessageEvent("bluetooth_data", da));

/***************************b15s  b15h*******************************/
            /**
             * 电池电量
             */
            if (da[0].equals("ab") && da[4].equals("91")) {
                //返回B15S的电池电量
                //sendMeassage(batterylevel, String.valueOf(Integer.parseInt(da[7], 16) + ""));
                EventBus.getDefault().post(new MessageEvent("all_day_batterylevel", String.valueOf(Integer.parseInt(da[7], 16) + "")));
                MyLogUtil.e("---batterylevel->" + String.valueOf(Integer.parseInt(da[7], 16) + ""));

                /**
                 * 步数
                 */

            } else if (da[0].equals("ab") && da[4].equals("51") && da[5].equals("08")) {
                int step = Integer.parseInt(da[6] + da[7] + da[8], 16);
//                sendMeassage(ReadSteps, String.valueOf(step));
                EventBus.getDefault().post(new MessageEvent(""));
                MyLogUtil.e("---step->" + step);

                //卡里路
                int b15sKcal = Integer.parseInt(da[9] + da[10]+da[11],16);
                Log.e(TAG,"-------B15s卡里路---"+b15sKcal);
                VerifyUtil.sendEventBus(MyApp.getApplication(), step,b15sKcal);

                //搖一搖拍照
            } else if (da[0].equals("ab") && da[1].equals("00") && da[2].equals("04") && da[3].equals("ff") && da[4].equals("79")) {
                EventBus.getDefault().post(new MessageEvent("Shakethecamera"));
                //心率单次数据
            } else if (da[0].equals("ab") && da[2].equals("05") && da[3].equals("ff") && da[4].equals("31") && da[5].equals("0a")) {
                if (Integer.parseInt(da[6], 16) < 50) {
                    return;
                } else {
                    EventBus.getDefault().post(new MessageEvent(HeartRate, String.valueOf(Integer.parseInt(da[6], 16) + "")));
                   // sendMeassage(HeartRate, String.valueOf(Integer.parseInt(da[6], 16) + ""));
                }
                //一键测量通用模式
            } else if (da[0].equals("ab") && da[1].equals("00") && da[2].equals("07") && da[3].equals("ff") && da[4].equals("32")) {
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("heartrate", String.valueOf(Integer.parseInt(da[6], 16)));//心率的值
                    jSONObject.put("systolicpressure", String.valueOf(Integer.parseInt(da[8], 16)));//收缩压
                    jSONObject.put("diastolicpressure", String.valueOf(Integer.parseInt(da[9], 16)));//舒张压
                    jSONObject.put("oxygen", String.valueOf(Integer.parseInt(da[7], 16)));//血氧的值
                    EventBus.getDefault().post(new MessageEvent(XieYng, jSONObject));//血压的值

                    //sendMeassage(OnekeyMeasurement, String.valueOf(jSONObject));
                } catch (Exception E) {
                    E.printStackTrace();
                }
                /**
                 * 固件版本号
                 */
            } else if (da[0].equals("ab") && da[4].equals("92")) {
                EventBus.getDefault().post(new MessageEvent("all_day_Currentversionnumber2", String.valueOf(Integer.parseInt(da[6]) + (Double.parseDouble(da[7]) / 100))));
                /**
                 *  睡眠的活动量
                 */
            } else if (da[0].equals("ab") && da[4].equals("52")) {


                //b15s睡眠年月日
                String sleepyaer,sleepyue,sleepday,sleephour,sleepminte;

                try {

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                   if(String.valueOf(Integer.valueOf(da[6], 16)).length()==1){sleepyaer="0"+String.valueOf(Integer.valueOf(da[6], 16));}else{sleepyaer=String.valueOf(Integer.valueOf(da[6], 16));}
                    if(String.valueOf(Integer.valueOf(da[7], 16)).length()==1){sleepyue="0"+String.valueOf(Integer.valueOf(da[7], 16));}else{sleepyue=String.valueOf(Integer.valueOf(da[7], 16));}
                    if(String.valueOf(Integer.valueOf(da[8], 16)+1).length()==1){sleepday="0"+String.valueOf(Integer.valueOf(da[8], 16));}else{sleepday=String.valueOf(Integer.valueOf(da[8], 16));}
                    if(String.valueOf(Integer.valueOf(da[9], 16)).length()==1){sleephour="0"+String.valueOf(Integer.valueOf(da[9], 16));}else{sleephour=String.valueOf(Integer.valueOf(da[9], 16));}
                    if(String.valueOf(Integer.valueOf(da[10], 16)).length()==1){sleepminte="0"+String.valueOf(Integer.valueOf(da[10], 16));}else{sleepminte=String.valueOf(Integer.valueOf(da[10], 16));}
                    sleepyaer="20"+sleepyaer;
                    String date =sleepyaer + "-" + sleepyue + "-" + sleepday+ " " + sleephour + ":" + sleepminte;
                    //进入深睡时间
                    Sleeptime sleep = null;
                    if (da[11].equals("02")) {
                        sleep = new Sleeptime(1, date, Integer.parseInt(da[12] + da[13] + "", 16));
                    } else if (da[11].equals("01")) {
                        sleep = new Sleeptime(0, date, Integer.parseInt(da[12] + da[13] + "", 16));
                    }
                    sleeptimeList.add(sleep);
                    //定时发送数据
                    if(idfasong==false){
                        idfasong=true;
                        handlerb.postDelayed(runnableb, 13000L);
                    }

                        //sendMeassage(Sleep, sleeptimeList);

                } catch (Exception E) {
                    E.printStackTrace();
                }

                /**
                 * b15s的及时步数
                 */
            } else if (da[0].equals("ab") && da[4].equals("51") && da[5].equals("20")) {

                //年月日
                String yue, ri, ri2, hour;
                if (String.valueOf(cendar.get(Calendar.MONTH)).length() == 1) {
                    yue = "0" + String.valueOf(cendar.get(Calendar.MONTH) + 1);
                } else {
                    yue = String.valueOf(cendar.get(Calendar.MONTH) + 1);
                }//月
                if (String.valueOf(Integer.parseInt(String.valueOf(da[8]), 16)).length() == 1) {
                    ri = "0" + String.valueOf(Integer.parseInt(String.valueOf(da[8]), 16));
                } else {
                    ri = String.valueOf(Integer.parseInt(String.valueOf(da[8]), 16));
                }//日
                if (String.valueOf(cendar.get(Calendar.DAY_OF_MONTH)).length() == 1) {
                    ri2 = "0" + String.valueOf(cendar.get(Calendar.DAY_OF_MONTH));
                } else {
                    ri2 = String.valueOf(cendar.get(Calendar.DAY_OF_MONTH));
                }//日
                if (ri .equals(ri2)) {
                    if (Integer.valueOf(String.valueOf(jinru)) < 0) {

                    } else {
                        if (String.valueOf(Integer.parseInt(String.valueOf(da[9]), 16)).length() == 1) {
                            hour = "0" + String.valueOf(Integer.parseInt(String.valueOf(da[9]), 16));
                        } else {
                            hour = String.valueOf(Integer.parseInt(String.valueOf(da[9]), 16));
                        }//小时
                        try {
                             map = new HashMap<String, Object>();

                            if (Integer.valueOf(String.valueOf(jinru)) <0) {
                                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
                                Date d1=new Date(time);
                                String t1=format.format(d1);
                                map.put("date",t1);
                                map.put("stepNumber", Math.abs(jinru));
                                map.put("calorie", String.valueOf(Integer.parseInt(da[13] + da[14] + da[15], 16)));//卡路里
                                map.put("heartRate", Integer.parseInt(da[16], 16));//心率
                                map.put("oxygen", Integer.parseInt(da[17], 16));//血氧
                                map.put("systolic", Integer.parseInt(da[18], 16));//收缩压
                                map.put("diastolic", Integer.parseInt(da[19], 16));//舒张压
                            }else{
                                if(alldayActivity.size()>1&&!hour.equals("00")){
                                    map.put("stepNumber", jinru);
                                    SimpleDateFormat format=new SimpleDateFormat("HH");
                                    long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
                                    Date d1=new Date(time);
                                    String t1=format.format(d1);
                                    map.put("date", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + yue + "-" + ri + " " + t1 + ":" + "00");
                                    jinru = (Integer.parseInt(da[10] + da[11] + da[12], 16) - zhuangtaizhi);//步数
                                    map.put("calorie", String.valueOf(Integer.parseInt(da[13] + da[14] + da[15], 16)));//卡路里
                                    map.put("heartRate", Integer.parseInt(da[16], 16));//心率
                                    map.put("oxygen", Integer.parseInt(da[17], 16));//血氧
                                    map.put("systolic", Integer.parseInt(da[18], 16));//收缩压
                                    map.put("diastolic", Integer.parseInt(da[19], 16));//舒张压
                                    zhuangtaizhi = Integer.parseInt(da[10] + da[11] + da[12], 16);
                                }else{
                                    map.put("stepNumber", jinru);
                                    map.put("date", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + yue + "-" + ri + " " + hour + ":" + "00");
                                    jinru = (Integer.parseInt(da[10] + da[11] + da[12], 16) - zhuangtaizhi);//步数
                                    map.put("calorie", String.valueOf(Integer.parseInt(da[13] + da[14] + da[15], 16)));//卡路里
                                    map.put("heartRate", Integer.parseInt(da[16], 16));//心率
                                    map.put("oxygen", Integer.parseInt(da[17], 16));//血氧
                                    map.put("systolic", Integer.parseInt(da[18], 16));//收缩压
                                    map.put("diastolic", Integer.parseInt(da[19], 16));//舒张压
                                    zhuangtaizhi = Integer.parseInt(da[10] + da[11] + da[12], 16);
                                }


                            }


                                MyLogUtil.i("-b15PSleepHeartRateStepBean-" + (int) map.get("systolic") + "-diastolic->" + (int) map.get("diastolic") + "-stepNumber->" + (int) map.get("stepNumber")
                                        + "-date->" + (String) map.get("date") + "-heartRate->" + (int) map.get("heartRate") + "-oxygen->" + (int) map.get("oxygen"));
                                B15PSleepHeartRateStepBean b15PSleepHeartRateStepBean = new B15PSleepHeartRateStepBean(
                                        (int) map.get("systolic"), (int) map.get("diastolic"), (int) map.get("stepNumber"), (String) map.get("date"), (int) map.get("heartRate"),
                                        (int) map.get("oxygen"), 0, Common.customer_id, MyCommandManager.ADDRESS);
                                //s数据发完了
                                alldayActivity.add(b15PSleepHeartRateStepBean);
                                //计时发送
                            if (isSendSteps == false) {
                                isSendSteps = true;
                                if (null == timer) {
                                    timer = new Timer();
                                }
                                if (null == task) {
                                    task = new TimerTask() {
                                        @Override
                                        public void run() {
                                            Message msg = new Message();
                                            msg.what = 8888;
                                            myhandler.sendMessage(msg);
                                        }
                                    };
                                }
                                timer.schedule(task, 0, 15000);//15秒吧
                            }


                        } catch (Exception E) {
                            E.printStackTrace();
                        }
                    }
                }



            }


/***************************b15p*******************************/
            else if (da[0].equals("a0")) {/**b15p的电池电量**/
               // sendMeassage(batterylevel, String.valueOf(huansuan(Integer.parseInt(da[2], 16))));
                EventBus.getDefault().post(new MessageEvent("all_day_batterylevel", String.valueOf(huansuan(Integer.parseInt(da[2], 16)))));

            } else if (da[0].equals("a8")) { /**步数**/
                int step = Integer.parseInt(da[1] + da[2] + da[3] + da[4], 16);
                VerifyUtil.sendEventBus(MyApp.getApplication(), step,0);
                MyLogUtil.i("-step->" + step);
                /**拍照
                 *
                 */
            } else if (da[0].equals("b6") && da[1].equals("01") && da[2].equals("02") && da[3].equals("00") && da[4].equals("00")) {
                EventBus.getDefault().post(new MessageEvent("Shakethecamera"));
                /**心率
                 *
                 */
            } else if (da[0].equals("d0")) {
                if (Integer.parseInt(da[1], 16) < 50) {
                    return;
                } else {
                    EventBus.getDefault().post(new MessageEvent(HeartRate, String.valueOf(Integer.parseInt(da[1], 16) + "")));
                    //sendMeassage(HeartRate, String.valueOf(Integer.parseInt(da[1], 16) + ""));
                }
                /**一键测量通用模式
                 *
                 */
            } else if (da[0].equals("90")) {
                if (Integer.parseInt(da[1], 16) < 50 || Integer.parseInt(da[2], 16) < 50) {
                    return;
                } else {
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("systolicpressure", String.valueOf(Integer.parseInt(da[1], 16)));//收缩压
                        jSONObject.put("diastolicpressure", String.valueOf(Integer.parseInt(da[2], 16)));//舒张压
                        //sendMeassage(OnekeyMeasurement, String.valueOf(jSONObject));
                        EventBus.getDefault().post(new MessageEvent(XieYa,jSONObject));//血压的值
                        MyLogUtil.i("-batteeeffff>" + String.valueOf(jSONObject));
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
                //是血压(私人模式)
            }  else if(da[0].equals("91")){
                String aaaf=da[1];
                System.out.print("aaaf"+aaaf+"-"+da[4]);
                if(da[4].equals("00")){
                    EventBus.getDefault().post(new MessageEvent("updatamsg"));
                }
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("systolicpressure", String.valueOf(Integer.parseInt(da[1], 16)));//收缩压
                    jSONObject.put("diastolicpressure", String.valueOf(Integer.parseInt(da[4], 16)));//舒张压
                    //sendMeassage(OnekeyMeasurement, String.valueOf(jSONObject));
                    EventBus.getDefault().post(new MessageEvent(OnekeyMeasurementsiren,jSONObject));//血压的值
                    MyLogUtil.i("-batteeeffff>" + String.valueOf(jSONObject));
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }

            /**查找手机中
             *
             */

            else if (da[0].equals("b5") && da[1].equals("00") && da[2].equals("00") && da[3].equals("00")) {
              //  sendMeassage(Findphone, "Findphone");
                EventBus.getDefault().post(new MessageEvent("Findphone"));
                /**
                 * 当前的手环版本号
                 *
                 */
            } else if (da[0].equals("a2")) {
               // sendMeassage(Currentversionnumber, String.valueOf("00" + da[1] + "." + da[2] + "00"));
                EventBus.getDefault().post(new MessageEvent("all_day_Currentversionnumber", String.valueOf("00" + da[1] + "." + da[2] + "00")));
                /**活动量
                 *
                 */
            } else if (da[0].equals("d1")) {
                //得到当前的包号两个字节
                pakege=da[1];//前段
                pakege2=da[2];//后端
                JSONObject map=new JSONObject();
                String aa = String.valueOf(Integer.parseInt(da[10] + da[11], 16));//步数
                String HATA = String.valueOf(Integer.parseInt(String.valueOf(da[14]), 16));  //心率
                if(0>=Integer.valueOf(aa)&&20>Integer.valueOf(HATA)){

                }else{
                    String yues;
                    if(String.valueOf(Integer.valueOf(cendar.get(Calendar.MONTH)+1)).length()==1){yues="0"+String.valueOf(Integer.valueOf(cendar.get(Calendar.MONTH)+1));}else{yues=String.valueOf(Integer.valueOf(cendar.get(Calendar.MONTH)+1));}
                    String myday = String.valueOf(Integer.valueOf(String.valueOf(da[7]),16));
                    String hour = String.valueOf(Integer.valueOf(String.valueOf(da[8]),16));
                    String minte = String.valueOf(Integer.valueOf(String.valueOf(da[9]),16));
                    if(minte.length()==1){minte="0"+minte;}
                    if(myday.length()==1){myday="0"+myday;}
                    if(hour.length()==1){hour="0"+hour;}
                    String mydat=String.valueOf(cendar.get(Calendar.YEAR))+"-"+yues+"-"+myday+" "+hour+":"+minte+":"+"00";
                    System.out.println("mydat"+mydat);
                    //查询登录标记
                    try{
                        map.put("date",mydat);
                        //查询登录标记
                        SharedPreferences denglu=  MyApp.context.getSharedPreferences("userId", Activity.MODE_PRIVATE);
                        if(null!=denglu){
                            map.put("userId",denglu.getString("userId", ""));
                        }
                        if(null!= SharedPreferencesUtils.readObject(MyApp.context,"mylanya")&&null!=SharedPreferencesUtils.readObject(MyApp.context,"mylanmac")) {
                            map.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.context,"mylanmac").toString());
                        }
                        if((Integer.parseInt(String.valueOf(Integer.parseInt(String.valueOf(da[15]),16)))<0)){
                            map.put("systolic",Math.abs(Integer.parseInt(String.valueOf(Integer.parseInt(String.valueOf(da[15]),16))) ));
                        }else{
                            map.put("systolic", Integer.parseInt(String.valueOf(da[15]),16));
                        }
                        if(Integer.parseInt(String.valueOf(da[16]))<0){
                            map.put("diastolic",Math.abs(Integer.parseInt(String.valueOf(Integer.parseInt(String.valueOf(da[16]),16))) ));
                        }else{
                            map.put("diastolic",Integer.parseInt(String.valueOf(da[16]),16));
                        }
                        if(Integer.valueOf(aa)<0){
                            map.put("stepNumber",Math.abs(Integer.parseInt(aa)));
                        }else{
                            map.put("stepNumber",Integer.parseInt(aa));
                        }
                        map.put("heartRate",Integer.valueOf(HATA));
                        map.put("status",0);
                        Date formatDate = sdateFormat.parse((String) map.get("date"));
                        String StrDate = sdateFormat.format(formatDate);
                        B15PSleepHeartRateStepBean b15PSleepHeartRateStepBean = new B15PSleepHeartRateStepBean(
                                (int) map.get("systolic"),
                                (int) map.get("diastolic"),
                                (int) map.get("stepNumber"),
                                StrDate, (int) map.get("heartRate"),
                                0,
                                0, Common.customer_id,
                                MyCommandManager.ADDRESS);
                        alldayActivity.add(b15PSleepHeartRateStepBean);
                    }catch (Exception e){e.printStackTrace();}
                }
                if(String.valueOf(da[1]).equals(String.valueOf(da[3]))&&String.valueOf(da[2]).equals(String.valueOf(da[4]))){
                    try{
                        if(null!=jsonArray){
                            if(jsonArray instanceof JSONArray){
                                //这里保存数据的包号
                                if(!"".equals(pakege)){
                                    SharedPreferencesUtils.saveObject(MyApp.getApplication(),"mypakegeone",pakege);
                                    SharedPreferencesUtils.saveObject(MyApp.getApplication(),"mypakegetwo",pakege2);
                                }
                                EventBus.getDefault().post(new MessageEvent("all_day_acyivity", alldayActivity));
                            }
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                /**睡眠数据
                 *
                 */
            } else if (da[0].equals("e0")) {

                try {
                    if (da[1].equals("05")) {
                        array = Arrays.copyOfRange(da, 4, 20);
                        //转换10 开始-结束（月日时分）
                        String startyue = String.valueOf(Integer.parseInt(da[5].toString(), 16) >= 10 ? Integer.parseInt(da[5].toString(), 16) : "0" + Integer.parseInt(da[5].toString(), 16));
                        String startri = String.valueOf(Integer.parseInt(da[6].toString(), 16) >= 10 ? Integer.parseInt(da[6].toString(), 16) : "0" + Integer.parseInt(da[6].toString(), 16));
                        String startshi = String.valueOf(Integer.parseInt(da[7].toString(), 16) >= 10 ? Integer.parseInt(da[7].toString(), 16) : "0" + Integer.parseInt(da[7].toString(), 16));
                        String startfen = String.valueOf(Integer.parseInt(da[8].toString(), 16) >= 10 ? Integer.parseInt(da[8].toString(), 16) : "0" + Integer.parseInt(da[8].toString(), 16));
                        String aaa = String.valueOf(Integer.parseInt(da[13].toString(), 16) >= 10 ? Integer.parseInt(da[13].toString(), 16) : "0" + Integer.parseInt(da[13].toString(), 16));//深睡眠
                        String bbb = String.valueOf(Integer.parseInt(da[14].toString(), 16) >= 10 ? Integer.parseInt(da[14].toString(), 16) : "0" + Integer.parseInt(da[14].toString(), 16));//浅睡眠
                        String ccc = String.valueOf(Integer.parseInt(da[15].toString(), 16) >= 10 ? Integer.parseInt(da[15].toString(), 16) : "0" + Integer.parseInt(da[15].toString(), 16));//睡眠质量 1-5等级
                        if (Integer.valueOf(startshi) == 00) {//是不是十二点睡觉
                            jieshuhour = String.valueOf(Integer.valueOf(12) + ((Integer.valueOf(aaa) * 5 + Integer.valueOf(bbb) * 5) / 60));//结束小时
                        } else {
                            jieshuhour = String.valueOf(Integer.valueOf(startshi) + ((Integer.valueOf(aaa) * 5 + Integer.valueOf(bbb) * 5) / 60));//结束小时
                        }
                        if (Integer.valueOf(jieshuhour) > 12) {
                            dateb = String.valueOf((Integer.valueOf(jieshuhour) - 12));
                            if (dateb.length() == 1) {
                                dateb = "0" + dateb;
                            }
                        }else{
                            if (jieshuhour.length() == 1) {
                                dateb = "0" + jieshuhour;
                            }else{
                                dateb= jieshuhour;
                            }

                        }
                        String jieshuminte = String.valueOf((Integer.valueOf(aaa) + Integer.valueOf(bbb) + Integer.valueOf(startfen)) % 60);
                        if (jieshuminte.length() == 1) {
                            jieshuminte = 0 + jieshuminte;
                        }
                        //开始时间
                        HashMap<String, Object> map = new HashMap<>();
                        //SimpleDateFormat df = new SimpleDateFormat("HH");//12小时制

                        if (Integer.valueOf(startshi) == 0) {
                            map.put("startTime", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + startyue + "-" + String.valueOf(Integer.valueOf(startri) - 1) + " " + "00" + ":" + startfen);
                        } else {
                            String date = startshi;
                            if (TextUtils.isEmpty(startshi) | "null".equals(startshi)) {
                                date = "00";
                            }

                            if (startri.equals(31)) {
                                map.put("startTime", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + startyue + "-" + "31" + " " + date + ":" + startfen);

                            } else {
                                if (Integer.valueOf(startshi) <= 12) {
                                    if(String.valueOf(Integer.valueOf(startri) - 1).length()==1){
                                        RI="0"+String.valueOf(Integer.valueOf(startri));
                                    }else{RI=String.valueOf(Integer.valueOf(startri));}
                                } else {
                                    if(String.valueOf(Integer.valueOf(startri)).length()==1){
                                        RI="0"+String.valueOf(Integer.valueOf(startri));
                                    }else{
                                        RI=String.valueOf(Integer.valueOf(startri));
                                    }}
                                map.put("startTime", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + startyue + "-" + RI + " " + date + ":" + startfen);

                            }
                        }
                        if (TextUtils.isEmpty(dateb)) {
                            dateb = "00";
                        }
                        if (startri.equals(31)) {
                            map.put("endTime", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + startyue + "-" + "01" + " " + dateb + ":" + jieshuminte);
                        } else {
                            //判断睡眠时间是否超过当前的起始时间

                            String date = startshi;
                            if (TextUtils.isEmpty(startshi) | "null".equals(startshi)) {
                                date = "00";
                            }
                                if("00".equals(date)||Integer.valueOf(startshi) <= 12){
                                    if(String.valueOf(Integer.valueOf(startri)).length()==1){
                                        sleepRI="0"+String.valueOf(Integer.valueOf(startri));
                                    }else{
                                        sleepRI=String.valueOf(Integer.valueOf(startri));
                                    }
                                }else{
                                    if(String.valueOf(Integer.valueOf(startri)+1).length()==1){
                                        sleepRI="0"+String.valueOf(Integer.valueOf(startri)+1);
                                    }else{
                                        sleepRI=String.valueOf(Integer.valueOf(startri)+1);
                                    }
                                }

                            map.put("endTime", String.valueOf(cendar.get(Calendar.YEAR)) + "-" + startyue + "-" +sleepRI + " " + dateb + ":" + jieshuminte);
                        }
                        map.put("Deepsleep", Integer.valueOf(aaa) * 5);//深睡分钟
                        map.put("Lightsleep", Integer.valueOf(bbb) * 5);//浅睡分钟
                        map.put("sleepquality", Integer.valueOf(ccc));//睡眠质量
                       B15PSleepBean b15PSleepBean = new B15PSleepBean((String) map.get("startTime"), (String) map.get("endTime"), 0, (int) map.get("Deepsleep"), (int) map.get("Lightsleep"), (int) map.get("sleepquality"));
                        //根据结束时间来排除重复上传
                         List<B15PSleepBean> list = MyApp.getApplication().getDaoSession().getB15PSleepBeanDao().queryBuilder().
                                where(B15PSleepBeanDao.Properties.DeviceCode.eq(MyCommandManager.ADDRESS)
                                        , B15PSleepBeanDao.Properties.UserId.eq(Common.customer_id)).orderAsc(B15PSleepBeanDao.Properties.EndTime).list();
                        EventBus.getDefault().post(new MessageEvent("sleep15p_data_service", b15PSleepBean));
                    } else if (da[1].equals("04")) {
                        array1 = Arrays.copyOfRange(da, 4, 20);
                    } else if (da[1].equals("03")) {
                        array2 = Arrays.copyOfRange(da, 4, 20);
                    } else if (da[1].equals("02")) {
                        array3 = Arrays.copyOfRange(da, 4, 20);
                    } else if (da[1].equals("01")) {
                        array4 = Arrays.copyOfRange(da, 4, 20);
                    } else if (da[1].equals("00")) {
                        array5 = Arrays.copyOfRange(da, 4, 20);
                        //得到睡眠的数据后解析数据
                        String[] myshuju = new String[]{};
                        myshuju = Customdata.concatAll(array, array1, array2, array3, array4, array5);
                        if(null!=myshuju[0]){
                        if ("a1".equals(myshuju[0])) {//返回这个是正确的
                            //这段是睡眠曲线图
                            String[] shumian = Arrays.copyOfRange(myshuju, 12, 42);
                            StringBuilder sb = new StringBuilder();//睡眠2进制数
                            for (int i = 0; i < shumian.length; i++) {
                                sb.append(Customdata.hexString2binaryString(shumian[i]));
                            }
                            ArrayList<SumBean> sumBeanArrayList = Common.getSleepSumList(sb.toString());
                            EventBus.getDefault().post(new MessageEvent("sleep15p_data_curve", new SleepCurveBean(sumBeanArrayList, sb.toString())));
                        }
                    }else{
                            EventBus.getDefault().post(new MessageEvent("sleep15p_data_curve", "0"));
                        }
                    }
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        }

       /* //发送广播的方法
        public void sendMeassage(String key, String mess) {
            Intent intet = new Intent(key);
            intet.putExtra(key, mess);
            sendBroadcast(intet);
        }

        //发送广播的方法
        public void sendMeassage(String key, Object object) {
            Intent intet = new Intent(key);
            intet.putExtra(key, (Serializable) object);
            sendBroadcast(intet);
        }
*/


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            //gatt.writeCharacteristic 的回调
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("-------->onCharacteristicWrite");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorWrite(gatt, descriptor, status);
            // mBluetoothGatt.writeDescriptor  的回调
            System.out.println("-------->onDescriptorWrite");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            super.onReadRemoteRssi(gatt, rssi, status);
            //读取信号强度
        }


    };

    //蓝牙是否连接
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    private void broadCast(String action,byte[] data){
        Intent intet = new Intent(action);
        intet.putExtra("data", data);
        sendBroadcast(intet);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()) || UUID_from_b15s.equals(characteristic.getUuid())) {
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
        } else {
            // For all other profiles, writes the data formatted in HEX.对于所有的文件，写入十六进制格式的文件
            //这里读取到数据
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    //以十六进制的形式输出
                    stringBuilder.append(String.format("%02X ", byteChar));
                // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                intent.putExtra(EXTRA_DATA, new String(data));
            }
        }
        //发送广播，广播接收器在哪？？
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular zook, close() is
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

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        //注册向蓝牙写入数据的广播
        initialize();
        coonected();
    }

    @Override
    public void onDestroy() {
        disconnect();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    public   boolean coonected(){
       try{
           String deviceaddress = (String) SharedPreferencesUtils.readObject(this, "mylanmac");
           if (!TextUtils.isEmpty(deviceaddress)) {
               boolean isconnec = connect(deviceaddress);
               return isconnec;
           } else {
            boolean isconnec=   connect(MyCommandManager.ADDRESS);
               return isconnec;
           }
       }catch (Exception E){E.printStackTrace();}
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEventMainThread(ServiceMessageEvent event) {

        String send_tag = event.getMessage();
        MyLogUtil.i("send_tag==>>" + send_tag);
        byte[] data_byte = (byte[]) event.getObject();
        if (send_tag.contains("send_data")) {
            boolean result =MyApp.getmBluetoothLeService(). writeRXCharacteristic(data_byte);
            EventBus.getDefault().post(new MessageEvent(send_tag.substring(send_tag.lastIndexOf("_") + 1), result));
        }else if (send_tag.equals("messagereminder")) {
            writeRXCharacteristic(data_byte);
        }else if("Bingdingshouhuan".equals(event.getMessage())){
            if(null!= SharedPreferencesUtils.readObject(MyApp.context,"mylanmac")){
                System.out.print("连接新设备");
                connect(SharedPreferencesUtils.readObject(MyApp.context,"mylanmac").toString());//连接蓝牙0,1为断开
            }

        }
    }


    // 连接设备
    public boolean connect(String address) {
        MyCommandManager.deviceAddress = address;
        mBluetoothDeviceAddress = "";
        initialize();
        if (!mBluetoothAdapter.isEnabled()) {
            return false;
        }
        if (MyCommandManager.deviceConnctState) {
            if (mBluetoothGatt != null) {
                // 断开连接
                Log.e("error", "断开连接");
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
            }
        }
        // 有为空的情况就直接返回
        if (mBluetoothAdapter == null || address == null) {
            // Log.e("error","BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // 以前连接设备。尝试重新连接。
        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            // Log.e("error","Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        // Log.e("error","运行到了这一步");
        // 通过设备地址获取设备
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address.toUpperCase().trim());
        if (device == null) {
            // Log.e("taa", "Device not found.  Unable to connect.");

            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        // Log.e("error", "device.getBondState==" + device.getBondState());
        mBluetoothDeviceAddress = address;
        return true;
    }
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
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

    //写入數據到指定的characteristic
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    boolean issend;
    public synchronized boolean writeRXCharacteristic(byte[] bb){
        if(!MyCommandManager.deviceConnctState){
            return false;
        }
        if(null!=SharedPreferencesUtils.readObject(MyApp.context, "mylanya")){
            if("B15P".equals((String) SharedPreferencesUtils.readObject(MyApp.context, "mylanya"))){
                issend=     writeLlsAlertLevel(SampleGattAttributes.HEART_RATE_SERVER,SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG,bb);
            }else{
                issend=    writeLlsAlertLevel(SampleGattAttributes.HEART_RATE_SERVERp,SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIGp,bb);
            }
        }
        return issend;
    }
    public synchronized boolean writeLlsAlertLevel(String service_uuid, String cha_uuid, byte[] bb) {
        StringBuffer b = new StringBuffer();
        for(int i=0;i<bb.length;i++){
            b.append(String.format("%02X ", bb[i]).toString().trim());
        }
        BluetoothGattService linkLossService;
        BluetoothGattCharacteristic alertLevel = getCharacter(service_uuid, cha_uuid);
        if (alertLevel == null) {
            Log.e("error","link loss Alert Level charateristic not found!");
            return false;
        }
        boolean status = false;
        int storedLevel = alertLevel.getWriteType();
        alertLevel.setValue(bb);
        alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        status = mBluetoothGatt.writeCharacteristic(alertLevel);
//		 Log.e("json","status"+status);
        return status;
    }


    /**
     * 获取特征值
     */
    public BluetoothGattCharacteristic getCharacter(String serviceUUID, String characterUUID) {
        // Log.e("error","设备名称："+mBluetoothGatt.getDevice().getAddress());
        if (mBluetoothGatt == null) {
            return null;
        }
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (service != null) {
            return service.getCharacteristic(UUID.fromString(characterUUID));
        }
        return null;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    //使指定的 UUID 能收到數據
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        // if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.DESC));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        //  }
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


    //找到指定的服務
    public BluetoothGattService getSupportedGattServices(UUID uuid) {
        BluetoothGattService mBluetoothGattService;
        if (mBluetoothGatt == null) return null;
        mBluetoothGattService = mBluetoothGatt.getService(uuid);
        return mBluetoothGattService;
    }


}
