package com.example.bozhilun.android.B18I.b18imonitor;

import android.database.Cursor;
import android.util.Log;

import com.afa.tourism.greendao.gen.B18iHeartDatasDao;
import com.afa.tourism.greendao.gen.B18iSleepDatasDao;
import com.afa.tourism.greendao.gen.B18iStepDatasDao;
import com.afa.tourism.greendao.gen.B18iUserInforDatasDao;
import com.afa.tourism.greendao.gen.DaoSession;
import com.example.bozhilun.android.B18I.b18ibean.B18iHeartDatas;
import com.example.bozhilun.android.B18I.b18ibean.B18iSleepDatas;
import com.example.bozhilun.android.B18I.b18ibean.B18iStepDatas;
import com.example.bozhilun.android.B18I.b18ibean.B18iUserInforDatas;
import com.example.bozhilun.android.B18I.b18idb.DBManager;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import cn.appscomm.bluetooth.model.HeartRateData;
import cn.appscomm.bluetooth.model.ReminderData;
import cn.appscomm.bluetooth.model.SleepData;
import cn.appscomm.bluetooth.model.SportCacheData;

/**
 * @aboutContent: 蓝牙操作回掉
 * @author： 安
 * @crateTime: 2017/8/28 14:09
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18iResultCallBack implements ResultCallBack {
    private static final String TAG = "B18iResultCallBack";

    private DaoSession daoSession = MyApp.getDBManager().getDaoSession();

    public static B18iResultCallBack getB18iResultCallBack() {
        return new B18iResultCallBack();
    }

    @Override
    public void onSuccess(int resultType, Object[] objects) {

        Log.e("------resultType-------", Arrays.toString(objects));
        switch (resultType) {
            //连接
            case ResultCallBack.TYPE_CONNECT:
                Log.e(TAG, "-------------------connected");
                EventBus.getDefault().post(new B18iEventBus("connected"));
                BluetoothSDK.stopScan();
                break;
            //断开
            case ResultCallBack.TYPE_DISCONNECT:
                Log.i(TAG, "disconnected");
                break;

            case ResultCallBack.TYPE_GET_SN:
                Log.i(TAG, "SN : " + objects[0]);
                SharedPreferencesUtils.saveObject(MyApp.getContext(), "isClearDB", String.valueOf(objects[0]));
                break;
            case ResultCallBack.TYPE_GET_DEVICE_VERSION:
                Log.i(TAG, "device version : " + objects[0]);
                break;
            case ResultCallBack.TYPE_SET_TIME_SURFACE:
                Log.i(TAG, "set time format success!!!");
                break;
            case ResultCallBack.TYPE_SET_DEVICE_TIME:
                Log.i(TAG, "set time success!!!");
                break;

            case ResultCallBack.TYPE_GET_BATTERY_POWER:
                Log.i(TAG, "battery power : " + objects[0]);
                break;
            case ResultCallBack.TYPE_GET_UNIT:
                Log.i(TAG, "unit : " + objects[0]);
                break;
            case ResultCallBack.TYPE_SET_UNIT:
                Log.i(TAG, "set unit success");
                break;
            case ResultCallBack.TYPE_RESTORE_FACTORY://重置设备
                Log.i(TAG, "重置设备成功restore factory success");
                break;
            case ResultCallBack.TYPE_GET_USERINFO:
                if (objects.length > 0) {
                    //Long ids, int sex, int age, int height, int weight
                    B18iUserInforDatas b18iUserInforDatas = null;
                    if (daoSession == null) {
                        return;
                    }
                    //保存数据库
                    b18iUserInforDatas = new B18iUserInforDatas(null,
                            (int) objects[0], (int) objects[1], (int) objects[2], (int) objects[3]);
                    if (b18iUserInforDatas != null) {
                        daoSession.clear();
                        daoSession.insert(b18iUserInforDatas);
                    }
                    B18iUserInforDatasDao userDaos = daoSession.getB18iUserInforDatasDao();
                    QueryBuilder<B18iUserInforDatas> qb = userDaos.queryBuilder();
                    List<B18iUserInforDatas> userList = qb.list();
                    for (B18iUserInforDatas user : userList) {
                        if (user.getIds() > 1) {
                            daoSession.delete(user);
                        }
                    }
//                    ////************************************查看DB中的数据
//                    List<B18iUserInforDatas> userLists = qb.list();
//                    for (int i = 0; i < userLists.size(); i++) {
//                        Log.e("----user----db中已存的数据：", userLists.get(i).toString());
//                    }
                }
                Log.i(TAG, "bright screen time : " + Arrays.toString(objects));
                break;
            case ResultCallBack.TYPE_SET_USERINFO:
                Log.i(TAG, "set userinfo success");
                break;
            //获取目标步数
            case ResultCallBack.TYPE_GET_GOAL_SETTING:
                EventBus.getDefault().post(new B18iEventBus("goal", objects));
                Log.e(TAG, "goal : " + Arrays.toString(objects));
                break;
            //设置目标数
            case ResultCallBack.TYPE_SET_STEP_GOAL:
                EventBus.getDefault().post(new B18iEventBus("stepGoal"));
                Log.e(TAG, "set step goal success");
                break;
            case ResultCallBack.TYPE_SET_CALORIES_GOAL:
                Log.i(TAG, "set calories goal success");
                break;
            case ResultCallBack.TYPE_SET_DISTANCE_GOAL:
                Log.i(TAG, "set distance goal success");
                break;
            case ResultCallBack.TYPE_SET_SLEEP_GOAL:
                Log.i(TAG, "set sleep goal success");
                break;

            case ResultCallBack.TYPE_GET_SPORT_SLEEP_MODE:
                Log.i(TAG, "sport sleep mode : " + objects[0]);
                break;
            case ResultCallBack.TYPE_GET_TOTAL_SPORT_SLEEP_COUNT:
                Log.i(TAG, "total sport sleep count : " + Arrays.toString(objects));
                break;
            //总计心率数
            case ResultCallBack.TYPE_GET_TOTAL_HEARTRATE_COUNT:
                EventBus.getDefault().post(new B18iEventBus("totalHeart"));
                Log.e(TAG, "total heart rate count : " + objects[0]);
                break;

            case ResultCallBack.TYPE_DELETE_SPORT_DATA:
                Log.i(TAG, "delete sport date success");
                break;
            case ResultCallBack.TYPE_DELETE_SLEEP_DATA:
                Log.i(TAG, "delete sleep data success");
                break;
            case ResultCallBack.TYPE_DELETE_HEARTRATE_DATA:
                Log.i(TAG, "delete heart rate data success");
                break;
            //获取运动数据
            case ResultCallBack.TYPE_GET_SPORT_DATA:
                if (objects.length > 0) {
                    List<SportCacheData> sportCacheDatas = (List<SportCacheData>) objects[0];
                    B18iStepDatas b18iStepDatas = null;
                    if (daoSession == null) {
                        EventBus.getDefault().post(new B18iEventBus("sportData", objects[0]));
                        return;
                    }

                    B18iStepDatasDao stepDatasDao = daoSession.getB18iStepDatasDao();
                    Cursor query = DBManager.getOpenHelper().getWritableDatabase().
                            query(stepDatasDao.getTablename(), stepDatasDao.getAllColumns(), null, null, null, null, null);
                    List<Integer> integers = new ArrayList<>();
                    while (query.moveToNext()) {
                        int anInt = query.getInt(1);
                        if (!integers.contains(anInt)) {
                            integers.add(anInt);
                        }
                    }
                    query.close();
                    for (int i = 0; i < sportCacheDatas.size(); i++) {
                        //Long ids, int id, int step, int distance, int sporttime,
                        // int calories, String time, long timestamp
                        //添加数据时确保数据唯一性
                        if (!integers.contains(sportCacheDatas.get(i).id)) {
                            b18iStepDatas = new B18iStepDatas(null, sportCacheDatas.get(i).id,
                                    sportCacheDatas.get(i).step, sportCacheDatas.get(i).distance, sportCacheDatas.get(i).sporttime,
                                    sportCacheDatas.get(i).calories, sportCacheDatas.get(i).time, sportCacheDatas.get(i).timestamp);
                            if (b18iStepDatas != null) {
                                daoSession.clear();
                                daoSession.insert(b18iStepDatas);
                            }
                        }
                    }
                    integers.clear();
                    QueryBuilder<B18iStepDatas> qb = stepDatasDao.queryBuilder();
                    List<B18iStepDatas> stepDatasList = qb.list();
                    for (B18iStepDatas stepDatas : stepDatasList) {
                        //当数据条数》100时，删除最先插入的前50条数据
                        if (stepDatas.getIds() > 500) {
                            if (stepDatas.getId() < 50) {
                                daoSession.delete(stepDatas);
                            }
                        }
                    }
//                    ////************************************查看DB中的数据
//                    List<B18iStepDatas> stepDatasLista = qb.list();
//                    for (int i = 0; i < stepDatasLista.size(); i++) {
//                        Log.e("----sport----db中已存的数据：", stepDatasLista.get(i).getIds()
//                                + "==" + stepDatasLista.get(i).id
//                                + "=步数=" + stepDatasLista.get(i).step
//                                + "=距离=" + stepDatasLista.get(i).distance
//                                + "==" + stepDatasLista.get(i).sporttime
//                                + "=卡路里=" + stepDatasLista.get(i).calories
//                                + "==" + stepDatasLista.get(i).time
//                                + "==" + stepDatasLista.get(i).timestamp);
//                    }
                    /*******************          *******************/
                    EventBus.getDefault().post(new B18iEventBus("sportData", objects[0]));
                } else {
                    Log.i(TAG, "There is no sport data ");
                }
                break;
            //睡眠数据
            case ResultCallBack.TYPE_GET_SLEEP_DATA:
                if (objects.length > 0) {
                    List<SleepData> sleepDatas = (List<SleepData>) objects[0];
                    B18iSleepDatas b18iSleepDatas = null;
                    if (daoSession == null) {
                        EventBus.getDefault().post(new B18iEventBus("sleepData", sleepDatas));
                        return;
                    }
                    B18iSleepDatasDao sleepDatasDao = daoSession.getB18iSleepDatasDao();
                    Cursor query = DBManager.getOpenHelper().getWritableDatabase().
                            query(sleepDatasDao.getTablename(), sleepDatasDao.getAllColumns(), null, null, null, null, null);
                    List<Integer> integers = new ArrayList<>();
                    while (query.moveToNext()) {
                        int anInt = query.getInt(1);
                        if (!integers.contains(anInt)) {
                            integers.add(anInt);
                        }
                    }
                    query.close();
                    for (int i = 0; i < sleepDatas.size(); i++) {
                        //Long ids, int id, int total, int awake, int light,int deep,
                        // int awaketime, String detail, String date, int flag, int type,long timeStamp
                        //保存数据库
                        if (!integers.contains(sleepDatas.get(i).id)) {
                            b18iSleepDatas = new B18iSleepDatas(null,
                                    sleepDatas.get(i).id,
                                    sleepDatas.get(i).total, sleepDatas.get(i).deep, sleepDatas.get(i).light,
                                    sleepDatas.get(i).awake, sleepDatas.get(i).awaketime, sleepDatas.get(i).detail, sleepDatas.get(i).date,
                                    sleepDatas.get(i).flag, sleepDatas.get(i).type, sleepDatas.get(i).timeStamp);
                            if (b18iSleepDatas != null) {
                                daoSession.clear();
                                daoSession.insert(b18iSleepDatas);
                            }
                        }
                    }
                    integers.clear();
                    QueryBuilder<B18iSleepDatas> qb = sleepDatasDao.queryBuilder();
                    List<B18iSleepDatas> sleepDatasList = qb.list();
                    for (B18iSleepDatas datas : sleepDatasList) {
                        if (datas.getIds() > 500) {
                            if (datas.getIds() < 50) {
                                daoSession.delete(b18iSleepDatas);
                            }
                        }
                    }
//                    ////************************************查看DB中的数据
//                    List<B18iSleepDatas> sleepDatasLists = qb.list();
//                    for (int i = 0; i < sleepDatasLists.size(); i++) {
//                        Log.e("----sleep----db中已存的数据：", sleepDatasLists.get(i).id + "=="
//                                + sleepDatasLists.get(i).total + "==" + sleepDatasLists.get(i).awake
//                                + "==" + sleepDatasLists.get(i).light + "==" + sleepDatasLists.get(i).deep
//                                + "==" + sleepDatasLists.get(i).awaketime + "==" + sleepDatasLists.get(i).detail
//                                + "==" + sleepDatasLists.get(i).date + "==" + sleepDatasLists.get(i).flag
//                                + "==" + sleepDatasLists.get(i).type + "==" + sleepDatasLists.get(i).timeStamp);
//                    }
                    /*******************          *******************/
                    EventBus.getDefault().post(new B18iEventBus("sleepData", sleepDatas));
                } else {
                    Log.i(TAG, "There is no sleep data ");
                }
                break;
            //获取心率数据
            case ResultCallBack.TYPE_GET_HEARTRATE_DATA:
                if (objects.length > 0) {
                    List<HeartRateData> heartRateDatas = (List<HeartRateData>) objects[0];
                    B18iHeartDatas b18iHeartDatas = null;
                    if (daoSession == null) {
                        EventBus.getDefault().post(new B18iEventBus("heartRate", heartRateDatas));
                    }
                    B18iHeartDatasDao heartDatasDao = daoSession.getB18iHeartDatasDao();
                    Cursor query = DBManager.getOpenHelper().getWritableDatabase().
                            query(heartDatasDao.getTablename(), heartDatasDao.getAllColumns(), null, null, null, null, null);
                    List<Integer> integers = new ArrayList<>();
                    while (query.moveToNext()) {
                        int anInt = query.getInt(1);
                        if (!integers.contains(anInt)) {
                            integers.add(anInt);
                        }
                    }
                    query.close();
                    for (int i = 0; i < heartRateDatas.size(); i++) {
                        //Long ids, int id, int avg, String date, long timestamp
                        //保存数据库
                        if (!integers.contains(heartRateDatas.get(i).id))
                            b18iHeartDatas = new B18iHeartDatas(null,
                                    heartRateDatas.get(i).id, heartRateDatas.get(i).avg,
                                    heartRateDatas.get(i).date, heartRateDatas.get(i).timestamp);
                        if (b18iHeartDatas != null) {
                            daoSession.clear();
                            daoSession.insert(b18iHeartDatas);
                        }
                    }
                    integers.clear();
                    QueryBuilder<B18iHeartDatas> heartDatasQueryBuilder = heartDatasDao.queryBuilder();
                    List<B18iHeartDatas> heartDatasList = heartDatasQueryBuilder.list();
                    for (B18iHeartDatas heartDatass : heartDatasList) {
                        if (heartDatass.getIds() > 1000) {
                            if (heartDatass.getIds() < 50) {
                                daoSession.delete(b18iHeartDatas);
                            }
                        }
                    }
                    ////************************************查看DB中的数据
                    List<B18iHeartDatas> list = heartDatasQueryBuilder.list();
                    for (int i = 0; i < list.size(); i++) {
                        Log.e("----heart----db中已存的数据：", list.get(i).getIds()
                                + "==" + list.get(i).date + "==" + list.get(i).avg + "==" + list.get(i).timestamp);
                    }
                    /*******************          *******************/
                    EventBus.getDefault().post(new B18iEventBus("heartRate", heartRateDatas));
                } else {
                    Log.i(TAG, "There is no heart rate data ");
                }
                break;

            case ResultCallBack.TYPE_GET_AUTO_SLEEP:
                Log.i(TAG, "auto sleep : " + Arrays.toString(objects));
                break;
            case ResultCallBack.TYPE_SET_AUTO_SLEEP:
                Log.i(TAG, "set auto sleep success");
                break;
            case ResultCallBack.TYPE_GET_AUTO_HEARTRATE:
                EventBus.getDefault().post(new B18iEventBus("autoHeart", objects[0]));
                Log.i(TAG, "auto heart rate : " + objects[0]);
                break;
            case ResultCallBack.TYPE_SET_AUTO_HEARTRATE:
                Log.i(TAG, "set auto heart rate success");
                break;

            case ResultCallBack.TYPE_GET_HEARTRATE_ALARM_THRESHOLD:
                Log.i(TAG, "heart rate alarm threshold : " + Arrays.toString(objects));
                break;
            case ResultCallBack.TYPE_SET_HEARTRATE_ALARM_THRESHOLD:
                Log.i(TAG, "set heart rate alarm threshold success");
                break;

            case ResultCallBack.TYPE_GET_SWITCH_SETTING:
                Log.i(TAG, "switch mysetting : " + Arrays.toString(objects));
                break;
            case ResultCallBack.TYPE_SET_SWITCH_ANTI_LOST:
                Log.i(TAG, "set switch anti lost success");
                break;
            case ResultCallBack.TYPE_SET_SWITCH_SENSOR:
                Log.i(TAG, "set switch sensor success");
                break;
            case ResultCallBack.TYPE_SENSOR_DATA:
                Log.i(TAG, "sensor data : " + Arrays.toString(objects));
                break;

            case ResultCallBack.TYPE_GET_REMINDER_COUNT:
                Log.i(TAG, "reminder count : " + Arrays.toString(objects));
                break;
            //闹钟
            case ResultCallBack.TYPE_GET_REMINDER:
                if (objects.length > 0) {
                    List<ReminderData> reminderDatas = (List<ReminderData>) objects[0];
                    for (ReminderData iReminderDatas : reminderDatas) {
                        Log.e("----heart----db中已存的数据：", iReminderDatas.id + "==" + iReminderDatas.type
                                + "==" + iReminderDatas.hour + "==" + iReminderDatas.min
                                + "==" + iReminderDatas.cycle + "==" + iReminderDatas.status + "==" + iReminderDatas.content);
                    }
                } else {
                    Log.i(TAG, "There is no reminder");
                }
                break;
            case ResultCallBack.TYPE_NEW_REMINDER:
                Log.i(TAG, "add reminder success");
                break;
            case ResultCallBack.TYPE_CHANGE_REMINDER:
                Log.i(TAG, "change reminder success");
                break;
            case ResultCallBack.TYPE_DELETE_A_REMINDER:
                Log.i(TAG, "delete a reminder success");
                break;
            case ResultCallBack.TYPE_DELETE_ALL_REMINDER:
                Log.i(TAG, "delete all reminder success");
                break;

            case ResultCallBack.TYPE_OPEN_HEARTRATE:
                Log.i(TAG, "open heart rate success");
                break;
            case ResultCallBack.TYPE_CLOSE_HEARTRATE:
                Log.i(TAG, "close heart rate success");
                break;
            case ResultCallBack.TYPE_REAL_TIME_HEARTRATE_DATA:
                Log.i(TAG, "real time heart rate : " + objects[0]);
                break;
            case ResultCallBack.TYPE_JUMP_TO_REAL_HEARTRATE:
                break;
            //返回到主页
            case ResultCallBack.TYPE_BACK_TO_HOME:
                Log.i(TAG, "back to home success");
                break;
            case ResultCallBack.TYPE_GOTO_UPDATE:
                Log.i(TAG, "go to update success");
                break;
            case ResultCallBack.TYPE_BIND_END:
                Log.i(TAG, "----------------s" + "+==" + Arrays.toString(objects));
                break;
            case ResultCallBack.TYPE_BIND_START:
                Log.i(TAG, "----------------s" + "+==" + Arrays.toString(objects));
                break;
        }
    }

    @Override
    public void onFail(int i) {

    }

}
