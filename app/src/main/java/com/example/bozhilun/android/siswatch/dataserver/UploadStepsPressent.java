package com.example.bozhilun.android.siswatch.dataserver;

import android.content.Context;
import android.util.Log;

import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/10.
 */

public class UploadStepsPressent {

    private UploadStepsView uploadStepsView;
    private UploadStepsModel uploadStepsModel;


    public UploadStepsPressent() {
        uploadStepsModel = new UploadStepsModel();
    }


    public void pressentUploadData(Context mContext, String url, Map<String,Object> objectMap){
        if(uploadStepsView != null){
            uploadStepsModel.modelUpdate(mContext,url,objectMap, new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String s) {
                    Log.e("ppp","---s="+s);
                    uploadStepsView.uploadResultData(s);
                }
            });
        }
    }

    //绑定
    public void attach(UploadStepsView uploadStepsView){
        this.uploadStepsView = uploadStepsView;
    }

    //解除绑定
    public void detach(){
        if(uploadStepsView != null){
            uploadStepsView = null;
        }
    }
}
