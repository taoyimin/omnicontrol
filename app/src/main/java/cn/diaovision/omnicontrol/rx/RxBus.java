package cn.diaovision.omnicontrol.rx;

import cn.diaovision.omnicontrol.conn.UdpClient;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class RxBus {
    private static RxBus rxBus;
    private final Subject<Object> _bus = PublishSubject.create();

    public RxBus getInstance(){
        if (rxBus == null) {
            rxBus = new RxBus();
        }
        return rxBus;
    }

    public void send(Object o){
        _bus.onNext(o);
    }

    public void subscribe(ObservableOnSubscribe subscriber){
        _bus.subscribe((Observer<? super Object>) subscriber);
    }

//    public void submitToUdpServer(final String cmd, Consumer consumer){
//        Flowable.create(new FlowableOnSubscribe<String>() {
//            @Override
//            public void subscribe(FlowableEmitter<String> e) throws Exception {
//                UdpClient client = new UdpClient("192.168.2.89", 5000);
//                byte[] recv = client.send(cmd.getBytes(), cmd.getBytes().length);
//                e.onNext(String.valueOf(recv));
//            }
//        }, BackpressureStrategy.BUFFER)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(consumer);
//    }
}
