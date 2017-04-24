package cn.diaovision.omnicontrol.core.message.conference;

import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * Header of MCUMessage
 * Created by liulingfeng on 2017/4/13.
 */


public class Header{
    int len; // 2 bytes, 消息长度
    byte version;		//软件版本号

    //消息类型	1.Request		2.Response
    //			3.Create_conf(创建会议)
    //			4.Invite_Term(邀请终端)
    //			5.用户登录
    //			6.添加会议
    //			7.添加终端
    //			8.添加终端到地址簿
    //			9.删除地址簿
    //			10.版本信息
    //			11.发言申请消息
    //			12.系统配置
    byte type;

    public Header(int len, byte type) {
        this.len = len;
        this.type = type;
        this.version = 0x01;//TODO: verify version
    }

    public byte[] toBytes(){
        byte[] bytes = new byte[4]; //2+1+1

        //len += 4;

        byte[] lenHex = ByteUtils.int2bytes(len, 2);
        bytes[0] = lenHex[0];
        bytes[1] = lenHex[1];

        bytes[2] = version;
        bytes[3] = type;
        return bytes;
    }

    public boolean verifyHeader(){
        return version == McuMessage.VERSION;
    }
}

