package cn.diaovision.omnicontrol.rx;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/* 简单的RxBus实例*
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

    public void post(RxMessage o){
        bus.observeOn(AndroidSchedulers.mainThread());
        bus.onNext(o);

    }

    public void subscribe(RxSubscription rxSubscription){
        rxSubscription.setDisposable( bus.subscribe(rxSubscription));
    }

    static public abstract class RxSubscription implements Consumer<RxMessage>{
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
