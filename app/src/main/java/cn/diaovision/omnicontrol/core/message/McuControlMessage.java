package cn.diaovision.omnicontrol.core.message;

/**
 * Created by liulingfeng on 2017/2/22.
 */

public class McuControlMessage {
    public McuControlMessage createLoginMessage(String name, String key){
        return new McuControlMessage();
    }
    public McuControlMessage createNewConfMesage(){
        return new McuControlMessage();
    }
    public McuControlMessage createDelConfMessage(){
        return new McuControlMessage();
    }
    public McuControlMessage createGetConfInfoMessage(){
        return new McuControlMessage();
    }
}
