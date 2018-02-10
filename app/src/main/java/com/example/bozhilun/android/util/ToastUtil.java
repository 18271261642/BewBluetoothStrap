package com.example.bozhilun.android.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by thinkpad on 2017/1/5.
 */

public class ToastUtil {

    private static Toast mToast;

    /**
     * Toast 提示
     * @param mContext
     * @param msg
     */
    public static void showToast(Context mContext,String msg){
        if(mToast == null){
            mToast = Toast.makeText(mContext,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();

    }


    // 短时间显示Toast信息
    public static void showShort(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    // 长时间显示Toast信息
    public static void showLong(Context context, String info) {
        if(mToast == null){
            mToast = Toast.makeText(context,info,Toast.LENGTH_LONG);
        }else{
            mToast.setText(info);
        }
        mToast.show();
    }

}
