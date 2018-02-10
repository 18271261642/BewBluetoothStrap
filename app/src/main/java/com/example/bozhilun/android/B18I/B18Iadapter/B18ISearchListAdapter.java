package com.example.bozhilun.android.B18I.B18Iadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dd.CircularProgressButton;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.B18I.B18imodle.B18IDeviceBean;
import java.util.List;

/**
 * Created by Administrator on 2017/8/24.
 */

public class B18ISearchListAdapter extends RecyclerView.Adapter<B18ISearchListAdapter.L38iHolder>{
    private Context mContext;
    private List<B18IDeviceBean> b18IDeviceBeanList;

    private OnBindClickListener bindClickListener;

    public void setBindClickListener(OnBindClickListener bindClickListener) {
        this.bindClickListener = bindClickListener;
    }

    public B18ISearchListAdapter(Context mContext, List<B18IDeviceBean> b18IDeviceBeanList) {
        this.mContext = mContext;
        this.b18IDeviceBeanList = b18IDeviceBeanList;
    }


    @Override
    public L38iHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_bluedevice,parent,false);
        return new L38iHolder(view);
    }

    @Override
    public void onBindViewHolder(L38iHolder holder, final int position) {
        //holder.img_logo
        holder.tvname.setText(b18IDeviceBeanList.get(position).getBluetoothDevice().getName());
        holder.tvmac.setText(b18IDeviceBeanList.get(position).getBluetoothDevice().getAddress()+"");
        holder.tvssi.setText(b18IDeviceBeanList.get(position).getSsi()+"");
        holder.bind_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bindClickListener != null){
                    bindClickListener.doBindClick(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return b18IDeviceBeanList.size();
    }


    class L38iHolder extends RecyclerView.ViewHolder{

        ImageView img_logo;
        TextView tvname;
        TextView tvmac;
        TextView tvssi;
        CircularProgressButton bind_btn;

        public L38iHolder(View itemView) {
            super(itemView);
            img_logo = (ImageView) itemView.findViewById(R.id.img_logo);
            tvname = (TextView) itemView.findViewById(R.id.blue_name_tv);
            tvmac = (TextView) itemView.findViewById(R.id.snmac_tv);
            tvssi = (TextView) itemView.findViewById(R.id.rssi_tv);
            bind_btn = (CircularProgressButton) itemView.findViewById(R.id.bind_btn);

        }
    }

    public interface OnBindClickListener{
        void doBindClick(int position);
    }
}
