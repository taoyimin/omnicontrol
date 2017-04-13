package cn.diaovision.omnicontrol.core.model.conference;

import java.net.Socket;

import cn.diaovision.omnicontrol.core.message.conference.ConfConfigMessage;

/**
 * Created by liulingfeng on 2017/4/5.
 */

public class Mcu {
    int id;
    int level; //1 byte, MCU级别，1：1级，段本部MCU，2：2级，车间MCU
    int type; //1 byte, MCU类型，1：广播端所在MCU，2：选看端所在MCU
    int state; //1 byte, MCU状态，0：不在线，1：在线
    String name;
    String ip;
    String descrip;
    int termNum;
    Socket skt;

    ConfConfigMessage confConfigTemplate;
    ConfConfigMessage confConfig;

    LiveConf[] conf; //32

    public Mcu(){
        conf = new LiveConf[32];
    }
}
