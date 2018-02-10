package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.h9.h9monitor.CommandResultCallback;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.push.CalanderPush;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.SocialPush;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.appscomm.bluetooth.app.BluetoothSDK;


/**
 * 提醒服务
 * 通过通知获取APP消息内容，需要打开通知功能
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AlertService extends MyNotificationListenerService {
    private static final String TAG = "AlertService";
    private static final String H8_NAME_TAG = "bozlun";

    //QQ
    private static final String QQ_PACKAGENAME = "com.tencent.mobileqq";
    //微信
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    //微博
    private static final String WEIBO_PACKAGENAME = "com.sina.weibo";
    //Facebook
    private static final String FACEBOOK_PACKAGENAME = "com.facebook.katana";
    //twitter
    private static final String TWITTER_PACKAGENAME = "com.twitter.android";
    //Whats
    private static final String WHATS_PACKAGENAME = "com.whatsapp";
    //viber
    private static final String VIBER_PACKAGENAME = "com.viber.voip";
    //instagram
    private static final String INSTANRAM_PACKAGENAME = "com.instagram.android";
    //日历
    private static final String CALENDAR_PACKAGENAME = "com.android.calendar";
    //信息 三星手机信息
    private static final String SAMSUNG_MSG_PACKNAME = "com.samsung.android.messaging";
    private static final String SAMSUNG_MSG_SRVERPCKNAME = "com.samsung.android.communicationservice";
    private static final String MSG_PACKAGENAME = "com.android.mms";

    private String newmsg = "";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "----------1111-onCreate--");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        Log.d(TAG, "---------222-onCreate--");
    }

    //当系统收到新的通知后出发回调
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //获取应用包名
        String packageName = sbn.getPackageName();

        Log.d(TAG, "=====kkkk===" + sbn.toString());
        Log.e(TAG, packageName);
        //获取notification对象
        Notification notification = sbn.getNotification();
        //获取消息内容
        CharSequence tickerText = notification.tickerText;
        if(tickerText != null){
            String msgCont = tickerText.toString();
            if(!WatchUtils.isEmpty(msgCont) && !msgCont.equals("[]")){
                String h9Msg = msgCont.substring(1, msgCont.length() - 1);    //去掉[]
                Log.e(TAG,"-------newmsg----"+h9Msg);
//                if (newmsg.length() > 16) {
//                    newmsg = h9Msg.substring(0, 16);
//                } else {
//                    newmsg = h9Msg + "....";
//                }
                //过滤包名
                if (packageName.equals(QQ_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "QQ");
                    //H9
                    sendMessH9(SocialPush.QQ, h9Msg, (byte) 0x08);

                    sendMsg(packageName, h9Msg);
                    // 微信
                } else if (packageName.equals(WECHAT_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "WeChat");
                    //H9
                    sendMessH9(SocialPush.WECHAT, h9Msg, (byte) 0x09);

                    sendMsg(packageName, h9Msg);
                }
                //facebook
                else if (packageName.equals(FACEBOOK_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "FaceBook");
                    //H9
                    sendMessH9(SocialPush.FACEBOOK, h9Msg, (byte) 0x0A);

                    sendMsg(packageName, h9Msg);
                }
                //Twitter
                else if (packageName.equals(TWITTER_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "Twitter");
                    //H9
                    sendMessH9(SocialPush.TWITTER, h9Msg, (byte) 0x0B);

                    sendMsg(packageName, h9Msg);
                }
                //Whats
                else if (packageName.equals(WHATS_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "Whats");
                    //H9
                    sendMessH9(SocialPush.WHATSAPP, h9Msg, (byte) 0x0C);

                    sendMsg(packageName, h9Msg);
                }    //Instagram
                else if (packageName.equals(INSTANRAM_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "Instagram");
                    //H9
                    sendMessH9(SocialPush.INSTAGRAM, h9Msg, (byte) 0x0F);

                    sendMsg(packageName, h9Msg);
                }
                //viber
                else if (packageName.equals(VIBER_PACKAGENAME)) {
                    //B18I手环
                    setSocialSMS(h9Msg, "viber");
                    sendMsg(packageName, h9Msg);
                } else if (packageName.equals(CALENDAR_PACKAGENAME)) {//日历提醒
                    //B18I手环
                    setSocialSMS(h9Msg, "calendar");
                    //H9
                    sendCalendarsH9(h9Msg);
                } else if (packageName.equals(MSG_PACKAGENAME) || packageName.equals(SAMSUNG_MSG_PACKNAME)
                        || packageName.equals(SAMSUNG_MSG_SRVERPCKNAME)) {
                    sendMsg(packageName, h9Msg);
                } else if (packageName.equals("com.android.phone")) {
                    sendMsg(packageName, h9Msg);
                }
            }
        }

    }


    /**
     * 日历消息推送
     *
     * @param newmsg
     */
    private void sendCalendarsH9(String newmsg) {
        String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanya");
        if (!TextUtils.isEmpty(mylanya) && mylanya.equals("W06X")) {
            if ((boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SOCIAL")) {
                String[] strings = B18iUtils.stringToArray(String.valueOf(newmsg));//分割出name
                String title = strings[0];
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i < strings.length; i++) {
                    sb.append(strings[i]);
                }
                Log.d(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
                sendCalendar(title + "" + sb.toString(), B18iUtils.H9TimeData(), MsgCountPush.SCHEDULE_TYPE, 1);
            }
            return;
        }
    }

    public void sendMsg(String pakage, String msg) {
        Log.d(TAG, "------package---" + pakage + "---msg--" + msg);
        //qq
        if (pakage.equals(QQ_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg"))) {
                    sendTo("qq", msg);
                }
            }
            // 微信
        } else if (pakage.equals(WECHAT_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg"))) {
                    sendTo("wechat", msg);
                }
            }
        }
        //facebook
        else if (pakage.equals(FACEBOOK_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook"))) {
                    sendTo("facebook", msg);
                }
            }
        }
        //Twitter
        else if (pakage.equals(TWITTER_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa"))) {
                    sendTo("twitter", msg);
                }
            }
        }
        //Whats
        else if (pakage.equals(WHATS_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp"))) {
                    sendTo("whats", msg);
                }
            }
        }    //Instagram
        else if (pakage.equals(INSTANRAM_PACKAGENAME)) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton"))) {
                    sendTo("instagram", msg);
                }
            }
        }
        //viber
        else if (pakage.equals("com.viber.voip")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber"))) {
                    sendTo("viber", msg);
                }
            }
        } else if (pakage.equals(MSG_PACKAGENAME) || pakage.equals(SAMSUNG_MSG_PACKNAME) || pakage.equals(SAMSUNG_MSG_SRVERPCKNAME)) {  //短信
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "msg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "msg"))) {
                    sendTo("mms", msg);
                }
            }
            String h8OnorOff = (String) SharedPreferencesUtils.getParam(MyApp.getApplication(), "messagealert", "");
            if (!WatchUtils.isEmpty(h8OnorOff) && h8OnorOff.equals("on")) {
                EventBus.getDefault().post(new MessageEvent("smsappalert"));
            }

        }

    }


    /**
     * H9发送社交消息
     */
    private void sendMessH9(byte socal, String newmsg, byte countType) {
        Log.e(TAG,"----h99---"+newmsg+"---socal-="+ socal+"---="+countType);
        String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya");
        if (!TextUtils.isEmpty(mylanya) && mylanya.equals("W06X")) {
            if ((boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SOCIAL")) {
                if (TextUtils.isEmpty(newmsg)) {
                    sendSocialCommands(getResources().getString(R.string.news),
                            getResources().getString(R.string.messages), B18iUtils.H9TimeData(), socal, 1, countType);
                    return;
                }
                String[] strings = B18iUtils.stringToArray(String.valueOf(newmsg));//分割出name
                String title = strings[0];
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i < strings.length; i++) {
                    sb.append(strings[i]);
                }
                Log.e(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
                sendSocialCommands(title, sb.toString(), B18iUtils.H9TimeData(), socal, 1, countType);
            }
            return;
        }
    }

    //H8手表发送指令
    public static void sendTo(String apptags, String msg) {
        Log.e(TAG, "------msg----" + msg + "----" + apptags);
        String bleName = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya");
        if (!WatchUtils.isEmpty(bleName) && bleName.equals(H8_NAME_TAG)) {
            if (apptags.equals("phone")) {    //电话
                EventBus.getDefault().post(new MessageEvent("laidianphone"));
            } else if (apptags.equals("mms")) {    //短信
                EventBus.getDefault().post(new MessageEvent("smsappalert"));
            } else {
                EventBus.getDefault().post(new MessageEvent("appalert"));
            }
        }
    }

    /**
     * 分包发送数据
     *
     * @param bs
     * @param currentPack
     * @return
     */


    private byte[] getContent(byte[] bs, int currentPack) {
        byte[] xxx = new byte[20];
        xxx[0] = Integer.valueOf(0xc2).byteValue();
        xxx[1] = Integer.valueOf(00).byteValue();
        //获取总包数 = total+1
        int total = bs.length / 14;
        if (total > 3) {
            xxx[2] = Integer.valueOf(0x0D).byteValue();
            xxx[3] = Integer.valueOf(04).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(01).byteValue();
            System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, 14 * currentPack - 1);
        } else {
            if (currentPack * 14 > bs.length) {
                xxx[2] = Integer.valueOf(bs.length - (currentPack - 1) * 14).byteValue();
            } else {
                xxx[2] = Integer.valueOf(0x0D).byteValue();
            }
            xxx[3] = Integer.valueOf(total + 1).byteValue();
            xxx[4] = Integer.valueOf(currentPack).byteValue();
            xxx[5] = Integer.valueOf(01).byteValue();
            if (bs.length > 14 * currentPack) {
                System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, 14);
            } else {
                System.arraycopy(bs, 14 * (currentPack - 1), xxx, 6, bs.length - (14 * (currentPack - 1)));
            }
        }
        return xxx;
    }

    /**
     * 判断数组的包数，，，
     *
     * @param current
     * @param total
     * @return
     */
    private boolean isExit(int current, int total) {
        float a = (float) total / 14;
        //超过4包就退出
        if (current > 4) {
            return false;
        }
        //不足4包的时候，当已发送完就退出
        if (current >= a + 1) {
            return false;
        }

        return true;
    }


    //当系统通知被删掉后出发回调
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }


    /**
     * B18I发送社交化消息
     *
     * @param tickerText
     * @param head
     */
    public void setSocialSMS(CharSequence tickerText, String head) {
        if (SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya").equals("B18I")) {
            if ((boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SOCIAL")) {
                String[] strings = B18iUtils.stringToArray(String.valueOf(tickerText));//分割出name
                String title = strings[0];
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i < strings.length; i++) {
                    sb.append(strings[i]);
                }
                if (!TextUtils.isEmpty(tickerText)) {
                    BluetoothSDK.sendSocial(B18iResultCallBack.getB18iResultCallBack(), head + "-" + title, sb.toString(), new Date());
                }
            }
            return;
        }
    }


    /**
     * H9推送日程消息
     *
     * @param content   内容
     * @param date      时间
     * @param countType
     * @param count     目前日程titile 固定为Schedule
     */
    public void sendCalendar(String content, String date, byte countType, int count) {
        CalanderPush calanderPushTitle = null, calendarPushContent = null, calendarPushDate = null;
        try {
            calanderPushTitle = new CalanderPush(CommandResultCallback.getCommandResultCallback());
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                calendarPushContent = new CalanderPush(CommandResultCallback.getCommandResultCallback(), CalanderPush.CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                calendarPushDate = new CalanderPush(CommandResultCallback.getCommandResultCallback(), CalanderPush.DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
        }
        MsgCountPush countPush = new MsgCountPush(CommandResultCallback.getCommandResultCallback(), countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        Log.i(TAG, "calanderPushTitle=" + calanderPushTitle + "calendarPushContent=" + calendarPushContent + "calendarPushDate=" + calendarPushDate);
        sendList.add(calanderPushTitle);
        if (calendarPushContent != null) {
            sendList.add(calendarPushContent);
        }
        if (calendarPushDate != null) {
            sendList.add(calendarPushDate);
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
    }

    /**
     * h9推送社交消息
     *
     * @param from       联系人
     * @param content    内容
     * @param date       data_time  (格式为年月日‘T’时分秒)
     * @param socialType 类型  FACEBOOK TWITTER INSTAGRAM QQ WECHAT WHATSAPP LINE SKYPE
     * @param count      数量
     */
    private void sendSocialCommands(String from, String content, String date, byte socialType, int count, byte countType) {
        SocialPush pushType = null, pushName = null, pushContent = null, pushDate = null;
        try {
            // 发送社交内容
            // 1、社交平台(QQ等)
            // 2、名称
            // 3、内容
            // 4、时间
            // 5、推送条数

            pushType = new SocialPush(CommandResultCallback.getCommandResultCallback(), socialType);

            if (!TextUtils.isEmpty(from)) {
                byte[] bName = from.getBytes("utf-8");
                pushName = new SocialPush(CommandResultCallback.getCommandResultCallback(), SocialPush.NAME_TYPE, bName);
            }
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                pushContent = new SocialPush(CommandResultCallback.getCommandResultCallback(), SocialPush.CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                pushDate = new SocialPush(CommandResultCallback.getCommandResultCallback(), SocialPush.DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        MsgCountPush countPush = new MsgCountPush(CommandResultCallback.getCommandResultCallback(), (byte) 0x08, (byte) count);
        MsgCountPush countPush = new MsgCountPush(CommandResultCallback.getCommandResultCallback(), (byte) countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        sendList.add(pushType);
        if (pushName != null) {
            sendList.add(pushName);
        }
        if (pushContent != null) {
            sendList.add(pushContent);
        }
        if (pushDate != null) {
            sendList.add(pushDate);
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
    }
}
