package com.example.bozhilun.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.bozhilun.android.R;

/**
 * Created by thinkpad on 2017/3/7.
 */

public class SelectRunningModePopupWindow extends PopupWindow {

    public SelectRunningModePopupWindow(Context context, View.OnClickListener onClickListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View mMenuView = inflater.inflate(R.layout.item_hint_popupwindow, null);
        ImageView huwaipao_img = (ImageView) mMenuView.findViewById(R.id.huwaipao_img);
        ImageView huwaiqixing_img = (ImageView) mMenuView.findViewById(R.id.huwaiqixing_img);
        huwaipao_img.setOnClickListener(onClickListener);
        huwaiqixing_img.setOnClickListener(onClickListener);
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
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.bottom_enter_style);
        // 实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        //this.setBackgroundDrawable(dw);
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

}
