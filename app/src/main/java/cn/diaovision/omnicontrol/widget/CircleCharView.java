package cn.diaovision.omnicontrol.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by liulingfeng on 2017/3/3.
 */
@Deprecated
public class CircleCharView extends View {
    public static final int ON = 0;
    public static final int OFF = 1;
    public static final int ERR = 2;
    public static final int NA = 3;

    String c;
    Paint p;

    boolean clicked;


    int preState = 0;
    int state = 0;

    //click color
    int clickIndicatorAlpha = 0;
    int clickIndicatorColor = Color.parseColor("#ffffff");

    public CircleCharView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setAntiAlias(true);

        //code to retrieve attrs from layout xml
        int attrsArray[] = new int[1];
        attrsArray[0] = android.R.attr.text;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, attrsArray);
        c = typedArray.getString(0);
        if (c == null)
            c = "";

        clicked = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        //circle radius
        float r = w > h ? h/2 : w/2;
        r -= 20;

        //this line set the baseline and the textsize
        p.setTextSize(r);
        p.setTextAlign(Paint.Align.LEFT);
        Rect txtBound = new Rect();
        p.getTextBounds(c, 0, c.length(), txtBound);
        Paint.FontMetrics metrics = p.getFontMetrics();
        float baseline = (getMeasuredHeight() - metrics.bottom + metrics.top)/2-metrics.top;

        //draw circle state
        switch(state){
            case ON:
                p.setColor(Color.parseColor("#84f757"));
                break;
            case OFF:
                p.setColor(Color.parseColor("#34352c"));
                break;
            case ERR:
                p.setColor(Color.parseColor("#eb3f2f"));
                break;
            case NA:
                p.setColor(Color.parseColor("#c9cabb"));
                break;
            default:
                p.setColor(Color.parseColor("#c9cabb"));
                break;
        }
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w/2.0f, h/2.0f, r-10, p);

        //draw text
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(10);
        p.setColor(Color.parseColor("#ffffff"));

        if (Integer.valueOf(c) >= 10) {
            //divide the number into to digits
            canvas.drawText(c, w / 2.0f - txtBound.width()*(0.5f), baseline, p);
        }
        else {
            canvas.drawText(c, w / 2.0f - txtBound.width() / 2.0f, baseline, p);
        }


        //draw click indicator
        p.setColor(clickIndicatorColor);
        p.setAlpha(clickIndicatorAlpha);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(20);


        canvas.drawCircle(w / 2.0f, h / 2.0f, r, p);
    }

    public void click(){
        clicked = !clicked;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation
        if (clicked) {
            clickIndicatorAlphaStart = 0;
            clickIndicatorAlphaStop = 255;
            clickIndicatorColor = Color.parseColor("#57d2f7");
        }
        else{
            clickIndicatorAlphaStart = 255;
            clickIndicatorAlphaStop = 0;
            clickIndicatorColor = Color.parseColor("#57d2f7");
        }

        ObjectAnimator anime = ObjectAnimator.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(200);
        anime.start();
    }

    public void unselect(){
        Log.i("info","unselect");
        clicked = false;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        //create a click transition animation
        int clickIndicatorAlphaStart = 255;
        int clickIndicatorAlphaStop = 0;
        ObjectAnimator anime = ObjectAnimator.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(0);
        anime.start();
    }

    public void unselect(long duration){
        Log.i("info","unselectduration");
        clicked = false;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 255;
        clickIndicatorAlphaStop = 0;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        ObjectAnimator anime = ObjectAnimator.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(duration);
        anime.start();
    }
    public void select(){
        Log.i("info","select");
        clicked = true;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 0;
        clickIndicatorAlphaStop = 255;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        ObjectAnimator anime = ObjectAnimator.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(0);
        anime.start();
    }

    public void select(long duration){
        Log.i("info","selectduration");
        clicked = true;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 0;
        clickIndicatorAlphaStop = 255;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        ObjectAnimator anime = ObjectAnimator.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(duration);
        anime.start();
    }

    public void setChar(String c){
        this.c = c;
        this.postInvalidate();
    }

    public void changeState(int state){
        if (state >= 0 && state < 4){
            if (this.state == state) return;

            this.preState = this.state; //backup preState
            this.state = state;
            this.postInvalidate();
        }
    }

    public int getClickIndicatorAlpha() {
        return clickIndicatorAlpha;
    }

    public void setClickIndicatorAlpha(int clickIndicatorAlpha) {
        this.clickIndicatorAlpha = clickIndicatorAlpha;
        //ObjectAnimator does not call invalidate when set the property value
        //manually call it in setter
        this.postInvalidate();
    }
}
