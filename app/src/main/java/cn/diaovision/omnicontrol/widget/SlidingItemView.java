package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.core.model.conference.Term;

/**
 * Created by TaoYimin on 2017/5/4.
 */
public class SlidingItemView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private Scroller mScroller;

    private float downX, dispatchDownX, dispatchDownY;

    private boolean isOpen;
    private boolean canDrag = true;
    private boolean open = false;
    private boolean lastOpen = false;

    private VelocityTracker mVelocityTracker;

    private View convertView;

    private View hideView;

    private int position;

    private List<Term> list;

    public OnHideViewClickListener onHideViewClickListener;

    //手指是否up
    private boolean isActionUp = false;

    private int hideViewWidth;

    private int hideViewMode;

    public SlidingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mScroller = new Scroller(context);
    }

    public void setHideView(int hideViewMode) {
        this.hideViewMode = hideViewMode;
        if (hideView == null) {
            createHideView();
            switch (hideViewMode) {
                case HideViewMode.MODE_HIDE_BOTTOM:

                   // if (hideView == null) {
                        if (convertView instanceof RelativeLayout) {
                            ((RelativeLayout) convertView).addView(hideView);
                            LayoutParams params = (LayoutParams) hideView
                                    .getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            hideView.setLayoutParams(params);
                            bringToFront();
                        }
                   // }

                    break;
                case HideViewMode.MODE_HIDE_RIGHT:
                    View container = getChildAt(0);
                    addView(hideView);
                    RelativeLayout.LayoutParams params = (LayoutParams) hideView
                            .getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    hideView.setLayoutParams(params);

                    params = (LayoutParams) container.getLayoutParams();
                    params.rightMargin = hideViewWidth;
                    container.setLayoutParams(params);

                    ViewGroup.LayoutParams viewGroupLp = getLayoutParams();
                    if (viewGroupLp instanceof LinearLayout.LayoutParams) {
                        ((LinearLayout.LayoutParams) viewGroupLp).rightMargin = -hideViewWidth;
                        setLayoutParams(viewGroupLp);
                    }
                    if (viewGroupLp instanceof RelativeLayout.LayoutParams) {
                        ((RelativeLayout.LayoutParams) viewGroupLp).rightMargin = -hideViewWidth;
                        setLayoutParams(viewGroupLp);
                    }
                    if (viewGroupLp instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) viewGroupLp).rightMargin = -hideViewWidth;
                        setLayoutParams(viewGroupLp);
                    }
                    break;
            }
        }
    }

    public View getHideView() {
        return hideView;
    }

    private void createHideView() {
        this.hideView = LayoutInflater.from(getContext()).inflate(R.layout.layout_hide, this, false);
        if (hideViewMode == HideViewMode.MODE_HIDE_RIGHT) {
            hideViewWidth = dip2px(context, 204);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    hideViewWidth, LayoutParams.MATCH_PARENT);
            hideView.setLayoutParams(params);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dispatchDownX = event.getX();
                dispatchDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(dispatchDownX - event.getX()) < 10
                        && Math.abs(dispatchDownY - event.getY()) < 10) {
                    View parent = (View) convertView.getParent();
                    if (canRemove()) {
                        int width = hideViewWidth;

                        if (hideViewMode == HideViewMode.MODE_HIDE_RIGHT) {
                            width = hideViewWidth * 2;
                        } else if (hideViewMode == HideViewMode.MODE_HIDE_BOTTOM) {
                            width = hideViewWidth;
                        }

                        if (dispatchDownX < getWidth() - width) {
                            ViewGroup viewGroup = (ViewGroup) parent;
                            //int count = viewGroup.getChildCount();
                            int count = viewGroup.getChildCount() - 1;
                            for (int i = 0; i < count; i++) {
                                View convertView = viewGroup.getChildAt(i);
                                SlidingItemView slidingItemView = (SlidingItemView) convertView
                                        .getTag(convertView.getId());
                                if (slidingItemView.canRemove()
                                        && slidingItemView != this) {
                                    slidingItemView.scrollToEnd(null, 0, 200);
                                }
                            }
                        }
                        return super.dispatchTouchEvent(event);
                    }

                    ViewGroup viewGroup = (ViewGroup) parent;
                    //int count = viewGroup.getChildCount();
                    int count = viewGroup.getChildCount() - 1;
                    boolean isExistOpenItem = false;
                    for (int i = 0; i < count; i++) {
                        View convertView = viewGroup.getChildAt(i);
                        SlidingItemView slidingItemView = (SlidingItemView) convertView
                                .getTag(convertView.getId());
                        if (slidingItemView.canRemove()) {
                            isExistOpenItem = true;
                            slidingItemView.scrollToEnd(null, 0, 200);
                        }
                    }
                    if (!isExistOpenItem) {
                        convertView.performClick();
                    }
                    return false;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX() + getScrollX();
                if (canRemove()
                        && event.getX() > getWidth() - hideView.getWidth()) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int distance = (int) (event.getX() - downX);
                if (getScrollX() > hideView.getWidth() / 10) {
                    isOpen = true;
                }
                // 当左移,滑动操作被禁用
                if (distance > 0) {
                    scrollTo(0, 0);
                    return false;
                } else if (distance < -hideView.getWidth()) {
                    scrollTo(hideView.getWidth(), 0);
                    return false;
                }
                scrollTo(-distance, 0);
                break;
            case MotionEvent.ACTION_UP:
                // 当右滑距离超过item控件宽度的一半就将其移除,否则还原
                if (Math.abs(getScrollX()) > hideView.getWidth() / 2) {
                    scrollToEnd(mVelocityTracker, hideView.getWidth(), 200);
                } else {
                    scrollToEnd(mVelocityTracker, 0, 200);
                }
                isOpen = false;
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    public void bindViewAndData(final View convertView, final List<Term> list, int hideViewMode, final int position) {
        scrollTo(0, 0);
        this.list = list;
        this.convertView = convertView;
        this.convertView.setTag(convertView.getId(), this);
        setHideView(hideViewMode);
        this.position = position;
        View btn1 = this.hideView.findViewById(R.id.button1);
        View btn2 = this.hideView.findViewById(R.id.button2);
        View btn3 = this.hideView.findViewById(R.id.button3);
/*        Term term=list.get(position);
        if(term.isSpeaking()){
            btn1.setText("取消\n发言");
        }else{
            btn1.setText("点名\n发言");
        }
        if(term.isMuted()){
            btn2.setText("取消\n静音");
        }else{
            btn2.setText("设置\n静音");
        }*/
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    public void scrollToEnd(VelocityTracker velocityTracker, int end,
                            int duration) {
        isActionUp = true;
        // 当前的偏移位置
        int scrollX = getScrollX();
        int velocityX = 0;
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1000);
            velocityX = (int) velocityTracker.getXVelocity();
        }
        int distance = 0;
        if (velocityX > 1000) {
            distance = -scrollX;
        } else if (velocityX < -1000) {
            distance = end - scrollX;
        } else {
            distance = end - scrollX;
        }
        mScroller.startScroll(scrollX, 0, distance, 0, duration);

        if (distance < 0) {
            if (open) {
                canDrag = false;
            } else {
                canDrag = true;
            }
            lastOpen = open;
            open = false;
        } else if (distance > 0) {
            canDrag = false;
            lastOpen = open;
            open = true;
        } else {
            lastOpen = open;
            open = !open;
            canDrag = false;
        }
        if (distance == -hideView.getWidth()) {
            canDrag = true;
        }
        //Log.i("info","distance="+distance+"上一次状态："+lastOpen+"这次状态:"+open+"能否拖拽："+canDrag);
        // 此时需要手动刷新View 否则没效果
        invalidate();
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    @Override
    public void computeScroll() {
        // 如果返回true，表示动画还没有结束
        // 因为前面startScroll，所以只有在startScroll完成时 才会为false
        if (mScroller.computeScrollOffset() && isActionUp) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            // 产生了动画效果 每次滚动一点
            scrollTo(x, y);
            // 刷新View 否则效果可能有误差
            postInvalidate();
        } else {
            if (isActionUp) {
                isActionUp = false;
            }
        }
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getPosition() {
        return position;
    }

    public boolean canRemove() {
        if (getScrollX() > hideView.getWidth() - 15) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        try {
            List<View> views = new ArrayList<>();
            ViewGroup parent = (ViewGroup) convertView.getParent();
            //int count = parent.getChildCount();
            int count = parent.getChildCount() - 1;
            for (int i = count - 1; i >= 0; i--) {
                View childView = parent.getChildAt(i);
                SlidingItemView slidingItemView = (SlidingItemView) childView
                        .getTag(childView.getId());
                if (slidingItemView.canRemove()) {
                    views.add(childView);
                    int pos = ((RecyclerView) parent)
                            .getChildViewHolder(childView)
                            .getLayoutPosition();
                    // 执行按钮的点击事件
                    if (slidingItemView.onHideViewClickListener != null) {
                        switch (v.getId()) {
                            case R.id.button1:
                                slidingItemView.onHideViewClickListener
                                        .onClick1(slidingItemView, pos);
                                slidingItemView.scrollToEnd(null, 0, 200);
                                (((RecyclerView) parent).getAdapter())
                                        .notifyItemChanged(pos);
                                break;
                            case R.id.button2:
                                slidingItemView.onHideViewClickListener
                                        .onClick2(slidingItemView, pos);
                                slidingItemView.scrollToEnd(null, 0, 200);
                                (((RecyclerView) parent).getAdapter())
                                        .notifyItemChanged(pos);
                                break;
                            case R.id.button3:
                                slidingItemView.onHideViewClickListener
                                        .onClick3(slidingItemView, pos);
                                list.remove(pos);
                                (((RecyclerView) parent).getAdapter())
                                        .notifyItemRemoved(pos);
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnHideViewClickListener {
        void onClick1(View view, int position);

        void onClick2(View view, int position);

        void onClick3(View view, int position);
    }

    public void setOnHideViewClickListener(
            OnHideViewClickListener onHideViewClickListener) {
        this.onHideViewClickListener = onHideViewClickListener;
    }

    public interface HideViewMode {
        int MODE_HIDE_BOTTOM = 0;

        int MODE_HIDE_RIGHT = 1;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

/*    public class DoNotRemoveDataException extends RuntimeException {
        public DoNotRemoveDataException(String detailMessage) {
            super(detailMessage);
        }
    }*/
}
