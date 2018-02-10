package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NeNotificationService extends AccessibilityService {
    public final static String ACTION_DATA = "com.alert.msg";
    private static String qqpimsecure = "com.tencent.qqpimsecure";
    public static Boolean weixinisheck = true, qqmsgisheck = true, Viberisheck = true, Twitterisheck = true, Facebookisheck = true, Whatsappisheck = true, Instagramisheck = true, laidiantixingsheck = true, MSGisheck = true;//选中的状态
    String newStr;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (event.getPackageName().equals(qqpimsecure)) {
            } else {
                try {

                    Parcelable data = event.getParcelableData();

                    if (data instanceof Notification) {
                        // Log.i(TAG, "Recieved notification");
                        Notification notification = (Notification) data;
                        if (null == event) {
                            return;
                        } else {
                            String MSG = event.getText().toString();


                            if (null != event.getText()) {
                                if (String.valueOf(event.getText()).length() >= 16) {
                                    newStr = event.getText().toString().subSequence(0, 16).toString();
                                } else {
                                    newStr = event.getText().toString() + ".................................";
                                    newStr = newStr.subSequence(0, 16).toString();
                                }

                            }
                            System.out.println("other" + event.getEventType() + " .package:" + event.getPackageName() + " .text:" + newStr);

                            if (newStr.equals("[]")) {
                                return;
                            } else {

                                //过滤包名
                                if (event.getPackageName().equals("com.tencent.mobileqq")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                    // 微信
                                } else if (event.getPackageName().equals("com.tencent.mm")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }
                                //facebook
                                else if (event.getPackageName().equals("com.facebook.katana")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }
                                //Twitter
                                else if (event.getPackageName().equals("com.twitter.android")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }
                                //Whats
                                else if (event.getPackageName().equals("com.whatsapp")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }    //Instagram
                                else if (event.getPackageName().equals("com.instagram.android")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }
                                //viber
                                else if (event.getPackageName().equals("com.viber.voip")) {
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }else if(event.getPackageName().equals("com.android.phone")){
                                    sendMsg(String.valueOf(event.getPackageName()), newStr);
                                }
                            }


                        }


                    }
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        }
    }

    //发送广播

/*	public  void  sendMsg(String key, String object){
        Intent intet = new Intent(ACTION_DATA);
		intet.putExtra("pack", key);
		intet.putExtra("msg",object);
	sendBroadcast(intet);
	}*/


    public void sendMsg(String pakage, String msg) {
        //qq
        if (pakage.equals("com.tencent.mobileqq")) {

            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "qqmsg"))) {
                    sendTo(1, msg);
                }
            }

            // 微信
        } else if (pakage.equals("com.tencent.mm")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "weixinmsg"))) {
                    sendTo(2, msg);
                }
            }
        }
        //facebook
        else if (pakage.equals("com.facebook.katana")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "facebook"))) {
                    sendTo(3, msg);
                }
            }
        }
        //Twitter
        else if (pakage.equals("com.twitter.android")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Twitteraa"))) {
                    sendTo(4, msg);
                }
            }
        }
        //Whats
        else if (pakage.equals("com.whatsapp")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Whatsapp"))) {
                    sendTo(5, msg);
                }
            }
        }    //Instagram
        else if (pakage.equals("com.instagram.android")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Instagrambutton"))) {
                    sendTo(6, msg);
                }
            }
        }
        //viber
        else if (pakage.equals("com.viber.voip")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "Viber"))) {
                    sendTo(7, msg);
                }
            }
        } else if (pakage.equals("com.android.mms")) {
            if (null != SharedPreferencesUtils.readObject(MyApp.getContext(), "msg")) {
                if ("0".equals(SharedPreferencesUtils.readObject(MyApp.getContext(), "msg"))) {
                    sendTo(8, msg);
                }
            }
        }

    }


    public static void sendTo(int id, String msg) {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("lanyaneme", MyCommandManager.DEVICENAME);
        hashMap.put("id", 0);
        hashMap.put("msg", msg);
        if (id == 1) {
            hashMap.put("type", 0);//qq
        } else if (id == 2) {
            hashMap.put("type", 1);//微信
        } else if (id == 3) {
            hashMap.put("type", 6);//facebook
        } else if (id == 4) {
            hashMap.put("type", 5);//Twitter
        } else if (id == 5) {
            hashMap.put("type", 7);//Whats
        } else if (id == 6) {
            hashMap.put("type", 8);//Instagram
        } else if (id == 7) {
            hashMap.put("type", 4);//viber
        } else if (id == 8) {
            hashMap.put("type", 2);//短信
        } else if (id == 9) {
            hashMap.put("type", 3);//电话
        }
        Log.e("NeNorificationService","------蓝牙名称----"+MyCommandManager.DEVICENAME);
        if("bozlun".equals(SharedPreferencesUtils.readObject(MyApp.getApplication(),"mylanya"))){
            Log.e("NeNorificationService","-----1111-----走到这里来了----");
            if("on".equals(SharedPreferencesUtils.getParam(MyApp.getApplication(),"messagealert",""))){
                Log.e("NeNorificationService","----222------走到这里来了----");
                Log.e("NeNorificationService","------msg---"+msg+"---id---"+id);
                EventBus.getDefault().post(new MessageEvent("appalert"));
            }
        }
        MyCommandManager.MessageReminder(hashMap);

    }

    private void analyzeNotify(Notification notification) {
        RemoteViews views = notification.contentView;
        Class secretClass = views.getClass();

        try {
            Map<Integer, String> text = new HashMap<Integer, String>();

            Field outerField = secretClass.getDeclaredField("mActions");
            outerField.setAccessible(true);
            ArrayList<Object> actions = (ArrayList<Object>) outerField.get(views);

            for (Object action : actions) {
                Field innerFields[] = action.getClass().getDeclaredFields();
                Field innerFieldsSuper[] = action.getClass().getSuperclass().getDeclaredFields();
                Object value = null;
                Integer type = null;
                Integer viewId = null;
                for (Field field : innerFields) {
                    field.setAccessible(true);
                    //AppLog.i("analyzeNotify innerFields :" + field.toString());
                    if (field.getName().equals("value")) {
                        value = field.get(action);
                    } else if (field.getName().equals("type")) {
                        type = field.getInt(action);
                    } else if (field.getName().equals("URI")) {

                        //AppLog.i("analyzeNotify innerFields URI :" + uri);

                    } else {
                        //Object obj = (Object)field.get(action);
                        //AppLog.i("analyzeNotify innerFields obj :" + obj);

                    }
                }
                for (Field field : innerFieldsSuper) {
                    field.setAccessible(true);
                    //AppLog.i("analyzeNotify innerFieldsSuper :" + field.toString());
                    if (field.getName().equals("viewId")) {
                        viewId = field.getInt(action);
                    }
                }

                if (value != null && type != null && viewId != null && (type == 9 || type == 10)) {
                    text.put(viewId, value.toString());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onServiceConnected() {

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onInterrupt() {

    }

}
