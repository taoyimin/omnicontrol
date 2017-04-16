package cn.diaovision.omnicontrol.core.model.conference;

/**
 * Created by liulingfeng on 2017/4/10.
 */

public class Term {
    public static final int STAT_OFF = 0;
    public static final int STAT_ON = 1;
    public static final int STAT_CONNECTED = 2;

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


    //new attributes (for application)
    int status; //连接状态
    int vol; //音量大小
    boolean isMuted; //是否静音
    boolean isSpeaking; //是否发言
    boolean videoEnabled; //是否传输画面
    boolean isChair; //是否是主席
}
