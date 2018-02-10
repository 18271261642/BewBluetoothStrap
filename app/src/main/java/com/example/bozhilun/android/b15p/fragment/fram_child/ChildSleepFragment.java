package com.example.bozhilun.android.b15p.fragment.fram_child;


import android.support.v4.app.Fragment;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b15p.fragment.fram_child_adapter.base.B15pBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildSleepFragment extends B15pBaseFragment {
    private static final String TAG = "===>>ChildSleepFragment";

    @Override
    protected int setContentView() {
        return R.layout.b18i_pie_chart_view;
    }

    @Override
    protected void lazyLoad() {
        refresh();
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
    }

    public void refresh() {
//        RefreshListenter.setmRefreshChangeListenter(new RefreshListenter.MRefreshChangeListenter() {
//            @Override
//            public void setMRefreshChangeListenter(Message msg) {
//                if (msg.what == RefreshListenter.getMessgeNumber()) {
//                    RefreshListenter.getmHandler().removeMessages(RefreshListenter.getMessgeNumber());
//                    if (B15pRecordFragment.getPAGES() == 2) {
//                        Log.d(TAG, "-------刷新--------" + "睡眠页面");
//                    }
//                    B15pRecordFragment.getSwipeRefresh().setRefreshing(false);
//                }
//            }
//        });
    }

}
