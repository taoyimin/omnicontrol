package cn.diaovision.omnicontrol.core.model.conference;

/**
 * Created by liulingfeng on 2017/4/10.
 */

public class Term {
    public static final int STATE_ONLINE = 0;
    public static final int STATE_OFFLINE = 1;

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CHAIR = 1;
    public static final int TYPE_SELECTVIEW = 2;

//    long id;
//    int type; //终端类型，1：广播端，即主会场，2：选看端，0：其他
//    String name; //MCU端配置的工区名
//    String alias; //H323协议栈用到的E164别名
//    String ip;
//    String descrip;
//    byte state; //0：不在线，1：在线，2：已经连接
//    byte micMute; //0：终端Mike静音（哑音），1：终端Mike不静音（取消哑音）
//    byte speakerMute; //0：终端Speaker静音，1：终端Speaker不静音
//    byte noSendVideo; //0：发送图像，1：不发送图像
//    boolean mixVoice; //true：混音，false：未混音
//    boolean mixVideo; //true:媒体混音,false:未混
//    int refresh; //刷新标记
//    boolean inviteFlag; //掉线手动邀请标志


    //new attributes (for application)
//    int vol; //音量大小

    long id; //4 bytes
    String ip; //ip address
    String name;
    int status; //连接状态
    boolean isMuted; //是否静音
    boolean isSpeaking; //是否发言
    boolean videoEnabled; //是否传输画面
    int type;

    public Term(long id) {
        this.id = id;
        isMuted = false;
        isSpeaking = false;
        type = TYPE_NORMAL;
        videoEnabled = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    public boolean isVideoEnabled() {
        return videoEnabled;
    }

    public void setVideoEnabled(boolean videoEnabled) {
        this.videoEnabled = videoEnabled;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
