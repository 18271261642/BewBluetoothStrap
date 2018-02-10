package com.example.bozhilun.android.siswatch;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bozhilun.android.R;

/**
 * Created by Administrator on 2018/2/5.
 */

public class WatchBaseFragment extends Fragment {

    private Dialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MSG_DISMISS_DIALOG == msg.what) {
                if (null != dialog) {
                    if (dialog.isShowing()) {
                        Log.i("----", "handler get mesage");
                        dialog.dismiss();
                    }
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private static int MSG_DISMISS_DIALOG = 101;
    public void showLoadingDialog(String msg) {
        if (dialog == null && !getActivity().isFinishing()) {
            dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.pro_dialog_layout_view);
            TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
            tv.setText(msg + "");
            dialog.setCancelable(true);
            dialog.show();
        } else {
            if(!getActivity().isFinishing()){
                dialog.setContentView(R.layout.pro_dialog_layout_view);
                dialog.setCancelable(true);
                TextView tv = (TextView) dialog.getWindow().findViewById(R.id.progress_tv);
                tv.setText(msg + "");
                dialog.show();
            }

        }
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, 30 * 1000);
    }

    //关闭进度条
    public void closeLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
