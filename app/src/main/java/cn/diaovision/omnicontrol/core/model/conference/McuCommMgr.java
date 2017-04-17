package cn.diaovision.omnicontrol.core.model.conference;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.diaovision.omnicontrol.BaseCyclicThread;
import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.util.ByteBuffer;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/4/17.
 */

public class McuCommMgr {
    private BlockingQueue<Pair<McuMessage, Consumer>> txQueue;
    private List<Pair<McuMessage, Consumer>> txSendList;
    ByteBuffer recvBuff;

    OnCommListener listener;

    TcpClient client;


    BaseCyclicThread pulseThread;
    BaseCyclicThread sendThread;
    BaseCyclicThread recvThread;

    BaseCyclicThread checkConnectThread;


    public McuCommMgr(String ip, int port){
        client = new TcpClient(ip, port);

        txQueue = new ArrayBlockingQueue<Pair<McuMessage, Consumer>>(10);
        txSendList = new ArrayList<>();

        recvBuff = new ByteBuffer(65535);

        threadInit();
    }

    /*
     * connect to mcu server
     */
    public void connect(Consumer consumer){
        Flowable flowable = RxExecutor.getInstance().buildFlow(new RxReq() {
            @Override
            public RxMessage request() {
                client.connect();
                if (client.getState() == TcpClient.STATE_CONNECTED) {

                    threadStart();

                    return new RxMessage("CONNECTED");
                }
                else if (client.getState() == TcpClient.STATE_DISCONNECTED){
                    return new RxMessage("DISCONNECTED");
                }
                else {
                    return new RxMessage("DISCONNECTED");
                }
            }
        }, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);

        if (consumer != null) {
            flowable.subscribe(consumer);
        }
    }

    /* ***************************************************
     * disconnect will return immediately
     * ***************************************************/
    public void disconnect(){
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                client.disconnect();
                return null;
            }
        }, RxExecutor.SCH_IO);

        threadStop();
    }

    public void send(McuMessage msg, Consumer consumer){
        txQueue.add(new Pair<McuMessage, Consumer>(msg, consumer));
        //return immediately, async called in consumer
    }


    private void threadStart(){
        if (sendThread == null || recvThread == null || pulseThread == null || checkConnectThread == null){
            threadInit();
        }

        sendThread.start();
        recvThread.start();
        pulseThread.start();

        checkConnectThread.start();
    }

    private void threadStop(){

        txQueue.clear();
        txSendList.clear();
        recvBuff.flush();

        sendThread.quit();
        recvThread.quit();
        pulseThread.quit();

        checkConnectThread.quit();

    }

    private void threadInit(){

        txQueue.clear();
        txSendList.clear();
        recvBuff.flush();

        if (sendThread != null){
            sendThread.quit();
        }
        if (recvThread != null){
            recvThread.quit();
        }
        if (pulseThread != null){
            pulseThread.quit();
        }

        checkConnectThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(5000);
                    if (client.getState() != TcpClient.STATE_CONNECTED){
                        disconnect();
                        connect(null);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

         //send thread output message+consumer(callback) to socket
        sendThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    final Pair<McuMessage, Consumer> msgPair = txQueue.take();

                    Flowable.just("")
                            .map(new Function<String, RxMessage>() {
                                @Override
                                public RxMessage apply(String s) throws Exception {
                                    int res = client.send(msgPair.first.toBytes());
                                    if (res > 0){
                                        txSendList.add(msgPair);
                                        return new RxMessage("SEND DONE", msgPair.first);
                                    }
                                    else {
                                        return null;
                                    }
                                }
                            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(msgPair.second);

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //client receive thread
        recvThread = new BaseCyclicThread() {
            @Override
            public void work() {
                byte[] rxdata = new byte[1024];
                int len = client.recv(rxdata);
                if (len > 0) {
                    recvBuff.push(rxdata, len);
                }

                final McuMessage msg = McuMessage.parse(recvBuff);

                if (msg != null) {
                    //TODO: if is transitted message (from mcu)

                    //TODO: if is ACK message
                    Flowable.just("")
                            .map(new Function<String, RxMessage>() {
                                @Override
                                public RxMessage apply(String s) throws Exception {
                                    int res = handleMessage(msg);
                                    return new RxMessage("RECV", msg);
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread());
                }
                else {
                    //continue
                }
            }
        };

        //a single thread to reclaim
        pulseThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(1000);
                    txQueue.put(new Pair<McuMessage, Consumer>(McuMessage.buildLogin("term", "123456"), null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /******************************************************
     * 消息处理主类型
     * @param msg
     * @return
     */
    public int handleMessage(McuMessage msg){
        //如果是应答消息，应当去txWithAckList找对应的消息缓存，然后删除掉
        //如果是转发消息，应当单独处理
        return 0;
   }

    public void setOnCommListener(OnCommListener listener) {
        this.listener = listener;
    }

    public interface OnCommListener{
        //这里只需要处理MCU的转发报文
        void onRecv(McuMessage rxMessage);
    }


}


