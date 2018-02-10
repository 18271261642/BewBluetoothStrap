package com.example.bozhilun.android.B18I.b18ibean;

import android.graphics.Color;

import java.util.List;

/**
 * 描述：直方图
 * </br>
 */
public class Square extends ChartData {
    /**
     * 直方图宽度
     */
    private int width = 10;

    /**
     * 边框宽度
     */
    private int borderWidth = 1;

    /**
     * 边框颜色
     */
    private int borderColor = Color.GRAY;

    /**
     * 是否填充
     */
    private boolean isFill = false;

    private boolean isNHow = false;

    private int isPos = 0;

    public int getIsPos() {
        return isPos;
    }

    public void setIsPos(int isPos) {
        this.isPos = isPos;
    }

    public boolean isNHow() {
        return isNHow;
    }

    public void setNHow(boolean NHow) {
        isNHow = NHow;
    }

    public Square(List<PointValue> values) {
        this.values = values;
    }


    public int getWidth() {
        return width;
    }

    public Square setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public Square setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public Square setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public boolean isFill() {
        return isFill;
    }

    public Square setFill(boolean fill) {
        isFill = fill;
        return this;
    }
}




















