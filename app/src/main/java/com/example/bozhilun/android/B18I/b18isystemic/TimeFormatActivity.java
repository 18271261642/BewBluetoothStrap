package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.B18I.b18ibean.TimeFormatBean;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.bean.DeviceTimeFormat;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.device.TimeSurfaceSetting;
import com.sdk.bluetooth.protocol.command.expands.RemindCount;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;

/**
 * @aboutContent: 显示格式
 * @author： 安
 * @crateTime: 2017/9/6 15:40
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */
public class TimeFormatActivity extends WatchBaseActivity {
    public final String TAG = "----->" + this.getClass();
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.time_format_list)
    ListView timeFormatList;

    private int[] image = {R.mipmap.time_a, R.mipmap.time_b, R.mipmap.time_c,
            R.mipmap.time_d, R.mipmap.time_e, R.mipmap.time_f,
            R.mipmap.time_g, R.mipmap.time_h, R.mipmap.time_i,
            R.mipmap.time_j, R.mipmap.time_k, R.mipmap.time_l};
    private int[] image2 = {R.mipmap.display_time_two,//R.mipmap.display_time_one,
            R.mipmap.display_time_three, R.mipmap.display_time_four};
    private MyTimeFormatAdpter adpter;
    //    List<Integer> integers = new ArrayList<>();
    List<TimeFormatBean> timeFormatBeenList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_time_format_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.time_forma));
//        whichDevice();//判断是B18i还是H9
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    private int TYPE = 0;

    /**
     * 判断是B18i还是H9
     */
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i){
            case "B18i":

                break;
            case "H9":
                showLoadingDialog(getResources().getString(R.string.dlog));
                getTypeSele();
                break;
            case "B15P":

                break;
        }

    }


    /**
     * H9设置时间显示格式
     *
     * @param number
     */
    public void setH9DisplaySu(int number) {
        showLoadingDialog(getResources().getString(R.string.dlog));
        // 日期格式(0x00:不显示日期   0x01:yy/mm/dd   0x02:dd/mm/yy   0x03:mm/dd/yy   0x04:星期几/mm/dd  0x05:mm/dd  0x06:dd/mm  0x07星期几dd/mm/yy  0x08： 星期几dd/mm)
        // 电池显示(0x00:不显示电池   0x01:显示电池)

        switch (number) {
//            case 0:
//                deviceTimeFormat.setDateFormat((byte) 0x00);
//                deviceTimeFormat.setBatteryFormat((byte) 0x00);
//                break;
            case 0:
                DeviceTimeFormat deviceTimeFormat = new DeviceTimeFormat();
                deviceTimeFormat.setDateFormat((byte) 0x00);
                deviceTimeFormat.setBatteryFormat((byte) 0x01);
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new TimeSurfaceSetting(commandResultCallback, deviceTimeFormat));
                break;
            case 1:
                DeviceTimeFormat deviceTimeFormat1 = new DeviceTimeFormat();
                deviceTimeFormat1.setDateFormat((byte) 0x04);
                deviceTimeFormat1.setBatteryFormat((byte) 0x00);
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new TimeSurfaceSetting(commandResultCallback, deviceTimeFormat1));
                break;
            case 2:
                DeviceTimeFormat deviceTimeFormat2 = new DeviceTimeFormat();
                deviceTimeFormat2.setDateFormat((byte) 0x04);
                deviceTimeFormat2.setBatteryFormat((byte) 0x01);
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new TimeSurfaceSetting(commandResultCallback, deviceTimeFormat2));
                break;
        }

    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof TimeSurfaceSetting) {
                if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {

                } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                    getTypeSele();
                }
//                //获取显示格式
//                AppsBluetoothManager.getInstance(MyApp.getContext())
//                        .sendCommand(new TimeSurfaceSetting(new BaseCommand.CommandResultCallback() {
//                            @Override
//                            public void onSuccess(BaseCommand baseCommand) {
//                                // 日期格式(0x00:不显示日期   0x01:yy/mm/dd   0x02:dd/mm/yy   0x03:mm/dd/yy   0x04:星期几/mm/dd  0x05:mm/dd  0x06:dd/mm  0x07星期几dd/mm/yy  0x08： 星期几dd/mm)
//                                // 电池显示(0x00:不显示电池   0x01:显示电池)
//                                Log.d(TAG, "日期格式" + GlobalVarManager.getInstance().getDeviceTimeFormat().getDateFormat() +
//                                        "电池显示:" + GlobalVarManager.getInstance().getDeviceTimeFormat().getBatteryFormat() + "");
//                                int DATE = GlobalVarManager.getInstance().getDeviceTimeFormat().getDateFormat();
//                                int BATT = GlobalVarManager.getInstance().getDeviceTimeFormat().getBatteryFormat();
//
//                                if (DATE == 0 && BATT == 1) {
//                                    TYPE = 0;
//                                } else if (DATE == 4 && BATT == 0) {
//                                    TYPE = 1;
//                                } else if (DATE == 4 && BATT == 1) {
//                                    TYPE = 2;
//                                }
//                                Log.d(TAG, TYPE + "");
//                                getDatas();
//                                adpter.notifyDataSetChanged();
//                                closeLoadingDialog();
//                            }
//
//                            @Override
//                            public void onFail(BaseCommand command) {
//                                Log.d(TAG, "获取显示格式失败");
//                                Toast.makeText(TimeFormatActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
//                                closeLoadingDialog();
//                            }
//                        }));
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            getTypeSele();
            closeLoadingDialog();
            Toast.makeText(TimeFormatActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
        }
    };

    public void getTypeSele() {
        //获取显示格式
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new TimeSurfaceSetting(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand baseCommand) {
                        // 日期格式(0x00:不显示日期   0x01:yy/mm/dd   0x02:dd/mm/yy   0x03:mm/dd/yy   0x04:星期几/mm/dd  0x05:mm/dd  0x06:dd/mm  0x07星期几dd/mm/yy  0x08： 星期几dd/mm)
                        // 电池显示(0x00:不显示电池   0x01:显示电池)
                        Log.d(TAG, "日期格式" + GlobalVarManager.getInstance().getDeviceTimeFormat().getDateFormat() +
                                "电池显示:" + GlobalVarManager.getInstance().getDeviceTimeFormat().getBatteryFormat() + "");
                        int DATE = GlobalVarManager.getInstance().getDeviceTimeFormat().getDateFormat();
                        int BATT = GlobalVarManager.getInstance().getDeviceTimeFormat().getBatteryFormat();


                        Log.e("----------", DATE + "===" + BATT);
                        if (DATE == 0 && BATT == 1) {
                            TYPE = 0;
                        }
                        else if (DATE == 0 && BATT == 0) {
                            TYPE = 0;
                        }
                        else if (DATE == 4 && BATT == 0) {
                            TYPE = 1;
                        } else if (DATE == 4 && BATT == 1) {
                            TYPE = 2;
                        }
                        Log.e(TAG, TYPE + "-----------------");
                        getDatas();
                        adpter.notifyDataSetChanged();
                        closeLoadingDialog();
                    }

                    @Override
                    public void onFail(BaseCommand baseCommand) {
//                        Log.d(TAG, "获取显示格式失败");
                        closeLoadingDialog();
                        if (baseCommand.getAction() == CommandConstant.ACTION_CHECK){
                            Toast.makeText(TimeFormatActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (baseCommand.getAction() == CommandConstant.ACTION_SET){
                            Toast.makeText(TimeFormatActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }


    private void getDatas() {
        if (timeFormatBeenList != null) {
            timeFormatBeenList.clear();
        }
        if (is18i.equals("B18i")) {
            for (int i = 0; i < image.length; i++) {
                TimeFormatBean timeFormatBean = new TimeFormatBean();
                timeFormatBean.setImagesTime(image2[i]);
                timeFormatBean.setStateTime(false);
            }
        } else {
            for (int i = 0; i < image2.length; i++) {
                TimeFormatBean timeFormatBean = new TimeFormatBean();
                timeFormatBean.setImagesTime(image2[i]);
                if (TYPE == i) {
                    timeFormatBean.setStateTime(true);
                } else {
                    timeFormatBean.setStateTime(false);
                }
                timeFormatBeenList.add(timeFormatBean);
            }
        }

        adpter = new MyTimeFormatAdpter(this);
        timeFormatList.setAdapter(adpter);
        timeFormatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "点击了" + position);
                if (is18i.equals("B18i")) {

                } else {
                    setH9DisplaySu(position);
                    timeFormatBeenList.clear();
                    for (int i = 0; i < image2.length; i++) {
                        TimeFormatBean timeFormatBean = new TimeFormatBean();
                        timeFormatBean.setImagesTime(image2[i]);
                        if (TYPE == position) {
                            timeFormatBean.setStateTime(true);
                        } else {
                            timeFormatBean.setStateTime(false);
                        }
                        timeFormatBeenList.add(timeFormatBean);
                    }
                    adpter.notifyDataSetChanged();
                }
            }
        });

    }

    public class MyTimeFormatAdpter extends BaseAdapter {
        private LayoutInflater inflater = null;
        private Context contexts = null;

        public MyTimeFormatAdpter(Context context) {
            this.contexts = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return timeFormatBeenList.size();
        }

        @Override
        public Object getItem(int position) {
            return timeFormatBeenList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.b18i_time_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.time_imge);
                viewHolder.imageRt = (ImageView) convertView.findViewById(R.id.image_rt);
                convertView.setTag(viewHolder);// 通过setTag将ViewHolder和convertView绑定
            } else {
                viewHolder = (ViewHolder) convertView.getTag(); // 获取，通过ViewHolder找到相应的控件
            }
            viewHolder.imageView.setImageResource(timeFormatBeenList.get(position).getImagesTime());

            if (timeFormatBeenList.get(position).isStateTime()) {
                viewHolder.imageRt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageRt.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imageView, imageRt;
        }
    }
}
