package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.diaovision.omnicontrol.R;

/**
 * Created by liulingfeng on 2017/4/7.
 */

public class DirectionPad extends RelativeLayout {
    int h = -1;
    int w = -1;

    float preX = -1;
    float preY = -1;
    long lastMove;


    ImageView handler;


    OnMoveListener onMoveListener;
    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    public DirectionPad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        handler = new ImageView(getContext());
        handler.setImageResource(R.drawable.handle_round);
        handler.setBackgroundResource(R.color.transparent);
        addView(handler);
        LayoutParams layoutParams = (LayoutParams) handler.getLayoutParams();
        layoutParams.addRule(CENTER_IN_PARENT);
        handler.setLayoutParams(layoutParams);

        this.setClickable(true);

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                h = getHeight();
                w = getWidth();
                LayoutParams layoutParams = (RelativeLayout.LayoutParams) handler.getLayoutParams();
                layoutParams.height = w / 2;
                layoutParams.width = w / 2;
                handler.setLayoutParams(layoutParams);

                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(listener);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (h < 0 || w < 0) {
                    return false;
                }

                float x = event.getX();
                float y = event.getY();

                float hw = handler.getWidth();
                float hh = handler.getHeight();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        preX = x;
                        preY = y;

                        if (preX+hw/2 > w) {
                            handler.setTranslationX(w/2 - hw/2);
                        }
                        else if (preX - hw/2 < 0){
                            handler.setTranslationX(hw/2 - w/2);
                        }
                        else{
                            handler.setTranslationX(preX - w/2);
                        }

                        if (preY+hh/2 > h) {
                            handler.setTranslationY(h/2 - hh/2);
                        }
                        else if (preY - hh/2 < 0){
                            handler.setTranslationY(hh/2 - h/2);
                        }
                        else{
                            handler.setTranslationY(preY - h/2);
                        }

                        if (onMoveListener != null){
                            lastMove = System.currentTimeMillis();
                            int deg = getQuatreDirection(preX, preY);
                            int[] velo = getVelocity(preX, preY);
                            onMoveListener.onMove(deg, velo[1]);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        preX = event.getX();
                        preY = event.getY();

                        if (preX+hw/2 > w) {
                            handler.setTranslationX(w/2 - hw/2);
                        }
                        else if (preX - hw/2 < 0){
                            handler.setTranslationX(hw/2 - w/2);
                        }
                        else{
                            handler.setTranslationX(preX - w/2);
                        }


                        if (preY+hh/2 > h) {
                            handler.setTranslationY(h/2 - hh/2);
                        }
                        else if (preY - hh/2 < 0){
                            handler.setTranslationY(hh/2 - h/2);
                        }
                        else{
                            handler.setTranslationY(preY - h/2);
                        }

                        int deg = getQuatreDirection(preX, preY);
                        int[] velo = getVelocity(preX, preY);

                        //trigger the onMove every 200ms
                        if (System.currentTimeMillis() - lastMove  > 200) {
                            if (onMoveListener != null){
                                onMoveListener.onMove(deg, velo[1]);
                            }
                            lastMove = System.currentTimeMillis();
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_HOVER_EXIT:
                    case MotionEvent.ACTION_OUTSIDE:
                        preX = 0;
                        preY = 0;
                        handler.setTranslationX(0);
                        handler.setTranslationY(0);
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
        float ddy = Math.abs(cy - dy);
        float dddx = dx-cx;
        float dddy = cy-dy;

        int deg = 0;
        if (ddx > ddy  && dddx >= 0) {
            deg = 0;
        }
        else if (ddx > ddy  && dddx < 0) {
            deg = 180;
        }
        else if (ddx <= ddy  && dddy >= 0) {
            deg = 90;
        }
        else if (ddx <= ddy  && dddy < 0) {
            deg = 270;
        }
        return deg;
    }

    /*return accurate direction*/
    int getDirection(float dx, float dy){
        float cx = w/2;
        float cy = h/2;

        float ddx = dx - cx;
        float ddy = cy - dy;
        float dd = (float) Math.sqrt(ddx*ddx + ddy*ddy);
        int deg = (int) (Math.acos(ddx/dd)/Math.PI*180);
        if (ddy < 0){
            deg = 360 - deg;
        }
        return deg;
    }

    /*return accurate direction + speed*/
    int[] getVelocity(float dx, float dy){
        int[] velo = new int[2];
        float cx = w/2;
        float cy = h/2;

        float ddx = dx - cx;
        float ddy = cy - dy;
        float dd = (float) Math.sqrt(ddx*ddx + ddy*ddy);
        int deg = (int) (Math.acos(ddx/dd)/Math.PI*180);
        if (ddy < 0){
            deg = 360 - deg;
        }

        velo[0] = deg;

        if (dd > w/2 || dd > h/2){
            velo[1] = 100;
        }
        else {
            velo[1] = (int) (dd / Math.min(w/2, h/2) * 100);
        }

        return velo;
    }

    public interface OnMoveListener{
        void onMove(int deg, int velo);
    }
}
