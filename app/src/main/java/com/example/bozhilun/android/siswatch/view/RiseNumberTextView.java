package com.example.bozhilun.android.siswatch.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/10/28.
 */

@SuppressLint("AppCompatCustomView")
public class RiseNumberTextView extends TextView implements IRaiseNumber{

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private int mPlayingState = STOPPED;
    private float toNumber;
    private float fromNumber;
    private long duration = 1500;
    private int numberType = 2;
    private DecimalFormat fnum = new DecimalFormat("##0.00");
    private EndListener mEndListener = null;

    public RiseNumberTextView(Context context) {
        this(context, null);
    }

    public RiseNumberTextView(Context context, AttributeSet attr) {
        super(context, attr);
//        setTextColor(context.getResources().getColor(R.color.newwatchdatatvcolor));
//        setTextSize(30);
    }

    public RiseNumberTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    public boolean isRunning() {
        //返回当前运行状态
        return (mPlayingState == RUNNING);
    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, toNumber);
        valueAnimator.setDuration(duration);
        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setText(fnum.format(Float.parseFloat(valueAnimator
                                .getAnimatedValue().toString())));
                        if (valueAnimator.getAnimatedFraction() >= 1) {
                            //大于等于1时认为动画运行结束
                            mPlayingState = STOPPED;
                            if (mEndListener != null)
                                mEndListener.onEndFinish();
                        }
                    }

                });
        valueAnimator.start();
    }

    private void runInt() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber,
                (int) toNumber);
        valueAnimator.setDuration(duration);
        valueAnimator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setText(valueAnimator.getAnimatedValue().toString());
                        if (valueAnimator.getAnimatedFraction() >= 1) {
                            //置标志位
                            mPlayingState = STOPPED;
                            if (mEndListener != null)
                                mEndListener.onEndFinish();
                        }
                    }
                });
        valueAnimator.start();
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1)
                runInt();
            else
                runFloat();
        }
    }

    @Override
    public void setFloat(float fromNum, float toNum) {
        toNumber = toNum;
        numberType = 2;
        fromNumber = fromNum;
    }

    @Override
    public void setInteger(int fromNum, int toNum) {
        toNumber = toNum;
        numberType = 1;
        fromNumber = fromNum;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void setOnEndListener(EndListener callback) {
        mEndListener = callback;
    }

    public interface EndListener {
        void onEndFinish();
    }
}
