package cn.diaovision.omnicontrol.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;

import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator;

import cn.diaovision.omnicontrol.R;

/**
 * Created by liulingfeng on 2017/3/3.
 */

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

    //state color
    int statePreAlpha = 0;
    int stateCurAlpha = 255;
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
        p.setShadowLayer(30, 0, 0, Color.BLACK);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(w/2.0f, h/2.0f, r-10, p);

        //draw text
        p.clearShadowLayer();
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(10);
        p.setColor(Color.parseColor("#ffffff"));

        /*
        if (Integer.valueOf(c) > 10) {
            //divide the number into to digits
            int val = Integer.valueOf(c);
            String dg10 = String.valueOf((int) (val / 10));
            p.getTextBounds(dg10, 0, dg10.length(), txtBound);
            canvas.drawText(dg10, w / 2.0f - txtBound.width()*1.2f, baseline, p);
            String dg = String.valueOf(val % 10);
            p.getTextBounds(dg, 0, dg.length(), txtBound);
            canvas.drawText(dg10, w / 2.0f + txtBound.width()*0.2f, baseline, p);
        }
        else {
        }
        */

        canvas.drawText(c, w / 2.0f - txtBound.width() / 2.0f, baseline, p);

        //draw click indicator
        p.setShadowLayer(10, 0, 0, Color.BLACK);
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

        AttributeAnimation anime = AttributeAnimation.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(200);
        this.startAnimation(anime);
    }

    public void unselect(){
        clicked = false;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 255;
        clickIndicatorAlphaStop = 0;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        AttributeAnimation anime = AttributeAnimation.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(200);
        this.startAnimation(anime);
    }

    public void unselect(long duration){
        clicked = false;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 255;
        clickIndicatorAlphaStop = 0;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        AttributeAnimation anime = AttributeAnimation.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(duration);
        this.startAnimation(anime);
    }
    public void select(){
        clicked = true;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 0;
        clickIndicatorAlphaStop = 255;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        AttributeAnimation anime = AttributeAnimation.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(200);
        this.startAnimation(anime);
    }

    public void select(long duration){
        clicked = true;

        int clickIndicatorAlphaStop = 0;
        int clickIndicatorAlphaStart = 0;
        //create a click transition animation

        clickIndicatorAlphaStart = 0;
        clickIndicatorAlphaStop = 255;
        clickIndicatorColor = Color.parseColor("#57d2f7");

        AttributeAnimation anime = AttributeAnimation.ofInt(this, "clickIndicatorAlpha", clickIndicatorAlphaStart, clickIndicatorAlphaStop);
        anime.setDuration(duration);
        this.startAnimation(anime);
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

}
