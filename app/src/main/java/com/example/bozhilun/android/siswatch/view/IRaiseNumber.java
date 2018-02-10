package com.example.bozhilun.android.siswatch.view;

/**
 * Created by Administrator on 2017/10/28.
 */

public interface IRaiseNumber {
    void start();
    void setFloat(float fromNum, float toNum);
    void setInteger(int fromNum, int toNum);
    void setDuration(long duration);
    void setOnEndListener(RiseNumberTextView.EndListener callback);
}
