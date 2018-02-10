package com.example.bozhilun.android.h9.settingactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.sdk.bluetooth.bean.BloodData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.clear.ClearBloodData;
import com.sdk.bluetooth.protocol.command.data.GetBloodData;
import com.sdk.bluetooth.protocol.command.other.BloodStatus;
import com.sdk.bluetooth.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 血压
 * @author： 安
 * @crateTime: 2017/10/10 14:46
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class BloodPressureActivity extends AppCompatActivity {
    private final String TAG = this.getClass().toString() + "----->>>";
    @BindView(R.id.bar_titles)
    TextView barTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_blood_layout);
        ButterKnife.bind(this);
        barTitles.setText("血压");
        getBloodSwitch();
        findViewById(R.id.line_celiang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开血压
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new BloodStatus(commandResultCallback, 1));
            }
        });
        //获取血压数据
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new GetBloodData(commandResultCallback, 0, 0, (int) GlobalVarManager.getInstance().getBloodCount()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭血压
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new BloodStatus(commandResultCallback, 0));
    }

    /**
     * 获取血压开关
     */
    private void getBloodSwitch() {
        AppsBluetoothManager.getInstance(MyApp.getApplication())
                .sendCommand(new BloodStatus(commandResultCallback));
    }

    @OnClick({R.id.image_back})
    public void Onclic(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

            if (baseCommand instanceof GetBloodData) {
                String bloodDatas = "";
                for (BloodData bloodData : GlobalDataManager.getInstance().getBloodDatas()) {
                    bloodDatas += "bigValue:" + bloodData.bigValue + "minValue:" + bloodData.minValue + "---time:" + DateUtil.dateToSec(DateUtil.timeStampToDate(bloodData.time_stamp * 1000)) + "\n";
                }
                Log.d(TAG, bloodDatas);
            }
//            if (baseCommand instanceof ClearBloodData) {
//                Log.d(TAG, "清除血压成功");
//            }


            if (baseCommand instanceof BloodStatus) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                    Log.d(TAG, "status:" + GlobalVarManager.getInstance().isBloodMeasure());
                    if (GlobalVarManager.getInstance().isBloodMeasure()) {
                        AppsBluetoothManager.getInstance(MyApp.getApplication())
                                .sendCommand(new BloodStatus(commandResultCallback));
                    } else {
                        //关闭血压
                        AppsBluetoothManager.getInstance(MyApp.getContext())
                                .sendCommand(new BloodStatus(commandResultCallback, 0));
                    }
                }
                if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    Log.d(TAG, "血压。。。。。成功");
                }
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {

        }
    };
}
