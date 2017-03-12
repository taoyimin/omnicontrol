package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsSeekBar;
import android.widget.SeekBar;

/**
 * A vertical seek bar for volume control
 * Created by liulingfeng on 2017/2/28.
 */

public class VolumeBar extends AppCompatSeekBar{
    public VolumeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        Log.i("<UI>", "<UI> height " + getHeight());

        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                Log.i("<UI>", "<UI> x,y=" + event.getX() + " " + event.getY());
                trackTouchEvent(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void trackTouchEvent(MotionEvent event){
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int height = getHeight();
        int width = getWidth();
        int availableY = height - paddingLeft - paddingRight;
        int availableX = width - paddingBottom - paddingTop;
        int y = (int) event.getY();
        int x = (int) event.getX();
        float value = y + paddingRight;
        float scale;

        if (value < 0) {
            scale = 0.0f;
        }
        else if (value > availableY) {
            scale = 1.0f;
        }
        else {
            scale = value / (float) availableY;
        }
        int max = getMax();
        float progress = scale*max;
        Drawable thumb = getThumb();
        Rect rect = thumb.getBounds();

        int bTop = -1;
        int bBtm = -1;
        int bLft = rect.left;
        int bRgt = rect.right;
        if (value > availableY){
            bTop = paddingRight;
            bBtm = bTop - thumb.getIntrinsicHeight();
        }
        else if (value < 0){
            bBtm = height - paddingLeft;
            bTop = bBtm-thumb.getIntrinsicHeight();
        }
        else {
            bTop = (int) (value + paddingRight);
            bBtm = (int) (value + paddingRight);
        }

        thumb.setBounds(bLft, bTop, bRgt, bBtm);

//        postInvalidate();
        super.setProgress((int) progress);
        onSizeChanged(getWidth(),getHeight(), 0, 0);
        Log.i("UI", "UI progress" + progress + " x,y" + x + " " + y);


    }




}

