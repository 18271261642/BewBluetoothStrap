package com.example.bozhilun.android.B18I.b18isystemic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 关于
 * @author： 安
 * @crateTime: 2017/9/26 08:57
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class SettingAboutActivity extends AppCompatActivity {

    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.version_tv)
    TextView versionTv;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_about_layout);
//        setContentView(R.layout.b18i_app_firmware_update_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.abour));
        BluetoothSDK.getDeviceVersion(resultCallBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothSDK.getDeviceVersion(resultCallBack);
    }

    @OnClick({R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
        }
    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_DEVICE_VERSION:
                    versionTv.setText(String.valueOf(objects[0]));
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };
}
