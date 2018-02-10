package com.example.bozhilun.android.h9.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.R;


public class CustomProgressBar extends View {
    /**
     * 第一圈的颜色
     */
    private int mFirstColor;
    /**
     * 第二圈的颜色
     */
    private int mSecondColor;
    /**
     * 圈的宽度
     */
    private int mCircleWidth;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 当前进度
     */
    private int mProgress = 0;

    /**
     * 速度
     */
    private int mSpeed;

    /**
     * 是否应该开始下一个
     */
    private boolean isNext = false;

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    /**
     * 必要的初始化，获得一些自定义的值
     *
     * @param context
     * @param attrs
     * @param defStyle
     */

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomProgressBar_firstColor:
                    mFirstColor = a.getColor(attr, Color.GREEN);
                    break;
                case R.styleable.CustomProgressBar_secondColor:
                    mSecondColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.CustomProgressBar_circleWidth:
                    mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomProgressBar_speed:
                    mSpeed = a.getInt(attr, 20);// 默认20
                    break;
            }
        }
        a.recycle();
        mPaint = new Paint();
//        // 绘图线程
//        new Thread() {
//            public void run() {
//                while (true) {
//                    mProgress++;
//                    if (mProgress == 360) {
//                        mProgress = 0;
//                        if (!isNext)
//                            isNext = true;
//                        else
//                            isNext = false;
//                    }
//                    postInvalidate();
//                    try {
//                        Thread.sleep(mSpeed);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = centre - mCircleWidth / 2;// 半径
        mPaint.setStrokeWidth(mCircleWidth); // 设置圆环的宽度
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限

        mPaint.setColor(mFirstColor); // 设置圆环的颜色
        canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
        mPaint.setColor(mSecondColor); // 设置圆环的颜色
        canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
//        if (!isNext) {// 第一颜色的圈完整，第二颜色跑
//            mPaint.setColor(mFirstColor); // 设置圆环的颜色
//            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
//            mPaint.setColor(mSecondColor); // 设置圆环的颜色
//            canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
//        } else {
//            mPaint.setColor(mSecondColor); // 设置圆环的颜色
//            canvas.drawCircle(centre, centre, radius, mPaint); // 画出圆环
//            mPaint.setColor(mFirstColor); // 设置圆环的颜色
//            canvas.drawArc(oval, -90, mProgress, false, mPaint); // 根据进度画圆弧
//        }
}

//    float starteX = width / 2;
//    float starteY = 0;

//    boolean isLongClickModule = false;
//    boolean isLongClicking = false;
//    float xDown, yDown, xUp;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                xDown = motionEvent.getX();
//                yDown = motionEvent.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
////                if (!isLongClickModule) {
////                    isLongClickModule = isLongPressed(xDown, yDown, motionEvent.getX(),
////                            motionEvent.getY(), motionEvent.getDownTime(), motionEvent.getEventTime(), 300);
////                }
////                if (isLongClickModule && !isLongClicking) {
////                    //处理长按事件
////                    isLongClicking = true;
////                    setCirm(true);
////                    postInvalidate();
////                }
//                setCirm(true);
//                postInvalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                //获取松开时的x坐标
//                if (isLongClickModule) {
//                    isLongClickModule = false;
//                    isLongClicking = false;
//                }
//                xUp = motionEvent.getX();
//
//                //按下和松开绝对值差当大于20时滑动，否则不显示
//                if ((xUp - xDown) > 20) {
//                    //添加要处理的内容
//                } else if ((xUp - xDown) < -20) {
//                    //添加要处理的内容
//                } else if (0 == (xDown - xUp)) {
//
//                }
//                break;
//        }
//        return true;
//    }
//
//
//    /**
//     * 判断是否有长按动作发生
//     *
//     * @param lastX         按下时X坐标
//     * @param lastY         按下时Y坐标
//     * @param thisX         移动时X坐标
//     * @param thisY         移动时Y坐标
//     * @param lastDownTime  按下时间
//     * @param thisEventTime 移动时间
//     * @param longPressTime 判断长按时间的阀值
//     */
//    private boolean isLongPressed(float lastX, float lastY,
//                                  float thisX, float thisY,
//                                  long lastDownTime, long thisEventTime,
//                                  long longPressTime) {
//        float offsetX = Math.abs(thisX - lastX);
//        float offsetY = Math.abs(thisY - lastY);
//        long intervalTime = thisEventTime - lastDownTime;
//        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
//            return true;
//        }
//        return false;
//    }


    public int getValues() {
        return mProgress;
    }

    public void setmP() {
        mProgress = 0;
        postInvalidate();
    }

    public void setCirm(boolean isNexts) {
        if (isNexts) {
            mProgress++;
        } else {
            mProgress--;
        }
        ifWhat();
        postInvalidate();
    }


    public void ifWhat() {
        if (mProgress > 360) {
            mProgress = 0;
        } else if (mProgress < 0) {
            mProgress = 360;
        }
    }
}
