package com.example.bozhilun.android.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * Created by liukun on 16/3/10.
 */
public class DialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private ProgressDialog progressDialog;

    private Context context;
    private boolean cancelable;
    private DialogCancelListener mProgressCancelListener;

    public DialogHandler(Context context, DialogCancelListener mProgressCancelListener,
                         boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    private void initDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loding...");
            //View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_mooc_alert_dialog, null);
//            BGAMoocStyleRefreshView moocImage = (BGAMoocStyleRefreshView) contentView.findViewById(R.id.zeffect_recordbutton_dialog_imageview);
//            moocImage.setOriginalImage(R.mipmap.refresh_star);
//            moocImage.setUltimateColor(R.color.login_blue);
//            moocImage.startRefreshing();裂缝3次巨龙  · 巨龙     |    裂缝3次  （道具城 · dnf助手）
            //dialog.setContentView(contentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //dialog.setCancelable(cancelable);
            if (cancelable) {
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }
            if (!((Activity) context).isFinishing() && progressDialog != null) {
                progressDialog.show();
            }
        }
    }

    private void dismissProgressDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

}
