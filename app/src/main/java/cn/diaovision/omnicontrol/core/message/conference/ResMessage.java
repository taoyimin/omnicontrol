package cn.diaovision.omnicontrol.core.message.conference;

import java.io.Serializable;

import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * Created by liulingfeng on 2017/4/13.
 */

public class ResMessage implements BaseMessage, Serializable{
    public final static int CONF_ALL = 1;
    public final static int CONF = 2;
    public final static int TERM_ALL = 3;
    public final static int TERM = 4;
    public final static int DEL_TERM = 5;
    public final static int MIX_AUDIO = 6;
    public final static int MIX_VIDEO = 7;
    public final static int RELEASE_CHAIR = 8;
    public final static int ASSIGN_CHAIR = 9;
    public final static int CONF_MODE_CHAIR = 10;
    public final static int CONF_MODE_VOICE = 11;
    public final static int DEL_CONF = 12;
    public final static int CONF_CONFIG = 17;
    public final static int ADDRBOOK = 19;
    public final static int CREATE_CONF = 20;
    public final static int INVITE_ATTEND = 31;
    public final static int USER = 32;
    public final static int USERLIST = 33;
    public final static int ADD_CONF = 34;
    public final static int ADD_ATTEND = 35;
    public final static int SPEAKER = 36; //发言申请
    public final static int CHAIRUSER = 0x25; //发言申请

    //定义发给WEB页面的错误字
    public final static int ERROR_WEB_CONF_ID = 0x1000; //会议ID错误
    public final static int ERROR_WEB_ACCESS = 0x1003;	//权限
    public final static int ERROR_WEB_LOGIN = 0x1004;	//登录
    public final static int ERROR_WEB_CONF_NUM = 0x1006; //会议超过最大数
    public final static int ERROR_WEB_TERM_NUM = 0x1007; //终端超过最大数
    public final static int ERROR_WEB_CONF_NAME = 0x1008; //会议重名
    public final static int ERROR_WEB_TERM_NAME = 0x1009; //终端重名
    public final static int ERROR_WEB_USER_NAME = 0x100a;	//该用户存在
    public final static int ERROR_WEB_USER_NO = 0x100b;//没有找到此用户
    public final static int ERROR_WEB_CONF_TIME = 0x100c;	//预约会议时间错误
    public final static int ERROR_WEB_CONF_MIX = 0x100d;	//只能有一个媒体混合的会议
    public final static int ERROR_WEB_DELDEF = 0x100e;	//不能删除默认会议
    public final static int ERROR_WEB_CONF_STR = 0x100f;	//流媒体端口冲突
    public final static int ERROR_WEB_TERM_ADDR = 0x1010;	//终端地址冲突
    public final static int ERROR_WEB_TERM_NOTFOUND = 0x1011;	//终端没有找到
    public final static int ERROR_WEB_INVALID_PASSWORD = 0x1012;	//密码不对

    //1.所有会议信息响应;2.指定会议响应;
    //3.会议中所有终端信息响应; 4.指定终端信息响应;
    //5.删除终端响应; 6.混音响应; 7.多画面响应; 8.释放主席响应;
    //9.指定主席响应
    //10.设置会议方式--主席方式; 11.设置会议方式--语音激励
    //12.删除会议响应
    //30.创建会议响应		31.邀请终端响应
    //32.用户登录响应		33.获取用户列表响应
    //34.添加会议响应		35.添加终端响应
    public byte type;

    public byte status; //响应状态 1 == 成功 0 == 失败
    public int error; //错误字(包含失败的原因)	在添加会议成功时此位代表会议ID

    // 响应包含的信息 0 == NULL 1 == ConfInfo 2== TermInfo
    // 3 ==ConfData  4==TermData
    // 5 == UserList
    public final static int INFO_CONFINFO = 1;
    public final static int INFO_TERMINFO = 2;
    public final static int INFO_CONFDATA = 3;
    public final static int INFO_TERMDATA = 4;
    public final static int INFO_USERLIST = 5;
    byte infoType;

    BaseMessage infoMsg;

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[5+infoMsg.calcMessageLength()];
        bytes[0] = type;
        System.arraycopy(ByteUtils.int2bytes(error, 2), 0, bytes, 2, 2);
        bytes[4] = infoType;
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 5+infoMsg.calcMessageLength();
    }
}

