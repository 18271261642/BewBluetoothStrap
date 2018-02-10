package com.example.bozhilun.android.B18I.b18iutils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.OnClick;


/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/8/31 10:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18iUtils {


    /**
     * 以空格分割
     *
     * @param str
     * @return
     */
    public static String[] stringToArray(String str) {
        return str.split("\\s+");
    }

    /**
     * dip 转换成px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getSystemTimer() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static String getSystemTimers2() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static String H9TimeData() {
        return getSystemData() + "T" + getSystemData11();
    }


    public static String getSystemData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static String getSystemDatasss() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static String getSystemDataStart() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }


    public static String getSystemData11() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    public static List<String> getTimes2() {

        List<String> timeData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String year = calendar.get(Calendar.YEAR) + "/";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + ":";
        String minute = calendar.get(Calendar.MINUTE) + ":";
        String secon = calendar.get(Calendar.SECOND) + "";
        timeData.add(year);
        timeData.add(month);
        timeData.add(day);
        timeData.add(hour);
        timeData.add(minute);
        timeData.add(secon);
//        String created = calendar.get(Calendar.YEAR) + "年"
//                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
//                + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                + calendar.get(Calendar.HOUR_OF_DAY) + "时"
//                + calendar.get(Calendar.MINUTE) + "分"+calendar.get(Calendar.SECOND)+"s";
//        Log.e("msg", created);
        return timeData;
    }

    public static String getTimeStamp() {
        long time = System.currentTimeMillis();
//        tnew Date().getTime();
//        timeSeconds = System.currentTimeMillis();
//        timeMillis = Calendar.getInstance().getTimeInMillis();
//        long time=System.currentimeMillis()/1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time / 1000);
        return str;
    }

    public static List<String> getTimes3() {

        List<String> timeData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String minute = calendar.get(Calendar.MINUTE) + "";
        String secon = calendar.get(Calendar.SECOND) + "";
        timeData.add(year);
        timeData.add(month);
        timeData.add(day);
        timeData.add(hour);
        timeData.add(minute);
        timeData.add(secon);
//        String created = calendar.get(Calendar.YEAR) + "年"
//                + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算
//                + calendar.get(Calendar.DAY_OF_MONTH) + "日"
//                + calendar.get(Calendar.HOUR_OF_DAY) + "时"
//                + calendar.get(Calendar.MINUTE) + "分"+calendar.get(Calendar.SECOND)+"s";
//        Log.e("msg", created);
        return timeData;
    }


    public static String getSysWeeks() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "日";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
//        return mYear + "年" + mMonth + "月" + mDay + "日" + "/星期" + mWay;
        return mWay;
    }

    /**
     * 获取周期
     *
     * @return
     */
//    public static int getCycle() {
//        //代码编写时间：2015年11月17日14:40:12
//        Calendar cal = Calendar.getInstance();//这一句必须要设置，否则美国认为第一天是周日，而我国认为是周一，对计算当期日期是第几周会有错误
////        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
//        cal.setFirstDayOfWeek(Calendar.SUNDAY); // 设置每周的第一天为星期日
////        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周从周一开始
//        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 每周从周一开始
//        cal.setMinimalDaysInFirstWeek(7); // 设置每周最少为7天
//        cal.setTime(new Date());
//        int weeks = cal.get(Calendar.WEEK_OF_YEAR);
//
//        Log.e("-----第几周期？？---->>>", weeks + "");
//        System.out.println(weeks);
//        return weeks;
//    }
    public static int getCycle() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String today = format.format(curDate);
        Date date = null;
        try {
            date = format.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int i = calendar.get(Calendar.WEEK_OF_YEAR);
        Log.e("------测试第几周-->>>：", "===" + i + "========" + Calendar.WEEK_OF_YEAR);
        return i;
    }

//
//    public static String getNextDay() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int DAY = day - 1;
//        if (DAY == 0) {
//            month = month - 1;
//            if ((month - 1) == 0) {
//                year = year - 1;
//                month = 12;
//            } else if ((month - 1) == 2) {
//                if (month == 1) {
//                    month = 12;
//                }
//                DAY = 28;
//            } else if ((month - 1) == 8 || (month - 1) == 10 || (month - 1) == 12) {
//                DAY = 31;
//            } else {
//                if ((month - 1) % 2 != 0) {
//                    DAY = 31;
//                } else {
//                    DAY = 30;
//                }
//            }
//        }
//        Log.d("-------------", year + "/" + month + "/" + DAY);
//        if (month <= 9) {
//            return year + "/" + "0" + month + "/" + DAY;
//        } else if (day <= 9) {
//            return year + "/" + month + "/" + "0" + DAY;
//        } else if (month <= 9 && day <= 9) {
//            return year + "/" + "0" + month + "/" + "0" + DAY;
//        } else if (month <= 9 && day > 9) {
//            return year + "/" + "0" + month + "/" + DAY;
//        } else if (month > 9 && day <= 9) {
//            return year + "/" + month + "/" + "0" + DAY;
//        } else if (month > 9 && day > 9) {
//            return year + "/" + month + "/" + DAY;
//        } else {
//            return year + "/" + month + "/" + DAY;
//        }
//    }
//
//    public static String getNextNumberDay(int number) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int DAY = day - number;
//        if (DAY == 0) {
//            month = month - 1;
//            if ((month - 1) == 0) {
//                year = year - 1;
//                month = 12;
//            } else if ((month - 1) == 2) {
//                if (month == 1) {
//                    month = 12;
//                }
//                DAY = 28;
//            } else if ((month - 1) == 8 || (month - 1) == 10 || (month - 1) == 12) {
//                DAY = 31;
//            } else {
//                if ((month - 1) % 2 != 0) {
//                    DAY = 31;
//                } else {
//                    DAY = 30;
//                }
//            }
//        }
//        Log.d("-------------", year + "/" + month + "/" + DAY);
//        if (month <= 9) {
//            return year + "/" + "0" + month + "/" + DAY;
//        } else if (day <= 9) {
//            return year + "/" + month + "/" + "0" + DAY;
//        } else if (month <= 9 && day <= 9) {
//            return year + "/" + "0" + month + "/" + "0" + DAY;
//        } else if (month <= 9 && day > 9) {
//            return year + "/" + "0" + month + "/" + DAY;
//        } else if (month > 9 && day <= 9) {
//            return year + "/" + month + "/" + "0" + DAY;
//        } else if (month > 9 && day > 9) {
//            return year + "/" + month + "/" + DAY;
//        } else {
//            return year + "/" + month + "/" + DAY;
//        }
//    }
//
//
//    public static String getNextNumberDays(int number) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int DAY = day - number;
//        if (DAY == 0) {
//            month = month - 1;
//            if ((month - 1) == 0) {
//                year = year - 1;
//                month = 12;
//            } else if ((month - 1) == 2) {
//                if (month == 1) {
//                    month = 12;
//                }
//                DAY = 28;
//            } else if ((month - 1) == 8 || (month - 1) == 10 || (month - 1) == 12) {
//                DAY = 31;
//            } else {
//                if ((month - 1) % 2 != 0) {
//                    DAY = 31;
//                } else {
//                    DAY = 30;
//                }
//            }
//        }
//        Log.d("-------------", year + "-" + month + "-" + DAY);
//        if (month >= 9) {
//            return year + "-" + "0" + month + "-" + DAY;
//        } else if (day >= 9) {
//            return year + "-" + month + "-" + "0" + DAY;
//        } else {
//            return year + "-" + month + "-" + DAY;
//        }
//    }
//
//    public static String getNextDays() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int DAY = day - 1;
//        if (DAY == 0) {
//            month = month - 1;
//            if ((month - 1) == 0) {
//                year = year - 1;
//                month = 12;
//            } else if ((month - 1) == 2) {
//                if (month == 1) {
//                    month = 12;
//                }
//                DAY = 28;
//            } else if ((month - 1) == 8 || (month - 1) == 10 || (month - 1) == 12) {
//                DAY = 31;
//            } else {
//                if ((month - 1) % 2 != 0) {
//                    DAY = 31;
//                } else {
//                    DAY = 30;
//                }
//            }
//        }
//        Log.d("-------------", year + "/" + month + "/" + DAY);
//        return year + "-" + month + "-" + DAY;
//    }

    /**
     * 将时间戳转为字符串
     *
     * @param cc_time
     * @return
     */
    public static String getStrTimes(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }


    /**
     * 排序
     *
     * @param a
     */
    public static void bubbleSort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 字符串截取
     *
     * @param s
     * @param start
     * @param end
     * @return
     */
    public static String interceptString(String s, int start, int end) {
        return s.substring(start, end);
    }


    /**
     * 从时间(毫秒)中提取出时间(时:分)
     * 时间格式:  时:分
     *
     * @param millisecond
     * @return
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(millisecond);
        String timeStr = simpleDateFormat.format(date);
        return timeStr;
    }


    // 获得当前周- 周一的日期
    public static String getCurrentMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
//        DateFormat df = DateFormat.getDateInstance();
//        String preMonday = df.format(monday);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = format.format(monday);
        return preMonday;
    }

    // 获得当前周- 周日  的日期
    public static String getPreviousSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
//        String preMonday = df.format(monday);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = format.format(monday);
        return preMonday;
    }

    // 获得本周一与当前日期相差的天数
    public static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }


    // 任意进制数转为十进制数
    public static String toD(String a, int b) {
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r = (int) (r + formatting(a.substring(i, i + 1))
                    * Math.pow(b, a.length() - i - 1));
        }
        return String.valueOf(r);
    }

    // 将十六进制中的字母转为对应的数字
    public static int formatting(String a) {
        int i = 0;
        for (int u = 0; u < 10; u++) {
            if (a.equals(String.valueOf(u))) {
                i = u;
            }
        }
        if (a.equals("a")) {
            i = 10;
        }
        if (a.equals("b")) {
            i = 11;
        }
        if (a.equals("c")) {
            i = 12;
        }
        if (a.equals("d")) {
            i = 13;
        }
        if (a.equals("e")) {
            i = 14;
        }
        if (a.equals("f")) {
            i = 15;
        }
        return i;
    }

    /**
     * 判断android SDK 版本是否大于等于5.0
     *
     * @return
     */
    public static boolean isAndroid5() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
