package com.example.bozhilun.android.net;

import android.text.TextUtils;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.util.MyLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by thinkpad on 2016/6/27.
 */
public class OkHttpObservable {

    private static OkHttpClient client = new OkHttpClient();

    private static class SingletonHolder {
        // private static String jsonParam;
        private static final OkHttpObservable okHttpObservable = new OkHttpObservable();
    }

    //获取单例
    public static OkHttpObservable getInstance() {
        return SingletonHolder.okHttpObservable;
    }

    public void getData(Subscriber<String> subscriber, final String url, final String jsonParam) {
         MyLogUtil.i("--url-jsonParam->"+url+jsonParam);
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    RequestBody formBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParam);
                    client.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseUrl = response.body().string();
                            MyLogUtil.i("msg", "--responseUrl-" + responseUrl);
                            if (!TextUtils.isEmpty(responseUrl)) {
                                try {
                                 //   JSONObject jsonObject = new JSONObject(responseUrl);
                                    subscriber.onNext(responseUrl);
                                    subscriber.onCompleted();
                                  /*  String resultCode = jsonObject.getString("resultCode");
                                    String resultMsg = jsonObject.getString("resultMsg");*/
                                  /*  if("001".equals(resultCode)){
                                    subscriber.onNext(responseUrl);
                                    subscriber.onCompleted();
                                    }else  if(responseUrl.contains("502")){
                                        subscriber.onError(new Exception("链接服务器失败"));
                                    }else{
                                        subscriber.onError(new Exception(jsonObject.getString("errorMessage")));
                                    }*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                                }
                            } else {
                                subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                            }
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        toSubscribe(observable, subscriber);
    }


    public void getNoParamData(Subscriber<String> subscriber, final String url) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    client.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseUrl = response.body().string();
                            if (!TextUtils.isEmpty(responseUrl)) {
                                subscriber.onNext(responseUrl);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(new Exception(MyApp.getApplication().getResources().getString(R.string.fuwuqi)));
                            }
                        }
                    });
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        toSubscribe(observable, subscriber);
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
