package cn.diaovision.omnicontrol.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.diaovision.omnicontrol.R;
import cn.diaovision.omnicontrol.widget.media.IjkVideoView;

/**
 * Created by TaoYimin on 2017/4/28.
 * 流媒体播放器布局
 */
public class VideoLayout extends RelativeLayout {
    @BindView(R.id.video_view)
    IjkVideoView videoView;
    @BindView(R.id.pause)
    Button btn_pause;
    @BindView(R.id.screen_shot)
    Button btn_screen_shot;
    @BindView(R.id.title)
    TextView text_title;
    @BindView(R.id.statue)
    ImageView image_statue;

    Context context;

    boolean needResume;

    public VideoLayout(@NonNull Context context) {
        this(context, null);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        this.context = context;
        View view = View.inflate(context, R.layout.layout_video, this);
        ButterKnife.bind(this, view);

/*        videoView.getHolder().setFormat(PixelFormat.RGBA_8888);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i("info","onPrepared");
                //videoView.setBufferSize(1024 * 512); //设置视频缓冲大小
                //videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);//设置播放画质
                mediaPlayer.setPlaybackSpeed(1.0f);
                mediaPlayer.start();
            }
        });
        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //Log.i("info","onBufferingUpdate"+percent+"%");
            }
        });*/
/*        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓存，暂停播放
                        Log.i("info","开始缓存，暂停播放");
                        if (mp.isPlaying()) {
                            mp.pause();
                            needResume = true;
                            Log.i("info","开始缓存，暂停播放执行了");
                        }
                        //mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //缓存完成，继续播放
                        Log.i("info","缓存完成，继续播放");
                        if (needResume) {
                            mp.start();
                            Log.i("info","缓存完成，继续播放执行了");
                        }
                       //mLoadingView.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        //显示 下载速度
                        Log.i("info","download rate:" + extra);
                        break;
                }
                return true;
            }
        });*/
/*        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("info","onError"+what+"----" + extra);
                return false;
            }
        });*/
        //videoView.requestFocus();
    }

    @OnClick(R.id.pause)
    void clickPause() {
        if (videoView == null) {
            return;
        }
        if (videoView.isPlaying()) {
            pause();
        } else {
            start();
        }
    }

/*    @OnClick(R.id.screen_shot)
    void screenShot() {
        if (videoView == null)
            return;
        Bitmap bitmap = videoView.getCurrentFrame();//截图方法
        if (bitmap == null)
            return;
        if (videoView.isPlaying()) {
            try {
                String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                String fileName = FileHelper.getSrceenShotPath() + "/" + name;
                File file = new File(FileHelper.getSrceenShotPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(context, "截图成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(context, "视频未播放，请稍候截图", Toast.LENGTH_SHORT).show();
    }*/

    @OnClick(R.id.statue)
    void clickStatue() {
        if (videoView == null && image_statue == null) {
            return;
        }
        if (!videoView.isPlaying()) {
            start();
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            text_title.setText(title);
        }
    }

    public void setVideoPath(String path) {
        if (videoView != null) {
            videoView.setVideoPath(path);
        }
    }

    public void start() {
        if (videoView != null) {
            videoView.start();
        }
        if (image_statue != null && image_statue.getVisibility() == View.VISIBLE) {
            image_statue.setVisibility(View.GONE);
        }
        if (btn_pause != null) {
            btn_pause.setText("暂停");
        }
    }

    public void pause() {
        if (videoView != null) {
            videoView.pause();
        }
        if (image_statue != null && image_statue.getVisibility() == View.GONE) {
            image_statue.setVisibility(View.VISIBLE);
        }
        if (btn_pause != null) {
            btn_pause.setText("播放");
        }
    }

    public void stopPlayback() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.release(true);
        }
    }

    public boolean isPlaying(){
        if(videoView!=null){
            return videoView.isPlaying();
        }
        return false;
    }
}
