package com.example.bozhilun.android.b15p.fragment.fram_child_adapter;

import android.support.v4.widget.SwipeRefreshLayout;

import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.MyNewHandler;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/12/8 14:55
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class RefreshListenter implements SwipeRefreshLayout.OnRefreshListener {
//    private static MRefreshChangeListenter mRefreshChangeListenter;
//    private static final int MessgeNumber = 11001;
//    public static int REFRESH_NUMBER = 600;
//
//    public static int getMessgeNumber() {
//        return MessgeNumber;
//    }
//
//    public static void setmRefreshChangeListenter(MRefreshChangeListenter mRefreshChangeListenter) {
//        RefreshListenter.mRefreshChangeListenter = mRefreshChangeListenter;
//    }
//
//    public static void setRefreshNumber(int refreshNumber) {
//        REFRESH_NUMBER = refreshNumber;
//    }
//
//    public interface MRefreshChangeListenter {
//        void setMRefreshChangeListenter(Message msg);
//    }
//
//    private static Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == MessgeNumber) {
//                mRefreshChangeListenter.setMRefreshChangeListenter(msg);
//            }
//            return false;
//        }
//    });
//
//    public static Handler getmHandler() {
//        return mHandler;
//    }

    @Override
    public void onRefresh() {
//        mHandler.sendEmptyMessageDelayed(MessgeNumber, REFRESH_NUMBER);
        MyNewHandler myNewHandler = MyNewHandler.getInstance();
        myNewHandler.sendEmptyMessageDelayed(myNewHandler.getMessgeNumber(), myNewHandler.getRefreshNumber());

//        Intent intent = new Intent();
//        intent.setAction("com.example.bozhilun.android.h9.connstate" + "_" + "broadName");
////                intent.putExtra("refresh", "YES");
//        MyApp.getContext().sendBroadcast(intent);
    }

}
