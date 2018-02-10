package com.aigestudio.wheelpicker.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import com.aigestudio.wheelpicker.R;
import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;

/**
 * Created by thinkpad on 2016/9/1.
 */

public class ProfessionPick extends PopupWindow implements WheelPicker.OnItemSelectedListener, View.OnClickListener {

    public Button cancelBtn;
    public Button confirmBtn;

    public View pickerContainerV;
    public View contentView;//root view

    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private WheelPicker main_wheel_profession;

    private ArrayList<String> provinceList;
    private String profession;

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        profession = (String) data;
        main_wheel_profession.setData(provinceList);
    }

    public static class Builder {
        //Required
        private Context context;
        private OnProCityPickedListener listener;

        public Builder(Context context, OnProCityPickedListener listener) {
            this.context = context;
            this.listener = listener;
        }

        //Option
        private ArrayList<String> provinceList;
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String dateChose = "";
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 25;

        public Builder setProvinceList(ArrayList<String> provinceList) {
            this.provinceList = provinceList;
            return this;
        }

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder dateChose(String dateChose) {
            this.dateChose = dateChose;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         *
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public ProfessionPick build() {
            return new ProfessionPick(this);
        }
    }

    public ProfessionPick(Builder builder) {
        this.provinceList = builder.provinceList;
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        setSelectedProCity(builder.dateChose);
        initView();
    }

    private OnProCityPickedListener mListener;

    private void initView() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_profession_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        main_wheel_profession = (WheelPicker) contentView.findViewById(R.id.main_wheel_profession);
        main_wheel_profession.setOnItemSelectedListener(this);

        main_wheel_profession.setData(provinceList);
        if (!TextUtils.isEmpty(profession)) {
            String proResult;
            for (int i = 0; i < provinceList.size(); i++) {
                proResult = provinceList.get(i);
                if (profession.equals(proResult)) {
                    main_wheel_profession.setSelectedItemPosition(i);
                }
            }
        } else {
            profession = provinceList.get(0);
        }
        cancelBtn.setText(textCancel);
        confirmBtn.setText(textConfirm);
        cancelBtn.setTextColor(colorCancel);
        cancelBtn.setTextSize(btnTextsize);
        confirmBtn.setTextColor(colorConfirm);
        confirmBtn.setTextSize(btnTextsize);
        //main_wheel_province.setItemTextSize(viewTextSize);
        //main_wheel_city.setItemTextSize(viewTextSize);
        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        contentView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = pickerContainerV.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismissPopWin();
                    }
                }
                return true;
            }
        });
    }


    /**
     * set selected date position value when initView.
     *
     * @param profession
     */
    public void setSelectedProCity(String profession) {
        if (!TextUtils.isEmpty(profession)) {
            this.profession = profession;
        }
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {
        if (null != activity) {
            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {
            if (null != mListener) {
                mListener.onProCityPickCompleted(profession);
            }
            dismissPopWin();
        }
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     *
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {
        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnProCityPickedListener {

        /**
         * Listener when date has been checked
         *
         * @param profession
         */
        void onProCityPickCompleted(String profession);
    }
}

