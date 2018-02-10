package com.example.bozhilun.android.h9.h9monitor;

import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.util.Log;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.sdk.bluetooth.bean.BloodData;
import com.sdk.bluetooth.bean.HeartData;
import com.sdk.bluetooth.bean.SleepData;
import com.sdk.bluetooth.bean.SportsData;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.bind.BindEnd;
import com.sdk.bluetooth.protocol.command.bind.BindStart;
import com.sdk.bluetooth.protocol.command.clear.ClearBloodData;
import com.sdk.bluetooth.protocol.command.clear.ClearHeartData;
import com.sdk.bluetooth.protocol.command.clear.ClearSleepData;
import com.sdk.bluetooth.protocol.command.clear.ClearSportData;
import com.sdk.bluetooth.protocol.command.count.AllDataCount;
import com.sdk.bluetooth.protocol.command.data.DeviceDisplaySportSleep;
import com.sdk.bluetooth.protocol.command.data.GetBloodData;
import com.sdk.bluetooth.protocol.command.data.GetHeartData;
import com.sdk.bluetooth.protocol.command.data.GetSleepData;
import com.sdk.bluetooth.protocol.command.data.GetSportData;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;
import com.sdk.bluetooth.protocol.command.data.SportSleepMode;
import com.sdk.bluetooth.protocol.command.device.BatteryPower;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.device.DeviceVersion;
import com.sdk.bluetooth.protocol.command.device.Language;
import com.sdk.bluetooth.protocol.command.device.Motor;
import com.sdk.bluetooth.protocol.command.device.RestoreFactory;
import com.sdk.bluetooth.protocol.command.device.TimeSurfaceSetting;
import com.sdk.bluetooth.protocol.command.device.Unit;
import com.sdk.bluetooth.protocol.command.device.WatchID;
import com.sdk.bluetooth.protocol.command.expands.AutomaticCorrectionTime;
import com.sdk.bluetooth.protocol.command.expands.CorrectionTime;
import com.sdk.bluetooth.protocol.command.expands.Point2Zero;
import com.sdk.bluetooth.protocol.command.expands.RemindCount;
import com.sdk.bluetooth.protocol.command.expands.TurnPointers;
import com.sdk.bluetooth.protocol.command.other.BloodStatus;
import com.sdk.bluetooth.protocol.command.other.HeartStatus;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.setting.AutoSleep;
import com.sdk.bluetooth.protocol.command.setting.GoalsSetting;
import com.sdk.bluetooth.protocol.command.setting.HeartRateAlarm;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;
import com.sdk.bluetooth.protocol.command.user.UserInfo;
import com.sdk.bluetooth.utils.DateUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/9/30 09:17
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class CommandResultCallback implements BaseCommand.CommandResultCallback {

    private final String TAG = "----->>>" + this.getClass().toString();

    public static CommandResultCallback getCommandResultCallback() {
        return new CommandResultCallback();
    }

    @Override
    public void onSuccess(BaseCommand baseCommand) {
        if ((baseCommand instanceof WatchID)) {//手表Id
            String watch = GlobalVarManager.getInstance().getWatchID();
            Log.d(TAG, "手表IDWatchID:---71---" + watch);
//            showTipDialog(watch);
        } else if (baseCommand instanceof DeviceVersion) {//设备版本
            String localVersion = GlobalVarManager.getInstance().getSoftVersion();
            Log.d(TAG, "设备版本DeviceVersion:---75---" + localVersion);
//            showTipDialog(localVersion);
        } else if (baseCommand instanceof BatteryPower) {//电池、电源
            Log.d(TAG, "电池、电源BatteryPower:---78---" + GlobalVarManager.getInstance().getBatteryPower() + "%");
//            showTipDialog(GlobalVarManager.getInstance().getBatteryPower() + "%");
        } else if (baseCommand instanceof TimeSurfaceSetting || baseCommand instanceof Unit || baseCommand instanceof Motor) {//时间显示格式、单位、马达震动
            Log.d(TAG, "时间显示格式TimeSurfaceSetting、单位Unit、马达震动Motor:---81---" + "设置成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof SportSleepMode) {//运动睡眠模式
            if (GlobalVarManager.getInstance().getSportSleepMode() == 0) {
                Log.d(TAG, "运动睡眠模式SportSleepMode:---85---" + "sport model");
//                showTipDialog("sport model");
            } else {
                Log.d(TAG, "运动睡眠模式SportSleepMode:---88---" + "sleep model");
//                showTipDialog("sleep model");
            }
        } else if (baseCommand instanceof DeviceDisplaySportSleep) {//运动睡眠显示装置
            Log.d(TAG, "运动睡眠显示装置DeviceDisplaySportSleep:---92---" + "步数Step:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
                    "\n 卡路里Calorie:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
                    "\n 距离Distance:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
                    "\n 睡眠时间Sleep time:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
            List<String> deviceDisplaySportSleepList = new ArrayList<>();
            deviceDisplaySportSleepList.add(GlobalVarManager.getInstance().getDeviceDisplayStep() + "");
            deviceDisplaySportSleepList.add(GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "");
            deviceDisplaySportSleepList.add(GlobalVarManager.getInstance().getDeviceDisplayDistance() + "");
            deviceDisplaySportSleepList.add(GlobalVarManager.getInstance().getDeviceDisplaySleep() + "");
            EventBus.getDefault().post(new B18iEventBus("deviceDisplaySportSleepList", deviceDisplaySportSleepList));
//            showTipDialog("Step:" + GlobalVarManager.getInstance().getDeviceDisplayStep() + "step" +
//                    "\n Calorie:" + GlobalVarManager.getInstance().getDeviceDisplayCalorie() + "cal" +
//                    "\n Distance:" + GlobalVarManager.getInstance().getDeviceDisplayDistance() + "m" +
//                    "\n Sleep time:" + GlobalVarManager.getInstance().getDeviceDisplaySleep() + "min");
        } else if (baseCommand instanceof BindStart || baseCommand instanceof UserInfo//绑定开始、用户信息
                || baseCommand instanceof BindEnd//绑定结束
                || baseCommand instanceof GoalsSetting//目标设置
                || baseCommand instanceof RestoreFactory || baseCommand instanceof Language) {//恢复出厂、语言
//            showTipDialog(getResources().getString(R.string.successful));
            Log.d(TAG, "绑定开始、用户信息、绑定结束、目标设置、恢复出厂、语言:---105---" + "其中一个设置成功");
        } else if (baseCommand instanceof DateTime) {//日期时间
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取时间
//                showTipDialog(GlobalVarManager.getInstance().getDeviceDateTime());
                Log.d(TAG, "获取时间DateTime-get:---109---" + GlobalVarManager.getInstance().getDeviceDateTime());
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置时间
                Log.d(TAG, "获取时间DateTime-set:---112---" + "时间设置成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
        } else if (baseCommand instanceof AutoSleep) {//自动休眠
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//获取
                Log.d(TAG, "自动休眠AutoSleep-get:---117---" + "enter sleep:" + GlobalVarManager.getInstance().getEnterSleepHour() + "hour" +
                        "\n enter sleep:" + GlobalVarManager.getInstance().getEnterSleepMin() + "min" +
                        "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepHour() + "hour" +
                        "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepMin() + "min" +
                        "\n myremind sleep cycle:" + GlobalVarManager.getInstance().getRemindSleepCycle());
//                showTipDialog("enter sleep:" + GlobalVarManager.getInstance().getEnterSleepHour() + "hour" +
//                        "\n enter sleep:" + GlobalVarManager.getInstance().getEnterSleepMin() + "min" +
//                        "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepHour() + "hour" +
//                        "\n quit sleep:" + GlobalVarManager.getInstance().getQuitSleepMin() + "min" +
//                        "\n myremind sleep cycle:" + GlobalVarManager.getInstance().getRemindSleepCycle());
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置
                Log.d(TAG, "自动休眠AutoSleep-set:---124---" + "自动休眠设置成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
        } else if (baseCommand instanceof GetSportData) {//获取运动数据
            //
            int step = 0;
            int calorie = 0;
            int distance = 0;
            if (GlobalDataManager.getInstance().getSportsDatas() == null) {
//                showTipDialog("null");
            } else {
                for (SportsData sportsData : GlobalDataManager.getInstance().getSportsDatas()) {
                    step += sportsData.sport_steps;
                    calorie += sportsData.sport_cal;
                    distance += sportsData.sport_energy;
                }
                Log.d(TAG, "获取运动数据GetSportData:---145---"
                        + "Step:" + step + "step" +
                        "\n Calorie:" + calorie + "cal" +
                        "\n Distance:" + distance + "m");
//                showTipDialog("Step:" + step + "step" +
//                        "\n Calorie:" + calorie + "cal" +
//                        "\n Distance:" + distance + "m");
            }
        } else if (baseCommand instanceof GetSleepData) {//获取睡眠数据
            if (GlobalDataManager.getInstance().getSleepDatas() == null) {
                Log.d(TAG, "获取睡眠数据:---155---" + "...null...");
//                showTipDialog("null");
            } else {
                LinkedList<SleepData> sleepDatas = GlobalDataManager.getInstance().getSleepDatas();
                EventBus.getDefault().post(new B18iEventBus("sleepDatas", sleepDatas));
                // sleepData.sleep_type
                // 0：睡着
                // 1：浅睡
                // 2：醒着
                // 3：准备入睡
                // 4：退出睡眠
                // 16：进入睡眠模式
                // 17：退出睡眠模式（本次睡眠非预设睡眠）
                // 18：退出睡眠模式（本次睡眠为预设睡眠）
                String sleepStr = "";
                for (SleepData sleepData : GlobalDataManager.getInstance().getSleepDatas()) {
                    sleepStr = sleepStr + DateUtil.dateToSec(DateUtil.timeStampToDate(sleepData.sleep_time_stamp * 1000)) + " 类型:" + sleepData.sleep_type + "\n";
                }
                Log.d(TAG, "获取睡眠数据:---171---" + sleepStr);
//                showTipDialog(sleepStr);
            }
        } else if (baseCommand instanceof ClearSportData) {//清除运动数据
            Log.d(TAG, "清除运动数据ClearSportData:---175---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof ClearSleepData) {//清除睡眠数据
            Log.d(TAG, "清除睡眠数据ClearSleepData:---178---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof SwitchSetting) {//通知开关
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {//读取
                // 防丢开关
                // 自动同步开关
                // 睡眠开关
                // 自动睡眠监测开关
                // 来电提醒开关
                // 未接来电提醒开关
                // 短信提醒开关
                // 社交提醒开关
                // 邮件提醒开关
                // 日历开关
                // 久坐提醒开关
                // 超低功耗功能开关
                // 二次提醒开关

                // 运动心率模式开关
                // FACEBOOK开关
                // TWITTER开关
                // INSTAGRAM开关
                // QQ开关
                // WECHAT开关
                // WHATSAPP开关
                // LINE开关
                Log.d(TAG, "通知开关SwitchSetting-read:---204---" + "isAntiLostSwitch:" + GlobalVarManager.getInstance().isAntiLostSwitch()
                        + "\n isAutoSyncSwitch:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
                        + "\n isSleepSwitch:" + GlobalVarManager.getInstance().isSleepSwitch()
                        + "\n isSleepStateSwitch:" + GlobalVarManager.getInstance().isSleepStateSwitch()
                        + "\n isIncomePhoneSwitch:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
                        + "\n isMissPhoneSwitch:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
                        + "\n isSmsSwitch:" + GlobalVarManager.getInstance().isSmsSwitch()
                        + "\n isSocialSwitch:" + GlobalVarManager.getInstance().isSocialSwitch()
                        + "\n isMailSwitch:" + GlobalVarManager.getInstance().isMailSwitch()
                        + "\n isCalendarSwitch:" + GlobalVarManager.getInstance().isCalendarSwitch()
                        + "\n isSedentarySwitch:" + GlobalVarManager.getInstance().isSedentarySwitch()
                        + "\n isLowPowerSwitch:" + GlobalVarManager.getInstance().isLowPowerSwitch()
                        + "\n isSecondRemindSwitch:" + GlobalVarManager.getInstance().isSecondRemindSwitch()
                        + "\n isSportHRSwitch:" + GlobalVarManager.getInstance().isSportHRSwitch()
                        + "\n isFacebookSwitch:" + GlobalVarManager.getInstance().isFacebookSwitch()
                        + "\n isTwitterSwitch:" + GlobalVarManager.getInstance().isTwitterSwitch()
                        + "\n isInstagamSwitch:" + GlobalVarManager.getInstance().isInstagamSwitch()
                        + "\n isQqSwitch:" + GlobalVarManager.getInstance().isQqSwitch()
                        + "\n isWechatSwitch:" + GlobalVarManager.getInstance().isWechatSwitch()
                        + "\n isWhatsappSwitch:" + GlobalVarManager.getInstance().isWhatsappSwitch()
                        + "\n isLineSwitch:" + GlobalVarManager.getInstance().isLineSwitch());
//                showTipDialog("isAntiLostSwitch:" + GlobalVarManager.getInstance().isAntiLostSwitch()
//                        + "\n isAutoSyncSwitch:" + GlobalVarManager.getInstance().isAutoSyncSwitch()
//                        + "\n isSleepSwitch:" + GlobalVarManager.getInstance().isSleepSwitch()
//                        + "\n isSleepStateSwitch:" + GlobalVarManager.getInstance().isSleepStateSwitch()
//                        + "\n isIncomePhoneSwitch:" + GlobalVarManager.getInstance().isIncomePhoneSwitch()
//                        + "\n isMissPhoneSwitch:" + GlobalVarManager.getInstance().isMissPhoneSwitch()
//                        + "\n isSmsSwitch:" + GlobalVarManager.getInstance().isSmsSwitch()
//                        + "\n isSocialSwitch:" + GlobalVarManager.getInstance().isSocialSwitch()
//                        + "\n isMailSwitch:" + GlobalVarManager.getInstance().isMailSwitch()
//                        + "\n isCalendarSwitch:" + GlobalVarManager.getInstance().isCalendarSwitch()
//                        + "\n isSedentarySwitch:" + GlobalVarManager.getInstance().isSedentarySwitch()
//                        + "\n isLowPowerSwitch:" + GlobalVarManager.getInstance().isLowPowerSwitch()
//                        + "\n isSecondRemindSwitch:" + GlobalVarManager.getInstance().isSecondRemindSwitch()
//                        + "\n isSportHRSwitch:" + GlobalVarManager.getInstance().isSportHRSwitch()
//                        + "\n isFacebookSwitch:" + GlobalVarManager.getInstance().isFacebookSwitch()
//                        + "\n isTwitterSwitch:" + GlobalVarManager.getInstance().isTwitterSwitch()
//                        + "\n isInstagamSwitch:" + GlobalVarManager.getInstance().isInstagamSwitch()
//                        + "\n isQqSwitch:" + GlobalVarManager.getInstance().isQqSwitch()
//                        + "\n isWechatSwitch:" + GlobalVarManager.getInstance().isWechatSwitch()
//                        + "\n isWhatsappSwitch:" + GlobalVarManager.getInstance().isWhatsappSwitch()
//                        + "\n isLineSwitch:" + GlobalVarManager.getInstance().isLineSwitch()
//                );
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {//设置
                Log.d(TAG, "通知开关SwitchSetting-write:---249---" + "成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
        } else if (baseCommand instanceof MsgCountPush) {
            Log.d(TAG, "MsgCountPush:---253---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof TurnPointers) {
            Log.d(TAG, "TurnPointers:---256---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof AutomaticCorrectionTime) {//自动校正时间
            Log.d(TAG, "自动校正时间AutomaticCorrectionTime:---259---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof CorrectionTime) {//校正时间
            Log.d(TAG, "校正时间CorrectionTime:---262---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        } else if (baseCommand instanceof Point2Zero) {//零点
            Log.d(TAG, "零点Point2Zero:---265---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        }
        //提醒相关
        else if (baseCommand instanceof RemindCount) {
            Log.d(TAG, "提醒数量RemindCount-read:---270---" + GlobalVarManager.getInstance().getRemindCount());
//            showTipDialog(GlobalVarManager.getInstance().getRemindCount() + "");
        } else if (baseCommand instanceof RemindSetting) {
            Log.d(TAG, "提醒数量RemindCount-set:---273---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        }

        // all data count
        if (baseCommand instanceof AllDataCount) {
            Log.d(TAG, "AllDataCount:---279---" + "SportCount:" + GlobalVarManager.getInstance().getSportCount()
                    + "\n SleepCount:" + GlobalVarManager.getInstance().getSleepCount()
                    + "\n HeartRateCount:" + GlobalVarManager.getInstance().getHeartRateCount()
                    + "\n BloodCount:" + GlobalVarManager.getInstance().getBloodCount());
//            showTipDialog("SportCount:" + GlobalVarManager.getInstance().getSportCount()
//                    + "\n SleepCount:" + GlobalVarManager.getInstance().getSleepCount()
//                    + "\n HeartRateCount:" + GlobalVarManager.getInstance().getHeartRateCount()
//                    + "\n BloodCount:" + GlobalVarManager.getInstance().getBloodCount()
//            );
            AppsBluetoothManager.getInstance(MyApp.getContext())
                    .sendCommand(new GetHeartData(CommandResultCallback.getCommandResultCallback(), 0, new Date().getTime() / 1000, (int) GlobalVarManager.getInstance().getHeartRateCount()));
        }

        // 心率
        if (baseCommand instanceof HeartRateAlarm) {
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {   //设置
                Log.d(TAG, "心率预警HeartRateAlarm-set:---293---" + "成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {    //查询
                Log.d(TAG, "心率预警HeartRateAlarm-read:---297---" + "HighLimit：" + GlobalVarManager.getInstance().getHighHeartLimit() + " bpm \n" +
                        "LowLimit：" + GlobalVarManager.getInstance().getHighHeartLimit() + " bpm \n" +
                        "AutoHeart：" + GlobalVarManager.getInstance().getAutoHeart() + " min \n" +
                        "isHeartAlarm：" + GlobalVarManager.getInstance().isHeartAlarm() + "\n" +
                        "isAutoHeart：" + GlobalVarManager.getInstance().isAutoHeart());
                //获取设备的心率预警状态(暂时获取本地的)
//                showTipDialog(
//                        "HighLimit：" + GlobalVarManager.getInstance().getHighHeartLimit() + " bpm \n" +
//                                "LowLimit：" + GlobalVarManager.getInstance().getHighHeartLimit() + " bpm \n" +
//                                "AutoHeart：" + GlobalVarManager.getInstance().getAutoHeart() + " min \n" +
//                                "isHeartAlarm：" + GlobalVarManager.getInstance().isHeartAlarm() + "\n" +
//                                "isAutoHeart：" + GlobalVarManager.getInstance().isAutoHeart()
//                );
            }
        }

        if (baseCommand instanceof GetHeartData) {
            LinkedList<HeartData> heartDatas = GlobalDataManager.getInstance().getHeartDatas();
            EventBus.getDefault().post(new B18iEventBus("heartDatas", heartDatas));
            String heartDatas1 = "";
            for (HeartData heartData1 : GlobalDataManager.getInstance().getHeartDatas()) {
                heartDatas1 += "value:" + heartData1.heartRate_value + "---time:" + DateUtil.dateToSec(DateUtil.timeStampToDate(heartData1.time_stamp * 1000)) + "\n";
            }
            Log.d(TAG, "心率数据GetHeartData:---318---" + heartDatas1);
//            showTipDialog(heartDatas);
        }
        if (baseCommand instanceof ClearHeartData) {
            Log.d(TAG, "ClearHeartData:---322---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        }
        if (baseCommand instanceof HeartStatus) {
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                Log.d(TAG, "HeartStatus:---327---" + "status:" + GlobalVarManager.getInstance().isHeartMeasure());
//                showTipDialog("status:" + GlobalVarManager.getInstance().isHeartMeasure());
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                Log.d(TAG, "HeartStatus:---331---" + "成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
        }

        // 血压
        if (baseCommand instanceof GetBloodData) {
            String bloodDatas = "";
            for (BloodData bloodData : GlobalDataManager.getInstance().getBloodDatas()) {
                bloodDatas += "bigValue:" + bloodData.bigValue + "minValue:" + bloodData.minValue + "---time:" + DateUtil.dateToSec(DateUtil.timeStampToDate(bloodData.time_stamp * 1000)) + "\n";
            }
            Log.d(TAG, "GetBloodData:---342---" + bloodDatas);
//            showTipDialog(bloodDatas);
        }
        if (baseCommand instanceof ClearBloodData) {
            Log.d(TAG, "ClearBloodData:---346---" + "成功");
//            showTipDialog(getResources().getString(R.string.successful));
        }

        if (baseCommand instanceof BloodStatus) {
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                Log.d(TAG, "BloodStatus:---353---" + "status:" + GlobalVarManager.getInstance().isBloodMeasure());
//                showTipDialog("status:" + GlobalVarManager.getInstance().isBloodMeasure());
            }
            if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                Log.d(TAG, "BloodStatus:---353---" + "成功");
//                showTipDialog(getResources().getString(R.string.successful));
            }
        }
    }

    @Override
    public void onFail(BaseCommand baseCommand) {
        if ((baseCommand instanceof WatchID)) {
        } else if (baseCommand instanceof DeviceVersion) {
        } else if (baseCommand instanceof BatteryPower) {
        } else {
            Log.d(TAG, "WatchID:---368---" + "失败");
//            showTipDialog(getResources().getString(R.string.failed));
        }
    }
}
