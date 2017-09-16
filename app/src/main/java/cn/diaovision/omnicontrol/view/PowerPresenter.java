package cn.diaovision.omnicontrol.view;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.util.ByteUtils;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    @Override
    public List<Device> getDeviceList() {
        return cfg.getDeviceList();
    }

    @Override
    public void sendCommand(final Device device, final Device.Command cmd) {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                byte[] bytes = null;
                if (!TextUtils.isEmpty(cmd.getStringCmd())) {
                    bytes = ByteUtils.ascii2bytes(cmd.getStringCmd());
                } else if (cmd.getByteCmd() != null) {
                    bytes = cmd.getByteCmd();
                }
                List<byte[]> recv = device.getController().send(bytes, bytes.length, true);
                return new RxMessage(RxMessage.DONE, recv);
            }
        }, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                List<byte[]> recv = ((List<byte[]>) ((RxMessage) o).val);
                if (recv != null&&recv.size()>1){
                    for(int i=0;i<recv.size();i++){
                        Log.i("info", ByteUtils.bytes2ascii(recv.get(i)));
                    }
                }
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }

    //TODO: add viewmodel operations if needed
    //    public void onTitleChanged(String str){
    //    }
}
