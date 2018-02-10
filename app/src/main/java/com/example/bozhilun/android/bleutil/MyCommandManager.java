package com.example.bozhilun.android.bleutil;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.event.AlarmClock;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.MyLogUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.example.bozhilun.android.bleutil.Customdata.addBytes;

/**
 * Created by thinkpad on 2017/3/19.
 */

public class MyCommandManager {


    /** 设备连接状态 */
    public static boolean deviceConnctState = false;
    /** 是否手动断开连接若意外断开连接重连 true为正常断开，flase为非正常断开 */
    public static boolean deviceDisconnState = false;
    public static BluetoothDevice timeDevice = null;
    public static String deviceAddress = "";


    public static UUID UUID_SERVICE;
    public static UUID UUID_WRITE;
    public static UUID UUID_READ;
    public static String DESC;

    public static String DEVICENAME = null;
    public static String ADDRESS;
    public static int CONNECTIONSTATE = 0;
    private static String yue, ri;
    private static String qian, HOU;
    /**
     * 同步个人信息
     * lanyaneme蓝牙名字
     * id         0开1关
     */
    public static void SynchronousPersonalInformation(Map<String, Object> map, int isopen, int isKm) {
        byte[] data;
        try {
            String lanyaneme = (String) map.get("lanyaneme");//蓝牙名字
            String Age = (String) map.get("age");//年龄
            String Height = (String) map.get("height");//身高
            String Weight = (String) map.get("wight");//体重
            String systolicpressure = (String) map.get("systolicpressure");//收缩压
            String diastolicpressure = (String) map.get("diastolicpressure");//舒张压
            //15s
            int step = (int) map.get("step");//step 必须大于=10000
            if ("B15P".equals(lanyaneme)) {
                String stepByte = String.valueOf(Customdata.hexString2binaryString(String.valueOf(step)));//解析成二进制
                //data = new byte[]{(byte) 0xa3, Integer.valueOf(Height).byteValue(), Integer.valueOf(Weight).byteValue(), Integer.valueOf(Age).byteValue(), (byte) isopen, Integer.valueOf(stepByte.substring(0, 4)).byteValue(), Integer.valueOf(stepByte.substring(4, 8)).byteValue()};
                data = new byte[]{(byte) 0xa3, Integer.valueOf(Height).byteValue(), Integer.valueOf(Weight).byteValue(), Integer.valueOf(Age).byteValue(), (byte) isopen, WatchUtils.hiUint16((short) step), WatchUtils.loUint16((short) step)};


            } else {
                data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x10, (byte) 0xff, (byte) 0x74, (byte) 0x80, (byte) isopen, Integer.valueOf(Age).byteValue(), Integer.valueOf(Height).byteValue()
                        , Integer.valueOf(systolicpressure).byteValue(), Integer.valueOf(diastolicpressure).byteValue(), (byte) isKm};
            }
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
           // EventBus.getDefault().post(new ServiceMessageEvent("send_data_synchronouspersonalinformation", data));
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    /**
     * 断开B15P手环
     */
    public static void disconnB15P(String bleName){
        byte[] bytes = new byte[2];
        if("B15P".equals(bleName)){
            bytes = new byte[]{(byte)0xaf,(byte)0x00};
        }
        MyApp.getmBluetoothLeService().writeRXCharacteristic(bytes);
    }

    /**
     * 抬手亮屏
     * lanyaneme蓝牙名字
     * id         0开1关
     */
    public static void Raisethebrightscreen(String lanyaneme, int isopen) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0x01, (byte) isopen};
            //下面是b15s的抬手亮屏
        } else {
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x77, (byte) 0x80, (byte) isopen};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_raisethebrightscreen", data));
    }

    /**
     * 智能防丢
     * lanyaneme蓝牙名字
     * id         0开1关
     */
    public static void Intelligentantilost(String lanyaneme, int id) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xae, (byte) id};
            //下面是b15s的智能防丢
        } else {
            if(id==0){id=1;}else{id=0;}//要取反
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x7a, (byte) 0x80, (byte) id};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_intelligentantilost", data));
    }

    /**
     * 电池电量
     * lanyaneme蓝牙名字
     * id
     */
    public static void Batterylevel(String lanyaneme) {
        byte[] data;

        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xa0,(byte) 0x00};
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
            //下面是b15s的电池电量
        } else {
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0xff, (byte) 0xff, (byte) 0x91, (byte) 0x80};
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
        }



     //   EventBus.getDefault().post(new ServiceMessageEvent("send_data_batterylevel", data));
    }


    /**
     * 闹钟提醒
     * <p>
     * lanyaneme蓝牙名字
     * BeginTime 开始小时1 2 3
     * EndTime      结束小时1
     * 三个闹钟
     * id 0开1关
     * <p>
     * 注意 b15p必须传所有的参数（10个值）
     * b15s 只需要 前三个参数 其他参数可为任何数
     */
    public static void Alarmclockb15p(Map<String, Object> map) {
        byte[] data;
        try {
            String lanyaneme = (String) map.get("lanyaneme");
            String BeginTime = (String) map.get("BeginHour");
            String EndTime = (String) map.get("Beginminte");
            int id = (int) map.get("id");
            if ("B15P".equals(lanyaneme)) {
                String BeginTime2 = (String) map.get("BeginHour2");
                String EndTime2 = (String) map.get("Beginminte2");
                int id2 = (int) map.get("id2");
                String BeginTime3 = (String) map.get("BeginHour3");
                String EndTime3 = (String) map.get("Beginminte3");
                int id3 = (int) map.get("id3");
                data = new byte[]{(byte) 0xAB, Integer.valueOf(BeginTime).byteValue(), Integer.valueOf(EndTime).byteValue(), (byte) id, Integer.valueOf(BeginTime2).byteValue()
                        , Integer.valueOf(EndTime2).byteValue(), (byte) id2, Integer.valueOf(BeginTime3).byteValue(), Integer.valueOf(EndTime3).byteValue(), (byte) id3, (byte) 0x01};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data_alarmclock", data));

               /* //下面是b15s的闹钟提醒  最多八个闹钟*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void Alarmclockb15s(byte  a,byte b,int count,int id) {
        try {
            byte[] data = new byte[11];
            data[0] = (byte) 0xAB;
            data[1] = (byte) 0;
            data[2] = (byte)8;
            //数据id + status 共 3 bytes
            data[3] = (byte) 0xff;
            data[4] = (byte) 0x73;
            data[5] = (byte)0x80;
            //数据值
            data[6] = (byte) count;//第几个闹钟
            if(id==1){
                data[7] = (byte) 01;
            }else{
                data[7] = (byte) 00;
            }
            data[8] =  a;
            data[9] =  b;
            data[10] = (byte)0xff;
            EventBus.getDefault().post(new ServiceMessageEvent("send_data_alarmclock", data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 久坐提醒（B15H不带这个功能）
     * lanyaneme蓝牙名字
     * id 0开 1关
     * BeginTime 开始小时
     * Beginminte 开始分钟
     * EndTime 结束小时
     * Endminte 结束分钟
     * <p>
     * TimeInterval 时间间隔(分钟)b15s的话没有
     */
    public static void Sedentaryreminder(Map<String, Object> map) {
        byte[] data;
        try {
            int id = (int) map.get("id");
            String lanyaneme = (String) map.get("lanyaneme");
            String BeginTime = (String) map.get("BeginTime");//HH:mm
            String Beginminte = (String) map.get("Beginminte");//HH:mm
            String EndTime = (String) map.get("EndTime");
            String Endminte = (String) map.get("Endminte");
            String TimeInterval = (String) map.get("TimeInterval"); //30 分钟 60分钟
            if ("B15P".equals(lanyaneme)) {
                data = new byte[]{(byte) 0xe1, Integer.valueOf(BeginTime).byteValue(), Integer.valueOf(Beginminte).byteValue()
                        , Integer.valueOf(EndTime).byteValue(), Integer.valueOf(Endminte).byteValue(), Integer.valueOf(TimeInterval).byteValue(), (byte) id};
                //下面是b15s的久坐提醒
            } else {
                data = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x08, (byte) 0xFF, (byte) 0x75, (byte) 0x80, (byte) id
                        , Integer.valueOf(BeginTime).byteValue(), Integer.valueOf(Beginminte).byteValue(), Integer.valueOf(EndTime).byteValue()
                        , Integer.valueOf(Endminte).byteValue()};
            }
            EventBus.getDefault().post(new ServiceMessageEvent("send_data_sedentaryreminder", data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NewAlock(String Bluename, AlarmClock ac, boolean onoff){
        byte [] dates ;
        int isOnOff;
        if(onoff == true){
            isOnOff = 1;
        }else{
            isOnOff = 0;
        }
        if ("B15P".equals(Bluename)) {
//            dates = new byte[]{(byte) 0xAB, Integer.valueOf(ac.getHour()).byteValue(), Integer.valueOf(ac.getMinute()).byteValue(), (byte) isOnOff, Integer.valueOf(22).byteValue()
//                    , Integer.valueOf(22).byteValue(), (byte) 0x02, Integer.valueOf(22).byteValue(), Integer.valueOf(22).byteValue(), (byte) 0x00, (byte) 0x01};
            dates = new byte[]{(byte)0xab,Integer.valueOf(ac.getHour()).byteValue(),Integer.valueOf(ac.getMinute()).byteValue(),
                    (byte) 0x01,Integer.valueOf(ac.getHour()).byteValue(),Integer.valueOf(ac.getMinute()).byteValue(),(byte) 0x01,Integer.valueOf(ac.getHour()).byteValue(),Integer.valueOf(ac.getMinute()).byteValue(),(byte)0x01,(byte)0x01};
            Log.e("Alarms","----设置闹钟----"+Arrays.toString(dates)+"--"+Customdata.bytesToHexString(dates));

          //  dates = new byte[]{(byte)0xb9,(byte)0x01,(byte)0x14,Integer.valueOf(ac.getHour()).byteValue(),Integer.valueOf(ac.getMinute()).byteValue(),(byte)0x01,(byte)0x01,(byte)0x00,(byte)0xE0,(byte)0x07,(byte)0x09,(byte)0x18};



        } else {
            dates = new byte []{(byte)0xab, (byte)0x00, (byte)0x08, (byte)0xff, (byte)0x73, (byte)0x80, (byte)(ac.getAlock_id()), (byte) isOnOff,
                    Integer.valueOf(ac.getHour()).byteValue(), Integer.valueOf(ac.getMinute()).byteValue(),Integer.valueOf(ac.getShiliu()).byteValue()};
        }
       // EventBus.getDefault().post(new ServiceMessageEvent("send_data_alock", dates));
        MyApp.getmBluetoothLeService().writeRXCharacteristic(dates);
    }


    /**
     * 摇一摇拍照
     * lanyaneme蓝牙名字
     * id  0开 1关
     */
    public static void Shakethecamera(String lanyaneme, int id) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xB6, (byte) id};
            //下面是b15s的摇一摇拍照
        } else {
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x79, (byte) 0x80, (byte) id};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_shakethecamera", data));
    }

    /**
     * 查找手环(只有B15p 才有)
     */
    public static void FindBracelet() {
        byte[] data = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x03, (byte) 0xFF, (byte) 0x71, (byte) 0x80};
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_findbracelet", data));
    }

    /**
     * 查找手机关闭(b15p才有)
     */
    public static void Findphoneoff() {
        byte[] data = new byte[]{(byte) 0xb5, (byte) 0x01};
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_findphoneoff", data));
    }

    /**
     * 自动心率检测开关
     * <p>
     * b15P才有
     * 公英制 Metric 0为公制，1为英制
     * 24小时制或者12小时制 HourSystem (0,1)
     * int id 0，开1关
     */
    public static void Automaticheartratedetection(int Metric, int HourSystem, int id) {
        byte[] data = new byte[]{(byte) 0xb8, (byte) 0x01, (byte) (Metric + 1), (byte) (HourSystem + 1), (byte) (id + 1)};
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_automaticheartratedetection", data));
    }

    /**
     * 整点测量
     * <p>
     * b15s才有
     * int id 0，开1关
     */
    public static void Integralpointmeasurement(int id) {
        byte[] data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x05, (byte) 0xff, (byte) 0x78, (byte) 0x80, (byte) id};
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_integralpointmeasurement", data));
    }

    /**
     * 读取当前手环的总步数
     * lanyaneme蓝牙名字
     * 返回 key ReadSteps
     */
    public static void ReadSteps(String lanyaneme) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) (0xa8), (byte) (0x00)};
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
        } else {
            Calendar cendara = Calendar.getInstance();
            Date date = new Date();
            byte[] dataw = new byte[]{(byte)0xAB,(byte)0x00,(byte)0x09,(byte)0xff,(byte)0x51,(byte)0x80,(byte)0x00,
                    (byte) ((cendara.get(Calendar.YEAR) - 2000)),
                    (byte) (date.getMonth() + 1),
                    (byte) (cendara.get(Calendar.DAY_OF_MONTH) - 1 & 0xff),
                    (byte) 0x00,
                    (byte) 0x00  };
            MyApp.getmBluetoothLeService().writeRXCharacteristic(dataw);
        }

    }

    /**
     * 语言切换
     * lanyaneme蓝牙名字
     * id 0中文   1为英文
     */
    public static void LanguageSwitching(String lanyaneme, int id) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xf4, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) id};
            //下面是b15s的语言切换
        } else {
            data = new byte[]{(byte) 0xab, (byte) 00, (byte) 0x04, (byte) 0xff, (byte) 0x7b, (byte) 0x80, (byte) id};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_languageswitching", data));
    }

    /**
     * 心率的单次测量
     * lanyaneme蓝牙名字
     * id 0开   1为关
     */
    public static void HeartRatemeasurement(String lanyaneme, int id) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xd0, (byte) id, (byte) 0x07};
            //下面是b15s的心率的单次测量
        } else {
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x31, (byte) 0x0a, (byte) id};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_heartratemeasurement", data));
    }

    /**
     *  血压取默认值
     *
     */
    public static void OnekeyMeasurememoren(String name, int id) {
      try{
          SharedPreferences mySharedPre= MyApp.getApplication().getSharedPreferences("shousuoya", Activity.MODE_PRIVATE);
          SharedPreferences denglu= MyApp.getApplication().getSharedPreferences("shuzhangya", Activity.MODE_PRIVATE);
          if(null!=mySharedPre&&null!=denglu){
              if(2==id){
                  byte[] WriteBytes =  new byte[]{(byte)0x91,(byte)0x01, Integer.valueOf(Integer.parseInt(String.valueOf(mySharedPre.getString("shousuoya","")),16)).byteValue(),
                          Integer.valueOf(Integer.parseInt(String.valueOf(denglu.getString("shuzhangya","")),16)).byteValue() };
                  EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
              }else{
                  //b15s的血压
                  byte[] WriteBytes = new byte[]{(byte)0x91,(byte)0x01,(byte)0x78,(byte)0x50};
                  EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
              }
          }else{
              //b15s的血压
              byte[] WriteBytes = new byte[]{(byte)0x91,(byte)0x01,(byte)0x78,(byte)0x50};
              EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));}
      }catch (Exception e){e.printStackTrace();}
    }

    /**
     * 心率测量测量（通用模式）
     * id 开 关
     */
    public static void OnekeyMeasurementXin(String name, int id) {
        if ("B15P".equals(name)) {
            //发送心率测试
            if(0==id){
                byte[] WriteBytes = new byte[]{(byte) 0xd0, (byte)0x00, (byte) 0x07};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }else{
                byte[] WriteBytes = new byte[]{(byte) 0xd0, (byte)0x01, (byte) 0x07};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }
        } else {
            if(1==id){
                byte[] WriteBytes = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x31, (byte) 0x0a, (byte) 0x00};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }else{
                byte[] WriteBytes = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x31, (byte) 0x0a, (byte) 0x01};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }

        }
    }

    /**
     * 私人模式下的血压
     */
    public static void OnekeyMeasurementxxXieya(String name, int id) {
        if ("B15P".equals(name)) {
            if(0==id){
                byte[] bytes = new byte[]{(byte) 0x90, (byte) 0x00, (byte) 0x00};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", bytes));
            }else{
                byte[] bytes = new byte[]{(byte) 0x90, (byte) 0x01, (byte) 0x00};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", bytes));
            }

        }else{
            //b15s的血压
            if(0==id){
                byte[] WriteBytes = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x32, (byte) 0x80, (byte) 0x00};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }else{
                byte[] WriteBytes = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x32, (byte) 0x80, (byte) 0x01};
                EventBus.getDefault().post(new ServiceMessageEvent("send_data", WriteBytes));
            }


        }

    }
    /**
     * 通用模式下的血压
     */

    public static void OnekeyMeasurementxxTongyong(String name, int id) {
        if ("B15P".equals(name)) {
            byte[] bytes = new byte[2];
            bytes[0] = (byte) 0x90;
            if (0 == id) {
                bytes[1]=(byte)0x01;
            } else {
                bytes[1]=(byte) 0x00;
            }

            EventBus.getDefault().post(new ServiceMessageEvent("send_data", bytes));
        }

    }

    /**
     * 当前手环的版本号
     * lanyaneme蓝牙名字
     * id 0开   1为关
     */
    public static void Currentversionnumber(String lanyaneme) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xA2, (byte) 0x00, (byte) 0x00, (byte) 0x02};
        } else {
            data = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x03, (byte) 0xFF, (byte) 0x92, (byte) 0x80};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_currentversionnumber", data));
    }

    /**
     * 全天活动量
     * lanyaneme蓝牙名字
     * id 0开   1为关
     * b15p有睡眠 记录没有步数
     */
    public static void DailyActivity(String lanyaneme,String id,String id2) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xd1, Integer.valueOf(id).byteValue(), Integer.valueOf(id2).byteValue(), (byte) 0x00};
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
        } else {
            Date date = new Date();
            Calendar cendar = Calendar.getInstance();
            data = new byte[]{(byte) 0xAB, (byte) 0x00, (byte) 0x09, (byte) 0xff, (byte) 0x51, (byte) 0x80, (byte) 0x00
                    , (byte) ((cendar.get(Calendar.YEAR) - 2000)), (byte) (date.getMonth() + 1), (byte) (cendar.get(Calendar.DAY_OF_MONTH) - 1 & 0xff)
                    , (byte) 0x00, (byte) 0x00};
            MyApp.getmBluetoothLeService().writeRXCharacteristic(data);
        }

    }


    /**
     * 消息提醒
     * lanyaneme蓝牙名字
     * id, 0开 1关
     */
    public static void MessageReminder(Map<String, Object> map) {
        Log.e("MYY","----map---"+map.toString()+"--n-"+map.get("lanyaneme"));
        String lanyaneme = (String) map.get("lanyaneme");//蓝牙名字
        Log.e("MYY","---lanyaneme---"+lanyaneme);
        int id = (int) map.get("id");//开关
        String msg = (String) map.get("msg");//消息内容
        String apptype = (String) map.get("type");  //判断是哪个APP
        Log.e("MYY","-----消息内容---"+msg+"----"+msg.length()+"--apptype--"+apptype);
        byte[] data;
        byte[] aaa;
        byte[] bbb;
        /**
         * 0,QQ
         * 1,微信
         * 2，短信
         * 3，来电
         * 4，Viber
         * 5，Twitter
         * 6，Facebook
         * 7，Whatsapp
         * 8，Instagram
         */
        try {
            byte[] bbbb = msg.getBytes("utf-8");
            MyLogUtil.i("msglength"+bbbb.length);
            //判断消息类容长度
            if ("B15P".equals(lanyaneme)) {
                new Duanxin(bbbb,id,apptype).start();
            }else if("B15S".equals(lanyaneme)){
                byte[] data3;
                data3 = new byte[12];
                System.arraycopy(bbbb, 0, data3, 0, 11);
                msg(apptype,id,data3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送短信
  private static   class Duanxin extends Thread{
        byte[] xxx;
        int idmy;
        String typemy;
        public Duanxin(byte[] xxx,int id,String type){
            this.xxx = xxx;
            this.idmy = id;
            this.typemy = type;
        }
        @Override
        public void run() {
            int current = 1;
            while(true){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isExit(current,xxx.length)){
                    break;
                }else{
                    byte[] result = new byte[20];
                    result = getContentduanxin(xxx,current,idmy,typemy);
                    EventBus.getDefault().post(new ServiceMessageEvent("send_data_messagereminder", result));
                    current++;
                }
            }
        }
    }

    /**
     * 分包发送数据(短信)
     * @param bs
     * @param currentPack
     * @return
     */
    private static byte[] getContentduanxin(byte[] bs,int currentPack,int id,String type){
        Log.e("MYY","---aaa--"+currentPack+"---"+id+"--"+type);
        byte[] xxx = new byte[20];
        xxx[0] = Integer.valueOf(0xc2).byteValue();
        if (0 == id && "qq".equals(type)) {
            xxx[1] = (byte) 0x03;//QQ
        } else if ("wechat".equals(type)) {
            xxx[1] = (byte) 0x02;//微信
        } else if (0 == id && "facebook".equals(type)) {
            xxx[1] = (byte) 0x05;//facebook
        } else if (0 == id && "twitter".equals(type)) {
            xxx[1] = (byte) 0x06;//Twitter
        } else if (0 == id && "whats".equals(type)) {
            xxx[1] = (byte) 0x09;//Whatsapp
        } else if (0 == id && "instagram".equals(type)) {
            xxx[1] = (byte) 0x06;//Instagram
        } else if (0 == id && "viber".equals(type)) {
            xxx[1] = (byte) 0x06; //viber（默认为Twitter）
        } else if (0 == id && "mms".equals(type)) {
            xxx[1] = (byte) 0x01;//短信
        } else if ("phone".equals(type)) {
            if(id == 0){    //电话提醒开
                xxx[1] = (byte) 0x00;
            }else if(id == 88){ //挂电话
                xxx[2] = (byte) 0x02;
            }
           // xxx[1] = (byte) 0x00;//电话   //0x00 不停的震动;0x01 只震动一次;0x02只震动两次 挂断电话;0x03 命令手环控制静音
        }
        //获取总包数 = total+1
        int total = bs.length / 14;
        if(total > 3){
            xxx[2] = Integer.valueOf(0x0D).byteValue();
            xxx[3] = Integer.valueOf(04).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(01).byteValue();
            System.arraycopy(bs,14*(currentPack-1),xxx,6,14*currentPack-1);
        }else{
            if(currentPack * 14 > bs.length){
                xxx[2] = Integer.valueOf(bs.length-(currentPack-1) * 14).byteValue();
            }else
            {
                xxx[2] = Integer.valueOf(0x0D).byteValue();
            }
            xxx[3] = Integer.valueOf(total+1).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(01).byteValue();
            if(bs.length > 14*currentPack){
                System.arraycopy(bs,14*(currentPack-1),xxx,6,14);
            }else{
                System.arraycopy(bs,14*(currentPack-1),xxx,6,bs.length-(14*(currentPack-1)));
            }
        }
        return  xxx;
    }
    /**
     * 判断数组的包数，，，
     * @param current
     * @param total
     * @return
     */
    private static boolean isExit(int current,int total){
        float a = (float)total/14;
        //超过4包就退出
        if(current > 4){
            return false;
        }
        //不足4包的时候，当已发送完就退出
        if(current >= a+1){
            return false;
        }

        return true;
    }
    /**
     *
     * @param type
     * @param id
     * @param
     */
    private static void msg(String type,int id,byte[] aa){
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 0xAB;
        bytes[1] = (byte) 0x00;
        bytes[2] = (byte)(aa.length+5);
        bytes[3] = (byte) 0xFF;
        bytes[4] = (byte) 0x72;
        bytes[5] = (byte) 0x80;
        if ("qq".equals(type)) {
            bytes[6] = (byte) 0x07;//QQ
        } else if ("wechat".equals(type)) {
            bytes[6] = (byte) 0x09;//微信
        } else if ("facebook".equals(type)) {
            bytes[6] = (byte) 0x16;//facebook
        } else if ("twitter".equals(type)) {
            bytes[6] = (byte) 0x15;//Twitter
        } else if ("instagram".equals(type)) {
            bytes[6] = (byte) 0x15;//Instagram
        } else if ("whats".equals(type)) {
            bytes[6] = (byte) 0x10;//Whats
        } else if ("mms".equals(type)) {
            bytes[6] = (byte) 0x03; //（默认为短信）
        } else if ("viber".equals(type)) {
            bytes[6] = (byte) 0x03;//viber
        } else if ("phone".equals(type)) {
            if(id == 0){    //来电提醒
                bytes[6] = (byte) 0x01;//电话
            }
            else if(id == 88){
                bytes[6] = (byte) 0x02;//挂断电话
            }
        }
        if (0 ==id) {
            bytes[7] = (byte) 0x02;//0关 1开 2来消息通知
        } else {
            bytes[7] = (byte) 0x00;//0关 1开 2来消息通知
        }
        byte[] data = addBytes(bytes, aa);
        Log.e("MYY","------需要发送的数据----"+ Arrays.toString(data)+"-----dd--"+data.toString());
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_messagereminder", data));
    }

    /**
     * 睡眠数据
     * lanyaneme蓝牙名字
     */
    public static void Sleepdata(String lanyaneme) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte)0xe0,(byte)0x01};
        } else {
            //打开开关
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x05, (byte) 0xff, (byte) 0x78, (byte) 0x80, (byte) 0x01};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_sleepdata", data));
    }

    /**
     * 同步时间
     * lanyaneme 蓝牙名字
     */
    public static void SynchronizationTime(String lanyaneme) {

        if ("B15P".equals(lanyaneme)) {
            Calendar cendar = Calendar.getInstance();
            String year = String.valueOf(cendar.get(Calendar.YEAR));
            if (String.valueOf(cendar.get(Calendar.MONTH) + 1).length() <= 1) {
                yue = "0" + String.valueOf(cendar.get(Calendar.MONTH) + 1);
            } else {
                yue = String.valueOf(cendar.get(Calendar.MONTH) + 1);
            }
            if (String.valueOf(cendar.get(Calendar.DAY_OF_MONTH)).length() <= 1) {
                ri = "0" + String.valueOf(cendar.get(Calendar.DAY_OF_MONTH));
            } else {
                ri = String.valueOf(cendar.get(Calendar.DAY_OF_MONTH));
            }
            Date time = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH");
            String xXXX = format.format(time);
            Date time1 = new Date();
            SimpleDateFormat format1 = new SimpleDateFormat("mm");
            String xXXX1 = format1.format(time1);
            Date time2 = new Date();
            SimpleDateFormat forma2t = new SimpleDateFormat("ss");
            String xXXX2 = forma2t.format(time2);
            //發送數據(设置时间)
            byte[] result = new byte[20];
            if (Integer.toHexString(Integer.parseInt(year)).length() == 3) {
                qian = "0" + String.valueOf(Integer.toHexString(Integer.parseInt(year))).substring(0, 1).toString();
                HOU = String.valueOf(Integer.toHexString(Integer.parseInt(year))).substring(1, 3).toString();
            } else {
                qian = String.valueOf(Integer.toHexString(Integer.parseInt(year))).substring(1, 2).toString();
                HOU = String.valueOf(Integer.toHexString(Integer.parseInt(year))).substring(2, 4).toString();
            }
            System.out.println("tytytyt" + "发送同步时间");
            // Log.d("dfg",HOU);
            result = new byte[]{(byte)0xa1,(byte)0x00,(byte)0x00,(byte)0x00,
                    Integer.valueOf(qian).byteValue(),
                    Integer.valueOf(Integer.parseInt(HOU, 16)).byteValue(),
                    Integer.valueOf(yue).byteValue(),
                    Integer.valueOf(ri).byteValue(),
                    Integer.valueOf(xXXX).byteValue(),
                    Integer.valueOf(xXXX1).byteValue(),
                    Integer.valueOf(xXXX2).byteValue(),
                    (byte) 0x00};
            EventBus.getDefault().post(new ServiceMessageEvent("send_data_synchronizationtime", result));



        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            byte[] datas = new byte[14];
            datas[0] = (byte) 0xAB;
            datas[1] = (byte) 0;
            datas[2] = (byte) 11;
            datas[3] = (byte) 0xff;
            datas[4] = (byte) 0x93;
            datas[5] = (byte) 0x80;
//        data[6] = (byte)0;//占位符
            datas[7] = (byte) ((year & 0xff00) >> 8);
            datas[8] = (byte) (year & 0xff);
            datas[9] = (byte) (month & 0xff);
            datas[10] = (byte) (day & 0xff);
            datas[11] = (byte) (hour & 0xff);
            datas[12] = (byte) (minute & 0xff);
            datas[13] = (byte) (second & 0xff);
            EventBus.getDefault().post(new ServiceMessageEvent("send_data_synchronizationtime", datas));
            try {
                Thread.sleep(2000);
            }catch (Exception E){
                E.printStackTrace();
            }
            Date datew=new Date();
            Calendar cendar = Calendar.getInstance();
            byte[] datawa = new byte[10];
            datawa[0] = (byte) 0xab;
            datawa[1] = (byte) 0x00;
            datawa[2] = (byte)0x07;
            datawa[3] = (byte) 0xff;
            datawa[4] = (byte) 0x52;
            datawa[5] = (byte) 0x80;
            datawa[6] = (byte)0x00;//占位符，没意义
            datawa[7] = (byte) ((cendar.get(Calendar.YEAR) - 2000));
            datawa[8] = (byte) (datew.getMonth()+1);
            datawa[9] = (byte) (cendar.get(Calendar.DAY_OF_MONTH)-1);
            EventBus.getDefault().post(new ServiceMessageEvent("send_data_synchronizationtime", datawa));

        }

    }

    /**
     * 固件升级指令
     * lanyaneme蓝牙名字
     * <p>
     * 这个指令 别乱用！！！！！！！！！
     * （最后写的原意是发送不升级的话就完了）
     */
    public static void FirmwareupgradeDirective(String lanyaneme) {
        byte[] data;
        if ("B15P".equals(lanyaneme)) {
            data = new byte[]{(byte) 0xA2, (byte) 0x00, (byte) 0x00, (byte) 0x01};
        } else {
            data = new byte[]{(byte) 0xab, (byte) 0x00, (byte) 0x04, (byte) 0xff, (byte) 0x25, (byte) 0x80, (byte) 0x00};
        }
        EventBus.getDefault().post(new ServiceMessageEvent("send_data_firmwareupgradedirective", data));
    }

/*    public static void initUUID(String deviceName) {
        if ("B15P".equals(deviceName)) {
            UUID_SERVICE = SampleGattAttributes.HEART_RATE_SERVER;
            UUID_WRITE = SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG;
            UUID_READ = SampleGattAttributes.CLIENT_CHARACTERISTIC_DATA;
        } else {
            UUID_SERVICE = SampleGattAttributes.HEART_RATE_SERVERp;
            UUID_WRITE = SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIGp;
            UUID_READ = SampleGattAttributes.CLIENT_CHARACTERISTIC_DATAp;
        }
        DESC = "00002902-0000-1000-8000-00805f9b34fb";
    }*/

    public static void initUUID(String deviceName) {
        if ("B15P".equals(deviceName)) {
            UUID_SERVICE = UUID.fromString(SampleGattAttributes.HEART_RATE_SERVER);
            UUID_WRITE = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
            UUID_READ = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_DATA);
        } else {
            UUID_SERVICE = UUID.fromString(SampleGattAttributes.HEART_RATE_SERVERp);
            UUID_WRITE = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIGp);
            UUID_READ = UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_DATAp);
        }
    }

}
