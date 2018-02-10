package com.example.bozhilun.android;


import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.B18I.b18idb.DBManager;
import com.example.bozhilun.android.B18I.b18ireceiver.RefreshBroadcastReceivers;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.AlertService;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.exection.CrashHandler;
import com.example.bozhilun.android.h9.fragment.H9RecordFragment;
import com.example.bozhilun.android.h9.h9monitor.CommandResultCallback;
import com.example.bozhilun.android.siswatch.bleus.WatchBluetoothService;
import com.example.bozhilun.android.siswatch.utils.CustomPhoneStateListener;
import com.sdk.bluetooth.app.BluetoothApplicationContext;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.veepoo.protocol.VPOperateManager;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.ota.util.OtaAppContext;


/**
 * Created by thinkpad on 2016/7/20.
 */

public class MyApp extends LitePalApplication {

    private static MyApp application;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //用于退出activity
    private List<AppCompatActivity> activities;


    public static Context context;
    // 蓝牙服务
    private static BluetoothLeService mBluetoothLeService;

    public static BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    //sis watch service
    private static WatchBluetoothService watchBluetoothService;

    public static WatchBluetoothService getWatchBluetoothService() {
        if (watchBluetoothService == null) {
            initWatchBlueTooth();
        }
        return watchBluetoothService;
    }


    public MyApp() {
        application = this;
    }

    public static MyApp getInstance() {

        return application;
    }

    //B15P
    private static VPOperateManager vpOperateManager;


    public static OnH9ConnListener h9ConnListener;

    public static void setH9ConnListener(OnH9ConnListener h9ConnListener) {
        MyApp.h9ConnListener = h9ConnListener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    static RequestQueue requestQueue;
    //B18I数据库管理
    private static DBManager dbManager;

    public static boolean isOne = true;
    public static boolean AppisOne = false;


    //监听来电
    private  static CustomPhoneStateListener customPhoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        AppisOne = true;
        Log.e("MyApp", "--------application---oncreate--");
        //LeakCanary.install(this);
        application = this;
        context = getApplicationContext();
        activities = new ArrayList<>();
        //初始化异常收集
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        bindAlertServices();    //绑定通知的服务
        // startService(new Intent(application, AlertService.class));//启动通知
        setDatabase();
        dbManager = DBManager.getInstance(context);
        initBlueTooth();
        //启动sis watch手表的服务
        initWatchBlueTooth();
        //初始化l38i手表sdk
        BluetoothSDK.initSDK(this);
        OtaAppContext.INSTANCE.init(this);
        //初始化H9手表SDK
        BluetoothApplicationContext.getInstance().init(application);
        //设置H9手表的连接监听回调
//        AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
        registerPhoneStateListener(); //注册H8手表的ianh监听
        timingDown();//同步数据
    }

    /**
     * 定时读取手环数据
     */
    private Handler mHandler = new Handler();
    private static final String RefreshBroad = "com.example.bozhilun.android.RefreshBroad";

    /**
     * 自动同步数据
     */
    private RefreshBroadcastReceivers refreshBroadcastReceiver;

    private void timingDown() {

        //动态注册广播
//        H9RecordFragment.RefreshBroadcastReceiver refreshBroadcastReceiver = new H9RecordFragment.RefreshBroadcastReceiver();
        refreshBroadcastReceiver = new RefreshBroadcastReceivers();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RefreshBroad);
        registerReceiver(refreshBroadcastReceiver, intentFilter);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("--------", "----rrrrrrrrrrrrrrrrr---z----自动同步数据重-");
                Intent intent = new Intent();
                intent.setAction(RefreshBroad);
//                intent.putExtra("refresh", "YES");
                sendBroadcast(intent);

                // 循环调用实现定时刷新界面
                mHandler.postDelayed(this, 10 * (1000 * 60));//300000ms = 5min  600000ms = 10 min
            }
        };
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, 10 * (1000 * 60));
    }

    //B15P所有操作管理单例
    public static VPOperateManager getVpOperateManager() {
        if (vpOperateManager == null) {
            vpOperateManager = VPOperateManager.getMangerInstance(application);
        }
        return vpOperateManager;
    }

    public static CustomPhoneStateListener getCustomPhoneStateListener(){
        if(customPhoneStateListener == null){
            synchronized (CustomPhoneStateListener.class){
                if(customPhoneStateListener == null){
                    customPhoneStateListener = new CustomPhoneStateListener(application);
                }
            }
        }
         return customPhoneStateListener;
    }


    //H8手表注册监听来电的监听
    private void registerPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(getCustomPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    /**
     * H9 手表的连接监听回调
     */
    public BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            Log.e("全局App", "------h9--onConnected--");
            if (h9ConnListener != null) {
                Log.e("全局App", "----111--h9--onConnected--");
                h9ConnListener.h9connect(bluetoothDevice);
            }
        }

        @Override
        public void onConnectFailed() {
            Log.e("全局App", "------h9--onConnected--");
//            closeLoadingDialog();
            if (h9ConnListener != null) {
                h9ConnListener.h9connectFailed();
            }
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            Log.e("全局App", "------h9--onConnected--");
//            Message message = new Message();
//            message.what = H9_REQUEST_CONNECT_CODE;
//            message.obj = bluetoothDevice;
//            handler.sendMessage(message);
            if (h9ConnListener != null) {
                h9ConnListener.h9onEnableToSendComand(bluetoothDevice);
            }
        }

        @Override
        public void onConnectDeviceTimeOut() {  //连接超时
            Log.e("全局App", "------h9--onConnected--");
//            closeLoadingDialog();
            if (h9ConnListener != null) {
                h9ConnListener.h9contimeout();
            }
        }
    };

    private void bindAlertServices() {
        Intent ints = new Intent(application.getApplicationContext(), AlertService.class);
        bindService(ints, alertConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection alertConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MyApp", "-----conn---");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MyApp", "-----disconn---");
        }
    };


    public static MyApp getApplication() {
        return application;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 设置greenDao
     */

    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public static DBManager getDBManager() {
        return dbManager;
    }


    // 开启Bluetoothseivice
    public static void initBlueTooth() {
        Intent gattServiceIntent = new Intent(getApplication(), BluetoothLeService.class);
        getApplication().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    //开启手表的bluetoothservie
    public static void initWatchBlueTooth() {
        Intent gattServiceIntent = new Intent(getApplication(), WatchBluetoothService.class);
        getApplication().bindService(gattServiceIntent, mWatchServiceConnection, BIND_AUTO_CREATE);
    }

    // 管理生命周期的代码
    public final static ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            try {
                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // 管理服务的生命周期
    private static final ServiceConnection mWatchServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            watchBluetoothService = ((WatchBluetoothService.LocalBinder) service).getService();
            if (!watchBluetoothService.initialize()) {
                // Log.e(TAG, "Unable to initialize Bluetooth");

            }
            // Automatically connects to the device upon successful start-up initialization.
            // watchBluetoothService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            watchBluetoothService = null;
        }
    };


    /**
     * 添加Activity
     */
    public void addActivity(AppCompatActivity activity) {
        // 判断当前集合中不存在该Activity
        if (!activities.contains(activity)) {
            activities.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (AppCompatActivity activity : activities) {
            //unregisterReceiver(refreshBroadcastReceiver);
            activity.finish();

        }
    }

    // 返回
    public static Context getContextObject() {
        return context;
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        return requestQueue;
    }

    public interface OnH9ConnListener {
        void h9connect(BluetoothDevice bluetoothDevice);

        void h9connectFailed();

        void h9onEnableToSendComand(BluetoothDevice bluetoothDevice);

        void h9contimeout();
    }

}
