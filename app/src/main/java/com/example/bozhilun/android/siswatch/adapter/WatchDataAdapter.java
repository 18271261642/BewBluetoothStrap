package com.example.bozhilun.android.siswatch.adapter;

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
 * Created by Administrator on 2017/7/20.
 */

public class WatchDataAdapter  extends RecyclerView.Adapter<WatchDataAdapter.WatchDataHolder>{

    private Context mContext;
    private List<WatchDataDatyBean> list;

    public WatchDataItemClickListener watchDataItemClickListener;

    public void setWatchDataItemClickListener(WatchDataItemClickListener watchDataItemClickListener) {
        this.watchDataItemClickListener = watchDataItemClickListener;
    }

    public WatchDataAdapter(Context mContext, List<WatchDataDatyBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public WatchDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_watch_data_layout,parent,false);
        return new WatchDataHolder(view);
    }

    @Override
    public void onBindViewHolder(final WatchDataHolder holder, int position) {
        String dates = list.get(position).getRtc();
        holder.timeTv.setText(dates.substring(5,dates.length()));
        holder.stepTv.setText(list.get(position).getStepNumber()+"");
        holder.distanceTv.setText(list.get(position).getDistance()+"");
        holder.kcalTv.setText(list.get(position).getCalories()+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getLayoutPosition();
                if(watchDataItemClickListener != null){
                    watchDataItemClickListener.getItemData(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class WatchDataHolder extends RecyclerView.ViewHolder{

        TextView timeTv,stepTv,distanceTv,kcalTv;

        public WatchDataHolder(View itemView) {
            super(itemView);

            timeTv = (TextView) itemView.findViewById(R.id.item_watch_data_timeTv);
            stepTv = (TextView) itemView.findViewById(R.id.item_watch_data_stepTv);
            distanceTv = (TextView) itemView.findViewById(R.id.item_watch_data_distanceTv);
            kcalTv = (TextView) itemView.findViewById(R.id.item_watch_data_kcalTv);

        }
    }

    public interface WatchDataItemClickListener{
        void getItemData(int position);
    }
}
