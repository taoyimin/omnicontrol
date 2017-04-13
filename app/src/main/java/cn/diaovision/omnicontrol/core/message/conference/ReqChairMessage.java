package cn.diaovision.omnicontrol.core.message.conference;

 /* *********************************
 * MCU转发过来的终端主席请求消息
 * DIR: MCU -> APP
 * Created by liulingfeng on 2017/4/13.
 * *********************************/

public class ReqChairMessage{
    //在SimpleMsg消息中增加主席操作消息，此消息实际上是MCU Manager发送给
    //WEB客户端的通知消息，在处理时按照MCU Manager的响应消息来处理
    //MCU Manager-->Web，MCU Manager转发MC的请求给WEB
    public final static byte TYPE_MGR_REQ_APPLYCHAIR = 0x60; //申请主席
    public final static byte TYPE_MGR_REQ_FREECHAIR = 0x61; //释放主席
    public final static byte TYPE_MGR_REQ_APPLYFLOOR = 0x63; //申请发言
    public final static byte TYPE_MGR_REQ_CANCELFLOOR = 0x64; //取消发言

    byte type; //0x60~0x64
    int confId;
    String confName; //32 bytes
    long termId; //4 bytes
    long termAddr; //ip addr
    String termName; //32 bytes
    String termGbkAlias; //32 bytes
    long broadcastId; //广播端ID
    long selectViewId;	//选看端ID
    long speechId; //点名发言端ID
}

