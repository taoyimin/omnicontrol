package cn.diaovision.omnicontrol;

import android.app.Application;
import android.content.SharedPreferences;

import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.conn.UdpClient;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class OmniControlApplication extends Application {
    final static private String PREF_NAME = "omnicontrol";

    //Singleton udpclient
    UdpClient udpClient;

    //singleton tcpclient
    TcpClient mcuClient;

    @Override
    public void onCreate() {
        super.onCreate();
        updateUdpClient();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public SharedPreferences getAppPreferences(){
        return getSharedPreferences(PREF_NAME, MODE_APPEND);
    }

    public void updateUdpClient(){
        String udpServerIp = getAppPreferences().getString("udp_server_ip", "192.168.2.89");
        int udpServerPort = getAppPreferences().getInt("udp_server_port", 5000);
        if (udpClient == null) {
            udpClient = new UdpClient(udpServerIp, udpServerPort);
        }
    }

    public void updateMcuClient(){
    }

    public UdpClient getUdpClient(){
        if (udpClient == null){
            updateUdpClient();
        }
        return udpClient;
    }
}
