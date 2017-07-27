package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by TaoYimin on 2017/7/27.
 */

public class MyDrawLayout extends DrawerLayout{
    public MyDrawLayout(Context context) {
        super(context);
    }

    public MyDrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //不消费触摸事件，继续向下传递给下层的RecyclerView
        return false;
    }
}
