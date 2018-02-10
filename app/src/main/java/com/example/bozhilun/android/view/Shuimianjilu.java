package com.example.bozhilun.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.util.MyLogUtil;
import com.example.bozhilun.android.util.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/25.
 * 自定义睡眠数据
 */

public class Shuimianjilu extends View {
    private List mBarLists;
    public void setBarLists(List barLists){
        mBarLists = barLists;
        postInvalidate();
    }

    private Paint myPaint,myPaint2;

    public  void intyy(Canvas canvas){
        myPaint = new Paint();
        myPaint2 = new Paint();
        //绘制条形图
        myPaint.setColor(Color.parseColor("#511D82")); //设置画笔颜色
        myPaint.setStyle(Paint.Style.FILL); //设置填充
        myPaint2.setColor(Color.parseColor("#6924A9")); //设置画笔颜色
        myPaint2.setStyle(Paint.Style.FILL); //设置填充
        canvas.drawRect(new Rect(20, 20,40*150, 200), myPaint2);// 画一个矩形,前两个参数是矩形左上角坐标，后两个参数是右下角坐标
        canvas.drawRect(new Rect((30*150)/3,20,60,200), myPaint);//第二个矩形
        try {
            if(null!= mBarLists){
                JSONObject AAA=new JSONObject(mBarLists.toString());
                MyLogUtil.i("-sumBeanAAA->" + AAA);
                SharedPreferencesUtils.readObject(MyApp.getApplication(),"shuimianjilu");
                canvas.drawRect(new Rect(20, 20,40*150, 200), myPaint2);// 画一个矩形,前两个参数是矩形左上角坐标，后两个参数是右下角坐标
                canvas.drawRect(new Rect((30*150)/3,20,60,200), myPaint);//第二个矩形

            }

        }catch (Exception e){e.printStackTrace();}



    }
    public Shuimianjilu(Context context, AttributeSet attr) {
        super(context,attr);
    }





    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        intyy(canvas);
        super.onDraw(canvas);

    }
}