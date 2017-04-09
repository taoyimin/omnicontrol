package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import cn.diaovision.omnicontrol.R;

/**
 * Created by liulingfeng on 2017/4/7.
 */

public class DirectionPad extends RelativeLayout {
    int h = -1;
    int w = -1;

    public DirectionPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.widget_pad_direction, null);
        addView(v);

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                h = getHeight();
                w = getWidth();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(listener);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (h < 0 || w < 0) {
                    return false;
                }

                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        Log.i("A", "x = " + x + " y = " + y);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    case MotionEvent.ACTION_UP:
                        Log.i("A", "touch ended");
                        break;
                }
                return false;
            }
        });
    }

    /*return up(90), down(270), left(180), right(0) directions*/
    int getQuatreDirection(float dx, float dy){
        float cx = w / 2;
        float cy = h / 2;

        float ddx = Math.abs(dx - cx);
        float ddy = Math.abs(dy - cy);

        int deg = 0;
        if (ddx > ddy  && ddx >= 0) {
            deg = 0;
        }
        else if (ddx > ddy  && ddx < 0) {
            deg = 180;
        }
        else if (ddx <= ddy  && ddy >= 0) {
            deg = 90;
        }
        else if (ddx <= ddy  && ddy < 0) {
            deg = 270;
        }
        return deg;
    }

    /*return accurate direction*/
    int getDirection(float dx, float dy){
        float cx = w / 2;
        float cy = h / 2;
        float radi = Math.min(cx, cy);

        float ddx = dx - cx;
        float ddy = dy - cy;
        float dd = (float) Math.sqrt(ddx*ddx + ddy*ddy);
        int deg = (int) Math.round(Math.acos(ddx/dd));
        return deg;
    }

    /*return accurate direction + speed*/
    float[] getVelocity(float dx, float dy){


        float[] velo = new float[2];
        float cx = w / 2;
        float cy = h / 2;
        float radi = Math.min(cx, cy);

        float ddx = dx - cx;
        float ddy = dy - cy;
        float dd = (float) Math.sqrt(ddx*ddx + ddy*ddy);

        float deg = (float) Math.acos(ddx/dd);
        float speed = dd > radi ? 100 : dd/radi*100;

        velo[0] = deg;
        velo[1] = speed;
        return velo;
    }
}
