package cn.diaovision.omnicontrol.core.message;

import java.util.Date;
import java.util.List;

import cn.diaovision.omnicontrol.core.model.conference.Attend;
import cn.diaovision.omnicontrol.util.ByteUtils;

/**
 * 视频会议消息协议
 * Created by liulingfeng on 2017/2/22.
 */

public class McuMessage {
    private final static int VERSION = 2;
    /*MCU manager messages*/
    /*Request MSG */
    public final static int REQ_CONF_ALL = 1; //获取所有会议信息
    public final static int REQ_CONF = 2; //获取某一个会议信息
    public final static int REQ_ATTEND_ALL = 3; //获取所有参会者信息
    public final static int REQ_ATTEND = 4; //获取某一个参会者信息
    public final static int DEL_ATTEND = 5; //删除一个参会者
    public final static int AUDIO_MULTIPLEX = 6; //混音
    public final static int MULTI_PIC = 7; //多画面
    public final static int RELEASE_CHAIR = 8; //release chairman of the conference
    public final static int ASSIGN_CHAIR = 9; //assign chairman of the conference
    public final static int SET_CONF_MODE_CHAIR = 10; //指定会议模式为主席模式
    public final static int SET_CONF_MODE_VOICE = 11; //set conference mode as voice
    public final static int DEL_CONF = 12; //delete conference
    public final static int SET_CONF_MODE_DIRECTOR = 13; //set conference mode as director
    public final static int SWITCH_MEDIA = 14; //switch media
    public final static int ASSIGN_SELECTVIEW = 15; //选看端指定
    public final static int REQ_USERLIST = 16; //选看端指定
    public final static int REQ_CONF_CONFIGED = 17; //获取会议配置模版
    public final static int GET_ADDRBOOK = 18; //get address book
    public final static int CMD_PIC = 19; //图像台命令
    public final static int DISABLE_INPUT = 20; //禁止图像声音输入
    public final static int ENABLE_INPUT = 21; //允许图像声音输入
    public final static int CONFIG_CONF_AUTO = 22; //设置会议为自动模式
    public final static int CONFIG_CONF_AUTOSELECT = 23; //设置会议为自动选看模式
    public final static int HANGUP_ATTEND = 24; //挂断参会者
    public final static int REQ_SYSCONFIG = 25; //request system config
    @Deprecated
    public final static int REQ_OPERATOR = 26; //operator request


    /*ACK MSG */

    /*client messages*/
    /*Request MSG */

    /*ACK MSG */


    private byte[] header;
    private byte[] payload;

    public McuMessage(byte[] header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public byte[] buildHeader(int len, int ver, int msgType){
        byte[] bytes = new byte[2+2+1];
        return bytes;
    }


    public byte[] buildLogin(String name, String password){
        byte[] bytes = new byte[64];
        System.arraycopy(name.getBytes(), 0, bytes, 0, 32);
        System.arraycopy(password.getBytes(), 0, bytes, 32, 32);
        return bytes;
    }

    public byte[] build(){return null;}


    public byte[] toBytes(){
        byte[] bytes = new byte[header.length + payload.length];
        System.arraycopy(header, 0, bytes, 0, header.length);
        System.arraycopy(payload.length, 0, bytes, header.length, payload.length);
        return bytes;
    }

    /* **********************************
     * message header verification
     * **********************************/
    public boolean verifyHeader(){
        byte[] bytes = new byte[2];
        System.arraycopy(header, 2, bytes, 0, 2);
        if (ByteUtils.bytes2int(bytes) == VERSION){
            return true;
        }
        else {
            return false;
        }
    }

    /* **********************************
     * 获得所有会议信息
     * **********************************/
    public byte[] buildReqConfAll(){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF_ALL), new byte[0]).toBytes();
    }

    /* **********************************
     * 获得某个会议信息
     * **********************************/
    public byte[] buildReqConf(String id){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 获得已配置会议信息
     * **********************************/
    public byte[] buildReqConfConfiged(){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF_CONFIGED), new byte[0]).toBytes();
    }


    /* **********************************
     * 获得所有参会成员信息
     * **********************************/
    public byte[] buildReqAttendAll(String confId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求创建会议
     * **********************************/
    public byte[] buildReqCreateConf(Date dateStart, Date dateEnd){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求创建会议 (with mcu and confConfig)
     * **********************************/
    public byte[] buildReqCreateConf(String mcu, String conf, Date dateStart, Date dateEnd){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求结束会议
     * **********************************/
    public byte[] buildReqDeleteConf(){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求结束会议 (with mcu and confConfig)
     * **********************************/
    public byte[] buildReqDeleteConf(String mcu, String conf){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求发送会议视频流
     * **********************************/
    public byte[] buildReqStream(String confId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求静音参会者 (with mcu and confConfig)
     * **********************************/
    public byte[] buildReqMute(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求开启声音参会者 (with mcu and confConfig)
     * **********************************/
    public byte[] buildReqUnmute(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求参会者画面广播
     * **********************************/
    public byte[] buildReqBroadcast(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求参会者画面为选看端（明确一下功能）
     * **********************************/
    public byte[] buildReqSelectView(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求参会者发言
     * **********************************/
    public byte[] buildReqAttendSpeachk(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求参会者取消发言
     * **********************************/
    public byte[] buildReqAttendCancelSpeech(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求控制参会者
     * **********************************/
    public byte[] buildReqAttendCtrl(String confId, String attendId, int ctrlType, int ctrlVal){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求邀请参会者
     * **********************************/
    public byte[] buildReqInviteAttend(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求挂断参会者
     * **********************************/
    public byte[] buildReqHangupAttend(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求控制远程摄像头（目前不使用）
     * **********************************/
    public byte[] buildReqCameralCtrl(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求改变会议模式（修改多画面轮训间隔）
     * **********************************/
    public byte[] buildReqChangeConfConfig(String confId, String attendId){
        return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 启动或停止双流(需要明确一下功能)
     * **********************************/
    public byte[] buildReqDuplex(String confId, boolean enabled){
        if (enabled) {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
        }
        else {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
        }
    }

    /* **********************************
     * 启动混音(最多36个终端)
     * **********************************/
    public byte[] buildReqConfAudioMix(String confId, List<Attend> attend){
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 取消混音
     * **********************************/
    public byte[] buildReqCancelConfAudioMix(String confId) {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 获取主席台响应
     * **********************************/
    public byte[] buildReqChairmanResponse(String confId, String chairmanId, int msgType, int msgVal) {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    /* **********************************
     * 请求会议终端多画面轮巡(暂时不用)
     * **********************************/
    public byte[] buildReqConfMultiPic(String confId, String chairmanId, List<Attend> attends) {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }


    /* **********************************
     * 请求会议终端取消多画面轮巡(暂时不用)
     * **********************************/
    public byte[] buildReqCancelConfMultiPic(String confId, String chairmanId, List<Attend> attends) {
            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
    }

    private class RequestMessage{
        int type;
        int confId; //会议id
        int attendId; //参会者id，上限为100个
    }

    private class ResponseMessage{
        int type;
        int status;
        int error;
    }
}
