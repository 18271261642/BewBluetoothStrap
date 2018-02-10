package com.example.bozhilun.android.B18I.b18isystemic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.AlarmTestBean;
import com.sdk.bluetooth.bean.RemindData;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.data.RemindSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * @aboutContent:
 * @author： 安
 * @crateTime: 2017/10/25 15:25
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class H9AlarmAdapter extends BaseAdapter {

    private SwitchOnclick setOnSwitchOnclick;
    private List<RemindData> remindDataList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    List<AlarmTestBean> alarmTestBeen;
    int[] weekArray = new int[]{1, 2, 4, 8, 16, 32, 64};

    public void setSetOnSwitchOnclick(SwitchOnclick setOnSwitchOnclick) {
        this.setOnSwitchOnclick = setOnSwitchOnclick;
    }

    public interface SwitchOnclick {
        void OnSwitchOnclick(CompoundButton buttonView, boolean isChecked, int position);
    }

    public H9AlarmAdapter(List<RemindData> remindDataList, Context mContext) {
        this.remindDataList = remindDataList;
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return remindDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return remindDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder holder = null;
        List<String> weekList = new ArrayList<>();
        alarmTestBeen = new ArrayList<>();
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.b18i_list_alarm_clock_item, parent, false);
            holder = new holder();
            holder.hour = (TextView) convertView.findViewById(R.id.text_hour);
            holder.min = (TextView) convertView.findViewById(R.id.text_min);
            holder.type = (TextView) convertView.findViewById(R.id.text_type);
            holder.imageType = (ImageView) convertView.findViewById(R.id.image_type);
            holder.switchA = (Switch) convertView.findViewById(R.id.switch_alarm);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.layout_item);

            RemindData remindData = remindDataList.get(position);
            //类型图片显示
            int remind_type = remindData.remind_type;
            if (remind_type == 0) {
                holder.imageType.setBackgroundResource(R.mipmap.eat);
            } else if (remind_type == 1) {
                holder.imageType.setBackgroundResource(R.mipmap.medicine);
            } else if (remind_type == 2) {
                holder.imageType.setBackgroundResource(R.mipmap.dring);
            } else if (remind_type == 3) {
                holder.imageType.setBackgroundResource(R.mipmap.sp);
            } else if (remind_type == 4) {
                holder.imageType.setBackgroundResource(R.mipmap.awakes);
            } else if (remind_type == 5) {
                holder.imageType.setBackgroundResource(R.mipmap.spr);
            } else if (remind_type == 6) {
                holder.imageType.setBackgroundResource(R.mipmap.metting);
            } else if (remind_type == 7) {
                holder.imageType.setBackgroundResource(R.mipmap.custom);
            }

            String stringHour = "12";
            String stringMin = "12";
            if (remindData.remind_time_hours <= 9) {
                stringHour = "0" + remindData.remind_time_hours;
            } else {
                stringHour = String.valueOf(remindData.remind_time_hours);
            }
            if (remindData.remind_time_minutes <= 9) {
                stringMin = "0" + remindData.remind_time_minutes;
            } else {
                stringMin = String.valueOf(remindData.remind_time_minutes);
            }
            //时间
            holder.hour.setText(stringHour + ":" + stringMin);
            //开关
            if (remindData.remind_set_ok == 0) {      //关
                holder.switchA.setChecked(false);
            } else if (remindData.remind_set_ok == 1) {    //开
                holder.switchA.setChecked(true);
            }
            holder.switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setOnSwitchOnclick.OnSwitchOnclick(buttonView, isChecked, position);
                }
            });
            //周期
            String weekDate = remindData.remind_week;
            int week = Integer.parseInt(B18iUtils.toD(remindData.remind_week, 2));
//            Log.e("闹钟适配器", "------week----" + weekDate + "-------week---" + week);

            if ((week & weekArray[0]) == 1) {   //周日
                alarmTestBeen.add(new AlarmTestBean(1, mContext.getResources().getString(R.string.sunday)));
            }
            if ((week & weekArray[1]) == 2) { //周一
                alarmTestBeen.add(new AlarmTestBean(2, mContext.getResources().getString(R.string.monday)));
            }
            if ((week & weekArray[2]) == 4) { //周二
                alarmTestBeen.add(new AlarmTestBean(4, mContext.getResources().getString(R.string.tuesday)));
            }
            if ((week & weekArray[3]) == 8) {  //周三
                alarmTestBeen.add(new AlarmTestBean(8, mContext.getResources().getString(R.string.wednesday)));
            }
            if ((week & weekArray[4]) == 16) {  //周四
                alarmTestBeen.add(new AlarmTestBean(16, mContext.getResources().getString(R.string.thursday)));
            }
            if ((week & weekArray[5]) == 32) {  //周五
                alarmTestBeen.add(new AlarmTestBean(32, mContext.getResources().getString(R.string.friday)));
            }
            if ((week & weekArray[6]) == 64) {  //周六
                alarmTestBeen.add(new AlarmTestBean(64, mContext.getResources().getString(R.string.saturday)));
            }

            holder.type.setText(getTestData(alarmTestBeen));
            alarmTestBeen.clear();

        }
        return convertView;
    }

    private String getTestData(List<AlarmTestBean> alarmTestBeen) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < alarmTestBeen.size(); i++) {
            sb.append(alarmTestBeen.get(i).getWeeks());
            sb.append(",");
        }
        if (sb.toString().length() > 1) {
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return null;
        }
    }

    class holder {
        TextView hour;
        TextView min;
        TextView type;
        ImageView imageType;
        Switch switchA;
        LinearLayout linearLayout;
    }

}
