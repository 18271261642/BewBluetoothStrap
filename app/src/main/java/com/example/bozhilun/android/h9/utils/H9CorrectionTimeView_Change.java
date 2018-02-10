package com.example.bozhilun.android.h9.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/11/10 10:54
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9CorrectionTimeView_Change extends View {

    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    private Paint strokePaint;
    private boolean stopDraw = false;
    private Paint paint;
    private int circleWidth;
    private int roundBackgroundColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private float progress = 0;
    private int[] colors = {0xffff4639, 0xffCDD513, 0xff3CDF5F};
    private int radius;
    private RectF oval;
    private Paint mPaintText;
    private int maxColorNumber = 100;
    private float singlPoint = 9;
    private float lineWidth = 0.3f;
    private int circleCenter;
    private SweepGradient sweepGradient;
    private boolean isLine;
//    boolean isSet = true;

    int isAddDatas = 2;//0归零1校震2自动

    int isHourOrMin = 0;//0时针1分针


    public void setIsAddDatas(int isAddDatas) {
        this.isAddDatas = isAddDatas;
    }

    public void setIsHourOrMin(int isHourOrMin) {
        this.isHourOrMin = isHourOrMin;
    }

    public H9CorrectionTimeView_Change(Context context) {
        this(context, null);
    }

    public H9CorrectionTimeView_Change(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public H9CorrectionTimeView_Change(Context context, AttributeSet attrs,
                                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.H9CorrectionTimeView);
        maxColorNumber = mTypedArray.getInt(R.styleable.H9CorrectionTimeView_circleNumber, 40);
        circleWidth = mTypedArray.getDimensionPixelOffset(R.styleable.H9CorrectionTimeView_circleWidths, getDpValue(180));
        roundBackgroundColor = mTypedArray.getColor(R.styleable.H9CorrectionTimeView_roundColor, 0xffdddddd);
        textColor = mTypedArray.getColor(R.styleable.H9CorrectionTimeView_circleTextColor, 0xff999999);
        roundWidth = mTypedArray.getDimension(R.styleable.H9CorrectionTimeView_circleRoundWidth, 40);
        textSize = mTypedArray.getDimension(R.styleable.H9CorrectionTimeView_circleTextSize, getDpValue(8));
        colors[0] = mTypedArray.getColor(R.styleable.H9CorrectionTimeView_circleColor1, 0xffff4639);
        colors[1] = mTypedArray.getColor(R.styleable.H9CorrectionTimeView_circleColor2, 0xffcdd513);
        colors[2] = mTypedArray.getColor(R.styleable.H9CorrectionTimeView_circleColor3, 0xff3cdf5f);
        initView();
        mTypedArray.recycle();
    }

    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext().getResources().getDisplayMetrics());
    }

    private void initView() {

        paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        circleCenter = circleWidth / 2;//半径
        singlPoint = (float) 360 / (float) maxColorNumber;
        radius = (int) (circleCenter - roundWidth / 2); // 圆环的半径
        sweepGradientInit();
        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(roundBackgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);

        // 用于定义的圆弧的形状和大小的界限
        oval = new RectF(circleCenter - radius, circleCenter - radius, circleCenter + radius, circleCenter + radius);
    }

    /**
     * 渐变初始化
     */
    public void sweepGradientInit() {
        //渐变颜色
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String format = redTimeNow();

        canvas.setDrawFilter(paintFlagsDrawFilter);

        paint.setColor(Color.parseColor("#4dddff"));
        canvas.drawArc(oval, -90, (float) 360, false, paint);
        //背景渐变颜色
        paint.setShader(sweepGradient);
//        canvas.drawArc(oval, -90, (float) (progress * 3.6), false, paint);
        canvas.drawArc(oval, -90, (float) progress, false, paint);
        paint.setShader(null);

        //是否是线条模式
        if (!isLine) {
            float start = -90f;
            float p = ((float) maxColorNumber / (float) 360);
            p = (int) (360 * p);
            for (int i = 0; i < p; i++) {
                paint.setColor(roundBackgroundColor);
                canvas.drawArc(oval, start + singlPoint - lineWidth, lineWidth, false, paint); // 绘制间隔快
                start = (start + singlPoint);
            }
        }
        //绘制剩下的空白区域
//        paint.setColor(roundBackgroundColor);
//        canvas.drawArc(oval, -90, (float) (-(100 - progress) * 3.6), false, paint);
//        paint.setColor(Color.RED);
//        canvas.drawArc(oval, -90, (float) progress, false, paint);
        //绘制文字刻度
        for (int i = 1; i <= 12; i++) {
            canvas.save();// 保存当前画布
            canvas.rotate(360 / 12 * i, circleCenter, circleCenter);
            canvas.drawText(i + "", circleCenter, circleCenter - radius + roundWidth / 2 + getDpValue(4) + textSize, mPaintText);
            canvas.restore();//
        }

//        //起始圆圈
//        paint.setColor(Color.parseColor("#4dddff"));
//        canvas.drawArc(oval, -90, (float) 360, false, paint);
//        //背景渐变颜色
//        paint.setShader(sweepGradient);
//        canvas.drawArc(oval, -90, (float) progress, false, paint);
//        paint.setShader(null);
//        //是否是线条模式
//        if (!isLine) {
//            float start = -90f;
//            float p = ((float) maxColorNumber / (float) 360);
//            p = (int) (360 * p);
//            for (int i = 0; i < p; i++) {
//                paint.setColor(roundBackgroundColor);
//                canvas.drawArc(oval, start + singlPoint - lineWidth, lineWidth, false, paint); // 绘制间隔快
//                start = (start + singlPoint);
//            }
//        }
//        //绘制文字刻度
//        for (int i = 1; i <= 12; i++) {
//            canvas.save();
//            canvas.rotate(360 / 12 * i, circleCenter, circleCenter);
//            canvas.drawText(i + "", circleCenter, circleCenter - radius + roundWidth / 2 + getDpValue(4) + textSize, mPaintText);
//            canvas.restore();//
//        }
//        if (isSet) {

        setWatchTimes(canvas, format);

//        setTimes(canvas, format);

//        }
    }

    private String redTimeNow() {
        long timeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String format = sdf.format(new Date(timeMillis));
        return format;
    }

//    public void setSet(boolean set) {
//        isSet = set;
//    }

    private void setTimes(Canvas canvas, String format) {
        canvas.save();
        canvas.translate((oval.width() + roundWidth + 0.5f) / 2, (oval.height() + roundWidth + 0.5f) / 2);
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
        strokePaint.setColor(Color.parseColor("#4dddff"));
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

        //分针
        startX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
        startY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
        stopX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));
        stopY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));

        strokePaint.setStrokeWidth(6);
        strokePaint.setColor(getResources().getColor(R.color.colorPrimaryDarks));
        strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

        strokePaint.setColor(getResources().getColor(R.color.colorPrimaryDarks));
        strokePaint.setStrokeWidth(10);
        canvas.drawCircle(0, 0, 8, strokePaint);

        strokePaint.setColor(getResources().getColor(R.color.dark_red));
        strokePaint.setStrokeWidth(10);
        canvas.drawCircle(0, 0, 4, strokePaint);

        //时针
        startX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
        startY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
        stopX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));
        stopY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));

        strokePaint.setColor(getResources().getColor(R.color.dark_red));
        strokePaint.setStrokeWidth(10);
        canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

        //绘制文本时间
        strokePaint.setStrokeWidth(1);
        strokePaint.setStyle(Paint.Style.STROKE.FILL);
        strokePaint.setTextSize(50f);
        float measureText = strokePaint.measureText(format);
        canvas.drawText(format, -measureText / 2, radius / 2f, strokePaint);

        canvas.restore();
        startTicking();
    }


    private void setWatchTimes(Canvas canvas, String format) {

        canvas.save();
        canvas.translate((oval.width() + roundWidth + 0.5f) / 2, (oval.height() + roundWidth + 0.5f) / 2);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);

        if (isAddDatas == 0) {
//            //秒钟
//            int startX = (int) (Math.cos(Math.toRadians(-90)) * (-50));
//            int startY = (int) (Math.sin(Math.toRadians(-90)) * (-50));
//            int stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 50));
//            int stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 50));
//
//            strokePaint.setStrokeWidth(4);
//            strokePaint.setColor(Color.parseColor("#4dddff"));
//            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            //分针
            int startX = (int) (Math.cos(Math.toRadians(-90)) * (-30));
            int startY = (int) (Math.sin(Math.toRadians(-90)) * (-30));
            int stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 100));
            int stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 100));

            strokePaint.setStrokeWidth(6);
            strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
            strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
            strokePaint.setStrokeWidth(10);
            canvas.drawCircle(0, 0, 8, strokePaint);

            strokePaint.setColor(getResources().getColor(R.color.dark_red));
            strokePaint.setStrokeWidth(10);
            canvas.drawCircle(0, 0, 4, strokePaint);

            //时针
            startX = (int) (Math.cos(Math.toRadians(-90)));
            startY = (int) (Math.sin(Math.toRadians(-90)));
            stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 150));
            stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 150));

            strokePaint.setColor(getResources().getColor(R.color.dark_red));
            strokePaint.setStrokeWidth(10);
            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);
        } else if (isAddDatas == 1) {
//            //秒钟
//            int startX = (int) (Math.cos(Math.toRadians(-90)) * (-50));
//            int startY = (int) (Math.sin(Math.toRadians(-90)) * (-50));
//            int stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 50));
//            int stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 50));
//
//            strokePaint.setStrokeWidth(4);
//            strokePaint.setColor(Color.parseColor("#4dddff"));
//            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            if (isHourOrMin == 0) {
                //分针
                int startX = (int) (Math.cos(Math.toRadians(-90)) * (-30));
                int startY = (int) (Math.sin(Math.toRadians(-90)) * (-30));
                int stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 100));
                int stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 100));

                strokePaint.setStrokeWidth(6);
                strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
                strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

                strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
                strokePaint.setStrokeWidth(10);
                canvas.drawCircle(0, 0, 8, strokePaint);

                strokePaint.setColor(getResources().getColor(R.color.dark_red));
                strokePaint.setStrokeWidth(10);
                canvas.drawCircle(0, 0, 4, strokePaint);

                //时针
                startX = (int) (Math.cos(Math.toRadians(progress - 90)));
                startY = (int) (Math.sin(Math.toRadians(progress - 90)));
                stopX = (int) (Math.cos(Math.toRadians(progress - 90)) * (radius - 150));
                stopY = (int) (Math.sin(Math.toRadians(progress - 90)) * (radius - 150));

                strokePaint.setColor(getResources().getColor(R.color.dark_red));
                strokePaint.setStrokeWidth(10);
                canvas.drawLine(startX, startY, stopX, stopY, strokePaint);
            } else if (isHourOrMin == 1) {
                //分针
                int startX = (int) (Math.cos(Math.toRadians(progress - 90)) * (-30));
                int startY = (int) (Math.sin(Math.toRadians(progress - 90)) * (-30));
                int stopX = (int) (Math.cos(Math.toRadians(progress - 90)) * (radius - 100));
                int stopY = (int) (Math.sin(Math.toRadians(progress - 90)) * (radius - 100));

                strokePaint.setStrokeWidth(6);
                strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
                strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

                strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
                strokePaint.setStrokeWidth(10);
                canvas.drawCircle(0, 0, 8, strokePaint);

                strokePaint.setColor(getResources().getColor(R.color.dark_red));
                strokePaint.setStrokeWidth(10);
                canvas.drawCircle(0, 0, 4, strokePaint);

                //时针
                startX = (int) (Math.cos(Math.toRadians(-90)));
                startY = (int) (Math.sin(Math.toRadians(-90)));
                stopX = (int) (Math.cos(Math.toRadians(-90)) * (radius - 150));
                stopY = (int) (Math.sin(Math.toRadians(-90)) * (radius - 150));

                strokePaint.setColor(getResources().getColor(R.color.dark_red));
                strokePaint.setStrokeWidth(10);
                canvas.drawLine(startX, startY, stopX, stopY, strokePaint);
            }
        } else if (isAddDatas == 2) {
            String[] strFormatTimes = format.split(":");
            int secondText = Integer.parseInt(strFormatTimes[2]);
            int minuteText = Integer.parseInt(strFormatTimes[1]);
            int hourText = Integer.parseInt(strFormatTimes[0]) % 12;

            //秒钟
            int startX = (int) (Math.cos(Math.toRadians(-90 + 6 * secondText)) * (-50));
            int startY = (int) (Math.sin(Math.toRadians(-90 + 6 * secondText)) * (-50));
            int stopX = (int) (Math.cos(Math.toRadians(-90 + 6 * secondText)) * (radius - 50));
            int stopY = (int) (Math.sin(Math.toRadians(-90 + 6 * secondText)) * (radius - 50));

            strokePaint.setStrokeWidth(4);
            strokePaint.setColor(getResources().getColor(R.color.sec_time_color));
            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            //分针
            startX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
            startY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (-30));
            stopX = (int) (Math.cos(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));
            stopY = (int) (Math.sin(Math.toRadians(-90 + minuteText * 6 + 6 * secondText / 60f)) * (radius - 100));

            strokePaint.setStrokeWidth(6);
            strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
            strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);

            strokePaint.setColor(getResources().getColor(R.color.blue_tog_btn_pressed));
            strokePaint.setStrokeWidth(10);
            canvas.drawCircle(0, 0, 8, strokePaint);

            strokePaint.setColor(getResources().getColor(R.color.dark_red));
            strokePaint.setStrokeWidth(10);
            canvas.drawCircle(0, 0, 4, strokePaint);

            //时针
            startX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
            startY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)));
            stopX = (int) (Math.cos(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));
            stopY = (int) (Math.sin(Math.toRadians(-90 + hourText * 30 + 30 * minuteText / 60f + 30 * secondText / 3600f)) * (radius - 150));

            strokePaint.setColor(getResources().getColor(R.color.dark_red));
            strokePaint.setStrokeWidth(10);
            canvas.drawLine(startX, startY, stopX, stopY, strokePaint);
        }


//        //绘制文本时间
//        strokePaint.setStrokeWidth(1);
//        strokePaint.setStyle(Paint.Style.STROKE.FILL);
//        strokePaint.setTextSize(50f);
//        float measureText = strokePaint.measureText(format);
//        canvas.drawText(format, -measureText / 2, radius / 2f, strokePaint);

        canvas.restore();
        if (isAddDatas == 2) {
            startTicking();
        }
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

    /**
     * 分割的数量（刻度数量）
     *
     * @param maxColorNumber 数量
     */
    public void setMaxColorNumber(int maxColorNumber) {
        this.maxColorNumber = maxColorNumber;
        singlPoint = (float) 360 / (float) maxColorNumber;
        invalidate();
    }

    /**
     * 是否是线条（刻度线条是否显示）
     *
     * @param line true 是 false否
     */
    public void setLine(boolean line) {
        isLine = line;
        invalidate();
    }

    public int getCircleWidth() {
        return circleWidth;
    }

    /**
     * 空白出颜色背景
     *
     * @param roundBackgroundColor
     */
    public void setRoundBackgroundColor(int roundBackgroundColor) {
        this.roundBackgroundColor = roundBackgroundColor;
        paint.setColor(roundBackgroundColor);
        invalidate();
    }

    /**
     * 刻度字体颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaintText.setColor(textColor);
        invalidate();
    }

    /**
     * 刻度字体大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mPaintText.setTextSize(textSize);
        invalidate();
    }

    /**
     * 渐变颜色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("colors length < 2");
        }
        this.colors = colors;
        sweepGradientInit();
        invalidate();
    }


    /**
     * 间隔角度大小
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }


    /**
     * 圆环宽度设置
     *
     * @param roundWidth 宽度
     */
    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
        if (roundWidth > circleCenter) {
            this.roundWidth = circleCenter;
        }
        radius = (int) (circleCenter - this.roundWidth / 2); // 圆环的半径
        oval.left = circleCenter - radius;
        oval.right = circleCenter + radius;
        oval.bottom = circleCenter + radius;
        oval.top = circleCenter - radius;
        paint.setStrokeWidth(this.roundWidth);
        invalidate();
    }

    /**
     * 圆环的直径
     *
     * @param circleWidth 直径
     */
    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        circleCenter = circleWidth / 2;

        if (roundWidth > circleCenter) {
            roundWidth = circleCenter;
        }
        setRoundWidth(roundWidth);
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }


    OnProgressScore onProgressScore;

    public interface OnProgressScore {
        void setProgressScore(float score);

    }


    public synchronized void setProgress(final float p) {
        progress = p;
        postInvalidate();
    }

    /**
     * 进度设置
     *
     * @param p
     */
    public synchronized void setProgress(final float p, OnProgressScore onProgressScore) {
        this.onProgressScore = onProgressScore;
        progress = p;
        postInvalidate();
    }

}
