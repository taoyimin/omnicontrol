package cn.diaovision.omnicontrol.core.model.conference;

import java.net.Socket;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.model.Config;

/**
 * Created by liulingfeng on 2017/4/5.
 */

public class Mcu {
    public static final byte STATE_ONLINE = 1;
    public static final byte STATE_OFFLINE = 0;

//    int id;
//    byte level; //1 byte, MCU级别，1：1级，段本部MCU，2：2级，车间MCU
//    byte type; //1 byte, MCU类型，1：广播端所在MCU，2：选看端所在MCU
//    byte state; //1 byte, MCU状态，0：不在线，1：在线
//    String name;
//    String descrip;
//    int termNum;
//    Socket skt;

    //application specific
    String ip;
    int port;

    Conf[] conf; //maximal 32

    public Mcu(String ip, int port){
        conf = new Conf[32];
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Conf[] getConf() {
        return conf;
    }

    public void setConf(Conf[] conf) {
        this.conf = conf;
    }
}
