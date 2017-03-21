package cn.diaovision.omnicontrol.core.message;

/**
 * Created by liulingfeng on 2017/2/22.
 */

public class McuMessage {
    public McuMessage createLoginMessage(String name, String key){
        return new McuMessage();
    }
    public McuMessage createNewConfMesage(){
        return new McuMessage();
    }
    public McuMessage createDelConfMessage(){
        return new McuMessage();
    }
    public McuMessage createGetConfInfoMessage(){
        return new McuMessage();
    }
}
