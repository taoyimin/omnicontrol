package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v4.widget.DrawerLayout;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.diaovision.omnicontrol.R;

/**
 * Created by TaoYimin on 2017/5/23.
 */

public class AssistDrawerLayout extends DrawerLayout {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer)
    View drawer;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.root_view)
    FrameLayout rootView;
    @BindView(R.id.set_channel)
    Button setChannel;

    OnEditCompleteListener onEditCompleteListener;

    public static final int MODE_1XN = 0; //正常显示
    public static final int MODE_2X1 = 1; //一行两列拼接显示
    public static final int MODE_2X2 = 2; //两行两列拼接显示
    public static final int MODE_3X3 = 3; //三行三列拼接显示

    public int mode = 0;

    public AssistDrawerLayout(Context context) {
        this(context, null);
    }

    public AssistDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AssistDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        final View view = View.inflate(context, R.layout.layout_drawer, this);
        ButterKnife.bind(this, view);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setChannel = (Button) view.findViewById(R.id.set_channel);
        setChannel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditCompleteListener != null) {
                    onEditCompleteListener.onComplete(mode);
                }
            }
        });
        final Scene scene1 = Scene.getSceneForLayout(rootView, R.layout.layout_scene1, context);
        final Scene scene2 = Scene.getSceneForLayout(rootView, R.layout.layout_scene2, context);
        final Scene scene3 = Scene.getSceneForLayout(rootView, R.layout.layout_scene3, context);
        final Scene scene4 = Scene.getSceneForLayout(rootView, R.layout.layout_scene4, context);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_btn1:
                        mode = AssistDrawerLayout.MODE_1XN;
                        TransitionManager.go(scene1);
                        break;
                    case R.id.radio_btn2:
                        mode = AssistDrawerLayout.MODE_2X1;
                        TransitionManager.go(scene2);
                        break;
                    case R.id.radio_btn3:
                        mode = AssistDrawerLayout.MODE_2X2;
                        TransitionManager.go(scene3);
                        break;
                    case R.id.radio_btn4:
                        mode = AssistDrawerLayout.MODE_3X3;
                        TransitionManager.go(scene4);
                        break;
                    default:
                        break;
                }
                setChannel = (Button) view.findViewById(R.id.set_channel);
                setChannel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onEditCompleteListener != null) {
                            onEditCompleteListener.onComplete(mode);
                        }
                    }
                });
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        radioGroup.getChildAt(mode).performClick();
    }

    public interface OnEditCompleteListener {
        void onComplete(int mode);
    }

    public void setOnEditCompleteListener(OnEditCompleteListener onEditCompleteListener) {
        this.onEditCompleteListener = onEditCompleteListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //不消费触摸事件，继续向下传递给下层的RecyclerView
        return false;
    }
}
