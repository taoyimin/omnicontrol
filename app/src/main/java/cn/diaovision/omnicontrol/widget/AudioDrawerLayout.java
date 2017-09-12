package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;

/**
 * Created by TaoYimin on 2017/5/23.
 */

public class AudioDrawerLayout extends DrawerLayout {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer)
    View drawer;
    @BindView(R.id.set_channel)
    Button setChannel;

    OnEditCompleteListener onEditCompleteListener;

    public AudioDrawerLayout(Context context) {
        this(context, null);
    }

    public AudioDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        final View view = View.inflate(context, R.layout.layout_audio_drawer, this);
        ButterKnife.bind(this, view);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setChannel = (Button) view.findViewById(R.id.set_channel);
        setChannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditCompleteListener != null) {
                    onEditCompleteListener.onComplete();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void openDrawer() {
        if (!drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.openDrawer(drawer);
        }
    }

    public void closeDrawer() {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        }
    }

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(drawer);
    }

    public interface OnEditCompleteListener {
        void onComplete();
    }

    public void setOnEditCompleteListener(OnEditCompleteListener onEditCompleteListener) {
        this.onEditCompleteListener = onEditCompleteListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //该控件覆盖住显示屏左侧辅助屏，导致辅助屏控件无法接收触摸事件
        //不消费触摸事件，继续向下传递给下层的RecyclerView
        return false;
    }
}
