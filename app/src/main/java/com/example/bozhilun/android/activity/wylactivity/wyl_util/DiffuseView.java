package com.example.bozhilun.android.activity.wylactivity.wyl_util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.BloodpressureTestActivity;
import com.example.bozhilun.android.util.VerifyUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class desc:
 *
 *
 * 扩散圆圈
 */
@SuppressLint("DrawAllocation")
public class DiffuseView extends View {

    /** 扩散圆圈颜色 */
    private int mColor = getResources().getColor(R.color.colorAccents);
    /** 圆圈中心颜色 */
    private int mCoreColor = getResources().getColor(R.color.new_colorAccent);
    /** 圆圈中心图片 */
    private Bitmap mBitmap;
    /** 中心圆半径 */
    private float mCoreRadius ;



    /** 扩散圆宽度 */
    private int mDiffuseWidth = 3;
    /** 最大宽度 */
    private Integer mMaxWidth = 500;
    /** 是否正在扩散中 */
    private boolean mIsDiffuse = false;
    /** 设置字体 */
    private  String typeface;
    // 透明度集合
    private List<Integer> mAlphas = new ArrayList<Integer>();
    // 扩散圆半径集合
    private List<Integer> mWidths = new ArrayList<Integer>();
    private Paint mPaint;
    private Paint Paintb;
    private Handler aaa = new Handler();

    public DiffuseView(Context context) {
        this(context, null);
    }

    public DiffuseView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DiffuseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();



        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiffuseView, defStyleAttr, 0);
        mColor = a.getColor(R.styleable.DiffuseView_diffuse_color, mColor);
        mCoreColor = a.getColor(R.styleable.DiffuseView_diffuse_coreColor, mCoreColor);
        mCoreRadius = a.getFloat(R.styleable.DiffuseView_diffuse_coreRadius,  getmCoreRadius());
        mDiffuseWidth = a.getInt(R.styleable.DiffuseView_diffuse_width, mDiffuseWidth);
        mMaxWidth = a.getInt(R.styleable.DiffuseView_diffuse_maxWidth, mMaxWidth);
        int imageId = a.getResourceId(R.styleable.DiffuseView_diffuse_coreImage, -1);
        if(imageId != -1) mBitmap = BitmapFactory.decodeResource(getResources(), imageId);
        a.recycle();
    }

    private void init() {
        mPaint = new Paint();
        Paintb=new  Paint();
        mPaint.setAntiAlias(true);
        mAlphas.add(50);//255
        mWidths.add(0);
    }

    @Override
    public void invalidate() {
        if(hasWindowFocus()){
            super.invalidate();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus){
            invalidate();
        }
    }

    @SuppressLint("DrawAllocation") @Override
    public void onDraw(Canvas canvas) {
        // 绘制扩散圆
        mPaint.setColor(mColor);
        for (int i = 0; i < mAlphas.size(); i++) {
            // 设置透明度
            Integer alpha = mAlphas.get(i);
            mPaint.setAlpha(alpha);
            // 绘制扩散圆
            Integer width = mWidths.get(i);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2,  getmCoreRadius() + width, mPaint);

            if(alpha > 0 && width < mMaxWidth){
                mAlphas.set(i, alpha - 1);
                mWidths.set(i, width + 1);
            }
        }
        // 判断当扩散圆扩散到指定宽度时添加新扩散圆
        if (mWidths.get(mWidths.size() - 1) == mMaxWidth / mDiffuseWidth) {
            mAlphas.add(50);//255
            mWidths.add(0);
        }
        // 超过10个扩散圆，删除最外层
        if(mWidths.size() >= 10){
            mWidths.remove(0);
            mAlphas.remove(0);
        }

        // 绘制中心圆及图片
        mPaint.setAlpha(500);//外圆的透明度
        mPaint.setColor(mCoreColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getmCoreRadius(), mPaint);

        if(mBitmap != null){
            canvas.drawBitmap(mBitmap, getWidth() / 2 - mBitmap.getWidth() / 2
                    , getHeight() / 2 - mBitmap.getHeight() / 2, mPaint);
        }

        if(mIsDiffuse){
            invalidate();
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);


        //写文字
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Align.CENTER);
        if(getResources().getString(R.string.star).equals(typeface)){
            paint.setTextSize(80); //以px为单位
            canvas.drawText(getResources().getString(R.string.star),getWidth() / 2+5, getHeight() / 2+20 ,paint);
        }else{
            //英文模式下字体缩小一半
            boolean  is= VerifyUtil.isZh(MyApp.getApplication());
            if(!is){
                paint.setTextSize(30); //以px为单位
            }else{
                paint.setTextSize(40); //以px为单位
            }
            canvas.drawText(getResources().getString(R.string.long_stop),getWidth() /2, getHeight() / 2+20 ,paint);
        }


        Paintb.setTextSize(40);
        Paintb.setColor(Color.BLUE);
        Paintb.setTextAlign(Align.CENTER);
    }

    /**
     * 开始扩散
     */
    public void start() {
        mIsDiffuse = true;
        invalidate();


    }

    /**
     * 停止扩散
     */
    public void stop() {
        mIsDiffuse = false;
        aaa.removeCallbacksAndMessages(null);
    }

    /**
     * 是否扩散中
     */
    public boolean isDiffuse(){
        return mIsDiffuse;
    }

    /**
     * 设置扩散圆颜色
     */
    public void setColor(int colorId){
        mColor = colorId;
    }

    /**
     * 设置中心圆颜色
     */
    public void setCoreColor(int colorId){
        mCoreColor = colorId;
    }


    /**
     * 设置中心圆图片
     */
    public void setCoreImage(int imageId){
        mBitmap = BitmapFactory.decodeResource(getResources(), imageId);
    }



    public float getmCoreRadius() {
        return mCoreRadius;
    }
    /**
     * 设置中心圆半径
     */
    public void setmCoreRadius(float mCoreRadius) {
        this.mCoreRadius = mCoreRadius;
    }
    /**
     * 设置扩散圆宽度(值越小宽度越大)
     */
    public void setDiffuseWidth(int width){
        mDiffuseWidth = width;
    }

    /**
     * 设置最大宽度
     */
    public void setMaxWidth(int maxWidth){
        mMaxWidth = maxWidth;
    }

    /**
     * 设置字体
     */

    public void Typeface(String  mytypeface){
        typeface = mytypeface;
    }
}
