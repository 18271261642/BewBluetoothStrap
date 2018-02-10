package com.example.bozhilun.android.h9.settingactivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.device.Unit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 单位
 * @author： 安
 * @crateTime: 2017/10/10 16:33
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class IsUnitActivity extends WatchBaseActivity {
    private final String TAG = this.getClass().toString() + "----->>>";
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.text_unit)
    TextView textUnit;
    @BindView(R.id.switch_unit)
    Switch switchUnit;
    private int unit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h9_isunit_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.unit));
//        whichDevice();//判断是B18i还是H9
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onB18iEventBus(B18iEventBus event) {
        switch (event.getName()) {
            case "STATE_ON":
                startActivity(NewSearchActivity.class);
                finish();
                break;
            case "STATE_TURNING_ON":
                break;
            case "STATE_OFF":
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
                break;
            case "STATE_TURNING_OFF":
                Toast.makeText(this, getResources().getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        whichDevice();//判断是B18i还是H9
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i){
            case "B18i":
                getUnit38iSwitch();
                break;
            case "H9":
                getUnitH9Switch();
                break;
            case "B15P":

                break;
        }
        switchUnit.setOnCheckedChangeListener(new OnChangeListenter());

    }

    private void getUnit38iSwitch() {
        BluetoothSDK.getUnit(resultCallBack);
    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_UNIT:
                    unit = (int) objects[0];
                    if (unit == 0) {
                        switchUnit.setChecked(false);
                        textUnit.setText(getResources().getString(R.string.setkm));
                    } else {
                        switchUnit.setChecked(true);
                        textUnit.setText(getResources().getString(R.string.setmi));
                    }
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    private void getUnitH9Switch() {
        showLoadingDialog(getResources().getString(R.string.dlog));
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new Unit(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        // 0位公制 1为英制
                        Log.d(TAG, "单位格式" + GlobalVarManager.getInstance().getUnit() + "");
                        unit = GlobalVarManager.getInstance().getUnit();
                        if (unit == 0) {
                            switchUnit.setChecked(false);
                            textUnit.setText(getResources().getString(R.string.setkm));
                        } else {
                            switchUnit.setChecked(true);
                            textUnit.setText(getResources().getString(R.string.setmi));
                        }
                        closeLoadingDialog();
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Log.d(TAG, "单位格式获取失败");
                        Toast.makeText(IsUnitActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
                        closeLoadingDialog();
                    }
                }));
    }

    @OnClick({R.id.image_back})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    private class OnChangeListenter implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.switch_unit:
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    if (b) {
                        textUnit.setText(getResources().getString(R.string.setmi));
                        if (is18i.equals("B18i")) {
                            BluetoothSDK.setUnit(resultCallBack, 1);
                        } else {
                            // 0位公制 1为英制
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new Unit(commandResultCallback, (byte) 0x01));
                        }
                    } else {
                        textUnit.setText(getResources().getString(R.string.setkm));
                        if (is18i.equals("B18i")) {
                            BluetoothSDK.setUnit(resultCallBack, 0);
                        } else {
                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                    .sendCommand(new Unit(commandResultCallback, (byte) 0x00));
                        }
                    }
                    break;
            }
        }
    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof Unit) {
                Log.d(TAG, "单位设置成功");
                closeLoadingDialog();
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            Toast.makeText(IsUnitActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
            closeLoadingDialog();
        }
    };
}
