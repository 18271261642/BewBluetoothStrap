package com.example.bozhilun.android.B18I.b18irenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.bozhilun.android.B18I.b18ibean.Axis;
import com.example.bozhilun.android.B18I.b18ibean.PointValue;
import com.example.bozhilun.android.B18I.b18ibean.Square;
import com.example.bozhilun.android.B18I.b18isupport.LeafUtil;

import java.util.List;

/**
 * 描述：
 * </br>
 */

public class B18ILeafSquareRenderer extends B18IAbsRenderer {

    public B18ILeafSquareRenderer(Context context, View view) {
        super(context, view);
    }

    public void drawSquares(Canvas canvas, Square square, Axis axisX) {
        if (square != null) {
            //1.画直方图边界
//            linePaint.setColor(square.getBorderColor());
            if (!square.isFill()) {
                linePaint.setStrokeWidth(LeafUtil.dp2px(mContext, square.getBorderWidth()));
                linePaint.setStyle(Paint.Style.STROKE);
            }
            List<PointValue> values = square.getValues();
            float width = LeafUtil.dp2px(mContext, square.getWidth());
            for (int i = 0; i < values.size(); i++) {
                PointValue pointValue = values.get(i);
                RectF rectF = new RectF(pointValue.getOriginX() - width / 2,
                        pointValue.getOriginY(), pointValue.getOriginX() + width / 2, axisX.getStartY());
                if (i == pointValue.getN()){
                    linePaint.setColor(Color.parseColor("#FFFFFFFF"));
                }else {
                    linePaint.setColor(square.getBorderColor());
                }
                canvas.drawRect(rectF, linePaint);
            }
            
//            for (PointValue point : values) {
//                RectF rectF = new RectF(point.getOriginX() - width / 2,
//                        point.getOriginY(), point.getOriginX() + width / 2, axisX.getStartY());
//                canvas.drawRect(rectF, linePaint);
//            }
        }
    }

}
