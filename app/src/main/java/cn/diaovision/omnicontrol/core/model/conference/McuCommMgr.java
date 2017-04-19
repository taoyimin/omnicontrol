package cn.diaovision.omnicontrol.core.model.conference;


import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.diaovision.omnicontrol.BaseCyclicThread;
import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import cn.diaovision.omnicontrol.util.ByteBuffer;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/4/17.
 */

public class McuCommMgr {
    private final static int RECV_BUFF_LEN = 65535; //buffer length for receiving
    private final static int ACK_TIMEOUT = 3000; //ACK timeout
    private final static int QUEUE_LEN = 10;
    private final static int RX_TIMEOUT = 2000; //Rx timeout

    private BlockingQueue<McuBundle> txQueue;

    private BlockingQueue<McuBundle> txSendWithAckQueue;
    ByteBuffer recvBuff;

    TcpClient client;

    List<BaseCyclicThread> threadList;

    CommListener commListener;

    public McuCommMgr(String ip, int port){
        client = new TcpClient(ip, port);

        txQueue = new ArrayBlockingQueue<>(QUEUE_LEN);

        txSendWithAckQueue = new ArrayBlockingQueue<McuBundle>(QUEUE_LEN);

        recvBuff = new ByteBuffer(RECV_BUFF_LEN);

        threadInit();
    }

    /*
     * connect to mcu server
     */
    public void connect(Subscriber subscriber){
        Flowable flowable = RxExecutor.getInstance().buildFlow(new RxReq() {
            @Override
            public RxMessage request() {
                client.connect();
                if (client.getState() == TcpClient.STATE_CONNECTED) {

                    threadStart();

                    return new RxMessage(RxMessage.CONNECTED);
                }
                else if (client.getState() == TcpClient.STATE_DISCONNECTED){
                    return new RxMessage(RxMessage.DISCONNECTED);
                }
                else {
                    return new RxMessage(RxMessage.DISCONNECTED);
                }
            }
        }, 2000, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);

        if (subscriber != null) {
            flowable.subscribe(subscriber);
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

    public void send(McuMessage msg, RxSubscriber subscriber){
        McuBundle bundle = new McuBundle();
        bundle.msg = msg;
        bundle.subscriber = subscriber;
        bundle.timeSend = System.currentTimeMillis();

        txQueue.add(bundle);
        //return immediately, async called in consumer
    }


    private void threadStart(){
        boolean hasInited = true;
        for (BaseCyclicThread thread  : threadList){
            if (thread == null) {
                hasInited = false;
                break;
            }
        }

        if (!hasInited){
            threadInit();
        }

        for (BaseCyclicThread thread : threadList){
            thread.start();
        }

    }

    private void threadStop(){

        txQueue.clear();
        txSendWithAckQueue.clear();
        recvBuff.flush();

        for (BaseCyclicThread thread : threadList){
            thread.quit();

        }

        threadList.clear();
    }

    private void threadInit(){

        txQueue.clear();
        txSendWithAckQueue.clear();
        recvBuff.flush();

        threadStop();

        BaseCyclicThread checkConnectThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(1000);
                    if (client.getState() != TcpClient.STATE_CONNECTED){
                        disconnect();
                        connect(null);
                    }
                    if (commListener != null){
                        commListener.onConnectionChanged(client.getState());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        threadList.add(checkConnectThread);

         //send thread output message+consumer(callback) to socket
        BaseCyclicThread sendThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    final McuBundle bundle = txQueue.take();

                    Flowable flowable = RxExecutor.getInstance().buildFlow(new RxReq() {
                        @Override
                        public RxMessage request() {
                            int res = client.send(bundle.msg.toBytes());
                            if (res > 0){
                                bundle.timeSend = System.currentTimeMillis();
                                try {
                                    txSendWithAckQueue.put(bundle);
                                    return new RxMessage(RxMessage.DONE, bundle.msg);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                            else {
                                return null;
                            }
                        }
                    }, 2000, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN);

                    if (bundle.subscriber != null){
                        flowable.subscribe(bundle.subscriber);
                    }

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadList.add(sendThread);

        //client receive thread
        BaseCyclicThread recvThread = new BaseCyclicThread() {
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
                    if (msg.getType() == McuMessage.TYPE_CHAIRUSER ){
                        if (commListener != null){
                            commListener.onRecv(msg);
                        }
                    }


                    //TODO: if is ACK message (Rx handling here)
                    Flowable.just("")
                            .map(new Function<String, RxMessage>() {
                                @Override
                                public RxMessage apply(String s) throws Exception {
                                    int res = handleMessage(msg);
                                    return new RxMessage(RxMessage.ACK, msg);
                                }
                            })
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            }
        };
        threadList.add(recvThread);

        //a single thread to reclaim
        BaseCyclicThread pulseThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(1000);
                    McuBundle bundle = new McuBundle();
                    bundle.msg = McuMessage.buildLogin("term", "123456");
                    bundle.subscriber = null;
                    bundle.timeSend = 0;
                    txQueue.put(bundle);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadList.add(pulseThread);

        BaseCyclicThread msgDieoutThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                McuBundle bundle = txSendWithAckQueue.peek();
                    if (System.currentTimeMillis() - bundle.timeSend > ACK_TIMEOUT) {
                        txSendWithAckQueue.take();
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        threadList.add(msgDieoutThread);
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

    public void setCommListener(CommListener listener) {
        this.commListener = listener;
        McuBundle bundle = new McuBundle();
    }

    /*
     * this interface handles non-async calling （返回非异步访问的并发回调接口）
     */
    public interface CommListener{

        //tcp连接问题
        void onConnectionChanged(int state);

        //处理MCU的转发报文
        void onRecv(McuMessage rxMessage);
    }

    private class McuBundle{
        long timeSend;
        McuMessage msg;
        RxSubscriber subscriber;
    }

}


