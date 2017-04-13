package cn.diaovision.omnicontrol.core.message.conference;

import cn.diaovision.omnicontrol.util.ByteUtils;

/* ********************
 * 流媒体消息
 * Created by liulingfeng on 2017/4/13.
 * ********************/

public class StreamMediaMessage implements BaseMessage{
    //消息类型  0.关闭本地流媒体 1.打开本地流媒体
    //2.关闭服务器流媒体 3.打开服务器流媒体
    //0x10.关闭选看端流媒体，0x11.打开选看端流媒体
    public static final byte CLOSE_LOCAL = 0;
    public static final byte OPEN_LOCAL = 1;
    public static final byte CLOSE_SERVER = 2;
    public static final byte OPEN_SERVER = 3;
    public static final byte CLOSE_SELECTVIEW = 0x10;
    public static final byte OPEN_SELECTVIEW = 0x11;

    byte type;

    int confId;
    long ipAddr;		//流媒体IP地址
    int videoPort;	//流媒体组播视频端口
    int audioPort;	//流媒体组播音频端口

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[calcMessageLength()];
        bytes[0] = type;
        System.arraycopy(ByteUtils.int2bytes(confId, 2), 0, bytes, 1, 2);
        System.arraycopy(ByteUtils.int2bytes((int) ipAddr, 4), 0, bytes, 3, 4);
        System.arraycopy(ByteUtils.int2bytes(videoPort, 2), 0, bytes, 7, 2);
        System.arraycopy(ByteUtils.int2bytes(audioPort, 2), 0, bytes, 9, 2);


        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 1+2+4+2+2;
    }
}

