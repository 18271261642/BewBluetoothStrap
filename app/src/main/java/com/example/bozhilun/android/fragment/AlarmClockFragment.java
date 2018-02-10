/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.bozhilun.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.AlarmClockEditActivity;
import com.example.bozhilun.android.activity.AlarmClockNewActivity;
import com.example.bozhilun.android.adpter.AlarmClockAdapter;
import com.example.bozhilun.android.alock.ErrorCatchLinearLayoutManager;
import com.example.bozhilun.android.alock.OnItemClickListener;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.db.AlarmClockOperate;
import com.example.bozhilun.android.event.AlarmClock;
import com.example.bozhilun.android.event.AlarmClockDeleteEvent;
import com.example.bozhilun.android.event.AlarmClockUpdateEvent;
import com.example.bozhilun.android.event.ShakeExplainCloseEvent;
import com.example.bozhilun.android.alock.MyUtil;
import com.example.bozhilun.android.alock.OttoAppConfig;
import com.example.bozhilun.android.alock.ToastUtil;
import com.example.bozhilun.android.util.WeacConstants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;


/**
 * 闹钟fragment
 *
 * @author 咖枯
 * @version 1.0 2015/05
 */
public class AlarmClockFragment extends BaseFragment implements OnClickListener {

    /**
     * 新建闹钟的requestCode
     */
    private static final int REQUEST_ALARM_CLOCK_NEW = 1;

    /**
     * 修改闹钟的requestCode
     */
    private static final int REQUEST_ALARM_CLOCK_EDIT = 2;

    /**
     * 闹钟列表
     */
    private RecyclerView mRecyclerView;

    /**
     * 保存闹钟信息的list
     */
    private List<AlarmClock> mAlarmClockList;

    /**
     * 保存闹钟信息的adapter
     */
    private AlarmClockAdapter mAdapter;

    /**
     * 操作栏编辑按钮
     */
    private ImageView mEditAction;

    /**
     * 操作栏编辑完成按钮
     */
    private ImageView mAcceptAction;

    /**
     * List内容为空时的视图
     */
    private LinearLayout mEmptyView;

    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoAppConfig.getInstance().register(this);
        mAlarmClockList = new ArrayList<>();
        mAdapter = new AlarmClockAdapter(getActivity(), mAlarmClockList);
        // 注册Loader
        // getLoaderManager().initLoader(1, null, this);


    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_alarm_clock, container, false);

        mEmptyView = (LinearLayout) view
                .findViewById(R.id.alarm_clock_empty);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        mRecyclerView.setHasFixedSize(true);
        //设置布局管理器
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new ErrorCatchLinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new ScaleInLeftAnimator(new OvershootInterpolator(1f)));
        mRecyclerView.getItemAnimator().setAddDuration(300);
        mRecyclerView.getItemAnimator().setRemoveDuration(300);
        mRecyclerView.getItemAnimator().setMoveDuration(300);
        mRecyclerView.getItemAnimator().setChangeDuration(300);
        mRecyclerView.setAdapter(mAdapter);

//        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        // 监听闹铃item点击事件Listener
        OnItemClickListener onItemClickListener = new OnItemClickListenerImpl();
        mAdapter.setOnItemClickListener(onItemClickListener);

        // 操作栏新建按钮
        ImageView newAction = (ImageView) view.findViewById(R.id.action_new);
        newAction.setOnClickListener(this);

        // 编辑闹钟
        mEditAction = (ImageView) view.findViewById(R.id.action_edit);
        mEditAction.setOnClickListener(this);


        // 完成按钮
        mAcceptAction = (ImageView) view.findViewById(R.id.action_accept);
        mAcceptAction.setOnClickListener(this);

        updateList();
        return view;
    }

    class OnItemClickListenerImpl implements OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            // 不响应重复点击
            if (MyUtil.isFastDoubleClick()) {
                return;
            }
            AlarmClock alarmClock = mAlarmClockList.get(position);
            Intent intent = new Intent(getActivity(),
                    AlarmClockEditActivity.class);
            intent.putExtra(WeacConstants.ALARM_CLOCK, alarmClock);
            // 开启编辑闹钟界面
            startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
            // 启动移动进入效果动画
            getActivity().overridePendingTransition(R.anim.move_in_bottom,
                    0);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            // 显示删除，完成按钮，隐藏修改按钮
            displayDeleteAccept();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_new:
                // 不响应重复点击
                if (MyUtil.isFastDoubleClick()) {
                    return;
                }


                if(AlarmClockOperate.getInstance().loadAlarmClocks().size() >= 9 && "B15S".equals(MyCommandManager.DEVICENAME)) {
                   // Toast.makeText(getActivity(), "B15S型号最多只能添加9个闹钟", Toast.LENGTH_SHORT).show();
                }else if (AlarmClockOperate.getInstance().loadAlarmClocks().size() >= 2 && "B15P".equals(MyCommandManager.DEVICENAME)){
                    //    Toast.makeText(getActivity(), "B15P型号最多只能添加3个闹钟", Toast.LENGTH_SHORT).show();
                }else {

                Intent intent = new Intent(getActivity(),
                        AlarmClockNewActivity.class);
                // 开启新建闹钟界面
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_NEW);
                // 启动渐变放大效果动画
                getActivity().overridePendingTransition(R.anim.zoomin, 0);
            }
                    break;
                    case R.id.action_edit:
                        getActivity().onBackPressed();

                        break;
                    case R.id.action_accept:
                        // 隐藏删除，完成按钮,显示修改按钮
                        hideDeleteAccept();
                        break;

        }

    }

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;
    private AlarmClock mDeletedAlarmClock;

    /**
     * 显示删除，完成按钮，隐藏修改按钮
     */
    private void displayDeleteAccept() {
        mAdapter.setIsCanClick(false);
        mAdapter.displayDeleteButton(true);
        mAdapter.notifyDataSetChanged();
        mAcceptAction.setVisibility(View.GONE);

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float xValue = Math.abs(event.values[0]);
                    float yValue = Math.abs(event.values[1]);
                    float zValue = Math.abs(event.values[2]);
                    // 认为用户摇动了手机，找回被删除的闹钟
                    if (xValue > 15 || yValue > 15 || zValue > 15) {
                        if (mDeletedAlarmClock != null) {
                            MyUtil.vibrate(getActivity());
                            AlarmClockOperate.getInstance().saveAlarmClock(mDeletedAlarmClock);
                            addList(mDeletedAlarmClock);
                            mDeletedAlarmClock = null;
                            ToastUtil.showLongToast(getActivity(), getString(R.string.retrieve_alarm_clock_success));
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    /**
     * 隐藏删除，完成按钮,显示修改按钮
     */
    private void hideDeleteAccept() {
        mAdapter.setIsCanClick(true);
        mAdapter.displayDeleteButton(false);
        mAdapter.notifyDataSetChanged();
        mAcceptAction.setVisibility(View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        AlarmClock ac = data.getParcelableExtra(WeacConstants.ALARM_CLOCK);

        switch (requestCode) {
            // 新建闹钟
            case REQUEST_ALARM_CLOCK_NEW:
                // 插入新闹钟数据
                AlarmClockOperate.getInstance().saveAlarmClock(ac);
                addList(ac);
                break;
            // 修改闹钟
            case REQUEST_ALARM_CLOCK_EDIT:
                // 更新闹钟数据
//                TabAlarmClockOperate.getInstance(getActivity()).update(ac);
                AlarmClockOperate.getInstance().updateAlarmClock(ac);
                updateList();
                break;

        }
    }

    @Subscribe
    public void onAlarmClockUpdate(AlarmClockUpdateEvent event) {
        updateList();
    }

    private boolean isShowingShakeExplain;

    @Subscribe
    public void OnAlarmClockDelete(AlarmClockDeleteEvent event) {
        deleteList(event);

        mDeletedAlarmClock = event.getAlarmClock();


//        }

    }

    @Subscribe
    public void OnShakeExplainClose(ShakeExplainCloseEvent event) {
        isShowingShakeExplain = false;
    }





    private void addList(AlarmClock ac) {
        mAlarmClockList.clear();

//        SharedPreferences share = getActivity().getSharedPreferences("alock_id", 0);
//        int a = share.getInt("id",0);
//        ac.setAlock_id(a);
//        MyCommandManager.NewAlock(MyCommandManager.DEVICENAME, ac,true);//闹钟提醒
//
//
//        SharedPreferences shares = getActivity().getSharedPreferences("alock_id", 0);
//        SharedPreferences.Editor editors = shares.edit();
//        int b = a+1;
//        editors.putInt("id",b);
//        editors.commit();

        Long id = ac.getId();
        int count = 0;
        int position = 0;
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            if (id == alarmClock.getId()) {
                position = count;
                if (alarmClock.isOnOff()) {
                    //MyUtil.startAlarmClock(getActivity(), alarmClock);
                }
            }
            count++;
        }

        checkIsEmpty(list);

        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    private void deleteList(AlarmClockDeleteEvent event) {
        mAlarmClockList.clear();

        int position = event.getPosition();
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);
        }
        // 列表为空时不显示删除，完成按钮
        if (mAlarmClockList.size() == 0) {
            mAcceptAction.setVisibility(View.GONE);
            mAdapter.displayDeleteButton(false);
        }

        checkIsEmpty(list);

        mAdapter.notifyItemRemoved(position);
//        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
    }

    private void updateList() {
        mAlarmClockList.clear();

        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            // 当闹钟为开时刷新开启闹钟
            if (alarmClock.isOnOff()) {
               // MyUtil.startAlarmClock(getActivity(), alarmClock);
            }
        }

        checkIsEmpty(list);

        mAdapter.notifyDataSetChanged();
    }

    private void checkIsEmpty(List<AlarmClock> list) {
        if (list.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);


        }
    }


    @Override
    public void onPause() {
        super.onPause();
        // 当没有显示摇一摇找回删除的闹钟操作说明
        if (!isShowingShakeExplain) {
            // 隐藏删除，完成按钮,显示修改按钮
            hideDeleteAccept();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OttoAppConfig.getInstance().unregister(this);
    }
}
