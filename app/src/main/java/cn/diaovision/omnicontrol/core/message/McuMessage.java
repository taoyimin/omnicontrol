package cn.diaovision.omnicontrol.core.message;

/**
 * 视频会议消息协议
 * Created by liulingfeng on 2017/2/22.
 */

public class McuMessage {
    /*MCU manager messages*/
    /*Request MSG */

    /*ACK MSG */

    /*client messages*/
    /*Request MSG */

    /*ACK MSG */


    public byte[] buildHeader(int len, int ver, int msgType){
        byte[] bytes = new byte[2+2+1];
        return bytes;
    }

    public byte[] buildLoginMessage(String name, String password){
        byte[] bytes = new byte[64];
        return bytes;
    }


    static public class Builder{
    }
}
