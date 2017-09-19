package cn.diaovision.omnicontrol;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import cn.diaovision.omnicontrol.conn.TcpClient;
import cn.diaovision.omnicontrol.core.model.device.matrix.MediaMatrix;
import cn.diaovision.omnicontrol.rx.RxBus;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class OmniControlApplication extends Application {
    final static private String PREF_NAME = "omnicontrol";
    private static Context applicationContext;

    //RxBus
    RxBus rxBus;

    //singleton tcpclient
    TcpClient mcuClient;

    MediaMatrix mediaMatrix;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext=getApplicationContext();
/*        SharedPreferences pref = getAppPreferences();
        String ip = pref.getString("ip", "192.168.1.1");
        int port = pref.getInt("port", 5000);
        saveAppPreference("ip", ip);
        saveAppPreference("port", port);
        mediaMatrix = new MediaMatrix.Builder().ip(ip).port(port).videoInInit(32).videoOutInit(32).id(1).build();

        //add camera if previously stored
        int camporto = getAppPreferences().getInt("cam_porto", -1);
        int camport = getAppPreferences().getInt("cam_port", -1);
        int cam_baudrate = getAppPreferences().getInt("cam_baudrate", -1);
        int cam_proto = getAppPreferences().getInt("cam_proto", -1);
        if (camporto >= 0) {
            mediaMatrix.addCamera(camporto, camport, cam_baudrate, cam_proto);
        }*/
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
        return getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    public void saveAppPreference(String name, String val){
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putString(name, val);
        editor.commit();
    }

    public void saveAppPreference(String name, int val){
        SharedPreferences.Editor editor = getAppPreferences().edit();
        editor.putInt(name, val);
        editor.commit();
    }

    public MediaMatrix getMediaMatrix() {
        return mediaMatrix;
    }

    public void setMediaMatrix(MediaMatrix mediaMatrix) {
        this.mediaMatrix = mediaMatrix;
    }

    public RxBus getRxBus() {
        return RxBus.getInstance();
    }

    public static Context getContext(){
        return applicationContext;
    }
}
