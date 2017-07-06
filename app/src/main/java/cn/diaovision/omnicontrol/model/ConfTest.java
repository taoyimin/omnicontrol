package cn.diaovision.omnicontrol.model;

import android.util.Log;

import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.message.conference.McuMessage;

/**
 * Created by TaoYimin on 2017/7/4.
 */

public class ConfTest {
    TcpClient client;

    public void init(){
        client = new TcpClient("192.168.10.100", 6190);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.connect();
            }
        }).start();
    }

    public void sendMessage(){
        //McuMessage msg=McuMessage.buildReqConfAll();
        McuMessage msg=McuMessage.buildReqDeleteConf(0);
        int res = client.send(msg.toBytes());
        Log.i("conf","res="+res);
    }
}
