package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrixRemoter;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.core.model.device.splicer.MediaSplicerRemoter;
import cn.diaovision.omnicontrol.core.model.device.splicer.Scene;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static cn.diaovision.omnicontrol.MainControlActivity.matrix;
import static cn.diaovision.omnicontrol.MainControlActivity.splicer;

/* 兼容MVVM模式的Presenter样板
 * Created by liulingfeng on 2017/4/3.
 */

public class VideoPresenter implements VideoContract.Presenter {
    static final String TAG = "video";
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
    MediaSplicerRemoter splicerRemoter = new MediaSplicerRemoter(splicer);

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

    /*获取输入端口对应的输出端口数组*/
    @Override
    public int[] getOutputIdx(int inputIdx) {
        Set<Channel> chnSet = matrix.getVideoChnSet();
        Iterator<Channel> iterator = chnSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.getInputIdx() == inputIdx) {
                return channel.getOutputIdx();
            }
        }
        return null;
    }

    /*获取输出端口对应的输入端口*/
    @Override
    public int getInputIdx(int outputIdx) {
        Set<Channel> chnSet = matrix.getVideoChnSet();
        Iterator<Channel> iterator = chnSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.containOutputIdx(outputIdx)) {
                return channel.getInputIdx();
            }
        }
        return -1;
    }

    /*获取矩阵输入端口的集合*/
    @Override
    public List<Port> getInputList() {
        return matrix.getVideoInPort();
    }

    /*获取矩阵输出端口的集合*/
    @Override
    public List<Port> getOutputList() {
        return matrix.getVideoOutPort();
    }

    /*获取矩阵已配置好的通道集合*/
    @Override
    public Set<Channel> getChannelSet() {
        return matrix.getVideoChnSet();
    }

    /*配置通道*/
    @Override
    public void setChannel(int input, int[] outputs, int mode) {
        //matrix.updateChannel(input,outputs,mode);
    }

    /*预览输入端口的流媒体时调用，将输入端口切换到流媒体卡端口*/
    @Override
    public void switchPreviewVideo(int portIn, int portOut) {
        int res = matrixRemoter.switchPreviewVideo(portIn, portOut, new RxSubscriber<RxMessage>() {
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

    /*一对多输出，将矩阵输入端口切换到多个输出端口*/
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

    /*一对多输出（局部分割），将矩阵输入端口分割成多个画面输出到多个输出端口*/
    @Override
    public void stitchVideo(int portIn, int columnCnt, int rowCnt, int[] portOut) {
        int res = matrixRemoter.stitchVideo(portIn, columnCnt, rowCnt, portOut, new RxSubscriber<RxMessage>() {
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

    /*矩阵端口叠加字幕*/
    @Override
    public void setSubtitle(int portIdx, String str) {
        int res = matrixRemoter.setSubtitle(portIdx, str, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG, "set subtitle success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG, "set subtitle failed");
            }
        });
        if (res < 0) {
            Log.i(TAG, "invalid set subtitle");
        }
    }

    /*获取某个屏的场景集合*/
    @Override
    public void getSceneList(int group) {
        splicerRemoter.getSceneList(group, new Subscriber<List<Scene>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(List<Scene> scenes) {
                splicer.setScenes(scenes);
                view.initScene(scenes);
            }


            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /*调用场景*/
    @Override
    public void loadSceneList(int sceneId) {
        splicerRemoter.loadScene(sceneId, new Subscriber<RxMessage>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(RxMessage rxMessage) {
                if (rxMessage.what == RxMessage.DONE) {
                    view.showToast("场景切换成功!");
                }
            }

            @Override
            public void onError(Throwable t) {
                view.showToast("场景切换失败！");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /*修改场景名*/
    @Override
    public void renameScene(int sceneId, String name, int groupId) {
        splicerRemoter.renameScene(sceneId, name, groupId, new Subscriber<RxMessage>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(RxMessage rxMessage) {
                if (rxMessage.what == RxMessage.DONE) {
                    view.showToast("修改成功！");
                    view.refreshSceneList();
                }
            }

            @Override
            public void onError(Throwable t) {
                view.showToast("修改失败！");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    //TODO: add viewmodel operations if needed
    //    public void onTitleChanged(String str){
    //    }
}
