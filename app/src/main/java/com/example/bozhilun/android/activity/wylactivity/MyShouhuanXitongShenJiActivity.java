package com.example.bozhilun.android.activity.wylactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.FileDownloadThread;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.DfuService;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.library.ArcProgress;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.ServiceMessageEvent;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bleutil.SampleGattAttributes;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import butterknife.BindView;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import static com.example.bozhilun.android.bleutil.MyCommandManager.Currentversionnumber;
import static com.example.bozhilun.android.bleutil.MyCommandManager.FirmwareupgradeDirective;

/**
 * Created by admin on 2016/9/5.
 * 我的手环系统升级
 */
public class MyShouhuanXitongShenJiActivity extends BaseActivity implements  BluetoothAdapter.LeScanCallback {

    String downloadUrl;
    String filepath;
    File file;
    private JSONObject bject = new JSONObject();
    public String mDeviceName, mDeviceAddress;//蓝牙名字和地址
    String version;//版本号
    private static final String TAG = MyShouhuanXitongShenJiActivity.class.getSimpleName();

    private boolean islinnajie = false;//是否连接好了
    private boolean istankuan= false;
    private BluetoothAdapter mBluetoothAdapter; // 本机蓝牙适配器对象
    private Handler handler;
    private Boolean ISB15=false;
    private Boolean isxiazai=false;
    private String mybanben;//当前的版本

    @BindView(R.id.jindu_xianshi)
    TextView JINDU;
    @BindView(R.id.xitongbanben)
    TextView banben;
    @BindView(R.id.download_message)
    TextView mMessageView;
    @BindView(R.id.download_progress)
    ProgressBar mProgressbar;
    @BindView(R.id.shengji_dianji)
    TextView SHENGJI;
    @BindView(R.id.myprogress_arcprogress)
    ArcProgress arcProgress;
    @BindView(R.id.tv_title)
    TextView gujian;
    protected void initViews() {
        gujian.setText(getResources().getString(R.string.firmware_upgrade));
        EventBus.getDefault().register(this);
        try {
            //取得蓝牙的名字和mac
            if (null != MyCommandManager.DEVICENAME) {
                mDeviceName =MyCommandManager.DEVICENAME;//蓝牙的名字
                mDeviceAddress = MyCommandManager.ADDRESS;//蓝牙的mac
          //查询下版本号
           if("B15P".equals(mDeviceName)){
               SHENGJI.setEnabled(false);
               Currentversionnumber(MyCommandManager.DEVICENAME);ISB15=true;
            }else if("B15S".equals(mDeviceName)||"B15S-H".equals(mDeviceName)){
               SHENGJI.setEnabled(false);
               Currentversionnumber(MyCommandManager.DEVICENAME);ISB15=false;
           }
         else if("DfuLang".equals(mDeviceName)){
               try{
                   SHENGJI.setText(getResources().getString(R.string.auto_upgrade));
                   arcProgress.setVisibility(View.VISIBLE);
                   SharedPreferences mySharedPrep= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("lanjiekj", Activity.MODE_PRIVATE);
                   SharedPreferences.Editor editorc = mySharedPrep.edit();
                   //用putString的方法保存数据
                   editorc.putString("lanjiekj","kaile");editorc.commit();
                   final DfuServiceInitiator starter = new DfuServiceInitiator(mDeviceAddress).setDeviceName("DfuLang").setKeepBond(false);
                   SharedPreferences mySharedPre= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("filepath", Activity.MODE_PRIVATE);
                   File file = new File(mySharedPre.getString("filepath",""));
                   Uri fileUri = Uri.fromFile(file);
                   starter.setZip(fileUri, mySharedPre.getString("filepath",""));
                   starter.start(MyShouhuanXitongShenJiActivity.this, DfuService.class);
               }catch (Exception E){
                   E.printStackTrace();
               }
           }

            }
        } catch (Exception e) {e.printStackTrace();}



        handler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                if(msg.what == 1){
                    try{
                        // 动态更新UI界面
                        String str = String.valueOf( msg.getData().getInt("num"));
                        JINDU.setText(str+"%");
                        arcProgress.setProgress(Integer.valueOf(str));
                        if(str.equals("100")){
                            SHENGJI.setEnabled(false);
                            JINDU.setVisibility(View.INVISIBLE);
                            banben.setText(mybanben);
                            SHENGJI.setText(getResources().getString(R.string.upgrade_completed));
                            arcProgress.setVisibility(View.GONE);
                            SHENGJI .setBackgroundDrawable(getResources().getDrawable(R.drawable.sms_verification));
                            //发送重新连接请求
                            EventBus.getDefault().post(new ServiceMessageEvent("Bingdingshouhuan"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }}}};


    }




    @Override
    protected int getContentViewId() {return R.layout.activity_mysgouhuan_gujianshensi;}
        /**
         * 方法必须重写
         */
        @Override
        protected void onResume() {
                super.onResume();
            DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        }



        @Override
        public void onDestroy() {
                super.onDestroy();
            EventBus.getDefault().unregister(this);
                islinnajie = false;
            SharedPreferences mySharedPre= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("lanjiekj", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editorc = mySharedPre.edit();
            //用putString的方法保存数据
            editorc.putString("lanjiekj","guan");
            editorc.commit();

        }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        String msg = event.getMessage();
        if ("all_day_Currentversionnumber".equals(msg)) {
            version = event.getObject().toString();
            banben.setText(version.toString());
            //查看是否是最新版
            Boolean is = com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages.isNetworkAvailable(MyShouhuanXitongShenJiActivity.this);
            if (is == true) {
                try {bject.put("clientType", "android");bject.put("version", version);if(ISB15){bject.put("status","0");}else{bject.put("status","1");}} catch (Exception e) {e.printStackTrace();}
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, "http://47.90.83.197:8080/watch/user/getVersion", bject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.optString("resultCode").equals("010")) {
                                    SHENGJI.setEnabled(false);
                                    Toast.makeText(MyShouhuanXitongShenJiActivity.this,getResources().getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
                                    return;
                                }else {
                                    //升级的代码
                                    try {
                                        mybanben=response.optString("version");
                                        downloadUrl = response.optString("url");
                                        SHENGJI.setEnabled(true);
                                               /* mProgressbar.setVisibility(View.GONE);*/
                                        //设备是否连接
                                        doDownload();
                                    } catch (Exception E) {E.printStackTrace();}}}}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyShouhuanXitongShenJiActivity.this,getResources().getString(R.string.wangluo), Toast.LENGTH_SHORT).show();
                    }}) {@Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json; charset=UTF-8");
                    return headers;}};requestQueue.add(jsonRequest);}
        }else if("all_day_Currentversionnumber2".equals(msg)){
            final String VERSION = event.getObject().toString();
            banben.setText(VERSION);
            //查看是否是最新版
            Boolean is = com.example.bozhilun.android.activity.wylactivity.wyl_util.service.ConnectManages.isNetworkAvailable(MyShouhuanXitongShenJiActivity.this);
            if (is == true) {
                try {
                    bject.put("clientType", "android");
                    bject.put("version", VERSION);
                    if(ISB15){bject.put("status","0");}else{bject.put("status","1");}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST,"http://47.90.83.197:8080/watch/user/getVersion", bject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.optString("resultCode").equals("001")) {
                                    if(response.optString("version").equals(VERSION)){
                                        SHENGJI.setEnabled(false);
                                        Toast.makeText(MyShouhuanXitongShenJiActivity.this,getResources().getString(R.string.latest_version), Toast.LENGTH_SHORT).show();
                                        return;
                                    }else{
                                        //升级的代码
                                        try {
                                            mybanben=response.optString("version");
                                            downloadUrl = response.optString("url");
                                            SHENGJI.setEnabled(true);
                                            mProgressbar.setVisibility(View.VISIBLE);
                                            //设备是否连接
                                            doDownload();
                                        } catch (Exception E) {E.printStackTrace();}}}}
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}}) {@Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    headers.put("Content-Type", "application/json; charset=UTF-8");return headers;}};
                requestQueue.add(jsonRequest);
            } else {
                Toast.makeText(MyShouhuanXitongShenJiActivity.this,getResources().getString(R.string.wangluo), Toast.LENGTH_SHORT).show();
            }

        }

    }





        /**
         * 使用Handler更新UI界面信息
         */
        @SuppressLint("HandlerLeak")
        Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                        mProgressbar.setProgress(msg.getData().getInt("size"));
                        float temp = (float) mProgressbar.getProgress()
                                / (float) mProgressbar.getMax();

                        int progress = (int) (temp * 100);
                        //mMessageView.setText("下载进度:" + progress + " %");
                        if (progress == 100) {
                                mProgressbar.setVisibility(View.INVISIBLE);
                                 JINDU.setVisibility(View.VISIBLE);
                                arcProgress.setVisibility(View.VISIBLE);
                                isxiazai=true;
                                //发送
                                SHENGJI.setEnabled(true);
                                SHENGJI.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_selector));
                        }

                }
        };



        @OnClick({R.id.shengji_dianji})
        public void onClick(View v) {
                switch (v.getId()) {
                        //升级
                        case R.id.shengji_dianji:
                                //查询是否已经绑定过设备
                                try {
                                    SHENGJI.setText(getResources().getString(R.string.upgrade));
                                    if(mDeviceName.equals("DfuLang")){
                                        Enoad(1);
                                    }else{
                                    if(isxiazai){
                                      //"发送升级命令
                                    MyCommandManager.deviceDisconnState = true;
                                    FirmwareupgradeDirective(mDeviceName);
                                     mHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                      System.out.println("给设备3秒，扫描" );
                                      scanDevice();
                                                }
                                            }, 3000);
                                        }}} catch (Exception e) {e.printStackTrace();}
                                break;
                }}
        public void scanDevice() {
            BluetoothLeService BluetoothLeService=new BluetoothLeService();
            BluetoothLeService.disconnect();
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if (mBluetoothAdapter == null) {
                        Toast.makeText(this,R.string.bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                        return;
                }
               // System.out.println("扫描开始" );
                mBluetoothAdapter.startLeScan(this);
        }

        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                        try{
                                //mDeviceAddress是正常模式下的地址
                                String address = bluetoothDevice.getAddress();
                                System.out.println("address" +address.toString());
                                if (TextUtils.equals(address, addmac(mDeviceAddress))) {
                                        SharedPreferences mySharedPre= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("lanjiekj", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor editorc = mySharedPre.edit();
                                        //用putString的方法保存数据
                                        editorc.putString("lanjiekj","kaile");
                                        editorc.commit();
                                        //调用固件升级的源码
                                        if (null != mBluetoothAdapter) {
                                                mBluetoothAdapter.stopLeScan(MyShouhuanXitongShenJiActivity.this);
                                            MyApp.getmBluetoothLeService().connect(addmac(mDeviceAddress));
                                                Enoad(0);
                                        }

                                }
                        }catch (Exception E){E.printStackTrace();}





        }
        /**
         * 下载准备工作，获取SD卡路径、开启线程
         */
        private void doDownload() {
                // 获取SD卡路径
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/amosdownload/";
                file = new File(path);
                // 如果SD卡目录不存在创建
                if (!file.exists()) {file.mkdir();}
                // 设置progressBar初始化
                mProgressbar.setProgress(0);
                // 简单起见，我先把URL和文件名称写死，其实这些都可以通过HttpHeader获取到
                String fileName = "oad.zip";
                int threadNum = 5;
                filepath = path + fileName;
                //保存
                SharedPreferences mySharedPre= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("filepath", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editorc = mySharedPre.edit();
                //用putString的方法保存数据
                editorc.putString("filepath",filepath);
                editorc.commit();
                Log.d(TAG, "downloadfilepath:" + filepath);
                downloadTask task = new downloadTask(downloadUrl, threadNum, filepath);
                task.start();
        }

        /**
         * 多线程文件下载
         *
         * @author yangxiaolong
         * @2014-8-7
         */
        class downloadTask extends Thread {
                private String downloadUrl;// 下载链接地址
                private int threadNum;// 开启的线程数
                private String filePath;// 保存文件路径地址
                private int blockSize;// 每一个线程的下载量

                public downloadTask(String downloadUrl, int threadNum, String fileptah) {
                        this.downloadUrl = downloadUrl;
                        this.threadNum = threadNum;
                        this.filePath = fileptah;
                }
                @Override
                public void run() {
                        FileDownloadThread[] threads = new FileDownloadThread[threadNum];
                        try {
                                URL url = new URL(downloadUrl);
                                Log.d(TAG, "download file http path:" + downloadUrl);
                                URLConnection conn = url.openConnection();
                                // 读取下载文件总大小
                                int fileSize = conn.getContentLength();
                                if (fileSize <= 0) {
                                        //System.out.println("读取文件失败");
                                        return;
                                }
                                // 设置ProgressBar最大的长度为文件Size
                                mProgressbar.setMax(fileSize);

                                // 计算每条线程下载的数据长度
                                blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                                        : fileSize / threadNum + 1;

                                Log.d(TAG, "fileSize:" + fileSize + "  blockSize:" + blockSize);

                                File file = new File(filePath);
                                for (int i = 0; i < threads.length; i++) {
                                        // 启动线程，分别下载每个线程需要下载的部分
                                        threads[i] = new FileDownloadThread(url, file, blockSize,
                                                (i + 1));
                                        threads[i].setName("Thread:" + i);
                                        threads[i].start();
                                }

                                boolean isfinished = false;
                                int downloadedAllSize = 0;
                                while (!isfinished) {
                                        isfinished = true;
                                        // 当前所有线程下载总量
                                        downloadedAllSize = 0;
                                        for (int i = 0; i < threads.length; i++) {
                                                downloadedAllSize += threads[i].getDownloadLength();
                                                if (!threads[i].isCompleted()) {
                                                        isfinished = false;
                                                }
                                        }
                                        // 通知handler去更新视图组件
                                        Message msg = new Message();
                                        msg.getData().putInt("size", downloadedAllSize);
                                        mHandler.sendMessage(msg);
                                        // Log.d(TAG, "current downloadSize:" + downloadedAllSize);
                                        Thread.sleep(1000);// 休息1秒后再读取下载进度
                                }
                                Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

                        } catch (MalformedURLException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }

                }
        }




        private void Enoad(int id ) {
                        try {
                            final DfuServiceInitiator starter;
                            if(0==id){
                                 starter = new DfuServiceInitiator(addmac(mDeviceAddress)).setDeviceName("DfuLang").setKeepBond(false);
                                File file = new File(filepath);
                                Uri fileUri = Uri.fromFile(file);
                                starter.setZip(fileUri, filepath);
                                starter.start(MyShouhuanXitongShenJiActivity.this, DfuService.class);
                            }else{
                                starter = new DfuServiceInitiator(mDeviceAddress).setDeviceName("DfuLang").setKeepBond(false);
                                SharedPreferences mySharedPre= MyShouhuanXitongShenJiActivity.this.getSharedPreferences("filepath", Activity.MODE_PRIVATE);
                                File file = new File(mySharedPre.getString("filepath",""));
                                Uri fileUri = Uri.fromFile(file);
                                starter.setZip(fileUri, mySharedPre.getString("filepath",""));
                                starter.start(MyShouhuanXitongShenJiActivity.this, DfuService.class);
                            }
                        } catch (Exception e) {e.printStackTrace();}
        }



        /**
         * 升级进度监听
         */
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
                public void onDeviceConnecting(final String deviceAddress) {
                        Log.d(TAG, "onDeviceConnecting");
                        System.out.print("onDeviceConnecting");
                        // empty default implementation
                }

                @Override
                public void onDeviceConnected(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDeviceConnected");
                        System.out.print("onDeviceConnected");
                }

                @Override
                public void onDfuProcessStarting(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDfuProcessStarting");
                        System.out.print("onDfuProcessStarting");
                    MyApp.getmBluetoothLeService().connect(addmac(mDeviceAddress));
                }

                @Override
                public void onDfuProcessStarted(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDfuProcessStarted");
                        System.out.print("onDfuProcessStarted");
                }

                @Override
                public void onEnablingDfuMode(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onEnablingDfuMode");
                        System.out.print("onEnablingDfuMode");
                }

                @Override
                public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
                                       try {
                                   if(percent!=0){
                                           Message msg = new Message();
                                           msg.what = 1;
                                           Bundle bundle = new Bundle();
                                           bundle.putInt("num",percent);
                                           msg.setData(bundle);
                                           handler.sendMessage(msg);
                                   }} catch (Exception e) {e.printStackTrace();}
                }

                @Override
                public void onFirmwareValidating(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onFirmwareValidating");
                        System.out.print("onFirmwareValidating");
                }

                @Override
                public void onDeviceDisconnecting(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDeviceDisconnecting");
                        System.out.print("onDeviceDisconnecting");
                }

                @Override
                public void onDeviceDisconnected(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDeviceDisconnected");
                        System.out.print("onDeviceDisconnected");
                }

                @Override
                public void onDfuCompleted(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDfuCompleted");
                        System.out.print("onDfuCompleted");
                }

                @Override
                public void onDfuAborted(final String deviceAddress) {
                        // empty default implementation
                        Log.d(TAG, "onDfuAborted");
                        System.out.print("onDfuAborted");
                }

                @Override
                public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
                        // empty default implementation
                        Log.d(TAG, "onError");
                        System.out.print("onError");
                        //调用固件升级的源码
                        if (null != mBluetoothAdapter) {
                                mBluetoothAdapter.stopLeScan(MyShouhuanXitongShenJiActivity.this);
                            MyApp.getmBluetoothLeService().connect(mDeviceAddress);
                                Enoad(0);
                        }
                        //多试,Enoad();

                }

        };


        @Override
        protected void onPause() {
                super.onPause();
                DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        }


    public static String addmac(String oldmac) {
        if (oldmac == null || oldmac.isEmpty()) {
            return "";
        }
        String newmac = oldmac;
        int length = oldmac.length();
        if (length >= 2) {
            String qian = oldmac.substring(0, length - 2);
            String houwei = oldmac.substring(length - 2, length);
            newmac = qian + huansuan(houwei);
            newmac=newmac.toUpperCase();
        }
        return newmac;
    }
    public static String huansuan(String old) {
        String strnew = old;
        int strw = 0;
        try {
            strw = Integer.valueOf(old, 16) + 1;
            strnew = Integer.toHexString(strw);
            //考虑是“0a”开头
            if (strw <= 15) {
                strnew = "0" + strnew;
            }
            //考虑是“ff”开头
            if (strnew.length() > 2) {
                strnew = strnew.substring(1, 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strnew;
    }
}