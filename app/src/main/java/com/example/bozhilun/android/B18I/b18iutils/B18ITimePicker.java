package com.example.bozhilun.android.B18I.b18iutils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bozhilun.android.B18I.b18isupport.Mode;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.AlarmTestBean;
import com.sdk.bluetooth.bean.RemindData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent: 设置闹钟界面
 * @author： 安
 * @crateTime: 2017/9/7 16:38
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class B18ITimePicker extends AppCompatActivity {

    @BindView(R.id.timer_set)
    TimePicker timerSet;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.checkbox_day)
    CheckBox checkboxDay;
    @BindView(R.id.checkbox_one)
    CheckBox checkboxOne;
    @BindView(R.id.checkbox_two)
    CheckBox checkboxTwo;
    @BindView(R.id.checkbox_three)
    CheckBox checkboxThree;
    @BindView(R.id.checkbox_four)
    CheckBox checkboxFour;
    @BindView(R.id.checkbox_five)
    CheckBox checkboxFive;
    @BindView(R.id.checkbox_six)
    CheckBox checkboxSix;
    private int H, M;
    private boolean TYPE = true;
    private int NUMBER = 0;


    private RemindData remindData;
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b18i_time_picker_layout);
        ButterKnife.bind(this);
        timerSet.setIs24HourView(true);//是否显示24小时制？默认false
        Intent intent = getIntent();
        setTitles(intent);
        setCheckBoxClick();
    }

    private void setCheckBoxClick() {
        timerSet.setOnTimeChangedListener(new ChangeLister());
        checkboxDay.setOnCheckedChangeListener(new CheckLister());
        checkboxOne.setOnCheckedChangeListener(new CheckLister());
        checkboxTwo.setOnCheckedChangeListener(new CheckLister());
        checkboxThree.setOnCheckedChangeListener(new CheckLister());
        checkboxFour.setOnCheckedChangeListener(new CheckLister());
        checkboxFive.setOnCheckedChangeListener(new CheckLister());
        checkboxSix.setOnCheckedChangeListener(new CheckLister());
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerSet.setIs24HourView(true);//是否显示24小时制？默认false
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("------", NUMBER + "");
    }

    /**
     * 判断设置title
     *
     * @param intent
     */
    private void setTitles(Intent intent) {
        String type = intent.getStringExtra("type");
        if (type.equals("new")) {
            TYPE = true;
            barTitles.setText(getResources().getString(R.string.new_alarm_clock));
//            if (H < 0 || M < 0) {
//                H = 00;
//                M = 00;
//            }
//            //设置显示时间为
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                timerSet.setHour(H);
//                timerSet.setMinute(M);
//            } else {
//                timerSet.setCurrentHour(H);
//                timerSet.setCurrentMinute(M);
//            }
        } else {
            TYPE = false;
            barTitles.setText(getResources().getString(R.string.edit_alarm_clock));
            remindData = (RemindData) intent.getExtras().getSerializable("remindData");
            initData();
//            String hour = intent.getStringExtra("hour");
//            String min = intent.getStringExtra("min");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                timerSet.setHour(Integer.valueOf(hour));
//                timerSet.setMinute(Integer.valueOf(min));
//            } else {
//                timerSet.setCurrentHour(Integer.valueOf(hour));
//                timerSet.setCurrentMinute(Integer.valueOf(min));
//            }
        }
    }

    private void initData() {
        if (remindData != null) {
            //时间
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timerSet.setHour(remindData.remind_time_hours);
                timerSet.setMinute(remindData.remind_time_minutes);
            } else {
                timerSet.setCurrentMinute(remindData.remind_time_minutes);
                timerSet.setCurrentHour(remindData.remind_time_hours);
            }
            //周期
            int week = Integer.parseInt(B18iUtils.toD(remindData.remind_week, 2));
            if ((week & weekArray[0]) == 1) {   //周日
                NUMBER += 1;
                checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxDay.setChecked(true);
            }
            if ((week & weekArray[1]) == 2) { //周一
                NUMBER += 2;
                checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxOne.setChecked(true);
            }
            if ((week & weekArray[2]) == 4) { //周二
                NUMBER += 4;
                checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxTwo.setChecked(true);
            }
            if ((week & weekArray[3]) == 8) {  //周三
                NUMBER += 8;
                checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxThree.setChecked(true);
            }
            if ((week & weekArray[4]) == 16) {  //周四
                NUMBER += 16;
                checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxFour.setChecked(true);
            }
            if ((week & weekArray[5]) == 32) {  //周五
                NUMBER += 32;
                checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxFive.setChecked(true);
            }
            if ((week & weekArray[6]) == 64) {  //周六
                NUMBER += 64;
                checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
                checkboxSix.setChecked(true);
            }

        }

    }

    private void getTime() {
        List<String> times3 = B18iUtils.getTimes3();
        H = Integer.valueOf(times3.get(3));
        M = Integer.valueOf(times3.get(4));
        Log.e("------H---M---", H + "===" + M);
    }


    @OnClick({R.id.image_back, R.id.image_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_right:
                if (NUMBER <= 0) {
                    NUMBER = 127;
                    Log.e("B18ITimePicker", "未选择周期默认设置全周");
                }
                Intent intent = new Intent();
                setHN(intent);
                if (TYPE) {
                    setResult(1, intent);
                } else {
                    setResult(2, intent);
                }
                finish();
                break;
            case R.id.image_back:
                NUMBER = 0;
                finish();
                break;
        }
    }


    /**
     * 传值
     *
     * @param intent
     */
    private void setHN(Intent intent) {
        if (H <= 0 && M <= 0) {
//            getTime();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                H = timerSet.getHour();
                M = timerSet.getMinute();
            } else {
                H = timerSet.getCurrentHour();
                M = timerSet.getCurrentMinute();
            }
        }
        if (H >= 0) {
            intent.putExtra("h", H);
        }
        if (M >= 0) {
            intent.putExtra("m", M);
        }
        intent.putExtra("c", NUMBER);
    }

    private class ChangeLister implements TimePicker.OnTimeChangedListener {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            Log.e("--------tttt", hourOfDay + "===" + minute +
                    "=========" + view.getCurrentHour() + "=====" + view.getCurrentMinute() + "=====" + view.getBaseline());
            H = hourOfDay;
            M = minute;
        }
    }


    private class CheckLister implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.e("---------", buttonView.getId() + "====" + isChecked);
            switch (buttonView.getId()) {
                case R.id.checkbox_day:
                    if (isChecked) {
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_select);
                        NUMBER += 1;
                    } else {
                        NUMBER -= 1;
                        checkboxDay.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_one:
                    if (isChecked) {
                        NUMBER += 2;
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 2;
                        checkboxOne.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_two:
                    if (isChecked) {
                        NUMBER += 4;
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 4;
                        checkboxTwo.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_three:
                    if (isChecked) {
                        NUMBER += 8;
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 8;
                        checkboxThree.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_four:
                    if (isChecked) {
                        NUMBER += 16;
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 16;
                        checkboxFour.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_five:
                    if (isChecked) {
                        NUMBER += 32;
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_select);
                    } else {
                        NUMBER -= 32;
                        checkboxFive.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
                case R.id.checkbox_six:
                    if (isChecked) {
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_select);
                        NUMBER += 64;
                    } else {
                        NUMBER -= 64;
                        checkboxSix.setBackgroundResource(R.drawable.b18i_text_unselect);
                    }
                    break;
            }
        }
    }
}
