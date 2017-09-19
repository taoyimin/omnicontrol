package cn.diaovision.omnicontrol.core.model.conference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import cn.diaovision.omnicontrol.BaseCyclicThread;
import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.conference.BaseMessage;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;
import cn.diaovision.omnicontrol.core.message.conference.ReqMessage;
import cn.diaovision.omnicontrol.core.message.conference.ResMessage;
import cn.diaovision.omnicontrol.rx.RxExecutor;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.rx.RxReq;
import cn.diaovision.omnicontrol.rx.RxSubscriber;
import cn.diaovision.omnicontrol.util.ByteBuffer;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * MCU communication manager
 * Created by liulingfeng on 2017/4/17.
 */

public class McuCommManager {
    private final static int RECV_BUFF_LEN = 65535; //buffer length for receiving
    private final static int ACK_TIMEOUT = 5000; //ACK timeout (in ms)

    private LinkedList<McuBundle> ackList;
    private ReentrantLock ackListLock;

    ByteBuffer recvBuff;

    TcpClient client;

    List<BaseCyclicThread> threadList;

    CommListener commListener;

    public McuCommManager(Mcu mcu) {
        client = new TcpClient(mcu.ip, mcu.port);

        recvBuff = new ByteBuffer(RECV_BUFF_LEN);

        ackList = new LinkedList<>();
        ackListLock = new ReentrantLock();
        threadList = new ArrayList<>();
        threadInit();
    }

    /*
     * connect to mcu server
     */
    public void connect(RxSubscriber subscriber) {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                client.connect();
                if (client.getState() == TcpClient.STATE_CONNECTED) {
                    threadStart();
                    return new RxMessage(RxMessage.CONNECTED);
                } else if (client.getState() == TcpClient.STATE_DISCONNECTED) {
                    return new RxMessage(RxMessage.DISCONNECTED);
                } else {
                    return new RxMessage(RxMessage.DISCONNECTED);
                }
            }
        }, subscriber, RxExecutor.SCH_IO, RxExecutor.SCH_ANDROID_MAIN, 2000);
    }

    /*连接服务器，返回一个flowable以便后续的操作*/
    public Flowable<RxMessage> connect() {
        return Flowable.create(new FlowableOnSubscribe<RxMessage>() {
            @Override
            public void subscribe(FlowableEmitter<RxMessage> e) throws Exception {
                client.connect();
                if (client.getState() == TcpClient.STATE_CONNECTED) {
                    threadStart();
                    Log.i("MCU", "connect result: " + client.getState());
                    e.onNext(new RxMessage(RxMessage.CONNECTED));
                } else if (client.getState() == TcpClient.STATE_DISCONNECTED) {
                    e.onError(new IOException());
                } else {
                    e.onError(new IOException());
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /* ***************************************************
     * disconnect will return immediately
     * ***************************************************/
    public void disconnect() {
        RxExecutor.getInstance().post(new RxReq() {
            @Override
            public RxMessage request() {
                client.disconnect();
                return null;
            }
        }, RxExecutor.SCH_IO);

        threadStop();
    }

    /*发送连续报文*/
    public void sendSequential(final List<McuBundle> bundleList, final RxSubscriber subscriber) {
        if (client.getState() == TcpClient.STATE_CONNECTED) {
            Flowable.fromIterable(bundleList)
                    .map(new Function<McuBundle, RxMessage>() {
                        @Override
                        public RxMessage apply(McuBundle mcuBundle) throws Exception {
                            final McuMessage msg = mcuBundle.msg;
                            int res = client.send(msg.toBytes());
                            if (res > 0) {
                                return new RxMessage(RxMessage.DONE, mcuBundle);
                            } else {
                                throw new IOException();
                            }
                        }
                    })
                    .map(new Function<RxMessage, RxMessage>() {
                        @Override
                        public RxMessage apply(RxMessage rxMessage) throws Exception {
                            McuBundle bundle = (McuBundle) rxMessage.val;
                            McuMessage msg = bundle.msg;
                            ConfEditor editor = bundle.confEditor;

                            if (!msg.requiresAck()) {
                                return new RxMessage(RxMessage.DONE);
                            }
                            //从ack消息队列里找对应的返回消息，如果找到了，则返回RxMessage，表示发送成功
                            while (true) {
                                McuMessage ackMsg = findAndPopAck(msg);
                                if (ackMsg != null) {
                                    if (editor != null) {
                                        editor.edit(ackMsg); //work after done
                                    }
                                    return new RxMessage(RxMessage.ACK, ackMsg.getSubmsg());
                                }
                                Thread.sleep(20);
                            }
                        }
                    })
                    //如果超过时限，则表示发送失败
                    .timeout(ACK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
        //未连接，则返回连接错误
        else {
            Flowable.just("")
                    .map(new Function<String, RxMessage>() {
                        @Override
                        public RxMessage apply(String s) throws Exception {
                            throw new IOException();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }


    /*这个方法同上，不同仅仅在于返回一个flowable供进一步的调用*/
    public Flowable<RxMessage> sendSequential(final List<McuBundle> bundleList) {
        if (client.getState() == TcpClient.STATE_CONNECTED) {
            Log.i("MCU", "send sequential start");
            return Flowable.fromIterable(bundleList)
                    .map(new Function<McuBundle, RxMessage>() {
                        @Override
                        public RxMessage apply(McuBundle mcuBundle) throws Exception {
                            final McuMessage msg = mcuBundle.msg;
                            Log.i("MCU", "send message");
                            int res = client.send(msg.toBytes());
                            if (res > 0) {
                                return new RxMessage(RxMessage.DONE, mcuBundle);
                            } else {
                                throw new IOException();
                            }
                        }
                    })
                    .map(new Function<RxMessage, RxMessage>() {
                        @Override
                        public RxMessage apply(RxMessage rxMessage) throws Exception {
                            Log.i("MCU", "edit conf");
                            McuBundle bundle = (McuBundle) rxMessage.val;
                            McuMessage msg = bundle.msg;
                            ConfEditor editor = bundle.confEditor;

                            if (!msg.requiresAck()) {
                                return new RxMessage(RxMessage.DONE);
                            }
                            while (true) {
                                McuMessage ackMsg = findAndPopAck(msg);
                                if (ackMsg != null) {
                                    if (editor != null) {
                                        editor.edit(ackMsg); //work after done
                                    }
                                    return new RxMessage(RxMessage.ACK, ackMsg.getSubmsg());
                                }
                                Thread.sleep(20);
                            }
                        }
                    })
                    .timeout(ACK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return Flowable.just("")
                    .map(new Function<String, RxMessage>() {
                        @Override
                        public RxMessage apply(String s) throws Exception {
                            throw new IOException();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    /*发送单个消息，如果消息需要返回则处理逻辑同上，confEditor为操作执行成功之后对会议状态的修改器*/
    public void send(final McuMessage msg, final RxSubscriber subscriber, final ConfEditor confEditor) {
        if (client.getState() == TcpClient.STATE_CONNECTED) {
            Flowable.just(msg)
                    //send
                    .map(new Function<McuMessage, McuMessage>() {
                        @Override
                        public McuMessage apply(McuMessage s) throws Exception {
                            int res = client.send(msg.toBytes());
                            if (res > 0) {
                                return msg;
                            } else {
                                throw new IOException();
                            }
                        }
                    })
                    //wait ack
                    .map(new Function<McuMessage, RxMessage>() {
                        @Override
                        public RxMessage apply(McuMessage mcuMessage) throws Exception {
                            if (!mcuMessage.requiresAck()) {
                                return new RxMessage(RxMessage.DONE);
                            }
                            while (true) {
                                McuMessage ackMsg = findAndPopAck(mcuMessage);
                                if (ackMsg != null) {
                                    return new RxMessage(RxMessage.ACK, ackMsg.getSubmsg());
                                }
                                Thread.sleep(20);
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(ACK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .doOnNext(new Consumer<RxMessage>() {
                        @Override
                        public void accept(RxMessage rxMessage) throws Exception {
                            confEditor.edit(rxMessage);
                        }
                    })
                    .subscribe(subscriber);
        } else {
            Flowable.just("")
                    .map(new Function<String, RxMessage>() {
                        @Override
                        public RxMessage apply(String s) throws Exception {
                            throw new IOException();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }

    /*同上，不同仅在于没有confEditor*/
    public void send(final McuMessage msg, final RxSubscriber subscriber) {
        if (client.getState() == TcpClient.STATE_CONNECTED) {
            Flowable.just(msg)
                    //send
                    .map(new Function<McuMessage, McuMessage>() {
                        @Override
                        public McuMessage apply(McuMessage s) throws Exception {
                            int res = client.send(msg.toBytes());
                            if (res > 0) {
                                return msg;
                            } else {
                                throw new IOException();
                            }
                        }
                    })
                    //wait ack
                    .map(new Function<McuMessage, RxMessage>() {
                        @Override
                        public RxMessage apply(McuMessage mcuMessage) throws Exception {
                            if (!mcuMessage.requiresAck()) {
                                return new RxMessage(RxMessage.DONE);
                            }
                            while (true) {
                                McuMessage ackMsg = findAndPopAck(mcuMessage);
                                if (ackMsg != null) {
                                    return new RxMessage(RxMessage.ACK, ackMsg.getSubmsg());
                                }
                                Thread.sleep(20);
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(ACK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .subscribe(subscriber);
        } else {
            Flowable.just("")
                    .map(new Function<String, RxMessage>() {
                        @Override
                        public RxMessage apply(String s) throws Exception {
                            throw new IOException();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }


    /*线程启动运行的方法*/
    private void threadStart() {
        boolean hasInited = true;
        for (BaseCyclicThread thread : threadList) {
            if (thread == null) {
                hasInited = false;
                break;
            }
        }

        if (!hasInited) {
            threadInit();
        }

        for (BaseCyclicThread thread : threadList) {
            thread.start();
        }

    }

    /*线程终止方法*/
    private void threadStop() {
        for (BaseCyclicThread thread : threadList) {
            thread.quit();
        }
        threadList.clear();

        ackListLock.lock();
        try {
            ackList.clear();
        } finally {
            ackListLock.unlock();
        }

        recvBuff.flush();
    }

    /*线程初始化方法，如果需要添加线程，在这个方法里面添加*/
    private void threadInit() {

        ackListLock.lock();
        try {
            ackList.clear();
        } finally {
            ackListLock.unlock();
        }

        recvBuff.flush();

        threadStop();

        BaseCyclicThread checkConnectThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(1000);
                    if (client.getState() != TcpClient.STATE_CONNECTED) {
                        disconnect();
                        connect(new RxSubscriber() {
                            @Override
                            public void onRxResult(Object o) {
                            }

                            @Override
                            public void onRxError(Throwable e) {
                            }
                        });
                    }
                    if (commListener != null) {
                        commListener.onConnectionChanged(client.getState());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadList.add(checkConnectThread);

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
                    if (msg.getType() == McuMessage.TYPE_RES) {
                        ackListLock.lock();
                        try {
                            McuBundle bundle = new McuBundle();
                            bundle.msg = msg;
                            bundle.timeRecv = System.currentTimeMillis();
                            bundle.subscriber = null;
                            ackList.add(bundle);
                        } finally {
                            ackListLock.unlock();
                        }
                    } else {
                        //send other types of messages to listener
                        if (commListener != null) {
                            commListener.onRecv(msg);
                        }
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadList.add(recvThread);

        //a single thread to pulse
        BaseCyclicThread pulseThread = new BaseCyclicThread() {
            @Override
            public void work() {
                try {
                    Thread.sleep(1000);
                    McuBundle bundle = new McuBundle();
                    bundle.msg = McuMessage.buildLogin("term", "123456");
                    bundle.subscriber = null;
                    send(bundle.msg, new RxSubscriber<RxMessage>() {
                        @Override
                        public void onRxResult(RxMessage msg) {
                            BaseMessage resMsg = (BaseMessage) msg.val;
                        }

                        @Override
                        public void onRxError(Throwable e) {
                            if (commListener != null) {
                                commListener.onConnectionChanged(client.getState());
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadList.add(pulseThread);

        //dieout recv ack messages
        BaseCyclicThread msgDieoutThread = new BaseCyclicThread() {
            @Override
            public void work() {
                ackListLock.lock();
                try {
//                    Log.i("MCU", "ackList size = " + ackList.size());
                    if (ackList.size() == 0){
                        //skip the check
                    }
                    else {
                        McuBundle bundle = ackList.getFirst();
                        if (System.currentTimeMillis() - bundle.timeRecv > ACK_TIMEOUT) {
                            ackList.removeFirst();
                        }
                    }
                } finally {
                    ackListLock.unlock();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        threadList.add(msgDieoutThread);
    }

    /* ************************************************************
     *对于消息分为两种，需要ACK和不需要ACK
     * 接收和发送分为两个线程，如果一个消息发送之后需要ACK，则到ACK接收队列中找对应的
     * find ACK given the req message, and pop it from the list
     * ************************************************************/
    public McuMessage findAndPopAck(McuMessage msg) {
        McuBundle bundle = null;

        if (msg.getType() == McuMessage.TYPE_REQ) {
            ReqMessage reqMsg = (ReqMessage) msg.getSubmsg();
            ackListLock.lock();
            try {
                boolean foundReq = false;
                int idx = -1;
                for (int m = 0; m < ackList.size(); m++) {
                    McuBundle bb = ackList.get(m);
                    if (bb.msg.getSubtype() == ResMessage.CONF_ALL && reqMsg.getType() == ReqMessage.REQ_CONF_ALL) {
                        foundReq = true;
                        idx = m;
                        break;
                    } else if (bb.msg.getSubtype() == ResMessage.CONF && reqMsg.getType() == ReqMessage.REQ_CONF) {
                        foundReq = true;
                        idx = m;
                        break;
                    } else if (bb.msg.getSubtype() == ResMessage.TERM_ALL && reqMsg.getType() == ReqMessage.REQ_TERM_ALL) {
                        foundReq = true;
                        idx = m;
                        break;
                    } else if (bb.msg.getSubtype() == ResMessage.CONF_CONFIG && reqMsg.getType() == ReqMessage.REQ_CONF_CONFIGED) {
                        foundReq = true;
                        idx = m;
                        break;
                    }
                }

                if (foundReq && idx > 0) {
                    bundle = ackList.get(idx);
                    ackList.remove(idx);
                }
            } finally {
                ackListLock.unlock();
            }
        } else if (msg.getType() == McuMessage.TYPE_CREATE_CONF) {
            //            ConfConfigMessage confConfigMessage = (ConfConfigMessage) msg.getSubmsg();
            ackListLock.lock();
            try {
                boolean foundReq = false;
                int idx = -1;
                for (int m = 0; m < ackList.size(); m++) {
                    McuBundle bb = ackList.get(m);
                    if (bb.msg.getSubtype() == ResMessage.CREATE_CONF) {
                        foundReq = true;
                        idx = m;
                        break;
                    }
                }
                if (foundReq && idx > 0) {
                    bundle = ackList.get(idx);
                    ackList.remove(idx);
                }
            } finally {
                ackListLock.unlock();
            }
        } else if (msg.getType() == McuMessage.TYPE_USER) {
            //            UserMessage userMsg = (UserMessage) msg.getSubmsg();
            ackListLock.lock();
            try {
                boolean foundReq = false;
                int idx = -1;
                for (int m = 0; m < ackList.size(); m++) {
                    McuBundle bb = ackList.get(m);
                    if (bb.msg.getSubtype() == ResMessage.USER) {
                        foundReq = true;
                        idx = m;
                        break;
                    }
                }
                if (foundReq && idx > 0) {
                    bundle = ackList.get(idx);
                    ackList.remove(idx);
                }
            } finally {
                ackListLock.unlock();
            }
        }

        if (bundle != null) {
            return bundle.msg;
        } else {
            return null;
        }

    }

    public void setCommListener(CommListener listener) {
        this.commListener = listener;
        McuBundle bundle = new McuBundle();
    }

    /*
     * this interface handles non-async calling （返回非异步访问的并发回调接口）
     */
    public interface CommListener {

        //tcp连接问题
        void onConnectionChanged(int state);

        //处理MCU的转发报文
        void onRecv(McuMessage rxMessage);
    }

    /*MCU消息体bundle*/
    static public class McuBundle {
        long timeRecv; //接收消息时间
        McuMessage msg; //消息本体
        RxSubscriber subscriber; //处理消息发送结果的subscriber
        ConfEditor confEditor; //处理消息完成之后对会议的编辑器
    }

}


