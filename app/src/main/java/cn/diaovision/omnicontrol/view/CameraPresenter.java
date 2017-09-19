package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrixRemoter;
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

public class CameraPresenter implements CameraContract.Presenter {
    static final String TAG="camera";

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
    private final CameraContract.View view;

    public CameraPresenter(CameraContract.View view){
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
    public void stopObserve(){
        if (subscription != null){
            subscription.dispose();
        }
    }

    /*摄像机移动*/
    @Override
    public void cameraCtrlGo(int portIdx, final int cmd, final int speed) {
        int res = matrixRemoter.startCameraGo(portIdx, cmd, speed, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"Camera go success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"Camera go failed");
            }
        });
        if (res < 0){
            Log.i(TAG,"invalid go");
        }
    }

    /*摄像机停止*/
    @Override
    public void cameraStopGo(int portIdx) {
        int res = matrixRemoter.stopCameraGo(portIdx,new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"Camera go stopped");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"Stop camera go failed");
            }
        });
        if (res < 0){
            Log.i(TAG,"invalid camera stop go");
        }
    }

    /*添加预置位*/
    @Override
    public void addPreset(int portIdx,int presetIdx, String name) {
        int res = matrixRemoter.storeCameraPreset(portIdx, presetIdx, name, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"store preset success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"store preset failed");
                e.printStackTrace();
            }
        });
        if (res < 0){
            Log.i(TAG,"invalid store preset");
        }
    }

    /*删除预置位*/
    @Override
    public void delPreset(int portIdx, int presetIdx) {
        int res=matrixRemoter.removeCameraPreset(portIdx,presetIdx, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"success to delete preset");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"failed to delete preset");
            }
        });
        if (res < 0){
            Log.i(TAG,"invalid delete preset");
        }
    }

    /*调用预置位*/
    @Override
    public void loadPreset(int portIdx,int presetIdx) {
        int res = matrixRemoter.loadCameraPreset(portIdx, presetIdx, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"success to load preset");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"failed to load preset");
            }
        });
        if (res < 0){
            Log.i(TAG,"invalid load preset");
        }
    }

    /*根据端口号获取摄像机*/
    @Override
    public HiCamera getCamera(int port) {
        return matrix.getCameras().get(port);
    }

    /*获取摄像机集合*/
    @Override
    public List<HiCamera> getCameraList() {
        Map<Integer,HiCamera> cameras = matrix.getCameras();
        List<HiCamera> cameraList=new ArrayList<>();
        for (HiCamera camera : cameras.values()) {
            cameraList.add(camera);
        }
        return cameraList;
    }

    /*预览输入端口的流媒体时调用，将输入端口切换到流媒体卡端口*/
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

    //TODO: add viewmodel operations if needed
//    public void onTitleChanged(String str){
//    }
}
