package com.example.bozhilun.android.adpter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.RssiBluetoothDevice;

import java.util.List;

/**
 * Created by thinkpad on 2016/6/30.
 */
public class BlueServiceAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RssiBluetoothDevice> datas;

    private Context context;

    static ClickListener clickListener;

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public BlueServiceAdpter(List<RssiBluetoothDevice> datas, Context context) {
        this.context = context;
        this.datas = datas;
    }

    public void updateView(List<RssiBluetoothDevice> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_bluedevice, viewGroup, false);
        return new ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RssiBluetoothDevice rssiBluetoothDevice = datas.get(position);
        final BluetoothDevice bluetoothDevice = rssiBluetoothDevice.getBluetoothDevice();
        if(bluetoothDevice != null){
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.blue_name_tv.setText(bluetoothDevice.getName());
            viewHolder.snmac_tv.setText(bluetoothDevice.getAddress());
            viewHolder.rssi_tv.setText("" + rssiBluetoothDevice.getRessi());
            if(bluetoothDevice.getName().equals("bozlun")){     //Bozlun的图标显示
                ((ViewHolder) holder).img_logo.setImageResource(R.mipmap.equipment_watch_picture);
            }else if(bluetoothDevice.getName().equals("B15P")){
                ((ViewHolder) holder).img_logo.setImageResource(R.mipmap.b15p_xiaotu);
            }else if("B18I".equals(bluetoothDevice.getName().substring(0,4))){    //B18I的图标
                ((ViewHolder) holder).img_logo.setImageResource(R.mipmap.icon_b18i_scanshow);
            }else if("W06X".equals(bluetoothDevice.getName().substring(0,4))){  //H9手表图标
                ((ViewHolder) holder).img_logo.setImageResource(R.mipmap.icon_h9_device);
            }
            else{   //B15S
                ((ViewHolder) holder).img_logo.setImageResource(R.mipmap.b15s_xiaotu);
            }

            viewHolder.bind_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onItemClick(position, view);
                    }

                }
            });
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView blue_name_tv, snmac_tv, rssi_tv;
        public ImageView img_logo;
        public CircularProgressButton bind_btn;

        public ViewHolder(View view) {
            super(view);
            img_logo = (ImageView) view.findViewById(R.id.img_logo);
            bind_btn = (CircularProgressButton) view.findViewById(R.id.bind_btn);
            blue_name_tv = (TextView) view.findViewById(R.id.blue_name_tv);
            snmac_tv = (TextView) view.findViewById(R.id.snmac_tv);
            rssi_tv = (TextView) view.findViewById(R.id.rssi_tv);

        }
    }
}

