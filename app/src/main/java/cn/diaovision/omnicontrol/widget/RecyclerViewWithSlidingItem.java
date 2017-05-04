package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by TaoYimin on 2017/5/4.
 */
public class RecyclerViewWithSlidingItem extends RecyclerView {

    private boolean isFirst;

    private DefaultItemTouchHelper mDefaultItemTouchHelper;

    public RecyclerViewWithSlidingItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag) {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.setLongPressDragEnabled(canDrag);
    }

    private void initializeItemTouchHelper() {
        if (mDefaultItemTouchHelper == null) {
            mDefaultItemTouchHelper = new DefaultItemTouchHelper();
            mDefaultItemTouchHelper.attachToRecyclerView(this);
        }
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param onItemMoveListener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener) {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.setOnItemMoveListener(onItemMoveListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isFirst = true;
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ViewGroup convertView = (ViewGroup) getChildAt(i);
            SlidingItemView itemview = (SlidingItemView) convertView.getTag(convertView.getId());
            if (itemview.isOpen()) {
                return false;
            }
        }
        boolean flag = super.onInterceptTouchEvent(ev);
        if (flag) {
            if (isFirst) {
                for (int i = 0; i < count; i++) {
                    ViewGroup convertView = (ViewGroup) getChildAt(i);
                    if(convertView==null){
                        return flag;
                    }
                    SlidingItemView itemview = (SlidingItemView) convertView.getTag(convertView.getId());
                    if (itemview.getScrollX() != 0) {
                        itemview.scrollToEnd(null, 0, 200);
                    }
                }
                isFirst = false;
            }
        }
        return flag;
    }
}
