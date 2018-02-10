package com.example.bozhilun.android.h9.settingactivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.h9.utils.H9CorrectionTimeView_Change;
import com.example.bozhilun.android.h9.utils.H9TimeUtil;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.device.DateTime;
import com.sdk.bluetooth.protocol.command.expands.CorrectionTime;
import com.sdk.bluetooth.protocol.command.expands.FinishCorroctionTime;
import com.sdk.bluetooth.protocol.command.expands.ManualTurnPointers;
import com.sdk.bluetooth.protocol.command.expands.Point2Zero;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 矫正时间
 * @author： 安
 * @crateTime: 2017/10/11 17:38
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class CorrectionTimeActivity extends WatchBaseActivity {
    private final String TAG = "----->>>" + this.getClass();
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.btn_ishour)
    Button btnIshour;//时针
    @BindView(R.id.btn_ismin)
    Button btnIsmin;//分针
    @BindView(R.id.btn_reduce)
    Button btnReduce;//减
    @BindView(R.id.btn_plus)
    Button btnPlus;//加
    //    @BindView(R.id.btn_reduce)
//    LongClickButton btnReduce;//减
//    @BindView(R.id.btn_plus)
//    LongClickButton btnPlus;//加
    @BindView(R.id.btn_complete)
    Button btnComplete;//完成
    //    @BindView(R.id.custom_prog)
//    CustomProgressBar customProg;//大角度调节进度条
    @BindView(R.id.textGuide)
    TextView textGuide;
    //    @BindView(R.id.h9_correction_view)
//    H9CorrectionTimeView h9CorrectionView;
    @BindView(R.id.h9_correction_view)
    H9CorrectionTimeView_Change h9CorrectionView;
    private boolean OnClick = false;
    private boolean LongOnClick = true;
    private boolean isOnclick = true;
    int number = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.correction_time_layout);
        ButterKnife.bind(this);
        barTitles.setText(getResources().getString(R.string.jiaozhen));

        int width = getWindowManager().getDefaultDisplay().getWidth();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) h9CorrectionView.getLayoutParams();
        layoutParams.width = width - width / 4;
        layoutParams.height = width - width / 4;
        h9CorrectionView.setLayoutParams(layoutParams);
        h9CorrectionView.setCircleWidth(width - width / 4);
        h9CorrectionView.setRoundWidth(30);
        h9CorrectionView.setMaxColorNumber(60);
        h9CorrectionView.setLine(false);


        btnReduce.setOnTouchListener(new mOnTouchListenter());
        btnPlus.setOnTouchListener(new mOnTouchListenter());
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    h9CorrectionView.setProgress(number);
                    break;
                case 0x02:
                    senM();
                    handler.removeMessages(0x01);
                    handler.removeMessages(0x02);
                    handler.sendEmptyMessageDelayed(0x03, 300);
                    number = 0;
                    h9CorrectionView.setProgress(number);
                    break;
                case 0x03:
                    LongOnClick = true;
                    handler.removeMessages(0x03);
                    break;
                default:
                    break;
            }
        }
    };


    public void dajiaosu(boolean waht, int number) {
        if (waht) {
            if (isSlecteHour) {
                correctionTime = new CorrectionTime(commandResultCallback, 1, number, 0, 0);
            } else {
                correctionTime = new CorrectionTime(commandResultCallback, 0, 0, 1, number);
            }
        } else {
            if (isSlecteHour) {
                correctionTime = new CorrectionTime(commandResultCallback, 0, number, 0, 0);
            } else {
                correctionTime = new CorrectionTime(commandResultCallback, 0, 0, 0, number);
            }
        }
        Log.d("--------", "长按松开" + "===" + waht + "===" + isSlecteHour);
    }

    public void xiaojiaosu(boolean waht) {
        if (waht) {
            if (isSlecteHour) {
                correctionTime = new CorrectionTime(commandResultCallback, 1, 2, 0, 0);
            } else {
                correctionTime = new CorrectionTime(commandResultCallback, 0, 0, 1, 2);
            }
        } else {
            if (isSlecteHour) {
                correctionTime = new CorrectionTime(commandResultCallback, 0, 2, 0, 0);
            } else {
                correctionTime = new CorrectionTime(commandResultCallback, 0, 0, 0, 2);
            }
        }
    }

    public void senM() {
        Log.d(TAG, "-----" + isOnclick);
        if (isOnclick) {
            isOnclick = false;
            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommand(correctionTime);
        }
    }


    private CorrectionTime correctionTime;
    private boolean isSlecteHour = true;

    private byte MINUTE_POINTER = (byte) 0xED; // 分针
    private byte HOUR_POINTER = (byte) 0xEE;   // 时针
    private byte ANTICLOCKWISE = 0x00;         // 逆时针 anticlockwise
    private byte CLOCKWISE = 0x01;             // 顺时针 clockwise
    private ManualTurnPointers manual;


    @OnClick({R.id.image_back, R.id.return_zero,
            R.id.btn_ishour, R.id.btn_ismin,
            //R.id.btn_reduce, R.id.btn_plus,
            R.id.btn_complete,
            R.id.textGuide})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.return_zero:
                showLoadingDialog(getResources().getString(R.string.dlog));
                AppsBluetoothManager.getInstance(MyApp.getContext())
                        .sendCommand(new Point2Zero(commandResultCallback, (byte) 0x01));
                break;
            case R.id.btn_ishour:
                isSlecteHour = true;
                btnIsmin.setBackground(getResources().getDrawable(R.drawable.my_btn_selector));
                btnIsmin.setTextColor(Color.parseColor("#333333"));
                btnIshour.setBackground(getResources().getDrawable(R.drawable.my_btn_selector_two));
                btnIshour.setTextColor(Color.WHITE);
                h9CorrectionView.setIsHourOrMin(0);//时针
                break;
            case R.id.btn_ismin:
                isSlecteHour = false;
                btnIsmin.setBackground(getResources().getDrawable(R.drawable.my_btn_selector_two));
                btnIsmin.setTextColor(Color.WHITE);
                btnIshour.setBackground(getResources().getDrawable(R.drawable.my_btn_selector));
                btnIshour.setTextColor(Color.parseColor("#333333"));
                h9CorrectionView.setIsHourOrMin(1);//分针
                break;
//            case R.id.btn_reduce:
//                if (LongOnClick) {
//                    Log.e("----------", "我老测试下");
//                    LongOnClick = true;
//                    xiaojiaosu(false);
//                    senM();
//                }
//                break;
//            case R.id.btn_plus:
//                if (LongOnClick) {
//                    Log.e("----+------", "我老测试下");
//                    LongOnClick = true;
//                    xiaojiaosu(true);
//                    senM();
//                }
//                break;
            case R.id.btn_complete:
                showLoadingDialog(getResources().getString(R.string.dlog));
                setTimes();
                break;
            case R.id.textGuide://校时指南
                startActivity(H9GuideActivity.class);
                break;
        }
    }

    /**
     * 完成按钮点击
     */
    private void setTimes() {

        //获取系统时间并设置于手环
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String secon = String.valueOf(calendar.get(Calendar.SECOND));
        if (Integer.valueOf(month) <= 9) {
            month = "0" + month;
        } else {
            month = "" + month;
        }

        if (Integer.valueOf(day) <= 9) {
            day = "0" + day;
        } else {
            day = "" + day;
        }

        if (Integer.valueOf(hour) <= 9) {
            hour = "0" + hour;
        } else {
            hour = "" + hour;
        }

        if (Integer.valueOf(minute) <= 9) {
            minute = "0" + minute;
        } else {
            minute = "" + minute;
        }

        if (Integer.valueOf(secon) <= 9) {
            secon = "0" + secon;
        } else {
            secon = "" + secon;
        }
//        Log.e("==============", year + "-" + month + "-" + day + "-" + hour + "-" + minute + "-" + secon);
        String s = Integer.toHexString(year);
//        Log.e(TAG, s + "\n" + (byte) (int) Integer.valueOf(month)
//                + "\n" + (byte) (int) Integer.valueOf(day)
//                + "\n" + (byte) (int) Integer.valueOf(hour)
//                + "\n" + (byte) (int) Integer.valueOf(minute)
//                + "\n" + (byte) (int) Integer.valueOf(secon)
//                + "---------" + Integer.toHexString((int) Integer.valueOf(month))
//                + "=\n=" + Integer.toHexString((int) Integer.valueOf(day))
//                + "=\n=" + Integer.toHexString((int) Integer.valueOf(hour))
//                + "=\n=" + Integer.toHexString((int) Integer.valueOf(minute))
//                + "=\n=" + Integer.toHexString((int) Integer.valueOf(secon)));
        byte[] bytes = H9TimeUtil.string2bytes(s);
//        Log.e(TAG,"-----"+Arrays.toString(bytes));
        AppsBluetoothManager.getInstance(MyApp.getContext())
                .sendCommand(new FinishCorroctionTime(commandResultCallback,
                        bytes,
                        (byte) (int) Integer.valueOf(month),
                        (byte) (int) Integer.valueOf(day),
                        (byte) (int) Integer.valueOf(hour),
                        (byte) (int) Integer.valueOf(minute),
                        (byte) (int) Integer.valueOf(secon)));

//        AppsBluetoothManager.getInstance(MyApp.getContext())
//                .sendCommand(new DateTime(commandResultCallback,
//                        7,
//                        Integer.valueOf(year),
//                        Integer.valueOf(month),
//                        Integer.valueOf(day),
//                        Integer.valueOf(hour),
//                        Integer.valueOf(minute),
//                        Integer.valueOf(secon)));//手机时间改变设置设备时间
    }




    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof CorrectionTime) {
//                Log.e(TAG, "角度设置ok");
                isOnclick = true;
                closeLoadingDialog();
            } else if (baseCommand instanceof Point2Zero) {
//                Log.e(TAG, "归零成功");
                isOnclick = true;
                h9CorrectionView.setIsAddDatas(0);//归零
                closeLoadingDialog();
            } else if (baseCommand instanceof DateTime) {
//                Log.e(TAG, "自动");
                closeLoadingDialog();
                isOnclick = true;
                //自动
                h9CorrectionView.setIsAddDatas(2);
            } else if (baseCommand instanceof FinishCorroctionTime) {
//                Log.d(TAG, "-----校针完成-----FinishCorroctionTime");
                closeLoadingDialog();
                isOnclick = true;
                //自动
                h9CorrectionView.setIsAddDatas(2);
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            closeLoadingDialog();
        }
    };

    private class mOnTouchListenter implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.btn_reduce://jian
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Log.d("---------", "按下了");
                        setDown();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        OnClick = false;
//                        Log.d("---------", "抬起来了发送--2--" + number);
                        dajiaosu(false, number);
                        handler.sendEmptyMessage(0x02);
                        setViewZero();
                    }
                    break;
                case R.id.btn_plus:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        Log.e("----+-----", "按下了");
                        setDown();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        OnClick = false;
                        dajiaosu(true, number);
                        handler.sendEmptyMessage(0x02);
                        setViewZero();
                    }
                    break;
            }
            return false;
        }
    }

    private void setViewZero() {
        h9CorrectionView.setIsAddDatas(0);//自动归零
        h9CorrectionView.invalidate();
    }


    private void setDown() {
        OnClick = true;
        h9CorrectionView.setIsAddDatas(1);//校针
        Thread t = new Thread() {
            public void run() {
                while (OnClick) {
                    number++;
                    if (number > 5) {
                        LongOnClick = false;
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    Log.e("---------", "进来了发送---1--" + number);
                    handler.sendEmptyMessage(0x01); //新开启的线程中不能修改UI界面，通过handler与UI线程交互
                }
            }
        };
        t.start();
    }
}
