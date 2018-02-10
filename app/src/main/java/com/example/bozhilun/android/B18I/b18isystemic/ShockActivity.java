package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.Motor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 震动
 * @author： 安
 * @crateTime: 2017/9/5 18:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class ShockActivity extends WatchBaseActivity {
    public final String TAG = "----->>>" + this.getClass();
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.shock_image1)
    ImageView shockImage1;
    @BindView(R.id.shock_image2)
    ImageView shockImage2;
    @BindView(R.id.shock_image3)
    ImageView shockImage3;
    @BindView(R.id.shock_image4)
    ImageView shockImage4;
    @BindView(R.id.shock_close)
    LinearLayout shockClose;
    private int SelectST = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_shock_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.shock_str));
//        whichDevice();//判断是B18i还是H9
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

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        switch (is18i){
            case "B18i":

                break;
            case "H9":
                showLoadingDialog(getResources().getString(R.string.dlog));
                shockClose.setVisibility(View.GONE);
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new Motor(commandResultCallback));
                break;
            case "B15P":

                break;
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        //----------------------需要获取震动强度---------->>>设置默认震动强度
////        BluetoothSDK.getShockStrength(resultCallBack);
//    }

    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_SET_SHOCK_STRENGTH:
                    Log.i("---------", "震动强度设置成功------" + Arrays.toString(objects));
                    break;
            }
        }

        @Override
        public void onFail(int i) {

        }
    };

    @OnClick({R.id.image_back, R.id.shock_close,
            R.id.shock_weak, R.id.shock_standard, R.id.shock_strong})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.shock_close:
                setRT(0);
                SelectST = 0;
                if (is18i.equals("B18i")) {
                    BluetoothSDK.setShockStrength(resultCallBack, SelectST * 30);
                }
                break;
            case R.id.shock_weak:
                setRT(1);
                SelectST = 1;
                if (is18i.equals("B18i")) {
                    BluetoothSDK.setShockStrength(resultCallBack, SelectST * 30);
                } else {
                    //H9
                    // 0x01 :  默认最弱震动强度
                    // 0x03 :  震动强度中
                    // 0x05 :  震动强度高
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new Motor(commandResultCallback, (byte) 0x01));
                }
                break;
            case R.id.shock_standard:
                setRT(2);
                SelectST = 2;
                if (is18i.equals("B18i")) {
                    BluetoothSDK.setShockStrength(resultCallBack, SelectST * 30);
                } else {
                    //H9
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new Motor(commandResultCallback, (byte) 0x03));
                }
                break;
            case R.id.shock_strong:
                setRT(3);
                SelectST = 3;
                if (is18i.equals("B18i")) {
                    BluetoothSDK.setShockStrength(resultCallBack, SelectST * 30);
                } else {
                    //H9
                    showLoadingDialog(getResources().getString(R.string.dlog));
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new Motor(commandResultCallback, (byte) 0x05));
                }
                break;
        }
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new Motor(commandResultCallback));
    }


    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {

        @Override
        public void onSuccess(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand instanceof Motor) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK){
                    byte motor = GlobalVarManager.getInstance().getMotor();
                    Log.d("-----------", motor + "");
                    if (motor == 1) {
                        setRT(1);
                    } else if (motor == 3) {
                        setRT(2);
                    } else if (motor == 5) {
                        setRT(3);
                    }
                }else if (baseCommand.getAction() == CommandConstant.ACTION_SET){
                    Log.d(TAG, "震动强度设置成功");
                }

            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK){
                Toast.makeText(ShockActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
                finish();
            }else if (baseCommand.getAction() == CommandConstant.ACTION_SET){
                Toast.makeText(ShockActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void setRT(int a) {
        if (a == 0) {
            shockImage1.setVisibility(View.VISIBLE);
            shockImage2.setVisibility(View.GONE);
            shockImage3.setVisibility(View.GONE);
            shockImage4.setVisibility(View.GONE);
        } else if (a == 1) {
            shockImage1.setVisibility(View.GONE);
            shockImage2.setVisibility(View.VISIBLE);
            shockImage3.setVisibility(View.GONE);
            shockImage4.setVisibility(View.GONE);
        } else if (a == 2) {
            shockImage1.setVisibility(View.GONE);
            shockImage2.setVisibility(View.GONE);
            shockImage3.setVisibility(View.VISIBLE);
            shockImage4.setVisibility(View.GONE);
        } else if (a == 3) {
            shockImage1.setVisibility(View.GONE);
            shockImage2.setVisibility(View.GONE);
            shockImage3.setVisibility(View.GONE);
            shockImage4.setVisibility(View.VISIBLE);
        }
    }
}
