package com.example.bozhilun.android.siswatch.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import com.example.bozhilun.android.R;

/**
 * Created by Administrator on 2017/7/19.
 */

public class TimePickerView extends Dialog implements View.OnClickListener{

    private TimePicker timePickerView;
    private Button cancleBtn,sureBtn;

    private int hour = 0;
    private int mine = 0;

    private OnTimePickerViewClickListener timepListener;

    public void setTimepListener(OnTimePickerViewClickListener timepListener) {
        this.timepListener = timepListener;
    }

    public TimePickerView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.time_picker_view);

        initViews();

        initData();

    }

    private void initData() {

    }

    private void initViews() {
        timePickerView = (TimePicker) findViewById(R.id.timePickerView);
        cancleBtn = (Button) findViewById(R.id.timepickerCancleBtn);
        sureBtn = (Button) findViewById(R.id.timepickerSureBtn);
        cancleBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        timePickerView.setIs24HourView(true);
        timePickerView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = timePicker.getCurrentHour();
                mine = timePicker.getCurrentMinute();
                Log.e("","------时间------"+hour+"----"+mine+"----"+timePicker.getHour());
                String date = hour+":"+mine;
                if(timepListener != null){
                    timepListener.doSure(date);

                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.timepickerCancleBtn:
                dismiss();
                break;
            case R.id.timepickerSureBtn:

                dismiss();
                break;
        }
    }

    public interface OnTimePickerViewClickListener{
      //  public void doCancle();
        public void doSure(String dateTime);
    }
}

