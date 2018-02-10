package com.example.bozhilun.android.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.bozhilun.android.R;
import com.example.bozhilun.android.helper.ItemTouchHelperAdapter;
import com.example.bozhilun.android.util.MyLogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wyl 2017
 */
public class DragAdapter extends RecyclerView.Adapter<DragAdapter.DragViewHolder> implements ItemTouchHelperAdapter ,View.OnClickListener{

    private Context context;



    private LayoutInflater inflater;
    private List<HashMap<String, Object>> mList;
   public static List<HashMap<String, Object>> bmList;

    public DragAdapter(Context context) {
        this.context = context;
    }
    public DragAdapter(Context context, List<HashMap<String, Object>> list) {
        this.context = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
    }
    //define interface
    public static interface OnRecyclerViewItemClickListener {

        void onItemClick(View view ,String sss);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,String.valueOf( v.getTag()));
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    @Override
    public DragViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_item,null);
        DragViewHolder  holder = new DragViewHolder(view);
        view  .setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(DragViewHolder holder, int position) {
        holder.iconImg.setImageResource((Integer) mList.get(position).get("item_image"));
        holder.textView.setText((CharSequence) mList.get(position).get("item_text"));
        holder.itemView.setTag(mList.get(position).get("item_text"));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        /**
         * 拖拽后，切换位置，数据排序
         */
        Collections.swap(mList,fromPosition,toPosition);

        /**
         * 测试经过adapter以后dragList数据发生变化
         */
        for (Object list:mList ) {
            Log.d("dragList","拖拽排序前后的顺序"+list);
        }
        notifyItemMoved(fromPosition,toPosition);
        //赋值
        bmList = new ArrayList<HashMap<String, Object>>();
        bmList=mList;

        return true;

    }

    @Override
    public void onItemDismiss(int position) {
        /**
         * 移除之前的数据
         */
        mList.remove(position);
        notifyItemRemoved(position);
        System.out.print("POSITION"+position);

    }

    public static class DragViewHolder extends RecyclerView.ViewHolder{
        public  TextView textView;
        public ImageView iconImg;
        public DragViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.name_tv);
            iconImg= (ImageView) itemView.findViewById(R.id.icon_img);
        }
    }
}
