package cn.diaovision.omnicontrol.rx;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class RxBus {
    private static RxBus rxBus;
    private final Subject bus = PublishSubject.create();

    public static RxBus getInstance(){
        if (rxBus == null) {
            rxBus = new RxBus();
        }
        return rxBus;
    }

    public void post(Object o){
        bus.onNext(o);

    }

    public void subscribe(RxSubscription rxSubscription){
        rxSubscription.setDisposable( bus.subscribe(rxSubscription));
    }


    static public abstract class RxSubscription implements Consumer{
        Disposable disposable;

        public Disposable getDisposable(){
            return disposable;
        }
        public void setDisposable(Disposable disposable){
            this.disposable = disposable;
        }

        public void unsubscribe(){
            if (disposable != null){
                disposable.dispose();
            }
        }
    }
}
