package cn.diaovision.omnicontrol.rx;

import android.util.Log;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 统一的Rx异步调用接口
 * Created by liulingfeng on 2017/4/3.
 */

public class RxExecutor {
    public final static int SCH_IO = 1;
    public final static int SCH_COMPUTATION = 2;
    public final static int SCH_NEW = 3;
    public final static int SCH_ANDROID_MAIN = 4;

    static private RxExecutor instance;

    private Flowable chainedFlow;

    private RxExecutor(){
    }

    /*单例模式*/
    static public RxExecutor getInstance(){
        if (instance == null){
            instance = new RxExecutor();
        }
        return instance;
    }

    /* **********************************
     * a simple post without subscriber / consumer
     * **********************************/
    public void post(final RxReq req, int subscribeOn){
        Flowable.just("")
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) throws Exception {
                        RxMessage res = req.request();
                        return res;
                    }
                })
                .subscribeOn(getScheduler(subscribeOn));
    }

    /* **********************************
     * a simple rx call with consumer
     * **********************************/
    public void post(final RxReq req, Consumer consumer, int subscribeOn, int observeOn){
        Flowable.just("")
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) throws Exception {
                        RxMessage res = req.request();
                        return res;
                    }
                })
                .subscribeOn(getScheduler(subscribeOn))
                .observeOn(getScheduler(observeOn))
                .subscribe(consumer);
    }


    /* **********************************
     * a standard rx call: with subscriber
     * **********************************/
    public void post(final RxReq req, RxSubscriber subscriber, int subsribeOn, int observeOn){
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> e) throws Exception {
                RxMessage res = req.request();
                if (res == null){
                    e.onError(new RuntimeException());
                }
                e.onNext(res);
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(getScheduler(subsribeOn))
                .observeOn(getScheduler(observeOn))
        .subscribe(subscriber);
    }


    /* *****************************************************
     * a standard rx call: with rxsubscriber and timeout
     * *****************************************************/
    public void post(final RxReq req, RxSubscriber subscriber, int subsribeOn, int observeOn, int timeout){
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> e) throws Exception {
                RxMessage res = req.request();
                if (res == null){
                    e.onError(new RuntimeException());
                }
                e.onNext(res);
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(getScheduler(subsribeOn))
                .observeOn(getScheduler(observeOn))
                .timeout(timeout, TimeUnit.MILLISECONDS)
        .subscribe(subscriber);
    }


    /* **********************************************************
     * a standard rx call: with rxsubscriber, timeout, and repeat
     * *********************************************************/
    public void post(final RxReq req, RxSubscriber subscriber, int subsribeOn, int observeOn, int timeout, int repeat){
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> e) throws Exception {
                RxMessage res = req.request();
                if (res == null){
                    e.onError(new RuntimeException());
                }
                e.onNext(res);
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(getScheduler(subsribeOn))
                .observeOn(getScheduler(observeOn))
                .timeout(timeout, TimeUnit.MILLISECONDS)
                .repeat(repeat)
                .subscribe(subscriber);
    }

    /*post serial of request returning a flowable for future subscribing*/
    public Flowable<RxMessage> post(List<RxReq> req, final int subscribeType, final int observeType){
        return Flowable.fromIterable(req)
                .flatMap(new Function<RxReq, Publisher<RxMessage>>() {
                    @Override
                    public Publisher<RxMessage> apply(final RxReq rxReq) throws Exception {
                        return Flowable.create(new FlowableOnSubscribe<RxMessage>() {
                            @Override
                            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                                RxMessage rxMsg = rxReq.request();
                                if (rxMsg != null){
                                    e.onNext(rxMsg);
                                }
                                else {
                                    e.onError(new Exception());
                                }
                            }
                        }, BackpressureStrategy.BUFFER)
                                .subscribeOn(getScheduler(subscribeType))
                                .observeOn(getScheduler(observeType));
                    }
                });
    }

    private Scheduler getScheduler(int type){
        switch (type){
            case SCH_IO:
                return Schedulers.io();
            case SCH_COMPUTATION:
                return Schedulers.computation();
            case SCH_NEW:
                return Schedulers.newThread();
            case SCH_ANDROID_MAIN:
                return AndroidSchedulers.mainThread();
            default:
                return Schedulers.newThread();
        }
    }
}
