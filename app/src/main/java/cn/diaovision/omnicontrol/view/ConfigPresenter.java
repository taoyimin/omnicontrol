package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;

import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static cn.diaovision.omnicontrol.MainControlActivity.cfg;

/* 兼容MVVM模式的Presenter样板
 * Created by liulingfeng on 2017/4/3.
 */

public class ConfigPresenter implements ConfigContract.Presenter {

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
    private final ConfigContract.View view;

    public ConfigPresenter(ConfigContract.View view){
        this.view = view;

        bus.subscribe(subscriber);
    }

    //TODO: remove if no preprocessing is needed
    @Override
    public void start() {

    }


    /* invoked by the view interaction*/
    @Override
    public void config(final String ip, final String port, final String id, final String name, final String password) {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                cfg.setMatrixId(id);
                cfg.setMatrixIp(ip);
                cfg.setMatrixUdpIpPort(port);
                cfg.setMainName(name);
                cfg.setMainPasswd(password);
                return new RxMessage(RxMessage.DONE);
            }
        }, new Consumer<RxMessage>() {
            @Override
            public void accept(RxMessage s) throws Exception {
                if(s.what==RxMessage.DONE){
                    view.showToast("配置成功！");
                }
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }


    //TODO: (optional) remove all subscriber before the presenter is destroyed
    public void stopObserve(){
        if (subscription != null){
            subscription.dispose();
        }
    }

    //TODO: add viewmodel operations if needed
//    public void onTitleChanged(String str){
//    }
}
