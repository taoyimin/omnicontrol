package cn.diaovision.omnicontrol.core.message.conference;

import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * Created by liulingfeng on 2017/4/13.
 */

public class ReqMessage implements BaseMessage{
    /*Request MSG */
    public final static byte REQ_CONF_ALL = 1; //获取所有会议信息
    public final static byte REQ_CONF = 2; //获取某一个会议信息
    public final static byte REQ_TERM_ALL = 3; //获取所有参会者信息
    public final static byte REQ_TERM = 4; //获取某一个参会者信息
    public final static byte DEL_TERM = 5; //删除一个参会者
    public final static byte DEl_DEFAULT_CONF_TERM_CONFIG = 51; //删除默认会议模版的终端（wtf）
    public final static byte MIX_AUDIO = 6; //混音
    public final static byte MIX_VIDEO = 7; //多画面
    public final static byte RELEASE_CHAIR = 8; //release chairman of the conference
    public final static byte ASSIGN_CHAIR = 9; //assign chairman of the conference
    public final static byte SET_CONF_MODE_CHAIR = 10; //指定会议模式为主席模式
    public final static byte SET_CONF_MODE_VOICE = 11; //set conference mode as voice
    public final static byte DEL_CONF = 12; //delete conference
    public final static byte SET_CONF_MODE_DIRECTOR = 13; //set conference mode as director
    public final static byte SWITCH_MEDIA = 14; //switch media
    public final static byte ASSIGN_SELECTVIEW = 15; //选看端指定

    public final static byte REQ_USERLIST = 16; //获取用户列表 (not used)

    public final static byte REQ_CONF_CONFIGED = 17; //获取已配置的会议信息
    public final static byte DEl_CONF_CONFIGED = 18; //删除已配置的会议信息
    public final static byte GET_ADDRBOOK = 19; //get address book
    public final static byte CMD_PIC = 20; //图像台命令
    public final static byte DISABLE_INPUT = 21; //禁止图像声音输入
    public final static byte ENABLE_INPUT = 22; //允许图像声音输入
    public final static byte SET_CONF_MODE_AUTO = 23; //设置会议为自动模式
    public final static byte SET_CONF_MODE_AUTOSELECT = 24; //设置会议为自动选看模式
    public final static byte HANGUP_TERM = 25; //挂断参会者
    public final static byte REQ_SYSCONFIG = 26; //request system config

    public final static byte REQ_OPERATOR = 28; //operator request (not used)

    public final static byte DEF_CONF_ATTEND_INFO = 30; //获取默认会议模版的终端配置

    public final static byte SET_CONF_MODE_AUTOINVITE = 31; //设置会议为自动邀请模式
    public final static byte SET_CONF_MODE_MANUALINVITE = 32; //设置会议为手动邀请模式
    public final static byte SET_CONF_MODE_WATCHSYNC = 33; //设置会议为选看同步模式
    public final static byte CANCEL_CONF_MODE_WATCHSYNC = 34; //取消会议选看同步模式

    public final static byte CALLOVER = 40; //点名发言
    public final static byte CONTI_MODE = 41; //四画面模式

    public final static byte REQ_DS = 44; //请求或停止双流
    public final static byte DISABLE_OUTPUT = 60; //禁止输出
    public final static byte ENABLE_OUTPUT = 61; //允许输出



    public final static byte REQ_CHAIR = 0x50; //申请主席 (from term side)
    public final static byte FREE_CHAIR = 0x51; //释放主席 (from term side)
    public final static byte WITHDRAW_CHAIR = 0x52; //回收主席 (from term side)
    public final static byte REQ_FLOOR = 0x53; //申请发言(from term side)
    public final static byte CANCEL_FLOOR = 0x54; //取消发言(from term side)

    byte type = 0;
    //1.所有会议信息请求;2.指定会议请求;
    //3.会议中所有终端信息请求; 4.指定终端信息请求;
    //5.删除终端; 6.混音; 7.多画面; 8.释放主席; 9.指定主席
    //10.设置会议方式--主席方式; 11.设置会议方式--语音激励
    //12.删除会议; 13.设置会议方式--导演方式;14.媒体切换
    //15.发言者选看;		16.获取所有用户名列表
    //17.获取所有已配置的会议	18.删除已配置的会议
    //19.获取址址簿		20.图像台命令
    //21.禁止图象声音输入 22.充许图象声音输入
    //23.设置会议方式--自动浏览方式
    //24.设置会议方式--自动选看方式
    //25.挂断终端
    //26.系统配置请求
    //27.指定任意观看端 28。指定被任意选看端

    int confId = 0; //会议id
    long[] termId; //参会者id，大小为100个

    public ReqMessage(byte type) {
        this.type = type;
        termId = new long[100];
    }

    @Override
    public byte[] toBytes() {
        int msgLen = 403;
        byte[] bytes = new byte[msgLen];
        bytes[0] = type;
        System.arraycopy(ByteUtils.int2bytes(confId, 2), 0, bytes, 1, 2);
        if (termId != null){
            for (int m = 0; m < termId.length; m ++){
                System.arraycopy(ByteUtils.int2bytes((int)termId[m], 4), 0, bytes, 2+m*4, 4);
            }
        }
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 403;
    }
}

