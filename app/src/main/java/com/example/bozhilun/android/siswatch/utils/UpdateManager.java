package com.example.bozhilun.android.siswatch.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.view.PromptDialog;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/28.
 */

public class UpdateManager {

    private Context mContext;
    private String url;
    private CommonSubscriber commonSubscriber;


    public UpdateManager(Context mContext,String url) {
        this.mContext = mContext;
        this.url = url;

    }

    public void setUpdate(final String upUrl){
        final PromptDialog pd = new PromptDialog(mContext);
        pd.show();
        pd.setTitle(mContext.getResources().getString(R.string.prompt));
        pd.setContent(mContext.getResources().getString(R.string.newversion));
        pd.setleftText("NO");
        pd.setrightText("YES");
        pd.setListener(new PromptDialog.OnPromptDialogListener() {
            @Override
            public void leftClick(int code) {
                pd.dismiss();
            }

            @Override
            public void rightClick(int code) {
                pd.dismiss();
                Intent intent = new Intent(mContext.getApplicationContext(),UpdateWebViewActivity.class);
                intent.putExtra("updateUrl",upUrl);
                mContext.startActivity(intent);
            }
        });
    }

    public void checkForUpdate(final boolean isTrue) {
        if (url != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("version", WatchUtils.getVersionCode(mContext));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commonSubscriber = new CommonSubscriber(new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String result) {
                    Log.e("update", "---result---" + result);
                    if (!WatchUtils.isEmpty(result)) {
                        try {
                            JSONObject jsono = new JSONObject(result);
                            if(jsono.getString("resultCode").equals("001")){
                                String verStr = jsono.getString("versionInfo");
                                if(!WatchUtils.isEmpty(verStr)){
                                    JSONObject versionInfo = new JSONObject(verStr);
                                    int version = versionInfo.getInt("version");
                                    if(version > WatchUtils.getVersionCode(mContext)){
                                        setUpdate(versionInfo.getString("url"));
                                    }else{
                                        if(isTrue){//latest_version
                                            ToastUtil.showToast(mContext,mContext.getResources().getString(R.string.latest_version));
                                        }
                                    }
                                }else{
                                    if(isTrue){
                                        ToastUtil.showToast(mContext,mContext.getResources().getString(R.string.latest_version));
                                    }

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, mContext);
            OkHttpObservable.getInstance().getData(commonSubscriber, url, jsonObject.toString());

        }
    }
}
