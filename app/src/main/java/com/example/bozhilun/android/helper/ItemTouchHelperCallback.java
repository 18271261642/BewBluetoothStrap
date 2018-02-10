package com.example.bozhilun.android.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.bozhilun.android.util.MyLogUtil;

/**
 * Created by wyl on 2017/9/8.
 *
 * 使用ItemTouchHelper需要实现ItemTouchHelper.Callback接口
 * 通过该接口对move和swipe事件进行监听、也可以控制View的选择状态和覆盖默认动画
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter adapter;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 返回true，开启长按拖拽
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 返回true，开启swipe事件
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * getMovementFlags、onMove、onSwiped是必须要实现的三个方法
     *
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        /**
         * ItemTouchHelper支持事件方向判断，但是必须重写当前getMovementFlags来指定支持的方向
         * 这里我同时设置了dragFlag为上下左右四个方向，swipeFlag的左右方向
         * 最后通过makeMovementFlags（dragFlag，swipe）创建方向的Flag，
         * 因为我们目前只需要实现拖拽，所以我并未创建swipe的flag
         */
        int dragFlag = ItemTouchHelper.UP| ItemTouchHelper.DOWN| ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT;
        int swipe = ItemTouchHelper.START| ItemTouchHelper.END;
        return makeMovementFlags(dragFlag,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        /**
         * 回调
         */
        adapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        /**
         * 回调
         */
       // Log.d("POSITION",viewHolder.getAdapterPosition()+"-------"+viewHolder.getAdapterPosition());
        adapter.onItemDismiss(viewHolder.getAdapterPosition());

    }
}
