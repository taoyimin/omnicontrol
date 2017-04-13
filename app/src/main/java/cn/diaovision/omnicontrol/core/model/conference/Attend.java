package cn.diaovision.omnicontrol.core.model.conference;

/** 参会者
 * Created by liulingfeng on 2017/4/5.
 */

public class Attend{
    String ip = "192.168.1.1";
    int port = 553;
    String name = "";

    public Attend(){
    }

    public Attend(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    static public class TermAttr{
        int id; //2 bytes
        int type;  //2 bytes
        long addr; //4 bytes, ip addr
        int bandwidth; //终端速率
    }
}
