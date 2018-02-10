package com.example.bozhilun.android.B18I;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.B18I.B18Iadapter.B18ISearchListAdapter;
import com.example.bozhilun.android.B18I.B18imodle.B18IDeviceBean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.BluetoothScanCallBack;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * Created by Administrator on 2017/8/24.
 */

/**
 * l38i搜索页面
 */
public class B18ISearchActivity extends WatchBaseActivity implements B18ISearchListAdapter.OnBindClickListener,SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "B18ISearchActivity";

    private static final int TRUNON_BLEU_CODE = 1001;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.l38iSearchRecyclerView)
    RecyclerView l38iSearchRecyclerView;
    @BindView(R.id.l38iSearchSwipe)
    SwipeRefreshLayout l38iSearchSwipe;
    private B18ISearchListAdapter b18ISearchListAdapter;
    private List<B18IDeviceBean> b18IDeviceBeanList;
    private BluetoothAdapter blueAdapter;

    private boolean isScann = false;    //是否正在扫描
    private String bleName;  //选择连接的蓝牙设备名称和mac地址
    private  String mac;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"---停止刷新--");
            if(l38iSearchSwipe.isRefreshing()){
                l38iSearchSwipe.setRefreshing(false);
            }
            BluetoothSDK.stopScan();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l38isearch);
        ButterKnife.bind(this);

        initViews();
        bleName = getIntent().getStringExtra("NAME");
        Log.e(TAG,"---name--"+bleName);

        checkBleisTurnOn(); //检测蓝牙是否打开


    }
    //检测是否支持蓝牙或蓝牙是否打开
    private void checkBleisTurnOn() {
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        blueAdapter = bm.getAdapter();
        if(blueAdapter == null){    //不支持蓝牙
            ToastUtil.showToast(B18ISearchActivity.this,getResources().getString(R.string.bluetooth_not_supported));
        }else{
            if(!blueAdapter.isEnabled()){   //蓝牙未打开
                turnOnBlue();   //请求打开蓝牙
            }else{  //蓝牙已打开
                l38iSearchSwipe.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanBlueDevice(true);   //扫描蓝牙设备
                    }
                }, 1000);

            }
        }
    }

    //扫描蓝牙设备
    private void scanBlueDevice(boolean b) {
        if(b){
            isScann = true;
            l38iSearchSwipe.setRefreshing(true);    //显示动画
            b18IDeviceBeanList.clear();
            b18ISearchListAdapter.notifyDataSetChanged();
            BluetoothSDK.startScan(new BluetoothScanCallBack() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice, int i) {
                    Log.e(TAG,"-----l38i----"+bluetoothDevice.getName()+"--"+bluetoothDevice.getAddress());
                    for(B18IDeviceBean lb : b18IDeviceBeanList){
                        if(lb.getBluetoothDevice().getName().equals(bluetoothDevice.getName())){
                            return;
                        }
                    }
                    b18IDeviceBeanList.add(new B18IDeviceBean(bluetoothDevice,Math.abs(i)));
                    b18ISearchListAdapter.notifyDataSetChanged();
                }
            }, bleName);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(8 * 1000);
                        handler.sendEmptyMessage(1111);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            l38iSearchSwipe.setRefreshing(false);
            BluetoothSDK.stopScan();    //停止扫描
        }
    }

    //打开蓝牙
    private void turnOnBlue() {
        Intent trunonIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //设置蓝牙设备可以被其它蓝牙设备扫描到
        trunonIntent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //设置蓝牙可见时间
        trunonIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                250);
        startActivityForResult(trunonIntent,TRUNON_BLEU_CODE);

    }

    private void initViews() {
        tvTitle.setText(getResources().getString(R.string.search_device));
        toolbar.setNavigationIcon(R.mipmap.backs);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LinearLayoutManager linm = new LinearLayoutManager(B18ISearchActivity.this);
        linm.setOrientation(LinearLayoutManager.VERTICAL);
        l38iSearchRecyclerView.setLayoutManager(linm);
        l38iSearchRecyclerView.addItemDecoration(new DividerItemDecoration(B18ISearchActivity.this,DividerItemDecoration.VERTICAL));

        b18IDeviceBeanList = new ArrayList<>();
        b18ISearchListAdapter = new B18ISearchListAdapter(B18ISearchActivity.this, b18IDeviceBeanList);
        l38iSearchRecyclerView.setAdapter(b18ISearchListAdapter);
        b18ISearchListAdapter.setBindClickListener(this);

        l38iSearchSwipe.setOnRefreshListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TRUNON_BLEU_CODE){
            scanBlueDevice(true);
        }
    }

    //蓝牙连接回调
    ResultCallBack resultCallback = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            Log.e(TAG,"----i--"+i);
            switch (i){
                case ResultCallBack.TYPE_CONNECT:   //连接成功
                    closeLoadingDialog();
                    ToastUtil.showToast(B18ISearchActivity.this,"connect successfull");
                    SharedPreferencesUtils.saveObject(B18ISearchActivity.this,"mylanya","HR");
                    SharedPreferencesUtils.saveObject(B18ISearchActivity.this,"mylanyamac",mac);
                    startActivity(B18IHomeActivity.class,new String[]{"b18imac"},new String[]{mac});
                    B18ISearchActivity.this.finish();
                    break;
                case ResultCallBack.TYPE_DISCONNECT:

                    break;
            }
        }

        @Override
        public void onFail(int i) {
            closeLoadingDialog();
        }
    };

    /**
     * 绑定按钮点击事件回调
     * @param position
     */
    @Override
    public void doBindClick(int position) {
        l38iSearchSwipe.setRefreshing(false);
        showLoadingDialog("connection...");
        mac = b18IDeviceBeanList.get(position).getBluetoothDevice().getAddress();
        if(isScann){
            scanBlueDevice(false);
        }
        BluetoothSDK.connectByMAC(resultCallback,mac);
    }

    //刷新
    @Override
    public void onRefresh() {
       scanBlueDevice(true);

    }
}
