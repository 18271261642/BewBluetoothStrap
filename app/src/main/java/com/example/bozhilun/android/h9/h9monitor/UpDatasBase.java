package com.example.bozhilun.android.h9.h9monitor;

import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.sdk.bluetooth.bean.SleepData;
import com.sdk.bluetooth.bean.SportsData;
import com.sdk.bluetooth.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/12/11 15:55
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class UpDatasBase {
    public static final String TAG = "UpDatasBase";

    /**************获取睡眠---测试****************/
    //获取睡眠
    private void getSleepDATAS(final int week) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sleepUrl = URLs.HTTPs + "/sleep/getSleepByTime";
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
            sleepJson.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac"));
            sleepJson.put("startDate", sdf.format(WatchUtils.getDateBefore(sdf.parse(WatchUtils.getCurrentDate()), week)));
            sleepJson.put("endDate", WatchUtils.getCurrentDate());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                Log.e(TAG, "----获取睡眠返回----" + result);
            }
        };
        CommonSubscriber commonSubscriber = new CommonSubscriber(subscriberOnNextListener, MyApp.getContext());
        OkHttpObservable.getInstance().getData(commonSubscriber, sleepUrl, sleepJson.toString());
    }
    /********************************************/


    /**
     * 前几天运动分配 （准备上传）
     *
     * @param sportsDatas
     * @param numberDay
     */
    public static void upSportDatasCrrur(LinkedList<SportsData> sportsDatas, int numberDay, float GOAL) {
        int step = 0;
        int calorie = 0;
//        String nextDay = B18iUtils.getNextNumberDay(numberDay);//前numberDay天时间
        Date dateBefore = H9TimeUtil.getDateBefore(new Date(), numberDay);
        String nextDay = H9TimeUtil.getValidDateStr(dateBefore);
        for (SportsData sportsData : sportsDatas) {
            String strTimes = B18iUtils.getStrTimes(String.valueOf(sportsData.getSport_time_stamp()));//时间戳转换
            String substring = strTimes.substring(0, 10);
            Log.d("-------------TEMT---time", substring + "===" + nextDay);
            if (nextDay.equals(substring)) {
                step += sportsData.sport_steps;
                calorie += sportsData.sport_cal;
            }
            Log.d("-------------", "Step:" + step + "step" + "Calorie:" + calorie + "cal");
        }
        Log.d("-------------==", "Step:" + step + "step" + "Calorie:" + calorie + "cal");
        int dis = 0;
        String sex = "M";
        String hight = "175";
        if ("M".equals(sex)) {
            dis = (int) (Integer.valueOf(hight) * 0.415 * step);
        } else {
            dis = (int) (Integer.valueOf(hight) * 0.413 * step);
        }

        String upSportTimes = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "upSportTimes", "");
        if (!TextUtils.isEmpty(upSportTimes)) {
            String timeDifference = H9TimeUtil.getTimeDifference(upSportTimes, B18iUtils.getSystemDataStart());
            if (!TextUtils.isEmpty(timeDifference)) {
                int number = Integer.valueOf(timeDifference.trim());
                int number2 = Integer.parseInt(timeDifference.trim());
                if (number >= 1000 || number2 >= 1000) {
                    updateLoadSportToServer2(GOAL, step, calorie, dis, H9TimeUtil.getValidDateStr2(dateBefore));
                    SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTimes", B18iUtils.getSystemDataStart());
                }
            }
        }else {
            updateLoadSportToServer2(GOAL, step, calorie, dis, H9TimeUtil.getValidDateStr2(dateBefore));
            SharedPreferencesUtils.setParam(MyApp.getContext(), "upSportTimes", B18iUtils.getSystemDataStart());
        }
    }

    /**
     * 前几天运动上传
     *
     * @param goal
     * @param step
     * @param calories
     * @param distance
     * @param nextDay
     */
    public static void updateLoadSportToServer2(float goal, float step, double calories, double distance, String nextDay) {
        Log.d("-------------TEMT---time", "运动上传----2----" + nextDay);
        Log.e(TAG, "--aaaaaaaaaaaaaaaa--运动上传-2-------" + goal + "==" + step + "==" + calories + "==" + distance + "==" + nextDay);
        int state = 1;  //步数是否达标
        if (goal - state >= 0) {  //达标
            state = 0;
        } else {
            state = 1;
        }
        JSONObject stepJons = new JSONObject();
        try {
            stepJons.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId")); //用户ID
            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac")); //mac地址
            stepJons.put("stepNumber", step);   //步数
            stepJons.put("distance", distance);  //路程
            stepJons.put("calories", calories);  //卡里路
            stepJons.put("timeLen", "0");    //时长
            stepJons.put("date", nextDay);   //data_time
            stepJons.put("status", state);     //是否达标
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {

            @Override
            public void onNext(String result) {
                Log.e("H9", "---前几天步数数据返回--" + result);
            }
        }, MyApp.getContext());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
    }


    /**
     * 整理number天睡眠数据（准备上传）
     *
     * @param numberDay
     */
    public static void sleepDataCrrur(int numberDay, int numberDayEnd, LinkedList<SleepData> sleepDatas) {
        int AWAKEDATAS = 0;
        int DEEPDATAS = 0;
        int SHALLOWDATAS = 0;

        int AllSleep = 0;
        boolean isSleeped = false;
        boolean isIntoSleeped = false;
        if (sleepDatas != null) {
            //当天日期
            String soberLenTime = "08:00";
//            String nextDay = B18iUtils.getNextNumberDay(numberDay);//前一天时间
//            String nextEndDay = B18iUtils.getNextNumberDay(numberDayEnd);//今天时间
            setSleepDatas(sleepDatas, AllSleep, numberDay, numberDayEnd, isSleeped, isIntoSleeped, SHALLOWDATAS, DEEPDATAS, AWAKEDATAS, soberLenTime);
        }
    }

    /**
     * 上传前number天睡眠数据
     *
     * @param AllSleep
     * @param isSleeped
     * @param isIntoSleeped
     * @param SHALLOWDATAS
     * @param DEEPDATAS
     * @param AWAKEDATAS
     * @param soberLenTime
     */
    public static void setSleepDatas(LinkedList<SleepData> sleepDatas, int AllSleep, int numberDay, int numberDayEnd, boolean isSleeped,
                                     boolean isIntoSleeped, int SHALLOWDATAS, int DEEPDATAS, int AWAKEDATAS, String soberLenTime) {
        Date dateBefore = H9TimeUtil.getDateBefore(new Date(), numberDay);
        String nextDay = H9TimeUtil.getValidDateStr(dateBefore);
        Date dateBefore2 = H9TimeUtil.getDateBefore(new Date(), numberDayEnd);
        String nextEndDay = H9TimeUtil.getValidDateStr(dateBefore2);
        int size = sleepDatas.size();
        for (int i = 0; i < size; i++) {
            String strTimes = B18iUtils.getStrTimes(String.valueOf(sleepDatas.get(i).sleep_time_stamp));//时间戳转换
            String timeDay = B18iUtils.interceptString(strTimes, 0, 10);//2017/10/20
            String timeH = B18iUtils.interceptString(strTimes, 11, 13);//8
            if (nextEndDay.equals(timeDay) || nextDay.equals(timeDay)) {
                if (Integer.valueOf(timeH) >= 20 || Integer.valueOf(timeH) <= 8) {
                    String timeDifference = "0";
                    if (0 < i) {
                        timeDifference = H9TimeUtil.getTimeDifference
                                (DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i - 1).sleep_time_stamp * 1000))
                                        , DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                    } else {
                        timeDifference = "0";
                    }

                    int SLEEPWAKE = 0;//苏醒次数
                    int sleep_type = sleepDatas.get(i).sleep_type;
                    // 0：睡着// 1：浅睡// 2：醒着// 3：准备入睡// 4：退出睡眠// 16：进入睡眠模式//
                    // 17：退出睡眠模式（本次睡眠非预设睡眠）
                    // 18：退出睡眠模式（本次睡眠为预设睡眠）
                    if (sleep_type == 0) {//--------》睡着
                        Log.e(TAG, "睡着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        AllSleep += Integer.valueOf(timeDifference);
                        DEEPDATAS += Integer.valueOf(timeDifference);//睡着的分钟数
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 1) {//--------》浅睡
                        Log.e(TAG, "浅睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        AllSleep += Integer.valueOf(timeDifference);
                        SHALLOWDATAS += Integer.valueOf(timeDifference);//浅睡的分钟数
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 2) {//--------》醒着
                        Log.e(TAG, "醒着时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 3) {//--------》准备入睡着
                        Log.e(TAG, "准备入睡时间：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        if (!isIntoSleeped) {
                            isIntoSleeped = true;
                            isSleeped = true;
                        }
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 4) {//--------》退出睡眠
                        Log.e(TAG, "退出睡眠：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        SLEEPWAKE++;
                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 16) {//--------》进入睡眠模式
                        Log.e(TAG, "进入睡眠模式：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        if (!isSleeped) {
                            isSleeped = true;
                            isIntoSleeped = true;
                        }
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 17) {//--------》退出睡眠模式（本次睡眠非预设睡眠）
                        Log.e(TAG, "退出睡眠模式==0=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        SLEEPWAKE++;
                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    } else if (sleep_type == 18) {//--------》退出睡眠模式（本次睡眠为预设睡眠）
                        Log.e(TAG, "退出睡眠模式==1=：" + Integer.valueOf(timeDifference) + "===" + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)));
                        SLEEPWAKE++;
                        soberLenTime = DateUtil.dateToSec(DateUtil.timeStampToDate(sleepDatas.get(i).sleep_time_stamp * 1000)).substring(11, 16);
                        Log.d(TAG, "===========" + SLEEPWAKE);
                    }
                    Log.d(TAG, DEEPDATAS + "----------222--------" + SHALLOWDATAS + "==============" + AllSleep + "===" + SLEEPWAKE);
                    //---------入睡时间-----苏醒次数--------苏醒时间
//                        TextView textSleepInto, textSleepWake, textSleepTime;
                }
            }
        }
        Log.d(TAG, DEEPDATAS + "----------121112--------" + SHALLOWDATAS + "==============" + AllSleep);
        AWAKEDATAS = AllSleep - (DEEPDATAS + SHALLOWDATAS);//清醒
        Log.d(TAG, "睡眠----清醒" + AWAKEDATAS);

        if (DEEPDATAS > 0 || SHALLOWDATAS > 0) {
            String upSleepTime = (String) SharedPreferencesUtils.getParam(MyApp.getContext(), "upSleepTimes", "");
            if (!TextUtils.isEmpty(upSleepTime)) {
                String timeDifference = H9TimeUtil.getTimeDifference(upSleepTime, B18iUtils.getSystemDataStart());
                if (!TextUtils.isEmpty(timeDifference)) {
                    int number = Integer.valueOf(timeDifference.trim());
                    int number2 = Integer.parseInt(timeDifference.trim());
//                        Log.e(TAG, "睡眠上传---------" + number + "--" + number2 + "==" + timeDifference.compareTo("5"));
                    if (number >= 1000 || number2 >= 1000) {

                        Log.d(TAG, "----清醒时间" + soberLenTime);
//                            Log.e(TAG, "睡眠上传-----in----" + number + "===前几天时间" + H9TimeUtil.getDateBefore(new Date(), 7) + "前一天时间" + B18iUtils.getNextDay());
                        upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS), H9TimeUtil.getValidDateStr2(dateBefore), H9TimeUtil.getValidDateStr2(dateBefore2));//上传睡眠数据
                        SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTimes", B18iUtils.getSystemDataStart());
                    }
                }
            } else {
                upDataSleep(String.valueOf(DEEPDATAS), String.valueOf(SHALLOWDATAS), H9TimeUtil.getValidDateStr2(dateBefore), H9TimeUtil.getValidDateStr2(dateBefore2));//上传睡眠数据
                SharedPreferencesUtils.setParam(MyApp.getContext(), "upSleepTimes", B18iUtils.getSystemDataStart());
            }
        }
    }

    /**
     * 上传睡眠数据
     *
     * @param deepSleep
     * @param shallowSleep
     */
    private static void upDataSleep(String deepSleep, String shallowSleep, String nextDay, String nextEndDay) {
        Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠上传--2------" + shallowSleep + "==" + deepSleep + "==" + nextDay + "==" + nextEndDay);
        Log.d("-------------TEMT---time", "睡眠上传----2----" + nextDay + "===" + nextEndDay);
        try {
            JSONObject map = new JSONObject();
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            String mylanmac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac");
            Log.d(TAG, "==设备名称与MAC==" + userId + "==" + mylanmac);
            map.put("userId", (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
            Log.d(TAG, nextDay + "===" + nextEndDay);
            map.put("startTime", nextDay);//
            map.put("endTime", nextEndDay);
            map.put("count", "10");
            map.put("deepLen", deepSleep);
            map.put("shallowLen", shallowSleep);
            map.put("deviceCode", (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac"));
            map.put("sleepQuality", "6");
            map.put("sleepLen", "4");
            map.put("sleepCurveP", "5");
            map.put("sleepCurveS", "8");
//            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, context);

            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String s) {
                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠数据上传--------" + s);
                }
            };
            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, MyApp.getContext());
            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSleep, map.toString());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    /**
     * 心率数据上传
     */
    public static void upDataHearte(String heartData, String stringTimer) {
        Log.e(TAG, "--aaaaaaaaaaaaaaaa--心率上传--------" + heartData + "==" + stringTimer);
        try {
            JSONObject map = new JSONObject();
            map.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
            map.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac"));
            map.put("systolic", "00");
            map.put("stepNumber", "00");
            map.put("date", stringTimer);
            map.put("heartRate", heartData);
            map.put("status", "0");
            JSONObject mapB = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            Object jsonArrayb = jsonArray.put(map);
            mapB.put("data", jsonArrayb);
            String mapjson = mapB.toString();
            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String s) {
                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--心率数据上传--------" + s);
                }
            };
            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, MyApp.getContext());
            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upHeart, mapjson);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }


    /**
     * @param goal     目标步数
     * @param step     手表步数
     * @param calories 手表卡路里
     * @param distance //手表公里
     */
    public static void updateLoadSportToServer(float goal, float step, double calories, double distance) {
        Log.e(TAG, "--aaaaaaaaaaaaaaaa---步数上传--------" + goal + "==" + step + "==" + calories + "==" + distance);
        int state = 1;  //步数是否达标
        if (goal - state >= 0) {  //达标
            state = 0;
        } else {
            state = 1;
        }
        JSONObject stepJons = new JSONObject();
        try {
            stepJons.put("userId", SharedPreferencesUtils.readObject(MyApp.getContext(), "userId")); //用户ID
            stepJons.put("deviceCode", SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac")); //mac地址
            stepJons.put("stepNumber", step);   //步数
            stepJons.put("distance", distance);  //路程
            stepJons.put("calories", calories);  //卡里路
            stepJons.put("timeLen", "0");    //时长
            stepJons.put("date", WatchUtils.getCurrentDate());   //data_time
            stepJons.put("status", state);     //是否达标
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "-----steJson-----" + stepJons.toString() + "--" + System.currentTimeMillis() / 1000 + "---" + new Date().getTime() / 1000);
        CommonSubscriber commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {

            @Override
            public void onNext(String result) {
                Log.e("H9", "---上次步数数据返回--" + result);
            }
        }, MyApp.getContext());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSportData, stepJons.toString());
    }


    /**
     * 上传睡眠数据
     *
     * @param deepSleep
     * @param shallowSleep
     */
    public static void upDataSleep(String deepSleep, String shallowSleep) {
        Date dateBefore = H9TimeUtil.getDateBefore(new Date(), 0);
        String nextDay = H9TimeUtil.getValidDateStr2(dateBefore);
        Date dateBefore2 = H9TimeUtil.getDateBefore(new Date(), 1);
        String nextDay2 = H9TimeUtil.getValidDateStr2(dateBefore2);
        Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠上传--------" + shallowSleep + "==" + deepSleep + "==" + nextDay2 + "==" + nextDay);
        try {
            JSONObject map = new JSONObject();
            String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");
            String mylanmac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac");
            Log.d(TAG, "==设备名称与MAC==" + userId + "==" + mylanmac);
            map.put("userId", (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId"));
            Log.d("-------------TEMT---time", "睡眠上传--------" + nextDay2 + "===" + nextDay);
            map.put("startTime", "2017-12-10");//
            map.put("endTime", "2017-12-11");
            map.put("count", "10");
            map.put("deepLen", deepSleep);
            map.put("shallowLen", shallowSleep);
            map.put("deviceCode", (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanmac"));
            map.put("sleepQuality", "6");
            map.put("sleepLen", "4");
            map.put("sleepCurveP", "5");
            map.put("sleepCurveS", "8");
//            dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, context);

            SubscriberOnNextListener sb = new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String s) {
                    Log.e(TAG, "--aaaaaaaaaaaaaaaa--睡眠数据上传--------" + s);
                }
            };
            CommonSubscriber commonSubscriber = new CommonSubscriber(sb, MyApp.getContext());
            OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.upSleep, map.toString());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

}
