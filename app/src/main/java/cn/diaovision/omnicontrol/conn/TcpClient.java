package cn.diaovision.omnicontrol.conn;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liulingfeng on 2017/3/19.
 */

public class TcpClient {
    public static final int STATE_CONNECTED = 0;
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;

    private String ip;
    private int port;
    private Socket skt;
    private InputStream input;
    private OutputStream output;
    private AtomicInteger state;

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.state = new AtomicInteger(STATE_DISCONNECTED);
    }


    public int getState() {
        return state.get();
    }

    //synchronously connect (for rxjava)
    synchronized public void connect(){
        if (ip == null){
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
                return;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("U", "A error");
            }
        }
    }

    synchronized public void disconnect(){
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

    synchronized public int send(byte data[]){
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
            //if at disconnected or connecting state
            return -1;
        }
    }

    /*
     * receive from tcp socket synchronously
     */
    synchronized public int recv(byte data[]){
        if (state.get() == STATE_CONNECTED && input != null) {
            try {
                byte buff[] = new byte[1024];
                int len = input.read(buff);
                if (len > 0){
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

    private void onDisconnected(){
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
}
