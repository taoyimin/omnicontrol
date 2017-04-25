package cn.diaovision.omnicontrol.core.model.conference;

import java.net.Socket;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;
import cn.diaovision.omnicontrol.model.Config;

/**
 * Created by liulingfeng on 2017/4/5.
 */

public class Mcu {
    public static final int STATE_ONLINE = 1;
    public static final int STATE_OFFLINE = 0;

    int id;
    int level; //1 byte, MCU级别，1：1级，段本部MCU，2：2级，车间MCU
    int type; //1 byte, MCU类型，1：广播端所在MCU，2：选看端所在MCU
    int state; //1 byte, MCU状态，0：不在线，1：在线
    String name;
    String ip;
    String descrip;
    int termNum;
    Socket skt;

    //application specific
    int port;

    Conf[] conf; //maximal 32

    public Mcu(){
        conf = new Conf[32];
    }

    public void init(Config cfg){
        ip = cfg.getMcuIp();
        port = cfg.getMcuPort();
    }
}
