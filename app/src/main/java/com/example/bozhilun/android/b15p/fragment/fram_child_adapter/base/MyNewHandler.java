package com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base;

import android.os.Handler;
import android.os.Message;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/12/14 17:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class MyNewHandler extends Handler {
    private final int MessgeNumber = 888666;
    private int REFRESH_NUMBER = 600;

    public int getMessgeNumber() {
        return MessgeNumber;
    }

    public int getRefreshNumber() {
        return REFRESH_NUMBER;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MessgeNumber) {
            myMessage.mHandler(msg);
        }
    }

    private MyMessage myMessage;

    public void setMyMessage(MyMessage myMessage) {
        this.myMessage = myMessage;
    }

    public interface MyMessage {
        void mHandler(Message msg);
    }

    private static MyNewHandler mInstance;

    public static MyNewHandler getInstance() {
        if (mInstance == null) {
            synchronized (MyNewHandler.class) {
                if (mInstance == null) {
                    mInstance = new MyNewHandler();
                }
            }
        }
        return mInstance;
    }
}
