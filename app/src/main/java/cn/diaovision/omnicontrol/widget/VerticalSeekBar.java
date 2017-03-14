package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ProgressBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A vertical seek bar inspired from
 *
 * Created by liulingfeng on 2017/2/28.
 */

@Deprecated
public class VerticalSeekBar extends AppCompatSeekBar{
    private Drawable thumb;
    private boolean isDragging = false;
    private Method methodSetProgressFromUser;

    public VerticalSeekBar(Context context){
        super(context);
    }
    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);

        super.onDraw(canvas);
    }

    @Override
    public void setThumb(Drawable thumb) {
        this.thumb = thumb;
        super.setThumb(thumb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                isDragging = true;
                trackTouchEvent(event);
                tryClaimDragging(true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging){
                    trackTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    trackTouchEvent(event);
                    isDragging = false;
                    setPressed(false);
                }
                else {
                    isDragging = true;
                    trackTouchEvent(event);
                    isDragging = false;
                    tryClaimDragging(false);
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isDragging){
                    isDragging = false;
                    setPressed(false);
                }
                break;
            default:
                break;
        }

        return true;
    }


    private void trackTouchEvent(MotionEvent event){
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int height = getHeight();
        int availableY = height - paddingLeft - paddingRight;
        int y = (int) event.getY();

        float value = availableY - y + paddingRight;

        float scale;

        if (value < 0 || availableY == 0) {
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

        setProgressFromUser((int) progress, true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isEnabled()) {
            final boolean handled;
            int direction = 0;

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    direction = 1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    direction = -1;
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    // move view focus to previous/next view
                    return false;
                default:
                    handled = false;
                    break;
            }

            if (handled) {
                final int keyProgressIncrement = getKeyProgressIncrement();
                int progress = getProgress();

                progress += (direction * keyProgressIncrement);

                if (progress >= 0 && progress <= getMax()) {
                    setProgressFromUser(progress, true);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void tryClaimDragging(boolean active){
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(active);
        }
    }

    private synchronized void setProgressFromUser(int progress, boolean fromUser){
        if (methodSetProgressFromUser == null){
            try {
                Method method;
                method = ProgressBar.class.getDeclaredMethod("setProgress", int.class, boolean.class);
                method.setAccessible(true);
                methodSetProgressFromUser = method;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (methodSetProgressFromUser != null){
            try {
                methodSetProgressFromUser.invoke(this, progress, fromUser);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        else {
            super.setProgress(progress);
        }
        refreshThumb();
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        refreshThumb();
    }

    private void refreshThumb(){
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(h, w, oldh, oldw);
    }
}


