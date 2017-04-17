package cn.diaovision.omnicontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
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
                    return new RxMessage("DONE");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {

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

        public void send(final McuMessage msg){
            Consumer consumer = new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                }
            };

            RxExecutor.getInstance().post(new RxReq() {
                @Override
                public RxMessage request() {
                    int res = tcpClient.sendSync(msg.toBytes());
                    return new RxMessage("SEND", res);
                }
            }, new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                }
            }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);
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
