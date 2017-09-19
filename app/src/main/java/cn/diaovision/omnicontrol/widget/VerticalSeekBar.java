package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by TaoYimin on 2017/6/13.
 * 只能拖动调整进度，不能点击调整进度的VerticalSeekBar(为了解决与HorizontalScrollView的滑动冲突)
 */

public class VerticalSeekBar extends com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar {

    //存放拖块的矩形
    private Rect mRect;

    //禁止点击的时候有进度效果
    private boolean isBanClick = true;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 得到拖块图片
        Drawable drawable = this.getThumb();
        // 得到放拖快图片的矩形。
        mRect = drawable.getBounds();
/*        int height=Math.abs(mRect.right-mRect.left);
        int width=Math.abs(mRect.bottom=mRect.top);
        mRect.left=mRect.left-width/2;
        mRect.right=mRect.right+width/2;
        mRect.top=mRect.top-height/2;
        mRect.bottom=mRect.bottom+height/2;*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isBanClick) {
                    return banAction(x, y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean banAction(float x, float y) {
        if (mRect != null) {
/*            if (mRect.contains((int) (x), (int) (y))) {
                Log.i("info","true");
                return true;
            } else {
                return false;
            }*/
            if(Math.abs(x-mRect.centerX())<mRect.width()&&Math.abs(y-mRect.centerY())<mRect.height()){
                return true;
            }else{
                return false;
            }
        } else {
            return true;
        }
    }
}
