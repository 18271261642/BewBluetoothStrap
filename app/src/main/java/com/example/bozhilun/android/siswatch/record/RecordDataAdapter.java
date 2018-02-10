package com.example.bozhilun.android.siswatch.record;

/**
 * Created by sunjianhua on 2017/11/7.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;

import java.util.List;


/**
 * H8点击时间进入记录适配器
 */
public class RecordDataAdapter extends RecyclerView.Adapter<RecordDataAdapter.RecordViewHolder>{

    private List<WatchDataDatyBean> list;
    private Context mContext;

    public RecordDataAdapter(List<WatchDataDatyBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.step_month_item,parent,false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        holder.tvDate.setText(list.get(position).getRtc().substring(5,list.get(position).getRtc().length()));
        holder.tvStep.setText(list.get(position).getStepNumber()+"");
        holder.tvKcal.setText(list.get(position).getCalories());
        holder.tvDisc.setText(list.get(position).getDistance());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate,tvStep,tvKcal,tvDisc;

        public RecordViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.itemtvDate);
            tvStep = (TextView) itemView.findViewById(R.id.itemtvStep);
            tvKcal = (TextView) itemView.findViewById(R.id.itemtvKcal);
            tvDisc = (TextView) itemView.findViewById(R.id.itemtvDisc);

        }
    }
}
