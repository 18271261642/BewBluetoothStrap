package com.example.bozhilun.android.siswatch.adapter;

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
import com.example.bozhilun.android.siswatch.bean.CustomBlueDevice;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 搜索页面适配器
 */
public class CustomBlueAdapter extends RecyclerView.Adapter<CustomBlueAdapter.CustomBlueViewHolder>{

    private List<CustomBlueDevice> customBlueDeviceList;
    private Context mContext;
    public OnSearchOnBindClickListener onBindClickListener;

    public void setOnBindClickListener(OnSearchOnBindClickListener onBindClickListener) {
        this.onBindClickListener = onBindClickListener;
    }

    public CustomBlueAdapter(List<CustomBlueDevice> customBlueDeviceList, Context mContext) {
        this.customBlueDeviceList = customBlueDeviceList;
        this.mContext = mContext;
    }

    @Override
    public CustomBlueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_bluedevice,null);
        return new CustomBlueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomBlueViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = customBlueDeviceList.get(position).getBluetoothDevice();
        if(bluetoothDevice != null){
            //蓝牙名称
            holder.bleNameTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getName());
            //mac地址
            holder.bleMacTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getAddress());
            //信号
            holder.bleRiisTv.setText(""+customBlueDeviceList.get(position).getRssi()+"");
            //展示图片
            String bleName = customBlueDeviceList.get(position).getBluetoothDevice().getName();
            if(!WatchUtils.isEmpty(bleName)){
                if(bleName.equals("B15P")){ //B15P手环
                    holder.img.setImageResource(R.mipmap.b15p_xiaotu);
                }else if(bleName.substring(0,3).equals("W06") || bleName.substring(0,2).equals("H9")){    //H9手表
                    holder.img.setImageResource(R.mipmap.h9_search);
                }
//                else if(bleName.substring(0,4).equals("B18I")){    //B18I手环 bzolun
//                    holder.img.setImageResource(R.mipmap.icon_b18i_scanshow);
//                }
                else {
                    if(customBlueDeviceList.get(position).getCompanyId() == 160
                            || customBlueDeviceList.get(position).getBluetoothDevice().getName().substring(0,2).equals("H8") ||
                            customBlueDeviceList.get(position).getBluetoothDevice().getName().substring(0,6).equals("bozlun")){   //H8手表
                        holder.img.setImageResource(R.mipmap.h8_search);
                    }
                }
            }

            //绑定按钮
            holder.circularProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onBindClickListener != null){
                        int position = holder.getLayoutPosition();
                        onBindClickListener.doBindOperator(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return customBlueDeviceList.size();
    }

    class CustomBlueViewHolder extends RecyclerView.ViewHolder{

        TextView bleNameTv,bleMacTv,bleRiisTv;
        ImageView img;  //显示手表或者手环图片
        CircularProgressButton circularProgressButton;

        public CustomBlueViewHolder(View itemView) {
            super(itemView);
            bleNameTv = (TextView) itemView.findViewById(R.id.blue_name_tv);
            bleMacTv = (TextView) itemView.findViewById(R.id.snmac_tv);
            bleRiisTv = (TextView) itemView.findViewById(R.id.rssi_tv);
            img = (ImageView) itemView.findViewById(R.id.img_logo);
            circularProgressButton = (CircularProgressButton) itemView.findViewById(R.id.bind_btn);
        }
    }

    public interface OnSearchOnBindClickListener{
        void doBindOperator(int position);
    }
}
