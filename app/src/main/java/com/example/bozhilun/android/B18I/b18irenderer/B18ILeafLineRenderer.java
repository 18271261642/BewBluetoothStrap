package com.example.bozhilun.android.B18I.b18irenderer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.view.View;

import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.ChartData;
import com.example.bozhilun.android.B18I.b18ibean.Line;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18isupport.LeafUtil;

import java.util.List;

/**
 * 描述：
 * </br>
 */

public class B18ILeafLineRenderer extends B18IAbsRenderer {
    private static final float LINE_SMOOTHNESS = 0.16f;

    /**
     * 填充画笔
     **/
    private Paint fillPaint;

    private PathMeasure measure;

    /**
     * 动画结束标志
     */
    private boolean isAnimateEnd;

    /**
     * 是否开始绘制，防止动画绘制之前绘制一次
     */
    private boolean isShow;

    private float phase;

    private LinearGradient fillShader;

    public B18ILeafLineRenderer(Context context, View view) {
        super(context, view);
    }

    @Override
    protected void initPaint() {
        super.initPaint();
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 画折线
     *
     * @param canvas
     */
    public void drawLines(Canvas canvas, Line line) {
        if (line != null && isShow) {
            linePaint.setColor(line.getLineColor());
            linePaint.setStrokeWidth(LeafUtil.dp2px(mContext, line.getLineWidth()));
            linePaint.setStyle(Paint.Style.STROKE);
            List<PointValue> values = line.getValues();
            Path path = line.getPath();
            int size = values.size();
            for (int i = 0; i < size; i++) {
                PointValue point = values.get(i);
                if (i == 0) path.moveTo(point.getOriginX(), point.getOriginY());
                else path.lineTo(point.getOriginX(), point.getOriginY());
            }

            measure = new PathMeasure(path, false);
            linePaint.setPathEffect(createPathEffect(measure.getLength(), phase, 0.0f));
            canvas.drawPath(path, linePaint);

        }
    }


    /**
     * 画曲线
     *
     * @param canvas
     */
    public void drawCubicPath(Canvas canvas, Line line) {
        if (line != null && isShow) {
            linePaint.setColor(line.getLineColor());
            linePaint.setStrokeWidth(LeafUtil.dp2px(mContext, line.getLineWidth()));
            linePaint.setStyle(Paint.Style.STROKE);
            Path path = line.getPath();

            float prePreviousPointX = Float.NaN;
            float prePreviousPointY = Float.NaN;
            float previousPointX = Float.NaN;
            float previousPointY = Float.NaN;
            float currentPointX = Float.NaN;
            float currentPointY = Float.NaN;
            float nextPointX = Float.NaN;
            float nextPointY = Float.NaN;

            List<PointValue> values = line.getValues();
            final int lineSize = values.size();
            for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
                if (Float.isNaN(currentPointX)) {
                    PointValue linePoint = values.get(valueIndex);
                    currentPointX = linePoint.getOriginX();
                    currentPointY = linePoint.getOriginY();
                }
                if (Float.isNaN(previousPointX)) {
                    if (valueIndex > 0) {
                        PointValue linePoint = values.get(valueIndex - 1);
                        previousPointX = linePoint.getOriginX();
                        previousPointY = linePoint.getOriginY();
                    } else {
                        previousPointX = currentPointX;
                        previousPointY = currentPointY;
                    }
                }

                if (Float.isNaN(prePreviousPointX)) {
                    if (valueIndex > 1) {
                        PointValue linePoint = values.get(valueIndex - 2);
                        prePreviousPointX = linePoint.getOriginX();
                        prePreviousPointY = linePoint.getOriginY();
                    } else {
                        prePreviousPointX = previousPointX;
                        prePreviousPointY = previousPointY;
                    }
                }

                // nextPoint is always new one or it is equal currentPoint.
                if (valueIndex < lineSize - 1) {
                    PointValue linePoint = values.get(valueIndex + 1);
                    nextPointX = linePoint.getOriginX();
                    nextPointY = linePoint.getOriginY();
                } else {
                    nextPointX = currentPointX;
                    nextPointY = currentPointY;
                }

                if (valueIndex == 0) {
                    // Move to start point.
                    path.moveTo(currentPointX, currentPointY);
                } else {
                    // Calculate control points.
                    final float firstDiffX = (currentPointX - prePreviousPointX);
                    final float firstDiffY = (currentPointY - prePreviousPointY);
                    final float secondDiffX = (nextPointX - previousPointX);
                    final float secondDiffY = (nextPointY - previousPointY);
                    final float firstControlPointX = previousPointX + (LINE_SMOOTHNESS * firstDiffX);
                    final float firstControlPointY = previousPointY + (LINE_SMOOTHNESS * firstDiffY);
                    final float secondControlPointX = currentPointX - (LINE_SMOOTHNESS * secondDiffX);
                    final float secondControlPointY = currentPointY - (LINE_SMOOTHNESS * secondDiffY);

                    if (currentPointY == previousPointY) {
                        path.lineTo(currentPointX, currentPointY);
                    } else {
                        path.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                                currentPointX, currentPointY);
                    }
                }

                // Shift values by one back to prevent recalculation of values that have
                // been already calculated.
                prePreviousPointX = previousPointX;
                prePreviousPointY = previousPointY;
                previousPointX = currentPointX;
                previousPointY = currentPointY;
                currentPointX = nextPointX;
                currentPointY = nextPointY;
            }

            measure = new PathMeasure(path, false);
            linePaint.setPathEffect(createPathEffect(measure.getLength(), phase, 0.0f));
            canvas.drawPath(path, linePaint);
        }
    }


    /**
     * 填充
     *
     * @param canvas
     */
    public void drawFillArea(Canvas canvas, Line line, Axis axisX) {
        //继续使用前面的 path
        if (line != null && line.getValues().size() > 1 && isShow) {
            List<PointValue> values = line.getValues();
            PointValue firstPoint = values.get(0);
            float firstX = firstPoint.getOriginX();

            Path path = line.getPath();
            PointValue lastPoint = values.get(values.size() - 1);
            float lastX = lastPoint.getOriginX();
            path.lineTo(lastX, axisX.getStartY());
            path.lineTo(firstX, axisX.getStartY());
            path.close();

            if (fillShader == null) {
                fillShader = new LinearGradient(0, 0, 0, mHeight, line.getFillColor(), Color.TRANSPARENT, Shader.TileMode.CLAMP);
                fillPaint.setShader(fillShader);
            }

            if (line.getFillColor() == 0)
                fillPaint.setAlpha(100);
            else
                fillPaint.setColor(line.getFillColor());

            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(firstX, 0, phase * (lastX - firstX) + firstX, mHeight);
            canvas.drawPath(path, fillPaint);
            canvas.restore();
            path.reset();
        }
    }

    /**
     * 画圆点
     *
     * @param canvas
     */
    public void drawPoints(Canvas canvas, Line line) {
        if (line != null && line.isHasPoints() && isShow) {
            List<PointValue> values = line.getValues();
            float radius = LeafUtil.dp2px(mContext, line.getPointRadius());
            float strokeWidth = LeafUtil.dp2px(mContext, 1);
            PointValue point;
            for (int i = 0, size = values.size(); i < size; i++) {
                point = values.get(i);
                labelPaint.setStyle(Paint.Style.FILL);
                labelPaint.setColor(line.getPointColor());
                canvas.drawCircle(point.getOriginX(), point.getOriginY(),
                        radius, labelPaint);
                labelPaint.setStyle(Paint.Style.STROKE);
                labelPaint.setColor(Color.WHITE);
                labelPaint.setStrokeWidth(strokeWidth);
                canvas.drawCircle(point.getOriginX(), point.getOriginY(),
                        radius, labelPaint);
            }
        }
    }

    /**
     * 带动画的绘制
     *
     * @param duration
     */
    public void showWithAnimation(int duration) {
        isAnimateEnd = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
        animator.setDuration(duration);
        animator.start();
        isShow = true;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                phase = (float) animation.getAnimatedValue();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimateEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    //showWithAnimation动画开启后会调用该方法
    public void setPhase(float phase) {
        chartView.invalidate();
    }

    private PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[]{phase * pathLength, pathLength}, 0);
    }


    @Override
    public void drawLabels(Canvas canvas, ChartData chartData, Axis axisY) {
        if (isAnimateEnd)
            super.drawLabels(canvas, chartData, axisY);
    }
}
