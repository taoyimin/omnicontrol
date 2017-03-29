package cn.diaovision.omnicontrol.conn;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liulingfeng on 2017/3/19.
 */

public class TcpClient {
    public static final int STATE_CONNECTED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;

    String ip;
    int port;
    Socket skt;
    InputStream input;
    OutputStream output;
    AtomicInteger state;
    ReadThread readThread;
    DataListener dataListener;

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.state = new AtomicInteger(STATE_DISCONNECTED);
    }

    public void connect() throws IOException{
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (ip == null || port < 1024){
                    onDisconnected();
                    return;
                }
                else {
                    if (state.get() == STATE_CONNECTING || state.get() == STATE_CONNECTED ){
                        return;
                    }

                    if (skt != null){
                        try{
                            skt.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    skt = new Socket();
                    try {
                        skt.setKeepAlive(true);
                        state.set(STATE_CONNECTING);
                        skt.connect(new InetSocketAddress(ip, port), 2000);
                        input = skt.getInputStream();
                        output = skt.getOutputStream();
                        state.set(STATE_CONNECTED);
                        startRead();
                        return;
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    //synchronously connect (for rxjava)
    public void connectSync(){
        if (ip == null || port < 1024){
            onDisconnected();
            return;
        }
        else {
            if (state.get() == STATE_CONNECTING || state.get() == STATE_CONNECTED ){
                return;
            }

            if (skt != null){
                try{
                    skt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            skt = new Socket();
            try {
                skt.setKeepAlive(true);
                skt.setSoTimeout(5);
                state.set(STATE_CONNECTING);
                skt.connect(new InetSocketAddress(ip, port), 2000);
                input = skt.getInputStream();
                output = skt.getOutputStream();
                state.set(STATE_CONNECTED);
                startRead();
                return;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("U", "A error");
            }
        }
    }

    public void disconnectSync(){
        try{

            if (state.get() == STATE_CONNECTING){
                skt.close();
            }

            state.set(STATE_DISCONNECTED);
            if (input != null){
                input.close();
            }
            if (output != null){
                output.close();
            }
            if (skt != null){
                skt.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    if (state.get() == STATE_CONNECTING){
                        skt.close();
                    }

                    state.set(STATE_DISCONNECTED);
                    if (input != null){
                        input.close();
                    }
                    if (output != null){
                        output.close();
                    }
                    if (skt != null){
                        skt.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int sendSync(byte data[]){
        if (state.get() == STATE_CONNECTED && output != null) {
            try {
                output.write(data);
                output.flush();
                return data.length;
            } catch (IOException e) {
                e.printStackTrace();
                onDisconnected();
                return -1;
            }
        }
        else {
            //if disconnected or connecting
            return -1;
        }
    }

    public void recvRx(Consumer consumer){
        Flowable.create(new FlowableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(FlowableEmitter<byte[]> e){
                while(true){
                    if (state.get() == STATE_CONNECTED){
                        byte buff[] = new byte[1024];
                        int len = 0;
                        try {
                            len = input.read(buff);
                             if (len > 0){
                                 byte dataRecv[] = new byte[len];
                                 e.onNext(dataRecv);
                             }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            e.onError(e1);
                        }

                    }
                }
            }
        }, BackpressureStrategy.BUFFER)
                .map(new Function<byte[], String>() {
                    @Override
                    public String apply(byte[] bytes) throws Exception {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(consumer);
    }

    //synchronously receive (for rxjava)
    public int recvSync(byte data[]){
        if (state.get() == STATE_CONNECTED && input != null) {
            try {
                byte buff[] = new byte[1024];
                int len = input.read(buff);
                if (len > 0 && dataListener != null){
                    System.arraycopy(buff, 0, data, 0, len);
                    return len;
                }
                else {
                    return 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
                onDisconnected();
                return -1;
            }
        }
        else {
            //if disconnected or connecting
            return -1;
        }
    }

    public void onDisconnected(){
        try{
            state.set(STATE_DISCONNECTED);
            if (input != null){
                input.close();
            }
            if (output != null){
                output.close();
            }
            if (skt != null){
                skt.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRead(){
        if (readThread != null){
            readThread.quit();
        }

        readThread = new ReadThread();
        readThread.start();
    }

    public void stopRead(){
        if (readThread != null){
            readThread.quit();
        }
    }

    public interface DataListener{
        void onDataReceived(byte data[], int len);
    }

    private class ReadThread extends Thread{
        boolean isRunning = true;

        synchronized public void quit(){
            isRunning = false;
            this.interrupt();
        }

        @Override
        public void run() {
            while (true){
                if (!isRunning) break;
                try {
                    Thread.sleep(50);
                    if (state.get() == STATE_CONNECTED){
                        try {
                            byte buff[] = new byte[1024];
                            int len = input.read(buff);
                            if (len > 0 && dataListener != null){
                                dataListener.onDataReceived(buff, len);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
