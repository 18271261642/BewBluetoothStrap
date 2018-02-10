package com.example.bozhilun.android.B18I.b18isystemic;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.B18I.b18iutils.B18ITimePicker;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.evententity.B18iEventBus;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.bean.RemindData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.manage.GlobalDataManager;
import com.sdk.bluetooth.manage.GlobalVarManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.base.CommandConstant;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.device.Motor;
import com.sdk.bluetooth.protocol.command.expands.RemindCount;
import com.sdk.bluetooth.protocol.command.setting.SwitchSetting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.bluetooth.app.BluetoothSDK;
import cn.appscomm.bluetooth.interfaces.ResultCallBack;
import cn.appscomm.bluetooth.model.ReminderData;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @aboutContent: 闹钟设置
 * @author： 安
 * @crateTime: 2017/9/6 09:02
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class AlarmClockRemindActivity extends WatchBaseActivity {

    private final String TAG = "----->>>" + this.getClass().toString();
    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.btn_new_remind)
    Button btnNewRemind;
    @BindView(R.id.prompt_layout_text)
    LinearLayout promptLayoutText;
    @BindView(R.id.list_alarm)
    ListView listAlarm;

    private final int REQUEST_ALARM_CLOCK_NEW = 1;// 新建闹钟的requestCode
    private final int REQUEST_ALARM_CLOCK_EDIT = 2;// 修改旧闹钟的requestCode
    private int SelectAlarmModlu = 0; //选择的闹钟类型
    private int ALARM_TYPE;//闹钟的操作类型
    private final int NEW_ALARMCLOCK = 0;//新建闹钟
    private final int CHANGE_ALARMCLOCK = 1;//修改闹钟
    private final int DELETE_ALARMCLOCK = 2;//删除脑子
    private ReminderData oldReminderData;//修改闹钟时要用到的旧的闹钟旧闹钟

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_alarm_clock_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.alarmclock));
        whichDevice();//判断是B18i还是H9
    }

    private String is18i;

    //判断是B18i还是H9
    private void whichDevice() {
        is18i = getIntent().getStringExtra("is18i");
        if (TextUtils.isEmpty(is18i)) finish();
        //在这里分别请求数据
        switch (is18i){
            case "B18i":
                BluetoothSDK.getReminder(resultCallBack);//获取闹钟
                break;
            case "H9":
                showLoadingDialog(getResources().getString(R.string.dlog));
                //读取所以提醒条数
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new RemindCount(commandResultCallback, 1, 0));
                break;
            case "B15P":

                break;
        }
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

    /**
     * 蓝牙操作监听回调
     */
    ResultCallBack resultCallBack = new ResultCallBack() {
        @Override
        public void onSuccess(int i, Object[] objects) {
            switch (i) {
                case ResultCallBack.TYPE_GET_REMINDER:
                    Log.e(TAG, "获取闹钟成功" + Arrays.toString(objects) + "====闹钟个数+" + objects.length);

                    if (objects.length > 0) {
                        promptLayoutText.setVisibility(View.GONE);
                        listAlarm.setVisibility(View.VISIBLE);
                        final List<ReminderData> reminderDatas = (List<ReminderData>) objects[0];
                        MyAlarmClockAdapter alarmClockAdapter = new MyAlarmClockAdapter(reminderDatas);
                        listAlarm.setAdapter(alarmClockAdapter);
                    } else {
                        promptLayoutText.setVisibility(View.VISIBLE);
                        listAlarm.setVisibility(View.GONE);
                    }
                    break;
                case ResultCallBack.TYPE_NEW_REMINDER:
                    Log.e(TAG, "新建成功" + Arrays.toString(objects));
                    break;
                case ResultCallBack.TYPE_CHANGE_REMINDER:
                    Log.e(TAG, "修改成功" + Arrays.toString(objects));
                    break;
                case ResultCallBack.TYPE_DELETE_A_REMINDER:
                    Log.e(TAG, "删除成功" + Arrays.toString(objects));
                    listAlarm.invalidate();
                    Toast.makeText(AlarmClockRemindActivity.this, "已删除闹钟", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onFail(int i) {
        }
    };

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof RemindCount) {
                Log.d(TAG, "所有提醒条数为：" + GlobalVarManager.getInstance().getRemindCount() + "");
                if (GlobalVarManager.getInstance().getRemindCount() <= 0) {
                    promptLayoutText.setVisibility(View.VISIBLE);
                    listAlarm.setVisibility(View.GONE);
                    listAlarm.deferNotifyDataSetChanged();
                }
                if (GlobalVarManager.getInstance().getRemindCount() >= 7) {
                    btnNewRemind.setVisibility(View.GONE);
                } else {
                    btnNewRemind.setVisibility(View.VISIBLE);
                }
                //读取所有提醒
                // 获取提醒数据详情需要传入提醒数据条数，所以在获取提醒数据详情之前必需先获取提醒数据条数。
                // 如果提醒条数为0，则提醒详情数必定为0。所以如果获取到提醒条数为0，则没有必要再去获取提醒详情数据。
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new RemindSetting(new BaseCommand.CommandResultCallback() {
                            @Override
                            public void onSuccess(BaseCommand command) {
                                promptLayoutText.setVisibility(View.GONE);
                                listAlarm.setVisibility(View.VISIBLE);

                                //id:1type:0 text:null
                                //hours:16 minutes:50 week:1111111 set_ok:1
                                //id:2type:4 text:null
                                //hours:4 minutes:32 week:0000001 set_ok:1
                                final List<RemindData> list = GlobalDataManager.getInstance().getRemindDatas();
                                final H9AlarmAdapter h9AlarmAdapter = new H9AlarmAdapter(list, AlarmClockRemindActivity.this);
                                listAlarm.setAdapter(h9AlarmAdapter);
                                listAlarm.deferNotifyDataSetChanged();
                                closeLoadingDialog();

                                listAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        Log.e(TAG, "--修改---" + list.get(position).remind_time_hours + ":" + list.get(position).remind_time_minutes);
                                        ALARM_TYPE = list.get(position).remind_type;
                                        int remind_set_ok = list.get(position).remind_set_ok;
                                        int remind_type = list.get(position).remind_type;
                                        int remind_time_hours = list.get(position).remind_time_hours;
                                        int remind_time_minutes = list.get(position).remind_time_minutes;
                                        int remind_week = Integer.parseInt(B18iUtils.toD(list.get(position).remind_week, 2));
                                        String remind_text = list.get(position).remind_text;
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_SWE_OK", remind_set_ok);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_TYPE", remind_type);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_HOURS", remind_time_hours);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_MIN", remind_time_minutes);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_WEEK", remind_week);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "ALARM_TEXT", remind_text);

                                        RemindData remindData = list.get(position);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("remindData", remindData);
                                        Intent intent = new Intent(AlarmClockRemindActivity.this, B18ITimePicker.class);
                                        intent.putExtra("type", "change");
                                        intent.putExtras(bundle);
                                        // 开启编辑闹钟界面
                                        startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
                                        // 启动移动进入效果动画
                                        overridePendingTransition(R.anim.move_in_bottom, 0);
                                    }
                                });

                                listAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                        new MaterialDialog.Builder(AlarmClockRemindActivity.this)
                                                .title(R.string.edit_alarm_clock)
                                                .content(R.string.deleda)
                                                .positiveText(getResources().getString(R.string.confirm))
                                                .negativeText(getResources().getString(R.string.cancle))
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                        dialog.dismiss();
                                                        Log.e(TAG, "--删除---" + list.get(position).remind_time_hours + ":" + list.get(position).remind_time_minutes);
                                                        final int aaa = Integer.parseInt(B18iUtils.toD(list.get(position).remind_week, 2));
                                                        int remind_set_ok = list.get(position).remind_set_ok;
                                                        final byte isopen;
                                                        if (remind_set_ok == 0) {
                                                            isopen = 0x00;
                                                        } else {
                                                            isopen = 0x01;
                                                        }
                                                        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
                                                            @Override
                                                            public void call(Subscriber<? super String> subscriber) {
                                                                AppsBluetoothManager.getInstance(MyApp.getContext())
                                                                        .sendCommand(new RemindSetting(commandResultCallback,
                                                                                (byte) 0x02,//删除
                                                                                (byte) list.get(position).remind_type,//类型：。。。"吃饭", "吃药", "喝水。。。
                                                                                (byte) list.get(position).remind_time_hours,//时
                                                                                (byte) list.get(position).remind_time_minutes,//分
                                                                                (byte) aaa,//周期
                                                                                (byte) isopen));//0关、1开
                                                                //读取所以提醒条数
                                                                AppsBluetoothManager.getInstance(MyApp.getContext())
                                                                        .sendCommand(new RemindCount(commandResultCallback, 1, 0));
                                                                showLoadingDialog(getResources().getString(R.string.dlog));
                                                                h9AlarmAdapter.notifyDataSetChanged();
                                                                subscriber.onCompleted();
                                                            }
                                                        });

                                                        Observer<String> observer = new Observer<String>() {
                                                            @Override
                                                            public void onNext(String s) {
                                                                Log.d(TAG, "Item: " + s);
                                                            }

                                                            @Override
                                                            public void onCompleted() {
                                                                Log.d(TAG, "Completed!");
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Log.d(TAG, "Error!");
                                                            }
                                                        };
                                                        observable.subscribe(observer);

                                                    }
                                                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                        return true;
                                    }
                                });
                                h9AlarmAdapter.setSetOnSwitchOnclick(new H9AlarmAdapter.SwitchOnclick() {
                                    @Override
                                    public void OnSwitchOnclick(CompoundButton buttonView, boolean isChecked, int position) {
                                        int aaa = Integer.parseInt(B18iUtils.toD(list.get(position).remind_week, 2));
                                        if (isChecked) {
                                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                                    .sendCommand(new RemindSetting(commandResultCallback,
                                                            11,
                                                            (byte) 0x01,//修改
                                                            (byte) list.get(position).remind_type,//类型：。。。"吃饭", "吃药", "喝水。。。
                                                            (byte) list.get(position).remind_time_hours,//时
                                                            (byte) list.get(position).remind_time_minutes,//分
                                                            (byte) aaa,//周期
                                                            (byte) list.get(position).remind_set_ok,
                                                            new byte[]{}, (byte) list.get(position).remind_type,
                                                            (byte) list.get(position).remind_time_hours, (byte) list.get(position).remind_time_minutes,
                                                            (byte) aaa, (byte) 0x01));//0关、1开
                                        } else {
                                            AppsBluetoothManager.getInstance(MyApp.getContext())
                                                    .sendCommand(new RemindSetting(commandResultCallback,
                                                            11,
                                                            (byte) 0x01,//修改
                                                            (byte) list.get(position).remind_type,//类型：。。。"吃饭", "吃药", "喝水。。。
                                                            (byte) list.get(position).remind_time_hours,//时
                                                            (byte) list.get(position).remind_time_minutes,//分
                                                            (byte) aaa,//周期
                                                            (byte) list.get(position).remind_set_ok,
                                                            new byte[]{}, (byte) list.get(position).remind_type,
                                                            (byte) list.get(position).remind_time_hours, (byte) list.get(position).remind_time_minutes,
                                                            (byte) aaa, (byte) 0x00));//0关、1开
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFail(BaseCommand command) {
                                closeLoadingDialog();
                                Log.d(TAG, "读取所有提醒失败");
                            }
                        }, GlobalVarManager.getInstance().getRemindCount()));
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
            if (baseCommand.getAction() == CommandConstant.ACTION_CHECK) {
                Toast.makeText(AlarmClockRemindActivity.this, getResources().getString(R.string.get_fail), Toast.LENGTH_SHORT).show();
                finish();
            } else if (baseCommand.getAction() == CommandConstant.ACTION_SET) {
                Toast.makeText(AlarmClockRemindActivity.this, getResources().getString(R.string.settings_fail), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @OnClick({R.id.btn_new_remind, R.id.image_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_new_remind:
                addNewAlarmClock();
                break;
        }
    }

    /**
     * 弹出pop提示
     * 添加新闹钟，选择闹钟模式
     */
    private void addNewAlarmClock() {
        View view = LayoutInflater.from(AlarmClockRemindActivity.this).inflate(R.layout.b18i_pop, null, false);
        PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setContentView(view);
        //设置pop数据
        setPopContent(popupWindow, view);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹出框消失.  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8f000000")));//new BitmapDrawable()
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        //设置可以点击
        popupWindow.setTouchable(true);
        //从底部显示
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }

    /**
     * 设置pop数据
     *
     * @param view
     */
    private void setPopContent(final PopupWindow popupWindow, View view) {
        final List<Map<String, Object>> data_list = new ArrayList<>();
        //获取数据
        getData(data_list);
        GridView gview = (GridView) view.findViewById(R.id.gview);
        gview.setVerticalSpacing(5);
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        SimpleAdapter sim_adapter = new SimpleAdapter(AlarmClockRemindActivity.this, data_list, R.layout.item, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectAlarmModlu = position;
                Log.e(TAG, data_list.get(position).toString() + "=====" + position);
//                Intent intent = new Intent(AlarmClockRemindActivity.this, AlarmClockNewActivity.class);
                Intent intent = new Intent(AlarmClockRemindActivity.this, B18ITimePicker.class);
                intent.putExtra("type", "new");
                // 开启新建闹钟界面
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_NEW);
                // 启动渐变放大效果动画
                overridePendingTransition(R.anim.zoomin, 0);
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置类型图标
     *
     * @return
     */
//    String[] iconName = {"吃饭", "吃药", "喝水", "睡觉", "起床", "运动", "会议", "自定义"};

    String[] iconName = new String[8];

//    String[] iconName = getResources().getStringArray(R.array.planets_array );
//    String[] iconName = {getResources().getString(R.string.alarm_eat),
//            getResources().getString(R.string.alarm_medicine),
//            getResources().getString(R.string.alarm_dring),
//            getResources().getString(R.string.alarm_sp),
//            getResources().getString(R.string.alarm_awakes),
//            getResources().getString(R.string.alarm_spr),
//            getResources().getString(R.string.alarm_metting),
//            getResources().getString(R.string.alarm_custom)};

    private void getData(List<Map<String, Object>> data_list) {
        iconName[0] = getResources().getString(R.string.alarm_eat);
        iconName[1] = getResources().getString(R.string.alarm_medicine);
        iconName[2] = getResources().getString(R.string.alarm_dring);
        iconName[3] = getResources().getString(R.string.alarm_sp);
        iconName[4] = getResources().getString(R.string.alarm_awakes);
        iconName[5] = getResources().getString(R.string.alarm_spr);
        iconName[6] = getResources().getString(R.string.alarm_metting);
        iconName[7] = getResources().getString(R.string.alarm_custom);

        final int[] icon = {R.mipmap.eat, R.mipmap.medicine, R.mipmap.dring,
                R.mipmap.sp, R.mipmap.awakes, R.mipmap.spr, R.mipmap.metting, R.mipmap.custom};
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
    }


    /**
     * 跳转返回传值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        Log.e(TAG, "-----------requestCode:" + requestCode + "=====resultCode:" + resultCode);
        int h = data.getIntExtra("h", 12);
        int m = data.getIntExtra("m", 12);
        int c = data.getIntExtra("c", 127);

        switch (requestCode) {
            case REQUEST_ALARM_CLOCK_NEW://添加新的闹钟
                //---------------------------------------类型--------------------时-------------------分-----------周期----提醒开关--------内容
                Log.e(TAG, "--新---" + h + ":" + m);
                if (is18i.equals("B18i")) {
                    ReminderData ttttt = new ReminderData(SelectAlarmModlu, h, m, c, true, iconName[SelectAlarmModlu]);
                    //------------------------监听--------类型（创建新的）----旧的-----新的
                    BluetoothSDK.setReminder(resultCallBack, NEW_ALARMCLOCK, null, ttttt);
                } else {
                    // callback
                    // 提醒操作(0x00:新增 0x01:修改 0x02:删除 0x03:全部删除)
                    // 新增或单条删除 提醒类型: 吃饭(00)/吃药(01)/喝水(02)/睡觉(03)/ 清醒(04)/ 运动(05)/会议(06)/自定义(07)
                    // 新增或单条删除 提醒时
                    // 新增或单条删除 提醒分
                    // 新增或单条删除 提醒周期 二进制 01111111  分别代表(星期日~星期一)  比如只要星期三传入0x04,星期一传入0x01
                    // 新增或单条删除 提醒开光状态 0关闭 1打开
                    // 注意:同一个时间不能有重复的提醒，否则返回失败(提醒是按时间区分)
                    // 例如 增加一个 星期三 2.10分 喝水的提醒  状态为关闭
                    //"吃饭", "吃药", "喝水", "睡觉", "起床", "运动", "会议", "自定义"
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    (byte) 0x00,//新增
                                    (byte) SelectAlarmModlu,//类型：。。。"吃饭", "吃药", "喝水。。。
                                    (byte) h,//时
                                    (byte) m,//分
                                    (byte) c,//周期
                                    (byte) 0x01));//0关、1开
                }
                break;
            case REQUEST_ALARM_CLOCK_EDIT://修改旧的闹钟
                Log.e(TAG, "--新---" + h + ":" + m + "===" + c);//B18iUtils.getCycle()
                if (is18i.equals("B18i")) {
                    ReminderData newReminderData = new ReminderData(ALARM_TYPE, h, m, c, true, iconName[SelectAlarmModlu]);
                    BluetoothSDK.setReminder(resultCallBack, CHANGE_ALARMCLOCK, oldReminderData, newReminderData);
                } else {
// int len, byte operation, byte remindType, byte remindHour, byte remindMin, byte remindCycle,
// byte remindSwitchStatus, byte[] remindContent, byte remindType1, byte remindHour1, byte remindMin1, byte remindCycle1,
// byte remindSwitchStatus1
                    //"吃饭", "吃药", "喝水", "睡觉", "起床", "运动", "会议", "自定义"
                    int alarm_swe_ok = (int) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_SWE_OK");
                    int alarm_type = (int) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_TYPE");
                    int alarm_hours = (int) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_HOURS");
                    int alarm_min = (int) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_MIN");
                    int alarm_week = (int) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_WEEK");
                    String alarm_text = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "ALARM_TEXT");
                    Log.d(TAG, "修改前的数据:" + alarm_swe_ok + "\n" + alarm_type + "\n" + alarm_hours +
                            "\n" + alarm_min + "\n" + alarm_week + "\n" + alarm_text +
                            "\n修改后的数据" + "===类型：" + ALARM_TYPE +
                            "\n" + h + "时" + m + "分" + c + "周期");
                    AppsBluetoothManager.getInstance(MyApp.getContext())
                            .sendCommand(new RemindSetting(commandResultCallback,
                                    11,
                                    (byte) 0x01,//修改
                                    (byte) alarm_type,//类型：。。。"吃饭", "吃药", "喝水。。。
                                    (byte) alarm_hours,//时
                                    (byte) alarm_min,//分
                                    (byte) alarm_week,//周期
                                    (byte) alarm_swe_ok,
                                    new byte[]{}, (byte) ALARM_TYPE, (byte) h, (byte) m, (byte) c, (byte) 0x01));//0关、1开
                }
                break;
        }
        if (is18i.equals("B18i")) {
            BluetoothSDK.getReminder(resultCallBack);//获取闹钟
        } else {
            //读取所以提醒条数
            AppsBluetoothManager.getInstance(MyApp.getContext())
                    .sendCommand(new RemindCount(commandResultCallback, 1, 0));
            showLoadingDialog(getResources().getString(R.string.dlog));
        }
    }


    /**
     * 闹钟列表内部Adapter
     */
    public class MyAlarmClockAdapter extends BaseAdapter {
        private List<ReminderData> reminderDatas = null;

        public MyAlarmClockAdapter(List<ReminderData> reminderDatas) {
            this.reminderDatas = reminderDatas;
        }

        @Override
        public int getCount() {
            return reminderDatas.size();

        }

        @Override
        public Object getItem(int position) {
            return reminderDatas.get(position);
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.b18i_list_alarm_clock_item, null);
                initViews(viewHolder, convertView, position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.hour.setText(String.valueOf(reminderDatas.get(position).hour));
            viewHolder.min.setText(String.valueOf(reminderDatas.get(position).min));
            viewHolder.switchA.setChecked(reminderDatas.get(position).status);
            setCycle(reminderDatas.get(position).cycle, viewHolder);
            setType(viewHolder, reminderDatas.get(position).type);
            return convertView;
        }

        /**
         * 循环模式
         *
         * @param cycle
         * @param viewHolder
         */
        private void setCycle(int cycle, ViewHolder viewHolder) {
            switch (cycle) {
                case 1:
                    viewHolder.type.setText(getResources().getString(R.string.monday));
//                    viewHolder.type.setText("周一");
                    break;
                case 2:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday));
//                    viewHolder.type.setText("周二");
                    break;
                case 3:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday));
//                    viewHolder.type.setText("周一，周二");
                    break;
                case 4:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday));
//                    viewHolder.type.setText("周三");
                    break;
                case 5:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.wednesday));
//                    viewHolder.type.setText("周一，周三");
                    break;
                case 6:
                    viewHolder.type.setText(getResources().getString(R.string.monday));
//                    viewHolder.type.setText("周二，周三");
                    break;
                case 7:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," +
                            getResources().getString(R.string.tuesday)
                            + "," + getResources().getString(R.string.wednesday));
//                    viewHolder.type.setText("周一，周二，周三");
                    break;
                case 8:
                    viewHolder.type.setText(getResources().getString(R.string.thursday));
//                    viewHolder.type.setText("周四");
                    break;
                case 9:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.thursday));
//                    viewHolder.type.setText("周一，周四");
                    break;
                case 10:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.thursday));
//                    viewHolder.type.setText("周二，周四");
                    break;
                case 12:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday));
//                    viewHolder.type.setText("周三，周四");
                    break;
                case 15:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday) + "," +
                            getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday));
//                    viewHolder.type.setText("周一，周二，周三，周四");
                    break;
                case 16:
                    viewHolder.type.setText(getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周五");
                    break;
                case 17:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周一，周五");
                    break;
                case 18:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周二，周五");
                    break;
                case 20:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周三，周五");
                    break;
                case 24:
                    viewHolder.type.setText(getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周四，周五");
                    break;
                case 31:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday)
                            + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.friday));
//                    viewHolder.type.setText("周一，周二，周三，周四，周五");
                    break;
                case 32:
                    viewHolder.type.setText(getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周六");
                    break;
                case 33:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周一，周六");
                    break;
                case 34:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周二，周六");
                    break;
                case 36:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周三，周六");
                    break;
                case 40:
                    viewHolder.type.setText(getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周四，周六");
                    break;
                case 48:
                    viewHolder.type.setText(getResources().getString(R.string.friday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周五，周六");
                    break;
                case 63:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday)
                            + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday)
                            + "," + getResources().getString(R.string.friday) + "," + getResources().getString(R.string.saturday));
//                    viewHolder.type.setText("周一，周二，周三，周四，周五，周六");
                    break;
                case 64:
                    viewHolder.type.setText(getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周日");
                    break;
                case 65:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周日");
                    break;
                case 66:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一,周二，周日");
                    break;
                case 67:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周二，周日");
                    break;
                case 68:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周三，周日");
                    break;
                case 69:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一,周三，周日");
                    break;
                case 70:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周二，周三，周日");
                    break;
                case 71:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周二，周三，周日");
                    break;
                case 72:
                    viewHolder.type.setText(getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周四，周日");
                    break;
                case 73:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周四，周日");
                    break;
                case 74:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周二，周四，周日");
                    break;
                case 76:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周三，周四，周日");
                    break;
                case 79:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周二，周三，周四，周日");
                    break;
                case 80:
                    viewHolder.type.setText(getResources().getString(R.string.friday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周五，周日");
                    break;
                case 81:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.friday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周五，周日");
                    break;
                case 82:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.friday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周二，周五，周日");
                    break;
                case 84:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.friday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周三，周五，周日");
                    break;
                case 88:
                    viewHolder.type.setText(getResources().getString(R.string.thursday) + "," + getResources().getString(R.string.friday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周四，周五，周日");
                    break;
                case 96:
                    viewHolder.type.setText(getResources().getString(R.string.saturday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周六，周日");
                    break;
                case 97:
                    viewHolder.type.setText(getResources().getString(R.string.monday) + "," + getResources().getString(R.string.saturday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周六，周日");
                    break;
                case 98:
                    viewHolder.type.setText(getResources().getString(R.string.tuesday) + "," + getResources().getString(R.string.saturday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周二，周六，周日");
                    break;
                case 100:
                    viewHolder.type.setText(getResources().getString(R.string.wednesday) + "," + getResources().getString(R.string.saturday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周三，周六，周日");
                    break;
                case 111:
                    viewHolder.type.setText(
                            getResources().getString(R.string.monday)
                                    + "," + getResources().getString(R.string.tuesday)
                                    + "," + getResources().getString(R.string.wednesday)
                                    + "," + getResources().getString(R.string.thursday)
                                    + "," + getResources().getString(R.string.friday)
                                    + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周一，周二，周三，周四，周五，周日");
                    break;
                case 112:
                    viewHolder.type.setText(getResources().getString(R.string.friday) + "," + getResources().getString(R.string.saturday) + "," + getResources().getString(R.string.sunday));
//                    viewHolder.type.setText("周五，周六，周日");
                    break;
                case 127:
                    viewHolder.type.setText(getResources().getString(R.string.every_time));
//                    viewHolder.type.setText("每天");
                    break;
            }
        }

        public void setType(ViewHolder viewHolder, int i) {
            if (i == 0) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.eat);
            } else if (i == 1) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.medicine);
            } else if (i == 2) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.dring);
            } else if (i == 3) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.sp);
            } else if (i == 4) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.awakes);
            } else if (i == 5) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.spr);
            } else if (i == 6) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.metting);
            } else if (i == 7) {
                viewHolder.imageType.setBackgroundResource(R.mipmap.custom);
            }
        }

        private void initViews(ViewHolder viewHolder, View convertView, final int position) {
            viewHolder.hour = (TextView) convertView.findViewById(R.id.text_hour);
            viewHolder.min = (TextView) convertView.findViewById(R.id.text_min);
            viewHolder.type = (TextView) convertView.findViewById(R.id.text_type);
            viewHolder.imageType = (ImageView) convertView.findViewById(R.id.image_type);
            viewHolder.switchA = (Switch) convertView.findViewById(R.id.switch_alarm);
            viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_item);
            //闹钟开关
            viewHolder.switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e(TAG, "--开关---" + reminderDatas.get(position).hour + ":" + reminderDatas.get(position).min);
                    if (is18i.equals("B18i")) {
                        ReminderData reminder = new ReminderData(reminderDatas.get(position).type,
                                reminderDatas.get(position).hour, reminderDatas.get(position).min, reminderDatas.get(position).cycle, reminderDatas.get(position).status, reminderDatas.get(position).content);

                        ReminderData reminderData1 = new ReminderData(reminderDatas.get(position).type,
                                reminderDatas.get(position).hour, reminderDatas.get(position).min, reminderDatas.get(position).cycle, isChecked, reminderDatas.get(position).content);
                        BluetoothSDK.setReminder(resultCallBack, CHANGE_ALARMCLOCK, reminder, reminderData1);
                    }

                }
            });
            //修改闹钟
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "--修改---" + reminderDatas.get(position).hour + ":" + reminderDatas.get(position).min);
                    ALARM_TYPE = reminderDatas.get(position).type;
                    oldReminderData = new ReminderData(reminderDatas.get(position).type,
                            reminderDatas.get(position).hour, reminderDatas.get(position).min, reminderDatas.get(position).cycle, reminderDatas.get(position).status, reminderDatas.get(position).content);
//                    AlarmClock alarmClock = new AlarmClock();
//                    AlarmClock alarmClock = mAlarmClockList.get(position);
//                    Intent intent = new Intent(AlarmClockRemindActivity.this, AlarmClockEditActivity.class);
                    Intent intent = new Intent(AlarmClockRemindActivity.this, B18ITimePicker.class);
                    intent.putExtra("type", "change");
                    intent.putExtra("hour", String.valueOf(reminderDatas.get(position).hour));
                    intent.putExtra("min", String.valueOf(reminderDatas.get(position).min));
//                    intent.putExtra(WeacConstants.ALARM_CLOCK, alarmClock);
                    // 开启编辑闹钟界面
                    startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
                    // 启动移动进入效果动画
                    overridePendingTransition(R.anim.move_in_bottom, 0);
                }
            });
            //删除闹钟
            viewHolder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Log.e(TAG, "--删除---" + reminderDatas.get(position).hour + ":" + reminderDatas.get(position).min);
                    if (is18i.equals("B18i")) {
                        ReminderData reminderData = new ReminderData(reminderDatas.get(position).type,
                                reminderDatas.get(position).hour, reminderDatas.get(position).min, reminderDatas.get(position).cycle, reminderDatas.get(position).status, reminderDatas.get(position).content);
                        BluetoothSDK.setReminder(resultCallBack, DELETE_ALARMCLOCK, reminderData, reminderData);
                        BluetoothSDK.getReminder(resultCallBack);//获取闹钟
                    }
                    notifyDataSetChanged();
                    return false;
                }
            });

        }

        private class ViewHolder {
            TextView hour;
            TextView min;
            TextView type;
            ImageView imageType;
            Switch switchA;
            LinearLayout linearLayout;
        }
    }
}
