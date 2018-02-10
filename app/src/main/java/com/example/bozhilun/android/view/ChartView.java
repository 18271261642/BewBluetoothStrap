package com.example.bozhilun.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.util.DensityUtils;
import com.sina.weibo.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 17/4/30.
 * 滚动的折线图
 */
public class ChartView extends View {

    private List mPointList = new ArrayList();
    private int mPointY = 0;
    private Paint mPoint = new Paint();   //画笔

    public ChartView(Context context, AttributeSet attrs) {
        // TODO Auto-generated constructor stub
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        //初始化画笔
        mPoint.setColor(Color.parseColor("#ffffffff"));
        mPoint.setStrokeWidth(2.0f);
        mPoint.setAntiAlias(true);
        //设置为圆的角
        mPoint.setStrokeJoin(Paint.Join.ROUND);
        mPoint.setStrokeCap(Paint.Cap.ROUND);
        mPoint.setDither(true);

    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        //mPointY = (int) (Math.random() * 100);

        if (mPointList.size() >= 2) {
            for (int k = 0; k < -1 + mPointList.size(); k++) {
                paramCanvas.drawLine(((Point) mPointList.get(k)).x,
                        ((Point) mPointList.get(k)).y,
                        ((Point) mPointList.get(k + 1)).x,
                        ((Point) mPointList.get(k + 1)).y, mPoint);
            }
        }
//        Point localPoint1 = new Point(getWidth(), mPointY);
        Point localPoint1 = new Point(0, mPointY);//从左边起绘制
        int i = mPointList.size();
        int j = 0;
        if (i > 501) {                    //最多绘制100个点，多余的出栈
            mPointList.remove(0);
            while (j < 500) {
                Point localPoint3 = (Point) mPointList.get(j);
//                localPoint3.x = (-50 + localPoint3.x);
                localPoint3.x = (DensityUtils.dip2px(getContext(), 1) + localPoint3.x);
                LogUtil.e("zry", localPoint3.x + "");
                j++;
            }
            mPointList.add(localPoint1);
            return;
        }

        while (j < mPointList.size()) {
            Point localPoint2 = (Point) mPointList.get(j);
//            localPoint2.x = (-20 + localPoint2.x);//每新产生使前面的每一个点左移20
            localPoint2.x = (DensityUtils.dip2px(getContext(), 1) + localPoint2.x);//每新产生使前面的每一个点右移20
            LogUtil.e("zry", localPoint2.x + "");
            j++;
        }
        mPointList.add(localPoint1);
    }

    public final void ClearList() {
        mPointList.clear();
    }

    public final void AddPointToList(int paramInt) {
        Log.e("zry", "paramInt:" + paramInt);
        mPointY = paramInt;
        invalidate();//重绘
    }

    public void stop(){
        mPointList.clear();
        invalidate();
    }
}