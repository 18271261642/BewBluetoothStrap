package com.example.bozhilun.android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MainActivity;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.MyShouhuanXitongShenJiActivity;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by thinkpad on 2017/3/11.
 * 我的手环
 */

public class MyStrapActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.isbind_tv)
    TextView isbindTv;
    @BindView(R.id.shengji_relayout)
    RelativeLayout shengjiRelayout;

    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.my_strap);
        isbindTv.setText(R.string.unbind);
        try {
            if (null != SharedPreferencesUtils.readObject(MyStrapActivity.this, "mylanya")) {
                mDeviceAddress = (String) SharedPreferencesUtils.readObject(MyStrapActivity.this, "mylanmac");//蓝牙的mac
            } else {
                mDeviceAddress = MyCommandManager.ADDRESS;
            }

        } catch (Exception E) {
            E.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_mystrap;
    }


    @OnClick({R.id.shengji_relayout, R.id.jiechu_relayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shengji_relayout:
                startActivity(new Intent(this, MyShouhuanXitongShenJiActivity.class));
                break;
            case R.id.jiechu_relayout:
                new MaterialDialog.Builder(this)
                        .title(R.string.unbind_strap)
                        .content(R.string.confirm_unbind_strap)
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                MyApp.getmBluetoothLeService().disconnect();//断开蓝牙
                                MyCommandManager.deviceDisconnState = true;
                                MyApp.getApplication().getDaoSession().getStepBeanDao().deleteAll();//清空数据库

                              //  MyApp.getWatchBluetoothService().onDestroy();
//                                SharedPreferencesUtils.saveObject(MyStrapActivity.this, "mylanya", null);
//                                SharedPreferencesUtils.saveObject(MyStrapActivity.this, "mylanmac", null);
                                MyCommandManager.ADDRESS = null;
                                MyCommandManager.DEVICENAME = null;
                                BluetoothLeService.isService = false;
                                dialog.dismiss();
                                startActivity(new Intent(MyStrapActivity.this,SearchDeviceActivity.class));
                                finish();
                            }
                        }).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
