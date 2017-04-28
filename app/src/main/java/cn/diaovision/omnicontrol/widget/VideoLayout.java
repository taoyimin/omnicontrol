package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import cn.diaovision.omnicontrol.R;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by TaoYimin on 2017/4/28.
 */

public class VideoLayout extends RelativeLayout{
    VideoView videoView;
    Button btn_pause;
    Button btn_screen_shot;

    public VideoLayout(@NonNull Context context) {
        this(context,null);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view= View.inflate(context, R.layout.layout_video,this);
        videoView= (VideoView) findViewById(R.id.video_view);
        btn_pause= (Button) view.findViewById(R.id.pause);
        btn_screen_shot= (Button) view.findViewById(R.id.screen_shot);
        btn_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()) {
                    btn_pause.setText("播放");
                    pause();
                }else {
                    btn_pause.setText("暂停");
                    start();
                }
            }
        });

        videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        videoView.setBufferSize(1024); //设置视频缓冲大小。默认1024KB
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
    }

    public void setVideoPath(String path){
        videoView.setVideoPath(path);
    }

    public void start(){
        videoView.start();
    }

    public void pause(){
        videoView.pause();
    }

    public void screenShot(){
        //截屏
    }

    public void stopPlayback(){
        videoView.stopPlayback();
    }
}
