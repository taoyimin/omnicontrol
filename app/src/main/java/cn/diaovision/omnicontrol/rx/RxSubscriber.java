package cn.diaovision.omnicontrol.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 封装subscriber
 * Created by liulingfeng on 2017/4/18.
 */

public abstract class RxSubscriber<T> implements Subscriber<T>{

    public abstract  void onRxResult(T t);
    public abstract  void onRxError(Throwable e);

    @Override
    public void onSubscribe(Subscription s) {
    }

    @Override
    public void onNext(T t) {
        onRxResult(t);
    }

    @Override
    public void onError(Throwable t) {
        onRxError(t);
    }

    @Override
    public void onComplete() {
    }
}
