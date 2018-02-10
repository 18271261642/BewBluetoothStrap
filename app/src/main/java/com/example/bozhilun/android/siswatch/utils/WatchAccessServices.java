package com.example.bozhilun.android.siswatch.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import com.example.bozhilun.android.B18I.b18imonitor.B18iResultCallBack;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.h9.h9monitor.CommandResultCallback;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.push.CalanderPush;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.SocialPush;
import org.greenrobot.eventbus.EventBus;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import cn.appscomm.bluetooth.app.BluetoothSDK;

/**
 * Created by Administrator on 2017/9/17.
 */

/**
 * 辅助功能获取APP消息
 */
public class WatchAccessServices extends AccessibilityService {
    private final String TAG = "WatchAccessServices";
    private static final String H8_NAME_TAG = "bozlun";
    private static final String QQ_PACKAGENAME = "com.tencent.mobileqq";
    private static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    private static final String WEIBO_PACKAGENAME = "com.sina.weibo";
    private static final String FACEBOOK_PACKAGENAME = "com.facebook.katana";
    private static final String TWITTER_PACKAGENAME = "com.twitter.android";
    private static final String WHATS_PACKAGENAME = "com.whatsapp";
    private static final String VIBER_PACKAGENAME = "com.viber.voip";
    private static final String INSTANRAM_PACKAGENAME = "com.instagram.android";
    private static final String ALLIPAY_PACKAGENAME = "com.eg.android.alipaygphone";
    private static final String MSG_MSGPACKNAME = "com.android.mms";    //短信
    private static final String SAMSUNG_MSGPACKAGENAME = "com.samsung.android.messaging";
    private static final String SAMSUNG_MSG_SRVERPCKNAME = "com.samsung.android.communicationservice";

    private String newStrmsg = "";

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo accinfo = getServiceInfo();
        //过滤包名
        accinfo.packageNames = new String[]{WECHAT_PACKAGENAME, QQ_PACKAGENAME, FACEBOOK_PACKAGENAME,
                TWITTER_PACKAGENAME, WHATS_PACKAGENAME, INSTANRAM_PACKAGENAME, VIBER_PACKAGENAME};
        accinfo.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED; //通知
        accinfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        accinfo.notificationTimeout = 100;
        setServiceInfo(accinfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent != null) {
            try {
                Log.e(TAG, "------sis---" + accessibilityEvent.getPackageName().toString() + "--" + accessibilityEvent.getText().toString());
                if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                    String packageName = accessibilityEvent.getPackageName().toString();
                    String appTextmsg = accessibilityEvent.getText().toString();
                    if (!WatchUtils.isEmpty(packageName) && !WatchUtils.isEmpty(appTextmsg)) {
                        if (!appTextmsg.equals("[]")) {
                            String newmsg = appTextmsg.substring(1, appTextmsg.length() - 1);    //去掉[]
                            if (newmsg.length() > 16) {
                                newStrmsg = newmsg.substring(0, 16);
                            } else {
                                newStrmsg = newmsg + "...............";
                            }
                            Log.e(TAG, "包：" + packageName + "内容：" + newmsg);
                            sendAppmsg(packageName, newStrmsg, newmsg);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //判断是否哪种APP
    private void sendAppmsg(String packageName, String newStrmsg, String newmsg) {

        Log.e(TAG,"------h9999----"+packageName+"---"+newStrmsg+"----"+newmsg);

        if (packageName.equals(QQ_PACKAGENAME)) { //QQ

            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg"))) {
                    sendToMsg("qq", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.QQ, newmsg,(byte) 0x08);

            //B18I手环
            setSocialSMS(newmsg, "QQ");
        } else if (packageName.equals(WECHAT_PACKAGENAME)) { //微信
            Log.e(TAG,"-----weichat-----");
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg"))) {
                    sendToMsg("wechat", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.WECHAT, newmsg,(byte) 0x09);
            //B18I手环
            setSocialSMS(newmsg, "WeChat");
        } else if (packageName.equals(WEIBO_PACKAGENAME)) {    //微博

        } else if (packageName.equals(FACEBOOK_PACKAGENAME)) { //facebook
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook"))) {
                    sendToMsg("facebook", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.FACEBOOK, newmsg,(byte) 0x0A);
            //B18I手环
            setSocialSMS(newmsg, "FaceBook");
        } else if (packageName.equals(TWITTER_PACKAGENAME)) { //twitter
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa"))) {
                    sendToMsg("twitter", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.TWITTER, newmsg,(byte) 0x0B);
            //B18I手环
            setSocialSMS(newmsg, "Twitter");
        } else if (packageName.equals(WHATS_PACKAGENAME)) { //whats
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp"))) {
                    sendToMsg("whats", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.WHATSAPP, newmsg,(byte) 0x0C);
            //B18I手环
            setSocialSMS(newmsg, "Whats");
        } else if (packageName.equals(VIBER_PACKAGENAME)) { //viber
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber"))) {
                    sendToMsg("viber", newStrmsg);
                }
            }

            //B18I手环
            setSocialSMS(newmsg, "viber");
        } else if (packageName.equals(INSTANRAM_PACKAGENAME)) { //instagram
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton"))) {
                    sendToMsg("instagram", newStrmsg);
                }
            }
            //H9
            sendMessH9(SocialPush.INSTAGRAM, newmsg,(byte) 0x0F);
            //B18I手环
            setSocialSMS(newmsg, "Instagram");
        } else if (packageName.equals(ALLIPAY_PACKAGENAME)) { //支付宝

        } else if (packageName.equals("com.android.calendar")) {//日历提醒

            //B18I手环
            setSocialSMS(newmsg, "calendar");
            //H9
            sendCalendarsH9(newmsg);
        }else if(packageName.equals(MSG_MSGPACKNAME) || packageName.equals(SAMSUNG_MSGPACKAGENAME) || packageName.equals(SAMSUNG_MSG_SRVERPCKNAME)){    //短信
            EventBus.getDefault().post(new MessageEvent("smsappalert"));
        }
    }


    /**
     * 日历消息推送
     * @param newmsg
     */
    private void sendCalendarsH9(String newmsg) {
        String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya");
        try {
            if (!TextUtils.isEmpty(mylanya) && mylanya.equals("W06X")) {
                if ((boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SOCIAL")) {
                    String[] strings = B18iUtils.stringToArray(String.valueOf(newmsg));//分割出name
                    String title = strings[0];
                    StringBuffer sb = new StringBuffer();
                    for (int i = 1; i < strings.length; i++) {
                        sb.append(strings[i]);
                    }
                    Log.d(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
                    sendCalendar(title+""+sb.toString(), B18iUtils.H9TimeData(), MsgCountPush.SCHEDULE_TYPE, 1);
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
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
     * H9发送社交消息
     */
    private void sendMessH9(byte socal, String newmsg,byte countType) {
        try {
            String mylanya = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya");
            if (!TextUtils.isEmpty(mylanya) && mylanya.equals("W06X")) {
                if ((boolean) SharedPreferencesUtils.readObject(MyApp.getApplication(), "SOCIAL")) {
                    if(TextUtils.isEmpty(newmsg)){
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
                    Log.d(TAG, title + "===" + sb.toString() + "时间为：" + B18iUtils.H9TimeData());
                    sendSocialCommands(title, sb.toString(), B18iUtils.H9TimeData(), socal, 1,countType);
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * B18I发送社交化消息
     *
     * @param tickerText
     * @param head
     */
    public void setSocialSMS(String tickerText, String head) {
        String myBleName = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "mylanya");
        if (!WatchUtils.isEmpty(myBleName) && myBleName.equals("B18I")) {
            if ((boolean) SharedPreferencesUtils.readObject(MyApp.getContext(), "SOCIAL")) {
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
    //推送消息
    private void sendToMsg(String apptags, String newStrmsg) {
        String bleName = (String) SharedPreferencesUtils.readObject(MyApp.getApplication(), "mylanya");
        Log.e(TAG,"-----H8手表接收到消息后发送指令----"+bleName+"--apptags-="+apptags+"--newStrmsg="+newStrmsg);
        //h8手表只需发送提醒即可，无需展示消息内容
        if(!WatchUtils.isEmpty(bleName) && bleName.equals(H8_NAME_TAG)){
            EventBus.getDefault().post(new MessageEvent("appalert"));
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * H9推送社交消息
     *
     * @param from       联系人
     * @param content    内容
     * @param date       data_time  (格式为年月日‘T’时分秒)
     * @param socialType 类型  FACEBOOK TWITTER INSTAGRAM QQ WECHAT WHATSAPP LINE SKYPE
     * @param count      数量
     */
    private void sendSocialCommands(String from, String content, String date, byte socialType, int count,byte countType) {
        SocialPush pushType = null, pushName = null, pushContent = null, pushDate = null;
        try {
            // 发送社交内容
            // 1、社交平台(QQ等)
            // 2、名称
            // 3、内容
            // 4、时间
            // 5、推送条数
            pushType = new SocialPush(CommandResultCallback.getCommandResultCallback(), (byte)socialType);
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
