package com.example.bozhilun.android.B18I.b18iview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.B18I.b18iutils.Constant;
import com.example.bozhilun.android.B18I.b18iutils.MiscUtil;
import com.example.bozhilun.android.BuildConfig;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.coverflow.BitmapUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 圆形进度条，类似 QQ 健康中运动步数的 UI 控件
 */

public class CircleProgress extends View {

    private static final String TAG = CircleProgress.class.getSimpleName();
    private Context mContext;

    //默认大小
    private int mDefaultSize;
    //是否开启抗锯齿
    private boolean antiAlias;
    //绘制提示
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //绘制单位
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //绘制数值
    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //绘制圆弧
    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;
    //渐变的颜色是360度，如果只显示270，那么则会缺失部分颜色
    private SweepGradient mSweepGradient;
    private int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
    //当前进度，[0.0f,1.0f]
    private float mPercent;
    //动画时间
    private long mAnimTime;
    //属性动画
    private ValueAnimator mAnimator;

    //绘制背景圆弧
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private float mBgArcWidth;

    private Paint mPoints;
    private Paint mTextp;
    private Paint myPoints;

    //圆心坐标，半径
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(mContext, Constant.DEFAULT_SIZE);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
        setValue(mValue);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

//        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias, Constant.ANTI_ALIAS);
//
//        mHint = typedArray.getString(R.styleable.CircleProgressBar_hint);
//        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor, Color.BLACK);
//        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize, Constant.DEFAULT_HINT_SIZE);
//
//        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, Constant.DEFAULT_VALUE);
//        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, Constant.DEFAULT_MAX_VALUE);
//        //内容数值精度格式
//        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0);
//        mPrecisionFormat = MiscUtil.getPrecisionFormat(mPrecision);
//        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK);
////        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, Constant.DEFAULT_VALUE_SIZE);
//        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, MyApp.getContext().getResources().getDimension(R.dimen.x66));
//
//        mUnit = typedArray.getString(R.styleable.CircleProgressBar_unit);
//        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK);
//        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, Constant.DEFAULT_UNIT_SIZE);
//
//        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, Constant.DEFAULT_ARC_WIDTH);
//        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, Constant.DEFAULT_START_ANGLE);
//        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, Constant.DEFAULT_SWEEP_ANGLE);
//
//        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.parseColor("#70FFFFFF"));//"#43FFFFFF"));
//        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, Constant.DEFAULT_ARC_WIDTH);
//        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.33f);
//
//        //mPercent = typedArray.getFloat(R.styleable.CircleProgressBar_percent, 0);
//        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, Constant.DEFAULT_ANIM_TIME);

        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias, Constant.ANTI_ALIAS);

        mHint = typedArray.getString(R.styleable.CircleProgressBar_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize, MyApp.getContext().getResources().getDimension(R.dimen.x15));

        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, Constant.DEFAULT_VALUE);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, Constant.DEFAULT_MAX_VALUE);
        //内容数值精度格式
        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0);
        mPrecisionFormat = MiscUtil.getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK);
//        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, Constant.DEFAULT_VALUE_SIZE);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, MyApp.getContext().getResources().getDimension(R.dimen.x30));

        mUnit = typedArray.getString(R.styleable.CircleProgressBar_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, MyApp.getContext().getResources().getDimension(R.dimen.x30));

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, MyApp.getContext().getResources().getDimension(R.dimen.x6));
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, Constant.DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, Constant.DEFAULT_SWEEP_ANGLE);

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.parseColor("#70FFFFFF"));//"#43FFFFFF"));
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, MyApp.getContext().getResources().getDimension(R.dimen.x6));
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.33f);

        //mPercent = typedArray.getFloat(R.styleable.CircleProgressBar_percent, 0);
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, Constant.DEFAULT_ANIM_TIME);

        int gradientArcColors = typedArray.getResourceId(R.styleable.CircleProgressBar_arcColors, 0);
        if (gradientArcColors != 0) {
            try {
                int[] gradientColors = getResources().getIntArray(gradientArcColors);
                if (gradientColors.length == 0) {//如果渐变色为数组为0，则尝试以单色读取色值
                    int color = getResources().getColor(gradientArcColors);
                    mGradientColors = new int[2];
                    mGradientColors[0] = color;
                    mGradientColors[1] = color;
                } else if (gradientColors.length == 1) {//如果渐变数组只有一种颜色，默认设为两种相同颜色
                    mGradientColors = new int[2];
                    mGradientColors[0] = gradientColors[0];
                    mGradientColors[1] = gradientColors[0];
                } else {
                    mGradientColors = gradientColors;
                }
            } catch (Resources.NotFoundException e) {
                throw new Resources.NotFoundException("the give resource not found.");
            }
        }

        typedArray.recycle();
    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        // 设置抗锯齿,会消耗较大资源，绘制图形速度会变慢。
        mHintPaint.setAntiAlias(antiAlias);
        // 设置绘制文字大小
        mHintPaint.setTextSize(mHintSize);
        // 设置画笔颜色
        mHintPaint.setColor(mHintColor);
        // 从中间向两边绘制，不需要再次计算文字
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Paint.Align.CENTER);
        // 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);


        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        mArcPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔粗细
        mArcPaint.setStrokeWidth(mArcWidth);
        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
        // Cap.ROUND,或方形样式 Cap.SQUARE
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);


        mPoints = new Paint();
        mPoints.setAntiAlias(antiAlias);
//        mPoints.setColor(Color.parseColor("#FFFFFF"));
        mPoints.setStyle(Paint.Style.STROKE);
//        mPoints.setStrokeWidth(mBgArcWidth);
        mPoints.setStrokeCap(Paint.Cap.ROUND);

        myPoints = new Paint();
        myPoints.setAntiAlias(antiAlias);
        myPoints.setStyle(Paint.Style.STROKE);
        myPoints.setStrokeCap(Paint.Cap.ROUND);


        mTextp = new Paint();
        mTextp.setColor(Color.WHITE);
        mTextp.setTextSize(MyApp.getContext().getResources().getDimension(R.dimen.x15));
        mTextp.setStyle(Paint.Style.FILL);
        mTextp.setTextAlign(Paint.Align.CENTER);
//        mTextp.setStrokeWidth(1);
        mTextp.setAntiAlias(true);
        mTextp.setDither(true);


        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, mDefaultSize),
                MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w = " + w + "; h = " + h + "; oldw = " + oldw + "; oldh = " + oldh);
        //求圆弧和背景圆弧的最大宽度
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //求最小值作为实际值
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);
//        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - (int) maxArcWidth,
//                h - getPaddingTop() - getPaddingBottom() - (int) maxArcWidth);
        //减去圆弧的宽度，否则会造成部分圆弧绘制在外围
        mRadius = minSize / 2;
        //获取圆的相关参数
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;

        //绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;

        //计算文字绘制时的 baseline
        //由于文字的baseline、descent、ascent等属性只与textSize和typeface有关，所以此时可以直接计算
        //若value、hint、unit由同一个画笔绘制或者需要动态设置文字的大小，则需要在每次更新后再次计算
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);
        updateArcPaint();
        Log.d(TAG, "onSizeChanged: 控件大小 = " + "(" + w + ", " + h + ")"
                + "圆心坐标 = " + mCenterPoint.toString()
                + ";圆半径 = " + mRadius
                + ";圆的外接矩形 = " + mRectF.toString());
    }

    private float getBaselineOffsetFromY(Paint paint) {
        return MiscUtil.measureTextHeight(paint) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
        drawNewPoint(canvas);
    }

    private void drawNewPoint(Canvas canvas) {
        float currentAngle = mSweepAngle * mPercent;
        if (currentAngle <= 0) {
            return;
        }
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
//        Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.dayuan_image)).getBitmap();
//        从资源文件中生成位图bitmap
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.dayuan_image);
//        for (int i = 0; i <= 10; i++) {
//            int x = (int) (mCenterPoint.x + (mRadius + (bitmap.getWidth() / 4)) * Math.cos(i * 36 * 3.14 / 180));
//            int y = (int) (mCenterPoint.y + (mRadius + (bitmap.getWidth() / 4)) * Math.sin(i * 36 * 3.14 / 180));
//            canvas.drawBitmap(bitmap, (int) x, (int) y, myPoints);
//            Log.d("----------hhhhhh---", bitmap.getHeight() + "==" + bitmap.getWidth() + "=="
//                    + x + "===" + y + "----"
//                    + (int) x + "====" + (int) y
//                    + "===" + Math.sin(i * 36 * 3.14 / 180) +
//                    "===" + Math.cos(i * 36 * 3.14 / 180));
//        }
        double x1 = (mCenterPoint.x + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x2)) * Math.cos(currentAngle * Math.PI / 180));
        double y1 = (mCenterPoint.y + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x2)) * Math.sin(currentAngle * Math.PI / 180));
        myPoints.setColor(Color.parseColor("#85ffffff"));
        myPoints.setStrokeWidth(MyApp.getContext().getResources().getDimension(R.dimen.x15));
        canvas.drawPoint((float) x1, (float) y1, myPoints);

        double x = (mCenterPoint.x + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x2)) * Math.cos(currentAngle * Math.PI / 180));
        double y = (mCenterPoint.y + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x2)) * Math.sin(currentAngle * Math.PI / 180));
        myPoints.setColor(Color.parseColor("#FFFFFF"));
        myPoints.setStrokeWidth(MyApp.getContext().getResources().getDimension(R.dimen.x10));
        canvas.drawPoint((float) x, (float) y, myPoints);
    }

    /**
     * 绘制内容文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 计算文字宽度，由于Paint已设置为居中绘制，故此处不需要重新计算
        // float textWidth = mValuePaint.measureText(mValue.toString());
        // float x = mCenterPoint.x - textWidth / 2;
//        canvas.drawText(String.format(mPrecisionFormat, aaa), mCenterPoint.x, mValueOffset, mValuePaint);
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);
        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }

        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save();
        float currentAngle = mSweepAngle * mPercent;
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        canvas.drawArc(mRectF, currentAngle, mSweepAngle - currentAngle + 2, false, mBgArcPaint);
//        canvas.drawPoint(mStartAngle,mStartAngle,mPoints);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.rous_back, new BitmapFactory.Options());
//        canvas.drawBitmap(bitmap,mRectF.height()/2, mRectF.height()/2,mBgArcPaint);
//        canvas.drawPoint(mRectF.height()/2+mBgArcWidth/2,mBgArcWidth/2,mPoints);


        /**
         *
         圆点坐标：(x0,y0)
         半径：r
         角度：a0
         则圆上任一点为：（x1,y1）
         double x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
         double y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
         */

        for (int i = 0; i <= 10; i++) {
            double x = (mCenterPoint.x + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x3)) * Math.cos(i * 36 * Math.PI / 180));
            double y = (mCenterPoint.y + (mRadius + MyApp.getContext().getResources().getDimension(R.dimen.x3)) * Math.sin(i * 36 * Math.PI / 180));
            mPoints.setColor(Color.parseColor("#FFFFFF"));
            mPoints.setStrokeWidth(mBgArcWidth);
//            Log.e(TAG, x + "===" + y);
            canvas.drawPoint((float) x, (float) y, mPoints);
        }

//        canvas.drawPoint(mRectF.height() / 2 + mBgArcWidth / 2, mRectF.height() + mBgArcWidth / 2, mPoints);
//        canvas.drawPoint(mRectF.height() / 2 + mBgArcWidth / 2, mBgArcWidth / 2, mPoints);
//
//        canvas.drawPoint(mRectF.height() + mBgArcWidth / 2, mRectF.height() / 2 + mBgArcWidth / 2, mPoints);
//        canvas.drawPoint(mBgArcWidth / 2, mRectF.height() / 2 + mBgArcWidth / 2, mPoints);


        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        canvas.drawArc(mRectF, 2, currentAngle, false, mArcPaint);


//        Log.e(TAG, "-----------------------" + mRectF.height() / 2 + mBgArcWidth / 2 + "===" + mRectF.height() / 2 + mBgArcWidth / 2);
        canvas.rotate(90, mRectF.height() / 2 + mBgArcWidth / 2, mRectF.height() / 2 + mBgArcWidth / 2);
//        canvas.drawText("今日步数:"+mMaxValue,mRectF.height()/2+mBgArcWidth/2,(mRectF.height()/2+mBgArcWidth/2)+55,mTextp);
//        canvas.drawText("目标",mRectF.height()/2+mBgArcWidth/2,(mRectF.height()/2+mBgArcWidth/2)-55,mTextp);
        canvas.drawText(getResources().getString(R.string.goal_step) + ":" + mMaxValue, mRectF.height() / 2 + mBgArcWidth / 2,
                (mRectF.height() / 2 + mBgArcWidth / 2) + mValueSize + 17,
                mTextp);

        canvas.drawText(getResources().getString(R.string.today_step), mRectF.height() / 2 + mBgArcWidth / 2,
                (mRectF.height() / 2 + mBgArcWidth / 2) - mValueSize - 15,
                mTextp);
        canvas.restore();
    }


    /**
     * 已知圆点坐标和半径，得到圆上的点
     *
     * @param mCenterPoint 圆心坐标
     * @param mRadius      圆半径
     */
    private List initPointsCircular(Point mCenterPoint, float mRadius) {
        List<Point> points = new LinkedList<Point>();
        for (int i = 0; i < 360; i += 1) {
            int x = (int) (mCenterPoint.x - mRadius * Math.sin(Math.PI * (i - 90) / 180));
            int y = (int) (mCenterPoint.y - mRadius - 10
                    + mRadius * Math.cos(Math.PI * (i - 90) / 180));
            points.add(new Point(x, y));
        }
        return points;
    }

    /**
     * 更新圆弧画笔
     */
    private void updateArcPaint() {
        // 设置渐变
//        int[] mGradientColors = {Color.GREEN, Color.YELLOW, Color.RED};
        int[] mGradientColors = {Color.WHITE, Color.WHITE, Color.WHITE};
        mSweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColors, null);
        mArcPaint.setShader(mSweepGradient);
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    /**
     * 设置当前值
     *
     * @param value
     */
    public void setValue(float value) {
//        if (value > mMaxValue) {
//            value = mMaxValue;
//        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ";currentAngle = " + (mSweepAngle * mPercent)
                            + ";value = " + mValue);
                }
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 获取最大值
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * 设置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    /**
     * 获取精度
     *
     * @return
     */
    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        mPrecision = precision;
        mPrecisionFormat = MiscUtil.getPrecisionFormat(precision);
    }

    public int[] getGradientColors() {
        return mGradientColors;
    }

    /**
     * 设置渐变
     *
     * @param gradientColors
     */
    public void setGradientColors(int[] gradientColors) {
        mGradientColors = gradientColors;
        updateArcPaint();
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    /**
     * 重置
     */
    public void reset() {
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //释放资源
    }
}
