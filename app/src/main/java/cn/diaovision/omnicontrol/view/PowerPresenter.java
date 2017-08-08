package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.State;
import cn.diaovision.omnicontrol.core.model.device.common.BarcoProjector;
import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static cn.diaovision.omnicontrol.MainControlActivity.cfg;

/* 兼容MVVM模式的Presenter样板
 * Created by liulingfeng on 2017/4/3.
 */

public class PowerPresenter implements PowerContract.Presenter {

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
    private final PowerContract.View view;

    public PowerPresenter(PowerContract.View view) {
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

    /*打开设备电源*/
    @Override
    public void powerOn(final CompoundButton buttonView, final CommonDevice device) {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                byte[] msg = device.buildPowerOnMessage();
                byte[] recv = device.getController().send(msg, msg.length);
                if (recv != null && recv.length > 0) {
                    return new RxMessage(RxMessage.DONE);
                } else {
                    return new RxMessage(RxMessage.ERR);
                }
            }
        }, new Consumer<RxMessage>() {
            @Override
            public void accept(RxMessage s) throws Exception {
                if (s.what == RxMessage.DONE) {
                    device.setState(State.ON);
                    view.refreshDeviceList();
                    view.showToast("设备打开成功！");
                } else if (s.what == RxMessage.ERR) {
                    view.removeAdapterListener();
                    buttonView.toggle();
                    view.initAdapterListener();
                    view.showToast("设备打开失败！");
                }
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }

    /*关闭设备电源*/
    @Override
    public void powerOff(final CompoundButton buttonView, final CommonDevice device) {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                byte[] msg = device.buildPowerOffMessage();
                byte[] recv = device.getController().send(msg, msg.length);
                if (recv != null && recv.length > 0) {
                    return new RxMessage(RxMessage.DONE);
                } else {
                    return new RxMessage(RxMessage.ERR);
                }
            }
        }, new Consumer<RxMessage>() {
            @Override
            public void accept(RxMessage s) throws Exception {
                if (s.what == RxMessage.DONE) {
                    device.setState(State.OFF);
                    view.refreshDeviceList();
                    view.showToast("设备关闭成功！");
                } else if (s.what == RxMessage.ERR) {
                    view.removeAdapterListener();
                    buttonView.toggle();
                    view.initAdapterListener();
                    view.showToast("设备关闭失败！");
                }
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }

    @Override
    public void initState(final List<CommonDevice> devices) {
        Flowable.fromIterable(devices)
                .map(new Function<CommonDevice, CommonDevice>() {
                    @Override
                    public CommonDevice apply(CommonDevice device) throws Exception {
                        byte[] msg = device.buildStateMessage();
                        byte[] recv = device.getController().send(msg, msg.length);
                        if (device instanceof BarcoProjector) {
                            if (recv == null)
                                device.setState(State.NA);
                            else if (recv.length > 15 && recv[15] == 49)
                                device.setState(State.ON);
                            else
                                device.setState(State.OFF);
                        } else{
                            if (recv == null)
                                device.setState(State.NA);
                            else
                                device.setState(State.ON);
                        }
                        return device;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        view.removeAdapterListener();
                        view.refreshDeviceList();
                    }
                })
                .observeOn(Schedulers.io())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Thread.sleep(1000);
                        view.initAdapterListener();
                    }
                })
                .subscribe(new Consumer<CommonDevice>() {
                    @Override
                    public void accept(CommonDevice device) throws Exception {
                    }
                });
    }

    @Override
    public List<CommonDevice> getDeviceList() {
        return cfg.getDeviceList();
    }

    //TODO: add viewmodel operations if needed
    //    public void onTitleChanged(String str){
    //    }
}
