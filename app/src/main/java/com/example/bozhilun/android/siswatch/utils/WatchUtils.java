package com.example.bozhilun.android.siswatch.utils;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;


/**
 * Created by Administrator on 2017/7/18.
 */

public class WatchUtils {

    public static final String WATCH_CONNECTED_STATE_ACTION = "com.example.bozhilun.android.siswatch.alarm.connectstate";  //判断蓝牙是否连接的广播action
    public static final String WATCH_GETWATCH_STEPS_ACTION = "com.example.bozhilun.android.siswatch.alarm.steps";   //步数的广播action
    public static final String WATCH_OPENTAKE_PHOTO_ACTION = "com.example.bozhilun.android.siswatch.takephoto";     //拍照指令的action
    public static final String WACTH_DISCONNECT_BLE_ACTION = "com.example.bozhilun.android.siswatch.bledisconnect";    //断开连接成功的action

    //卡路里的常量
    public  static double kcalcanstanc = 65.4;  //计算卡路里常量

    // 字符串的非空
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前系统时间 毫秒数
     * @return
     */
    public static Long getNowTime(){
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间，格式为 :yyyy-MM-dd
     *
     * @return
     */
    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间
     * @return
     */
    public static String getCurrentDate1(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 获取H:mm:ss格式时间
     * @return
     */
    public static String getCurrentDate2(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String date = dateFormat.format(now);
        return date;
    }

    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }


    /**
     * 除法运算
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 两个double相乘
     * @param v1
     * @param v2
     * @return
     */
    public static Double mul(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }


//    //判断错误类型
//    public static String getMessage(Object error,Context context){
//        if(error instanceof ConnectTimeoutException){  //连接超时
//            return context.getResources().getString(R.string.error_timeout);
//        }else if(error instanceof ConnectException){  	//客户端请求超时
//            return context.getResources().getString(R.string.error_connetexection);
//        }else if(error instanceof SocketTimeoutException){	//服务器响应超时,客户端已请求，服务器未响应
//            return context.getResources().getString(R.string.error_sockettimeout);
//        }else if(error instanceof JSONException){	//JSON解析异常
//            return context.getResources().getString(R.string.error_jsonexection);
//        }else if(error instanceof Resources.NotFoundException){	//404 地址为找到
//            return context.getResources().getString(R.string.error_notfound);
//        }
//        return context.getResources().getString(R.string.error_message);
//    }


    /**
     * j计算步长
     * @param
     * @param height
     * @return
     */
    public static double getStepLong(int height){
        double stepLong;
        if (height < 155) {

          //  stepLong = (height *20)/(42*100);
            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height),Double.valueOf(20)),
                    WatchUtils.mul(Double.valueOf(42),Double.valueOf(100)),2);
        }
        else if (height >= 155 && height < 174){

//            stepLong = (height *13)/(28*100);

            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height),Double.valueOf(13)),
                    WatchUtils.mul(Double.valueOf(28),Double.valueOf(100)),2);

        }
        else{
          //  stepLong = (height *19)/(42*100);
            stepLong = WatchUtils.div(WatchUtils.mul(Double.valueOf(height),Double.valueOf(19)),
                    WatchUtils.mul(Double.valueOf(42),Double.valueOf(100)),2);
        }

        return stepLong;

    }

    /**
     * 计算路程
     */
    public static double getDistants(int step,double stepLong){

       return WatchUtils.div(WatchUtils.mul(Double.valueOf(step),stepLong),Double.valueOf(1000),2);

    }

    /**
     * 计算卡里路
     */
    public static double getKcal(int step,double stepLong){
        double distans = WatchUtils.div(WatchUtils.mul(Double.valueOf(step),stepLong),Double.valueOf(1000),5);
        return WatchUtils.mul(distans,65.4);
    }

    /**
     * 获取应用版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){
        try {
            PackageManager packM = context.getPackageManager();
            PackageInfo packInfo = packM.getPackageInfo(context.getPackageName(), 0);

            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * 获取手机IME码
     */
    public static String getPhoneInfo(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }

    /**
     * 判断通知是否打开
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
     /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断辅助功能是否开启
     * @param
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = mContext.getPackageName() + "/" + WatchAccessServices.class.getCanonicalName();
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("", "Error finding mysetting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("", "We've found the correct mysetting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("", "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        } else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            } else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
            } else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                } else if (dayMinus >= 0) {
                    age = age - 1;
                }
            } else if (monthMinus > 0) {
                age = age - 1;
            }
        }
        return age;
    }

    /**
     * 转换为高8位
     * @param v
     * @return
     */
    public static byte loUint16(short v) {
        return (byte)(v & 255);
    }

    /**
     * 转换为低8位
     * @param v
     * @return
     */
    public static byte hiUint16(short v) {
        return (byte)(v >> 8);
    }

    //获取闹钟的action
    public static IntentFilter regeditAlarmBraod(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.bozhilun.android.siswatch.alarm");
        return intentFilter;
    }

    /**
     * 蓝牙连接状态的action
     */
    public static IntentFilter h8ConnectState(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.WATCH_CONNECTED_STATE_ACTION);
        intentFilter.addAction(WatchUtils.WATCH_GETWATCH_STEPS_ACTION);
        intentFilter.addAction(WatchUtils.WATCH_OPENTAKE_PHOTO_ACTION);
        return intentFilter;
    }

    /**
     * 公里转换为英里 1英里(mi)=1.609344公里(km)、1公里(km)=0.6213712英里(mi)
     * @param km
     * @param mi
     * @return
     */
    public static double kmToMi(double km){
        double tempkmmi = 0.62;
        return WatchUtils.mul(km,tempkmmi);
    }

    /**
     * 检测服务是否处于运行状态
     * @param servicename
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String servicename,Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info: infos){
            if(servicename.equals(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }


    /**
     * 断开H8共用处理
     */
    public static void disCommH8(){
        MyApp.getWatchBluetoothService().disconnect();//断开蓝牙
        MyCommandManager.deviceDisconnState = true;
        MyCommandManager.ADDRESS = null;
        MyCommandManager.DEVICENAME = null;
        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanya",null);
        SharedPreferencesUtils.saveObject(MyApp.getContext(),"mylanmac",null);
        SharedPreferencesUtils.setParam(MyApp.getContext(), "stepsnum", "0");
    }


}
