package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.diaovision.omnicontrol.R;

/**
 * Created by liulingfeng on 2017/3/18.
 */

public class PlayerControllerView extends RelativeLayout {
    static final int CMD_GO_UP = 0;
    static final int CMD_GO_DOWN = 1;
    static final int CMD_GO_LEFT = 2;
    static final int CMD_GO_RIGHT = 3;
    static final int CMD_GO_ZOOMIN = 4;
    static final int CMD_GO_ZOOMOUT = 5;
    static final int CMD_PLAY = 6;
    static final int CMD_PAUSE = 7;
    static final int CMD_BACKWARDS = 8;
    static final int CMD_FORWARDS = 9;

    boolean isPlaying = false;

    @BindView(R.id.btn_go_up)
    AppCompatImageButton btnGoUp;
    @BindView(R.id.btn_go_left)
    AppCompatImageButton btnGoLeft;
    @BindView(R.id.btn_go_right)
    AppCompatImageButton btnGoRight;
    @BindView(R.id.btn_go_down)
    AppCompatImageButton btnGoDown;

    @BindView(R.id.btn_play)
    AppCompatImageButton btnPlayPause;
    @BindView(R.id.btn_backwards)
    AppCompatImageButton btnBackwards;
    @BindView(R.id.btn_forwards)
    AppCompatImageButton btnForwards;
    @BindView(R.id.btn_zoomin)
    AppCompatImageButton btnZoomin;
    @BindView(R.id.btn_zoomout)
    AppCompatImageButton btnZoomout;

    OnControlListener controlListener;

    public PlayerControllerView(Context context) {
        super(context);
    }

    public PlayerControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View rootView = inflate(context, R.layout.widget_player_controller, this);
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public interface OnControlListener{
        void onControl(int cmd);
    }

    public void setOnControlListener(OnControlListener controlListener) {
        this.controlListener = controlListener;
    }

    @OnClick(R.id.btn_go_up)
    void onGoUp(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_UP);
        }
    }

    @OnClick(R.id.btn_go_down)
    void onGoDown(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_DOWN);
        }

    }

    @OnClick(R.id.btn_go_left)
    void onGoLeft(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_LEFT);
        }

    }

    @OnClick(R.id.btn_go_right)
    void onGoRight(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_RIGHT);
        }

    }

    @OnClick(R.id.btn_play)
    void onPlay(){
        if (isPlaying) {
            isPlaying = false;
            if (controlListener != null) {
                controlListener.onControl(CMD_PAUSE);
            }
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        }
        else {
            isPlaying = true;
            if (controlListener != null) {
                controlListener.onControl(CMD_PLAY);
            }
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    @OnClick(R.id.btn_backwards)
    void onBackwards(){
        if (controlListener != null){
            controlListener.onControl(CMD_BACKWARDS);
        }
    }

    @OnClick(R.id.btn_forwards)
    void onForwards(){
        if (controlListener != null){
            controlListener.onControl(CMD_FORWARDS);
        }
    }

    @OnClick(R.id.btn_zoomin)
    void onZoomin(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_ZOOMIN);
        }
    }

    @OnClick(R.id.btn_zoomout)
    void onZoomout(){
        if (controlListener != null){
            controlListener.onControl(CMD_GO_ZOOMOUT);
        }
    }
}
