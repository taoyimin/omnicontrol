package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrixRemoter;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static cn.diaovision.omnicontrol.MainControlActivity.matrix;

/* 兼容MVVM模式的Presenter样板
 * Created by liulingfeng on 2017/4/3.
 */

public class VideoPresenter implements VideoContract.Presenter {
    static final String TAG="video";
/*    Config cfg = new ConfigFixed();
    MediaMatrix matrix = new MediaMatrix.Builder()
            .id(cfg.getMatrixId())
            .ip(cfg.getMatrixIp())
            .port(cfg.getMatrixUdpIpPort())
            .localPreviewVideo(cfg.getMatrixPreviewIp(), cfg.getMatrixPreviewPort())
            .videoInInit(cfg.getMatrixInputVideoNum())
            .videoOutInit(cfg.getMatrixOutputVideoNum())
            .build();*/

    MediaMatrixRemoter matrixRemoter = new MediaMatrixRemoter(matrix);

    //通过Subject实现ViewModel的双向绑定
    Subject bus = PublishSubject.create();

    //不同的subscriber可以用来处理不同的view更新
    Consumer<RxMessage> subscriber = new Consumer<RxMessage>() {
        @Override
        public void accept(RxMessage o) throws Exception {
            //TODO: 处理view的更新(可选)
        }
    };

    Disposable subscription;


    /*double binding between view and presenter*/
    @NonNull
    private final VideoContract.View view;

    public VideoPresenter(VideoContract.View view) {
        this.view = view;

        bus.subscribe(subscriber);
    }

    //TODO: remove if no preprocessing is needed
    @Override
    public void start() {

    }


    /* invoked by the view interaction*/
    public void func() {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                /*TODO: 这里添加presenter对model的交互逻辑*/
                return null;
            }
        }, new Consumer<RxMessage>() {
            @Override
            public void accept(RxMessage s) throws Exception {
                //TODO: 这里添加presenter根据提交请求的异步返回结果对view的操作逻辑

                //TODO: 采用MVVM方式更新(可选)
                //RxBus.getInstance().post(new RxMessage("MVP", null));
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }


    //TODO: (optional) remove all subscriber before the presenter is destroyed
    public void stopObserve() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    @Override
    public int[] getOutputIdx(int inputIdx) {
        Set<Channel> chnSet= matrix.getVideoChnSet();
        Iterator<Channel> iterator=chnSet.iterator();
        while (iterator.hasNext()){
            Channel channel=iterator.next();
            if(channel.getInputIdx()==inputIdx){
                return channel.getOutputIdx();
            }
        }
        return null;
    }

    @Override
    public int getInputIdx(int outputIdx) {
        Set<Channel> chnSet=matrix.getVideoChnSet();
        Iterator<Channel> iterator=chnSet.iterator();
        while (iterator.hasNext()){
            Channel channel=iterator.next();
            if(channel.containOutputIdx(outputIdx)){
                return channel.getInputIdx();
            }
        }
        return -1;
    }

    @Override
    public List<Port> getInputList() {
        return matrix.getVideoInPort();
    }

    @Override
    public List<Port> getOutputList() {
        return matrix.getVideoOutPort();
    }

    @Override
    public Set<Channel> getChannelSet(){
        return matrix.getVideoChnSet();
    }

    @Override
    public void setChannel(int input, int[] outputs, int mode) {
        matrix.updateChannel(input,outputs,mode);
    }

    @Override
    public void switchPreviewVideo(int portIn, int portOut){
        int res=matrixRemoter.switchPreviewVideo(portIn, portOut, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG, "Switch preview video succeed");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG, "Switch preview video failed");
            }
        });
        if (res < 0) {
            Log.i(TAG, "invalid switch preview video");
        }
    }

    @Override
    public void switchVideo(int portIn, int[] portOut) {
        int res = matrixRemoter.switchVideo(portIn, portOut, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG, "Switch succeed");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG, "Switch failed");
            }
        });
        if (res < 0) {
            Log.i(TAG, "invalid switch");
        }
    }

    @Override
    public void stitchVideo( int portIn,  int columnCnt,  int rowCnt,  int[] portOut) {
        int res = matrixRemoter.stitchVideo( portIn,   columnCnt,   rowCnt, portOut, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG, "Stitch succeed");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG, "Stitch failed");
            }
        });
        if (res < 0) {
            Log.i(TAG, "invalid stitch");
        }
    }

    @Override
    public void setSubtitle(int portIdx, String str) {
        int res=matrixRemoter.setSubtitle(portIdx, str, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"set subtitle success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"set subtitle failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid set subtitle");
        }
    }

    //TODO: add viewmodel operations if needed
    //    public void onTitleChanged(String str){
    //    }
}
