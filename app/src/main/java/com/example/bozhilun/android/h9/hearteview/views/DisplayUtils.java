package com.example.bozhilun.android.h9.hearteview.views;

import android.content.Context;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/19 11:34
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class DisplayUtils {

    public static int dip2px(Context context, float dip) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    public static int px2dip(Context context, float px) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }
}