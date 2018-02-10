package com.example.bozhilun.android.h9.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.settingactivity.CorrectionTimeActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Device_Time_Activity extends WatchBaseActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.text_nowtime)
    TextView textNowTime;
    @BindView(R.id.text_devicetime)
    TextView textDeviceTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device__time_activity);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.device_times));
        String systemTimer = B18iUtils.getSystemTimers2();
        textNowTime.setText(systemTimer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        whichDevice();
    }

    //判断是B18i还是H9
    String is18i = "H9";

    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        if (is18i.equals("B18i")) {

        } else {
            showLoadingDialog(getResources().getString(R.string.dlog));
            AppsBluetoothManager.getInstance(MyApp.getContext())
                    .sendCommand(new DateTime(commandResultCallback));
        }
    }

    @OnClick({R.id.image_back, R.id.btn_get_deviceTime, R.id.text_goto_coorectionTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_get_deviceTime:
                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new DateTime(commandResultCallback));
                String systemTimer = B18iUtils.getSystemTimers2();
                textNowTime.setText(systemTimer);
                break;
            case R.id.text_goto_coorectionTime:
                startActivity(CorrectionTimeActivity.class);
                break;
        }
    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof DateTime) {
                closeLoadingDialog();
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    String deviceDateTime = GlobalVarManager.getInstance().getDeviceDateTime();
                    String[] splitTime = deviceDateTime.split("\\s+");
                    String s1 = splitTime[0];
                    String s2 = splitTime[1];

                    String[] split1 = s1.split("-");
                    String deviceTimes1 = split1[0].trim();
                    for (int i = 1; i < split1.length; i++) {
                        String times = split1[i].trim();
                        int number = Integer.valueOf(times);
                        if (number > 9) {
                            deviceTimes1 += "-" + number;
                        } else {
                            deviceTimes1 += "-" + "0" + number;
                        }
                    }

                    String[] split2 = s2.split(":");
//                    String deviceTimes2 = split2[0].trim();
                    String deviceTimes2 = "";
                    for (int i = 0; i < split2.length; i++) {
                        String times = split2[i].trim();
                        int number = Integer.valueOf(times);
                        if (number > 9) {
                            deviceTimes2 += ":" + number;
                        } else {
                            deviceTimes2 += ":" + "0" + number;
                        }
                    }
                    Log.d("-----------dd", "----设备时间2-----" + deviceDateTime + "===s1===" + s1 + "===s2===" + s2);
                    Log.e("-----------dd", "----日期-----：" + deviceTimes1
                            + "\n----时间----：" + deviceTimes2.substring(1, deviceTimes2.length()));
                    textDeviceTime.setText(deviceTimes1 + " " + deviceTimes2.substring(1, deviceTimes2.length()));
//                    textDeviceTime.setText(deviceDateTime);
                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.d("-----Device_Time_Activity", "设置时间");
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
            Toast.makeText(Device_Time_Activity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
        }
    };
}
