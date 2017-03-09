package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * A vertical seek bar for volume control
 * Created by liulingfeng on 2017/2/28.
 */

public class VolumeBar extends SeekBar{
    public VolumeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;
        }
        return super.onTouchEvent(event);
    }
}

