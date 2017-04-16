package cn.diaovision.omnicontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import io.reactivex.functions.Consumer;

/**
 * 通信类服务
 * Created by liulingfeng on 2017/2/21.
 */

public class ConferenceService extends Service {
    private List<McuMessage> sendMsgList;
    TcpClient tcpClient;

    @Override
    public void onCreate() {
        super.onCreate();
        sendMsgList = new ArrayList<>();
        tcpClient = new TcpClient("192.168.2.1", 6000);

        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                try {
                    tcpClient.connect();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SendThread extends Thread{
        BlockingQueue<McuMessage> sendMsgList;

        public SendThread(){
            this.sendMsgList = new ArrayBlockingQueue<McuMessage>(20);
        }

        public Consumer send(McuMessage msg){
            RxExecutor.getInstance().
        }

        @Override
        public void run() {
            while(true){
                try {
                    McuMessage msg = sendMsgList.take(); //blocking take
                    int res = tcpClient.sendSync(msg.toBytes());
                    if (res > 0){
                        switch(msg.getType()){
                            case McuMessage.TYPE_REQ:
                                break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
