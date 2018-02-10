package com.example.bozhilun.android.h9.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @aboutContent: 自定义控件之时钟
 * @author： 安
 * @crateTime: 2017/11/10 10:21
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class TickingClockView extends View {

    private PaintFlagsDrawFilter paintFlagsDrawFilter;

    private Paint strokePaint;

    private int radius;


    private int borderSize = 15;

    private boolean stopDraw = false;


    public TickingClockView(Context context) {
        this(context, null);
    }

    public TickingClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public TickingClockView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCanvas(context);
    }

    private void initCanvas(Context c) {

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        radius = Math.min(getWidth(), getHeight()) / 2 - borderSize;
        canvas.setDrawFilter(paintFlagsDrawFilter);

//        Rect rect = new Rect(radius, radius, radius, radius);
//        strokePaint.setColor(Color.BLUE);
//        canvas.drawRect(rect, strokePaint);


        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String format = sdf.format(new Date(timeMillis));

//        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        strokePaint.setColor(Color.parseColor("#4dddff"));
        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setStrokeWidth(borderSize);
        strokePaint.setStrokeWidth(5);
        canvas.drawCircle(0, 0, radius, strokePaint);


        canvas.save();

        strokePaint.setTextSize(10);
        canvas.rotate(0);

        for (int i = 0; i < 12; i++) {
//            strokePaint.setColor(Color.BLUE);
//            strokePaint.setStrokeWidth(borderSize);
//
            int startX = (int) (Math.cos(Math.toRadians(-i * 30)) * (radius - 20));
            int startY = (int) (Math.sin(Math.toRadians(-i * 30)) * (radius - 20));
            int stopX = (int) (Math.cos(Math.toRadians(-i * 30)) * (radius - 5));
            int stopY = (int) (Math.sin(Math.toRadians(-i * 30)) * (radius - 5));

            String text = "" + (i + 1);

            float textWidth = strokePaint.measureText(text);
//            int clockHourPosX = (int) ((Math.cos(Math.toRadians(i * 30 + 270 + 30)) * (radius - 40)) - textWidth / 3);
//            int clockHourPosY = (int) ((Math.sin(Math.toRadians(i * 30 + 270 + 30)) * (radius - 40)) + textWidth / 3);
//            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            int clockHourPosX = (int) ((Math.cos(Math.toRadians(i * 30 + 270 + 30)) * (radius - 40)) - textWidth);
            int clockHourPosY = (int) ((Math.sin(Math.toRadians(i * 30 + 270 + 30)) * (radius - 40)) + textWidth);

            strokePaint.setStrokeWidth(borderSize / 2);
            strokePaint.setColor(Color.BLACK);
            strokePaint.setStyle(Paint.Style.STROKE.FILL);
            canvas.drawText(text, clockHourPosX, stopY, strokePaint);
//            for (int j = 1; j < 5; j++) {
//
//                int minStartX = (int) (Math.cos(Math.toRadians(-i * 30 - j * 6)) * (radius - 15));
//                int minStartY = (int) (Math.sin(Math.toRadians(-i * 30 - j * 6)) * (radius - 15));
//                int minStopX = (int) (Math.cos(Math.toRadians(-i * 30 - j * 6)) * (radius - 5));
//                int minStopY = (int) (Math.sin(Math.toRadians(-i * 30 - j * 6)) * (radius - 5));
//
//                canvas.drawLine(minStartX, minStartY, minStopX, minStopY, strokePaint);
//            }
        }

        String[] strFormatTimes = format.split(":");
        int secondText = Integer.parseInt(strFormatTimes[2]);
        int minuteText = Integer.parseInt(strFormatTimes[1]);
        int hourText = Integer.parseInt(strFormatTimes[0]) % 12;

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

        //秒钟
        int startX = (int) (Math.cos(Math.toRadians(-90 + 6 * secondText)) * (-50));
        int startY = (int) (Math.sin(Math.toRadians(-90 + 6 * secondText)) * (-50));
        int stopX = (int) (Math.cos(Math.toRadians(-90 + 6 * secondText)) * (radius - 50));
        int stopY = (int) (Math.sin(Math.toRadians(-90 + 6 * secondText)) * (radius - 50));
        strokePaint.setStrokeWidth(4);
        strokePaint.setColor(Color.DKGRAY);
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

        //分针
        startX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
        startY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
        stopX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));
        stopY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));
        strokePaint.setStrokeWidth(6);
        strokePaint.setColor(Color.MAGENTA);
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

        //时针
        startX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
        startY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
        stopX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));
        stopY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(10);
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

//        strokePaint.setStrokeWidth(1);
//        strokePaint.setStyle(Paint.Style.STROKE.FILL);
//        strokePaint.setTextSize(50f);
//        float measureText = strokePaint.measureText(format);
//        canvas.drawText(format, -measureText / 2, radius / 2f, strokePaint);


        strokePaint.setColor(Color.CYAN);
        strokePaint.setStrokeWidth(10);
        canvas.drawCircle(0, 0, 5, strokePaint);


        canvas.restore();

        startTicking();
    }

    private void startTicking() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                if (!stopDraw) {
                    postDelayed(this, 1000);
                }
            }
        };
        removeCallbacks(runnable);
        postDelayed(runnable, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopDraw = true;
    }

}
