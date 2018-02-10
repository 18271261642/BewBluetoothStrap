package com.example.bozhilun.android.rxandroid;

import android.content.Context;
import android.widget.Toast;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by thinkpad on 2016/6/22.
 */

public class CommonSubscriber<T> extends Subscriber<T> {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private Context context;

    public CommonSubscriber(SubscriberOnNextListener mSubscriberOnNextListener,Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context=context;
    }

    @Override
    public void onCompleted() {
        //Toast.makeText(context, "Get  Completed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
           // Toast.makeText(context, MyApp.getApplication().getResources().getString(R.string.wangluo), Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
          //  Toast.makeText(context,MyApp.getApplication().getResources().getString(R.string.wangluo), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }
}
