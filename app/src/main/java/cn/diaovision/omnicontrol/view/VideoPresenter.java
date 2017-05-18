package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrixRemoter;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.model.Config;
import cn.diaovision.omnicontrol.model.ConfigFixed;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/* 兼容MVVM模式的Presenter样板
 * Created by liulingfeng on 2017/4/3.
 */

public class VideoPresenter implements VideoContract.Presenter {
    Config cfg = new ConfigFixed();
    MediaMatrix matrix = new MediaMatrix.Builder()
            .id(cfg.getMatrixId())
            .ip(cfg.getMatrixIp())
            .port(cfg.getMatrixUdpIpPort())
            .localPreviewVideo(cfg.getMatrixPreviewIp(), cfg.getMatrixPreviewPort())
            .videoInInit(cfg.getMatrixInputVideoNum())
            .videoOutInit(cfg.getMatrixOutputVideoNum())
            .build();

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
    public Port getOutput(Port input) {
        //获取系统配置的通道输出端，同步调用
        return null;
    }

    @Override
    public Port getInput(Port output) {
        //获取系统配置的通道输入端，同步调用
        return null;
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
    public void setChannel(Port input, List<Port> output) {
        //配置系统，异步调用

    }

    @Override
    public void switchVideo(int portIn, int[] portOut) {
        int res = matrixRemoter.switchVideo(portIn, portOut, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i("info", "Switch succeed");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i("info", "Switch failed");
            }
        });
        if (res < 0) {
            Log.i("info", "invalid switch");
        }
    }

    @Override
    public void stitchVideo( int portIn,  int columnCnt,  int rowCnt,  int[] portOut) {
        int res = matrixRemoter.stitchVideo( portIn,   columnCnt,   rowCnt, portOut, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i("info", "Stitch succeed");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i("info", "Stitch failed");
            }
        });
        if (res < 0) {
            Log.i("info", "invalid stitch");
        }
    }

    //TODO: add viewmodel operations if needed
    //    public void onTitleChanged(String str){
    //    }
}
