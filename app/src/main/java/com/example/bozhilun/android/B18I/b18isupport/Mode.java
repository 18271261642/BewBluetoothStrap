package com.example.bozhilun.android.B18I.b18isupport;

/**
 * 描述：
 * </br>
 */
public interface Mode {
    /**
     * 交叉，x、y轴都超出0点
     */
    int ACROSS = 1;

    /**
     * 相交， x、y轴交于0点
     */
    int INTERSECT = 2;

    /**
     * x轴超出0点
     */
    int X_ACROSS = 3;

    /**
     * y轴超出0点
     */
    int Y_ACROSS = 4;

}
