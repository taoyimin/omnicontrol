package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.util.Log;

import cn.diaovision.omnicontrol.core.model.conference.ConfManager;
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
    ConfManager confManager;
    Config cfg=new ConfigFixed();

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
        confManager.init(cfg, new RxSubscriber<RxMessage>() {
            @Override
            public void onRxResult(RxMessage rxMessage) {
                Log.i("info","ConfManager init success");
            }

            @Override
            public void onRxError(Throwable e) {
                Log.i("info","ConfManager init failed");
            }
        });
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

    //TODO: add viewmodel operations if needed
//    public void onTitleChanged(String str){
//    }
}
