package com.example.bozhilun.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.example.bozhilun.android.R;
/**
 * Created by ishratkhan on 26/02/16.
 */
public class ViewUtils {

  /*  public static void handleVerticalLines(View v2, View v3) {

        RelativeLayout.LayoutParams pram = new RelativeLayout.LayoutParams(3, RelativeLayout.LayoutParams.MATCH_PARENT);
        pram.setMarginStart(ViewUtils.getLevelOneMargin());
        v2.setLayoutParams(pram);

        RelativeLayout.LayoutParams pram2 = new RelativeLayout.LayoutParams(3, RelativeLayout.LayoutParams.MATCH_PARENT);
        pram2.setMarginStart(getLevelTwoMargin());
        v3.setLayoutParams(pram2);
    }*/

    public static int getLevelOneMargin() {
        return 100;
    }

    public static int getLevelTwoMargin() {
        return 200;
    }

    public static void showSnackbar(View view, String content, Context context){
        SpannableString spanText = new SpannableString(content);
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        Snackbar snackbar = Snackbar.make(view, spanText, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        snackbar.show();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
