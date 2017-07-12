package cn.diaovision.omnicontrol.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by TaoYimin on 2017/5/5.
 */

public class TermItemTouchCallback extends ItemTouchHelper.Callback {
    private OnDragListener onDragListener;

    public TermItemTouchCallback() {
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //可以上下左右拖拽
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        //不需要滑动
        final int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        //拖动过程中不需要交换item位置，直接返回false
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //不需要滑动删除效果
    }

    @Override
    public boolean isLongPressDragEnabled() {
        //需要根据item的位置判断是否可以拖拽，所以返回false
        return false;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //拖拽结束后，将完成拖拽的viewHolder回调出去
        if (onDragListener != null) {
            onDragListener.onFinishDrag(viewHolder);
        }
    }

    /*设置拖拽完成的回调*/
    public TermItemTouchCallback setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
        return this;
    }

    /*拖拽完成的回调接口*/
    public interface OnDragListener {
        void onFinishDrag(RecyclerView.ViewHolder viewHolder);
    }
}
