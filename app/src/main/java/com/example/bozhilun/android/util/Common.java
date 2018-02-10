package com.example.bozhilun.android.util;


import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.BlueUser;
import com.example.bozhilun.android.bleutil.SumBean;
import com.example.bozhilun.android.onekeyshare.OnekeyShare;
import com.example.bozhilun.android.onekeyshare.OnekeyShareTheme;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * Created by thinkpad on 2016/6/29.
 */
public class Common {

    public static BlueUser userInfo;

    public static String customer_id;

    public final static int REFRESH_DATA_FINISH = 1005;


    static   Vibrator vibrator;//震动
    static  MediaPlayer mMediaPlayer;//响铃
    //震动 和响铃
    //震动 和响铃
    public static  void VibratorandMusic(boolean isture,Context context) {
        try {
            if (isture) {

                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if(null==mMediaPlayer){
                    mMediaPlayer =  getMediaPlayer(context);
                    mMediaPlayer.setDataSource(context, alert);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    mMediaPlayer.prepareAsync();

                    // 为解决第二次播放时抛出的IllegalStateException，这里做了try-catch处理
                    boolean isPlaying = false;
                    try {
                        isPlaying = mMediaPlayer.isPlaying();
                    }
                    catch (IllegalStateException e) {
                        mMediaPlayer = null;
                        mMediaPlayer = new MediaPlayer();
                        if (null != vibrator) {
                            vibrator.cancel();
                            vibrator = null;
                        }
                    }
                    if (isPlaying)
                    {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        mMediaPlayer = new MediaPlayer();
                    }
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer amp) {
                            amp.start();
                        }
                    });
                }
                if (null == vibrator) {
                    //开启震动
                    vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                    long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, 0);
                }else{
                    vibrator.cancel();
                    vibrator = null;
                }

            } else {
                //关闭震动

                if (null != vibrator) {
                    vibrator.cancel();
                    vibrator = null;
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (vibrator != null) {
                vibrator.cancel();
                vibrator = null;
            }

        }
    }


    private static MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler() {
                    @Override
                    public void publish(LogRecord record) {

                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void close() throws SecurityException {

                    }
                });
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {

        }
        return mediaplayer;
    }


    /**
     * 启动分享
     * @param context
     * @param platformToShare
     * @param showContentEdit
     * @param uil
     */
    public static void showShare(Context context, String platformToShare, boolean showContentEdit, String uil) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        oks.setDialogMode();
        oks.disableSSOWhenAuthorize();
        oks.setImagePath(uil);  //分享sdcard目录下的图片
        oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
        oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
        // 启动分享
        oks.show(context);
    }



    /**
     * 上传图片的限制大小(kb)
     */
    public static final int IMAGE_MAX_SIZE = 5 * 1024;

    /**
     * 最大改变压缩次数（内存溢出时）
     */
    public final static int REVISION_IMAGE_MAX_NUM = 3;

    public static boolean isServiceRunning(Context context, String service_Name) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service_Name.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }




    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    public static String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals("0") ? "0.0" : distanceStr;
    }


    /**
     * 得到几天前的时间
     *
     * @param
     * @param
     * @return
     */
    public static String getStatetime(int id) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, - id);
        Date monday = c.getTime();
        String preMonday = sdf.format(monday);
        return preMonday;
    }




    public static List getDayListOfMonth(Context context) {
        List<String> list = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        int month = Integer.valueOf(dateStr.substring(5, 7));//月份
        int day = Integer.valueOf(dateStr.substring(8, 10));
        for (int i = 1; i <= day; i++) {
            String aDate = null;
            if (i < 10) {
                aDate = month + "/0" + i;
            } else if (i == day - 1) {
                aDate = context.getResources().getString(R.string.yesterday);
            } else if (i == day) {
                aDate = context.getResources().getString(R.string.today);
            } else {
                aDate = month + "/" + i;
            }
            list.add(aDate);
        }
        return list;
    }

    public static ArrayList<SumBean> getSleepSumList(String str) {
        ArrayList<SumBean> beanList = new ArrayList<>();
        int a = 0, b = 0;
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char result = str.charAt(i);
            if ('0' == result) {
                a = a + 1;
                SumBean sum = new SumBean(0, a);
                b = 0;
                if (a > 1) {
                    beanList.remove(beanList.size() - 1);
                }
                beanList.add(sum);
            } else if ('1' == result) {
                a = 0;
                b = b + 1;
                SumBean sum = new SumBean(1, b);
                if (b > 1) {
                    beanList.remove(beanList.size() - 1);
                }
                beanList.add(sum);
            }
        }
        return beanList;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }else{
            //强制开启
            openGPS(context);
        }

        return false;
    }







    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static boolean  openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        return  true;
    }


    //判断是否为表情
    public static boolean noContainsEmoji(String str) {//真为不含有表情
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }


    // 两次点击按钮之间的点击间隔不能少于2000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }



    //md5加密
    public static String Md532(String sourceStr) {
        String  result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i; StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0)
                    i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("result: " + result);//32位的加密
//   System.out.println("result: " + buf.toString().substring(8,24));//16位的加密
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return result;
    }
}
