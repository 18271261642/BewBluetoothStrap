package com.example.bozhilun.android.B18I.b18isupport;

/**
 * 描述：
 * </br>
 */
public interface OnPointSelectListener {

    /**
     * @param position  x轴位置
     * @param xLabel    x轴对应刻度值
     * @param value     对应点数值
     */
    void onPointSelect(int position, String xLabel, String value);

}
