package cn.diaovision.omnicontrol.core.model.conference;

/**
 * Created by liulingfeng on 2017/4/10.
 */

public class Term {
    long id;
    int type; //终端类型，1：广播端，即主会场，2：选看端，0：其他
    String name; //MCU端配置的工区名
    String alias; //H323协议栈用到的E164别名
    String ip;
    String descrip;
    byte state; //0：不在线，1：在线，2：已经连接
    byte micMute; //0：终端Mike静音（哑音），1：终端Mike不静音（取消哑音）
    byte speakerMute; //0：终端Speaker静音，1：终端Speaker不静音
    byte noSendVideo; //0：发送图像，1：不发送图像
    boolean mixVoice; //true：混音，false：未混音
    boolean mixVideo; //true:媒体混音,false:未混
    int refresh; //刷新标记
    boolean inviteFlag; //掉线手动邀请标志

    static public class Attr{
        int id; //2 bytes
        int type;  //2 bytes
        long addr; //4 bytes
        int bandwidth; //2 bytes
    }

    static public class Config{
        String name; //32 bytes
        String gbkAlias; //32 bytes alias in GBK coding
        long id; //term id
        long addr; //4 bytes, ip addr
        int port; //2 bytes
        byte isChair; //是否是主席（或广播端）
        byte isSelected; //是否是选看端
        byte status; //终端状态 0--已配置, 1--正在邀请中, 2--等待加入,
        		     // 3--成功加入会议, 4--邀请失败, 5--正在挂断终端
                     // 6--终端删除, 7--终端已退出会议, 8--终端已挂断
                     // 9--自动邀请3次失败, 10--终端掉点
        byte mix; //混合标志
        byte viaGateway; //是否是通过网关邀请
        String telNumGateway; //20 bytes, 通过网关的地址电话号

        byte videoFlag; //视频标志
        byte audioFlag; //音频标志
        byte dataFlag; //数据标志

        byte pingOnline; //ping 诊断

        byte chair;			//0:正常 1:主席
        byte floor;			//0:正常 1:申请主席
        //byte[] reserved;     //保留
        byte type;       //0--呼入  1--呼出  2--呼入呼出
        int bandwidth;	        //终端带宽
    }
}
