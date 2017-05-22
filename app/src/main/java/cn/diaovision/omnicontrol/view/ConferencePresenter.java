package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Date;

import cn.diaovision.omnicontrol.core.model.conference.ConfManager;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrixRemoter;
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

public class ConferencePresenter implements ConferenceContract.Presenter {
    static final String TAG="conf";
    ConfManager confManager;
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
    private final ConferenceContract.View view;

    public ConferencePresenter(ConferenceContract.View view){
        this.view = view;

        bus.subscribe(subscriber);
        confManager=new ConfManager();
/*        confManager.init(cfg, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"ConfManager init success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"ConfManager init failed");
            }
        });*/
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

    @Override
    public boolean login(String name, String passwd) {
        return true;
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

    @Override
    public void setSubtitleFormat(int portIdx, int sublen, byte fontSize, byte fontColor) {
        int res=matrixRemoter.setSubtitleFormat(portIdx, sublen, fontSize, fontColor, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"set subtitle format success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"set subtitle format failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid set subtitle format");
        }
    }

    @Override
    public void reqConfTemplate() {

    }

    @Override
    public void reqConfInfo() {

    }

    @Override
    public void startConf(Date startTime, Date endTime, int confId) {
        int res=confManager.startConf(startTime, endTime, confId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"start conf success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"start conf failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid start conf");
        }
    }

    @Override
    public void endConf(int confId) {
        int res=confManager.endConf(confId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"end conf success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"end conf failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid end conf");
        }
    }

    @Override
    public void inviteTerm(int confId, long termId) {
        int res=confManager.inviteTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"invite term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"invite term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid invite term");
        }
    }

    @Override
    public void hangupTerm(int confId, long termId) {
        int res=confManager.inviteTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"hangup term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"hangup term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid hangup term");
        }
    }

    @Override
    public void muteTerm(int confId, long termId) {
        int res=confManager.muteTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"mute term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"mute term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid mute term");
        }
    }

    @Override
    public void unmuteTerm(int confId, long termId) {
        int res=confManager.unmuteTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"unmute term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"unmute term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid unmute term");
        }
    }

    @Override
    public void speechTerm(int confId, long termId) {
        int res=confManager.speechTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"speech term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"speech term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid speech term");
        }
    }

    @Override
    public void cancelSpeechTerm(int confId, long termId) {
        int res=confManager.cancelSpeechTerm(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"cancel speech term success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"cancel speech term failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid cancel speech term");
        }
    }

    @Override
    public void makeChair(int confId, long termId) {
        int res=confManager.makeChair(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"make chair success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"make chair failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid make chair");
        }
    }

    @Override
    public void makeSelectview(int confId, long termId) {
        int res=confManager.makeSelectview(confId, termId, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i(TAG,"make selectview success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i(TAG,"make selectview failed");
            }
        });
        if(res<0){
            Log.i(TAG,"invalid make selectview");
        }
    }

    //TODO: add viewmodel operations if needed
//    public void onTitleChanged(String str){
//    }
}
