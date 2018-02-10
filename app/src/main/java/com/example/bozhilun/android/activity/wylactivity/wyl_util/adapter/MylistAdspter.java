package com.example.bozhilun.android.activity.wylactivity.wyl_util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/3/30.
 */

public class MylistAdspter extends BaseAdapter {

        private List<Map<String, Object>> data;
        private LayoutInflater layoutInflater;
        private Context context;
        public MylistAdspter(Context contextS,List<Map<String, Object>> data){
            this.context=contextS;
            this.data=data;
            this.layoutInflater=LayoutInflater.from(context);
        }
        /**
         * 组件集合，对应list.xml中的控件
         * @author Administrator
         */
        public final class Zujian{
            public ImageView image;//跑步-骑行
            public TextView year;//年
            public TextView day;//日
            public TextView zonggongli;//总公里
            public TextView qixing;//骑行或者跑步
            public TextView chixugongli;//持续公里数
            public TextView chixutime;//持续时间
            public TextView kclal;//卡路里
        }
        @Override
        public int getCount() {
            return data.size();
        }
        /**
         * 获得某一位置的数据
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }
        /**
         * 获得唯一标识
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Zujian zujian=null;
            if(convertView==null){
                zujian=new Zujian();
                //获得组件，实例化组件
                convertView=layoutInflater.inflate(R.layout.sporthistory_list_item, null);
                zujian.year=(TextView) convertView.findViewById(R.id.my_year);//年
                zujian. zonggongli=(TextView)convertView.findViewById(R.id.shuji_zonggongli);//总公里
                zujian. image=(ImageView)convertView.findViewById(R.id.my_paobu);//跑步-骑行
                zujian. day=(TextView)convertView.findViewById(R.id.ri_xiangqing);//日
                zujian. qixing=(TextView)convertView.findViewById(R.id.qixing_my_huwai);//骑行或者跑步
                zujian.chixugongli=(TextView)convertView.findViewById(R.id.chixugongli_time);//持续公里数
                zujian. chixutime=(TextView)convertView.findViewById(R.id.chixu_time);//持续时间
                zujian. kclal=(TextView)convertView.findViewById(R.id.my_kacal);//卡路里
                zujian.year.setTag(position);
                zujian.zonggongli.setTag(position);
                zujian.image.setTag(position);
                zujian.day.setTag(position);
                zujian.qixing.setTag(position);
                zujian.chixugongli.setTag(position);
                zujian.chixutime.setTag(position);
                zujian.kclal.setTag(position);
                convertView.setTag(zujian);
            }else{
                zujian=(Zujian)convertView.getTag();
            }
            //绑定数据
            zujian.year.setText((String)data.get(position).get("year"));
            zujian.zonggongli.setText((String)data.get(position).get("zonggongli"));
            zujian.image.setBackgroundResource((Integer)data.get(position).get("image"));
            zujian.day.setText((String)data.get(position).get("day"));
            zujian.qixing.setText((String)data.get(position).get("qixing"));
            zujian.chixugongli.setText((String)data.get(position).get("chixugongli"));
            zujian.chixutime.setText((String)data.get(position).get("chixutime"));
            zujian.kclal.setText((String)data.get(position).get("kclal"));
            return convertView;
        }
}
