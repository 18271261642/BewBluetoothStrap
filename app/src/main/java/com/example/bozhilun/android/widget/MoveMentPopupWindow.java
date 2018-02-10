package com.example.bozhilun.android.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.bozhilun.android.R;

/**
 * Created by thinkpad on 2017/3/7.
 */

public class MoveMentPopupWindow extends PopupWindow {

    private final static long DURATION_SHORT = 400;
    private boolean isOpen = false;

    public MoveMentPopupWindow(Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.activity_movement, null);
        final ImageView shineiyundong_iv = (ImageView) mMenuView.findViewById(R.id.shineiyundong_iv);
        final TextView indoor_sports_tv = (TextView) mMenuView.findViewById(R.id.indoor_sports_tv);

        final ImageView outdoor_running_iv = (ImageView) mMenuView.findViewById(R.id.outdoor_running_iv);
        final TextView outdoor_running_tv = (TextView) mMenuView.findViewById(R.id.outdoor_running_tv);

        final ImageView outdoor_cycling_iv = (ImageView) mMenuView.findViewById(R.id.outdoor_cycling_iv);
        final TextView outdoor_cycling_tv = (TextView) mMenuView.findViewById(R.id.outdoor_cycling_tv);
        ImageView close_iv = (ImageView) mMenuView.findViewById(R.id.close_iv);
        show(shineiyundong_iv, 1, 300);
        show(indoor_sports_tv, 1, 300);
        show(outdoor_running_iv, 2, 300);
        show(outdoor_running_tv, 2, 300);
        show(outdoor_cycling_iv, 3, 300);
        show(outdoor_cycling_tv, 3, 300);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(shineiyundong_iv);
                hide(indoor_sports_tv);
                hide(outdoor_running_iv);
                hide(outdoor_running_tv);
                hide(outdoor_cycling_iv);
                hide(outdoor_cycling_tv);
                dismiss();
            }
        });
      /*  BoomMenuButton boomMenuButton = (BoomMenuButton) mMenuView.findViewById(R.id.boom_btn);
        boomMenuButton.setButtonEnum(ButtonEnum.TextOutsideCircle);
        boomMenuButton.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        boomMenuButton.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);
        TextOutsideCircleButton.Builder builder_one = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.shineiyundong)
                .normalTextRes(R.string.indoor_sports)
                .pieceColor(Color.YELLOW);
        TextOutsideCircleButton.Builder builder_two = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.huwaipaobu)
                .normalTextRes(R.string.outdoor_running)
                .pieceColor(Color.YELLOW);
        TextOutsideCircleButton.Builder builder_three = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.mipmap.huwaiqiche)
                .normalTextRes(R.string.outdoor_cycling)
                .pieceColor(Color.YELLOW);
        boomMenuButton.addBuilder(builder_one);
        boomMenuButton.addBuilder(builder_two);
        boomMenuButton.addBuilder(builder_three);*/
        //boomMenuButton.boom();
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.bottom_enter_style);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //this.getBackground().setAlpha(0);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private final void hide(final View child) {
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .start();
    }

    private final void show(final View child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                break;
            case 2:
                angleDeg += 45.f;
                break;
            case 3:
                angleDeg += 90.f;
                break;
            case 4:
                angleDeg += 135.f;
                break;
            case 5:
                angleDeg += 180.f;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                break;
        }
        final float angleRad = (float) (angleDeg * Math.PI / 180.f);
        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(x)
                .translationY(y)
                .start();
    }
}
