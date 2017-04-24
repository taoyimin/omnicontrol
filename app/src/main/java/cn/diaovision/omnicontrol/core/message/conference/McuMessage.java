package cn.diaovision.omnicontrol.core.message.conference;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.diaovision.omnicontrol.core.model.conference.LiveConf;
import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.rx.RxMessage;
import cn.diaovision.omnicontrol.util.ByteBuffer;
import cn.diaovision.omnicontrol.util.ByteUtils;
import cn.diaovision.omnicontrol.util.DateHelper;

/****************************************************************
 * 视频会议消息协议
 * data formats:
 * 1. byte stores 1 byte data
 * 2. int stores 2 bytes data
 * 3. long stores 4 bytes data
 * 4. String stores byte array (size specified in constructor)
 * Created by liulingfeng on 2017/2/22.
 ****************************************************************/

public class McuMessage implements BaseMessage{
    public final static byte VERSION = 0x01;

    public final static byte TYPE_REQ = 1;
    public final static byte TYPE_RES = 2;
    public final static byte TYPE_CREATE_CONF = 3;
    public final static byte TYPE_INVITE_TERM = 4;
    public final static byte TYPE_USER = 5; //login
    public final static byte TYPE_ADD_CONF = 6; //not used
    public final static byte TYPE_ADD_TERM = 7;
    public final static byte TYPE_ADD_ADDRBOOK = 8; //not used
    public final static byte TYPE_DEL_ADDRBOOK = 9; //not used
    public final static byte TYPE_VER_INFO = 10;
    public final static byte TYPE_SPEAKER = 11; //发言
    public final static byte TYPE_CONFIG = 12;
    public final static byte TYPE_UPGRAGE_MSG = 13;
    public final static byte TYPE_UPGRAGE_MSG_RSP = 14;
    public final static byte TYPE_CHAIRUSER = 0x25;
    public final static byte TYPE_MCMPINFO = 15; //not used
    //远遥，摄像头远程控制，Added by jinyuhe,2010.12.24
    public final static byte TYPE_REMOTECTRL = 20; //not used
    //注册请求，提交注册码(license)，Added by jinyuhe,2011.3.21
    public final static byte TYPE_REGISTER = 21; //not used
    //流媒体操作请求，Added by jinyuhe,2011.4.19
    public final static byte TYPE_STREAMMEDIA = 22;
    //终端控制请求，Added by jinyuhe,2011.8.4
    public final static byte TYPE_TERM_CTRL = 23;
    //简单消息扩展，Added by jinyuhe,2013.3.5
    public final static byte TYPE_REQ_EXTEND = 24;

    //WEB-->MCU Manager，WEB响应MCU Manager的主席请求消息，
    //在处理时按照MCU Manager的请求消息来处理
    public final static byte TYPE_RES_APPLYCHAIR = 0x68; //申请主席
    public final static byte TYPE_RES_FREECHAIR = 0x69; //释放主席
    public final static byte TYPE_RES_RECOVERYCHAIR = 0x6A; //回收主席，此消息由WEB发起
    public final static byte TYPE_RES_APPLYFLOOR = 0x6B; //申请发言
    public final static byte TYPE_RES_CANCELFLOOR = 0x6C; //取消发言
    //Ended

//    //定义发给WEB页面的错误字
//    public final static int ERROR_WEB_CONF_ID = 0x1000; //会议ID错误
//    public final static int ERROR_WEB_ACCESS = 0x1003;	//权限
//    public final static int ERROR_WEB_LOGIN = 0x1004;	//登录
//    public final static int ERROR_WEB_CONF_NUM = 0x1006; //会议超过最大数
//    public final static int ERROR_WEB_TERM_NUM = 0x1007; //终端超过最大数
//    public final static int ERROR_WEB_CONF_NAME = 0x1008; //会议重名
//    public final static int ERROR_WEB_TERM_NAME = 0x1009; //终端重名
//    public final static int ERROR_WEB_USER_NAME = 0x100a;	//该用户存在
//    public final static int ERROR_WEB_USER_NO = 0x100b;//没有找到此用户
//    public final static int ERROR_WEB_CONF_TIME = 0x100c;	//预约会议时间错误
//    public final static int ERROR_WEB_CONF_MIX = 0x100d;	//只能有一个媒体混合的会议
//    public final static int ERROR_WEB_DELDEF = 0x100e;	//不能删除默认会议
//    public final static int ERROR_WEB_CONF_STR = 0x100f;	//流媒体端口冲突
//    public final static int ERROR_WEB_TERM_ADDR = 0x1010;	//终端地址冲突
//    public final static int ERROR_WEB_TERM_NOTFOUND = 0x1011;	//终端没有找到
//    public final static int ERROR_WEB_INVALID_PASSWORD = 0x1012;	//密码不对

    private Header header;
    private BaseMessage submsg;

    private int type;

    public McuMessage(Header header, BaseMessage submsg) {
        this.header = header;
        this.submsg = submsg;
        this.type = header.type;
    }

    public int getType(){
        return header.type;
    }

    public int getSubtype() {
        switch (type){
            case TYPE_REQ:
                return ((ReqMessage) submsg).type;
            case TYPE_USER:
                return ((UserMessage) submsg).type;
            case TYPE_STREAMMEDIA:
                return ((StreamMediaMessage) submsg).type;
            case TYPE_CREATE_CONF:
                return 0;
            case TYPE_TERM_CTRL:
                return 0;
            default:
                return 0;
        }
    }

    @Override
    public byte[] toBytes(){
        byte[] payload = submsg.toBytes();
        byte[] bytes = new byte[header.toBytes().length + payload.length];
        System.arraycopy(header, 0, bytes, 0, header.toBytes().length);
        System.arraycopy(payload.length, 0, bytes, header.toBytes().length, payload.length);
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return header.toBytes().length + submsg.toBytes().length;
    }

    static public McuMessage buildLogin(String name, String passwd){
        if (name.length() == 0 || passwd.length() == 0){
            return null;
        }

        UserMessage userMessage = new UserMessage();
        userMessage.type = UserMessage.LOGIN;
        userMessage.name = name;
        userMessage.passwd = passwd;

        Header header = new Header(65, TYPE_USER);

        return new McuMessage(header, userMessage);
    }

    /* **********************************
     * 获得所有会议信息
     * **********************************/
    static public McuMessage buildReqConfAll(){
        int payloadLen = 1 + 2 + 100*4;


        Header header = new Header(payloadLen, TYPE_REQ);

        ReqMessage reqMsg = new ReqMessage(ReqMessage.REQ_CONF_ALL);

        return new McuMessage(header, reqMsg);
    }


    /* **********************************
     * 获得某个会议信息
     * **********************************/
    static public McuMessage buildReqConf(int id){
        int payloadLen = 1 + 2 + 100*4;
        Header header = new Header(payloadLen, TYPE_REQ);

        ReqMessage reqMsg = new ReqMessage(ReqMessage.REQ_CONF);
        reqMsg.confId = id;

        return new McuMessage(header, reqMsg);
    }


    /* **********************************
     * 获得已配置会议信息
     * **********************************/
    static public McuMessage buildReqConfConfiged(){
        int payloadLen = 1 + 2 + 100*4;
        Header header = new Header(payloadLen, TYPE_REQ);

        ReqMessage reqMsg = new ReqMessage(ReqMessage.REQ_CONF_CONFIGED);

        return new McuMessage(header, reqMsg);
    }


    /* **********************************
     * 获得所有参会终端信息
     * **********************************/
    static public McuMessage buildReqTermAll(int confId){
        int payloadLen = 1 + 2 + 100*4;
        Header header = new Header(payloadLen, TYPE_REQ);

        ReqMessage reqMsg = new ReqMessage(ReqMessage.REQ_TERM_ALL);
        reqMsg.confId = confId;

        return new McuMessage(header, reqMsg);
    }


    /* **********************************
     * 请求创建会议
     * **********************************/
    static public McuMessage buildReqCreateConf(Date dateStart, Date dateEnd, ConfConfigMessage template){
        DateHelper h = DateHelper.getInstance();
        int yS = h.getYear(dateStart);
        int mS = h.getMonth(dateStart);
        int dS = h.getDay(dateStart);
        int hS = h.getHour(dateStart);
        int minS = h.getMin(dateStart);

        int yE = h.getYear(dateEnd);
        int mE = h.getMonth(dateEnd);
        int dE = h.getDay(dateEnd);
        int hE = h.getHour(dateEnd);
        int minE = h.getMin(dateEnd);

        ConfConfigMessage confConfigMsg = ConfConfigMessage.copyFrom(template);
        confConfigMsg.startYear = yS;
        confConfigMsg.startMonth = (byte) mS;
        confConfigMsg.startDay = (byte) dS;
        confConfigMsg.startHour = (byte) hS;
        confConfigMsg.startMin = (byte) minS;

        confConfigMsg.endYear = yE;
        confConfigMsg.endMonth = (byte) mE;
        confConfigMsg.endDay = (byte) dE;
        confConfigMsg.endHour = (byte) hE;
        confConfigMsg.endMin = (byte) minE;

        confConfigMsg.termNum = 0;
        confConfigMsg.termAttrNum = 0;

        int payloadLen = confConfigMsg.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_CREATE_CONF);

        return new McuMessage(header, confConfigMsg);
    }

//    /* **********************************
//     * 请求结束会议
//     * **********************************/
//    public McuMessage buildReqDeleteConfAll(){
//        ReqMessage reqMessage = new ReqMessage();
//        reqMessage.type = ReqMessage.DEL_CONF;
//        int payloadLen = reqMessage.calcMessageLength();
//        Header header = new Header(payloadLen, TYPE_REQ);
//
//        return new McuMessage(header, reqMessage.toBytes());
//    }

    /* **********************************
     * 请求结束会议 (with confId)
     * **********************************/
    static public McuMessage buildReqDeleteConf(int confId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.DEL_CONF);
        reqMessage.confId = confId;
        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);

        return new McuMessage(header, reqMessage);
    }

    /* **********************************
     * 请求发送会议视频流到本地端口
     * **********************************/
    static public McuMessage buildReqStream(int confId, byte type, String localIp){

        StreamMediaMessage streamMediaMessage = new StreamMediaMessage();
        streamMediaMessage.type = type;
        streamMediaMessage.ipAddr = ByteUtils.ip2num(localIp); //本机IP
        streamMediaMessage.videoPort = 6002;
        streamMediaMessage.audioPort = 6000;


        int payloadLen = streamMediaMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_STREAMMEDIA);

        return new McuMessage(header, streamMediaMessage);
    }

    /* **********************************
     * 请求静音参会者 (with confId and termId)
     * **********************************/
    static public McuMessage buildReqMute(int confId, int termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.DISABLE_INPUT);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }

    /* **********************************
     * 请求开启声音参会者 (with mcu and confConfig)
     * **********************************/
     static public McuMessage buildReqUnmute(int confId, int termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.ENABLE_INPUT);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }


    /* **********************************
     * 请求参会者画面广播
     * **********************************/
    static public McuMessage buildReqTermBroadcast(int confId, long termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.SWITCH_MEDIA);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }

    /* **********************************
     * 请求参会者画面为选看端（明确一下功能）
     * **********************************/
    static public McuMessage buildReqTermSelectView(int confId, long termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.ASSIGN_SELECTVIEW);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }

    /* **********************************
     * 请求参会者发言
     * **********************************/
    static public McuMessage buildReqTermSpeach(int confId, long termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.CALLOVER);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }

    /* **********************************
     * 请求参会者取消发言
     * TODO: check message type
     * **********************************/
    static public McuMessage buildReqTermCancelSpeach(int confId, long termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.CANCEL_FLOOR);
        reqMessage.confId =  confId;
        reqMessage.termId[0] = termId;

        return new McuMessage(new Header(1, TYPE_REQ), reqMessage);
    }


    /* **********************************
     * 请求控制参会者
     * **********************************/
    public McuMessage buildReqAttendCtrl(int confId, int termId, long termIp, byte msgType, long ctrlVal){
        TermCtrlMessage termCtrlMessage = new TermCtrlMessage(msgType);
        termCtrlMessage.confId = confId;
        termCtrlMessage.termId = termId;
        termCtrlMessage.termIp = termIp;
        termCtrlMessage.val = ctrlVal;

        int payloadLen = termCtrlMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_TERM_CTRL);
        return new McuMessage(header, termCtrlMessage);
    }

    /* **********************************
     * 请求邀请参会者
     * **********************************/
    public McuMessage buildReqInviteTerm(int confId, long termId){
        TermConfigMessage termConfigMessage = new TermConfigMessage();
        termConfigMessage.id = termId;
        termConfigMessage.port = confId;
        termConfigMessage.termType = 1;

        int payloadLen = termConfigMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_TERM_CTRL);
        return new McuMessage(header, termConfigMessage);
    }


    /* **********************************
     * 请求挂断参会者
     * **********************************/
    public McuMessage buildReqHangupTerm(int confId, long termId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.HANGUP_TERM);
        reqMessage.confId = confId;
        reqMessage.termId[0] = termId;
        int paylaodLen = reqMessage.calcMessageLength();
        Header header = new Header(paylaodLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }


    /* **********************************
     * 请求控制远程摄像头（目前不使用）
     * **********************************/
    @Deprecated
    public McuMessage buildReqCameralCtrl(int confId, long termId){
        return null;
    }

    /* **********************************
     * 请求改变会议模式（修改多画面轮训间隔）
     * **********************************/
    public McuMessage buildReqChangeConfConfig(int confId, byte confType, int interval){
        ReqMessage reqMessage = new ReqMessage(confType);
        reqMessage.confId = confId;
        reqMessage.termId[0] = interval;
        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }

    /* **********************************
     * 启动或停止双流(需要明确一下功能)
     * **********************************/
    public McuMessage buildReqDS(int confId, boolean enabled){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.MIX_AUDIO);
        reqMessage.confId = confId;
        if (enabled) {
            reqMessage.termId[0] = 1;
        }
        else {
            reqMessage.termId[0] = 0;
        }

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }

    /* **********************************
     * 启动混音(最多4个终端)
     * **********************************/
    public McuMessage buildReqConfAudioMix(int confId, List<Integer> termIdList){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.MIX_AUDIO);
        reqMessage.confId = confId;

        if (termIdList.size() > 4){
            for (int m = 0 ; m < 4; m ++){
                reqMessage.termId[m] = termIdList.get(m);
            }
        }
        else {
            for (int m = 0 ; m < termIdList.size(); m ++){
                reqMessage.termId[m] = termIdList.get(m);
            }
        }

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }

    /* **********************************
     * 取消混音 (所有的termid置零)
     * **********************************/
    public McuMessage buildReqCancelConfAudioMix(int confId) {
        ReqMessage reqMessage = new ReqMessage(ReqMessage.REQ_DS);
        reqMessage.confId = confId;

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }


    /* **********************************
     * 终端主席操作应答消息
     * **********************************/
    public McuMessage buildReqChairResponse(int confId, long termId, byte msgType, long msgVal) {
        ReqMessage reqMessage = new ReqMessage(msgType);
        reqMessage.confId = confId;
        reqMessage.termId[0] = termId;
        reqMessage.termId[1] = msgVal;

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }

    public McuMessage buildReqConfVideoMix(int confId, List<Integer> termIdList){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.MIX_VIDEO);
        reqMessage.confId = confId;

        if (termIdList.size() > 36){
            for (int m = 0 ; m < 36; m ++){
                reqMessage.termId[m] = termIdList.get(m);
            }
        }
        else {
            for (int m = 0 ; m < termIdList.size(); m ++){
                reqMessage.termId[m] = termIdList.get(m);
            }
        }

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }


    public McuMessage buildReqCancelConfVideoMix(int confId){
        ReqMessage reqMessage = new ReqMessage(ReqMessage.MIX_VIDEO);
        reqMessage.confId = confId;

        int payloadLen = reqMessage.calcMessageLength();
        Header header = new Header(payloadLen, TYPE_REQ);
        return new McuMessage(header, reqMessage);
    }

    /* **********************************
     * 请求会议终端多画面轮巡(暂时不用)
     * **********************************/
//    public McuMessage buildReqConfMultiPic(int confId, int  chairmanId, List<Attend> attends) {
//            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
//    }


//    /* **********************************
//     * 请求会议终端取消多画面轮巡(暂时不用)
//     * **********************************/
//    public byte[] buildReqCancelConfMultiPic(String confId, String chairmanId, List<Attend> attends) {
//            return new McuMessage(buildHeader(0, VERSION, REQ_CONF), new byte[0]).toBytes();
//    }


    public BaseMessage getSubmsg() {
        return submsg;
    }

    public void setSubmsg(BaseMessage submsg) {
        this.submsg = submsg;
    }

    public boolean requiresAck(){
        if (getType() == TYPE_REQ && getSubtype() == ReqMessage.REQ_TERM_ALL)
            return true;
        else if (getType() == TYPE_REQ && getSubtype() == ReqMessage.REQ_CONF_ALL)
            return true;
        else if (getType() == TYPE_REQ && getSubtype() == ReqMessage.REQ_CONF)
            return true;
        else if (getType() == TYPE_REQ && getSubtype() == ReqMessage.REQ_CONF_CONFIGED)
            return true;
        else
            return false;
    }

    static public McuMessage parse(ByteBuffer buffer){
        byte[] headerBytes = new byte[4];
        byte[] msgBytes;

        if (buffer.getContentLen() < 4){
            return null;
        }

        buffer.read(headerBytes, 4);
        if (headerBytes[2] != VERSION || headerBytes[3] != TYPE_RES){
            //if msg format unmatched
            buffer.pop(headerBytes, 1);
            return null;
        }

        int msgLen = ByteUtils.bytes2int(headerBytes, 0, 2);

        if (msgLen+4 > buffer.getContentLen()){
            //if buffer is insufficient
            return null;
        }
        else {
            msgBytes = new byte[msgLen + 4];
            buffer.read(msgBytes, msgLen + 4);
            BaseMessage msg = null;
            if (msgBytes[5] == 0){
                //REQ failed
                Header header = new Header(msgLen, TYPE_RES);
                ResMessage submsg = new ResMessage();
                submsg.type = msgBytes[4];
                submsg.status = msgBytes[5];
                submsg.error = ByteUtils.bytes2int(msgBytes, 6, 2);
                submsg.infoType = msgBytes[8];
                buffer.pop(msgBytes, 9); //remove the global header and res msg header in the buffer
                return new McuMessage(header, submsg);
            }
            else {
                boolean hasInfo = true;
                switch (msgBytes[4]) {
                    case ResMessage.USER:
                        msg = parseLogin(msgBytes);
                        break;
                    case ResMessage.CONF_ALL:
                        msg = parseConfAll(msgBytes, 9, msgLen - 9);
                        break;
                    case ResMessage.TERM_ALL:
                        msg = parseTermAll(msgBytes, 9, msgLen - 9);
                        break;
                    case ResMessage.CONF:
                        msg = parseConfInfo(msgBytes, 9, msgLen - 9);
                        break;
                    case ResMessage.CONF_CONFIG:
                        msg = parseConfTemplate(msgBytes, 9, msgLen - 9);
                        break;
                    default:
                        msg = null;
                        hasInfo = false;
                        break;
                }
                if (msg != null) {
                    buffer.pop(msgBytes, msgLen + 4);
                    Header header = new Header(msgLen, TYPE_RES);
                    ResMessage submsg = new ResMessage();
                    submsg.type = msgBytes[4];
                    submsg.status = msgBytes[5];
                    submsg.error = ByteUtils.bytes2int(msgBytes, 6, 2);
                    submsg.infoType = msgBytes[8];
                    submsg.infoMsg = msg;
                    return new McuMessage(header, submsg);
                } else {
                    if (!hasInfo){
                        //if ACK msg do not contain info
                        Header header = new Header(msgLen, TYPE_RES);
                        ResMessage submsg = new ResMessage();
                        submsg.type = msgBytes[4];
                        submsg.status = msgBytes[5];
                        submsg.error = ByteUtils.bytes2int(msgBytes, 6, 2);
                        submsg.infoType = msgBytes[8];
                        buffer.pop(msgBytes, 9); //remove the global header and res msg header in the buffer
                        return new McuMessage(header, submsg);
                    }
                    else {
                        //read error, dump 1 byte
                        buffer.pop(headerBytes, 1);
                        return null;
                    }
                }
            }
        }
    }

    static private UserMessage parseLogin(byte[] buffer){
        return new UserMessage();
    }

    /*return a list of all conferences*/
    static private ConfInfoMessage parseConfAll(byte[] buffer, int offset, int msgLen){

        ConfInfoMessage confInfoMessage = new ConfInfoMessage();
        confInfoMessage.confNum = buffer[offset];
        if (confInfoMessage.confNum > 32){
           return null;
        }


        ConfConfigMessage  confConfigMessage = new ConfConfigMessage();
        int len = confConfigMessage.calcMessageLength(); //length of every confInfoMessage byte arrays


        //1 as the length of confNum
        if (confInfoMessage.confNum*len +1 != msgLen){
            return null;
        }

        int d = 1;
        for (int m = 0; m < confInfoMessage.confNum; m ++){
            confInfoMessage.confConfig[m].dumpBytes(buffer, offset+d);
            d += len;
        }
        return confInfoMessage;
    }

    /*return a list of certain conference*/
    static private ConfInfoMessage parseConfInfo(byte[] buffer, int offset, int msgLen){

        ConfInfoMessage confInfoMessage = new ConfInfoMessage();
        confInfoMessage.confNum = buffer[offset];
        if (confInfoMessage.confNum > 32){
           return null;
        }

        ConfConfigMessage confConfigMessage = new ConfConfigMessage();
        int d = 1;
        int len = confConfigMessage.calcMessageLength(); //length of every confInfoMessage byte arrays

        //1 as the length of confNum
        if (confInfoMessage.confNum*len +1 != msgLen){
            return null;
        }

        int  cnt = 0;
        while(d < buffer.length - offset){
            confInfoMessage.confConfig[cnt].dumpBytes(buffer, offset+d);
            d += len;
            cnt ++;
        }
        return confInfoMessage;
    }

    /* **************************************************
     * parse conf template for all conf in the server
     * **************************************************/
    static private ConfInfoMessage parseConfTemplate(byte[] buffer, int offset, int msgLen){
        ConfConfigMessage confConfigMessage = new ConfConfigMessage();
        confConfigMessage.dumpBytes(buffer, offset+1);
        if (confConfigMessage.id == 0xff){
            //服务端有时会将正在召开的会议信息发到会议模板中，此地预处理过滤此异常
            return null;
        }

        ConfInfoMessage confInfoMessage = parseConfInfo(buffer, offset, msgLen);

        return confInfoMessage;
    }

    static private TermInfoMessage parseTermAll(byte[] buffer, int offset, int msgLen){
        TermConfigMessage termConfigMessage = new TermConfigMessage();
        int len = termConfigMessage.calcMessageLength();
        TermInfoMessage termInfoMessage = new TermInfoMessage();
        termInfoMessage.termNum = buffer[offset];

        if (termInfoMessage.termNum > 256){
           return null;
        }

        //1 as the length of termNum
        if (termInfoMessage.termNum*len +1 != msgLen){
            return null;
        }

        int d = 1;
        for (int m = 0; m < termInfoMessage.termNum; m ++){
            termInfoMessage.termConfig[m].dumpBytes(buffer, offset+d);
            d += msgLen;
        }

        return termInfoMessage;
    }

//    static public class ResInfoMessage{
//        public final static int CONFINFO = 1;
//        public final static int TERMINFO = 2;
//        public final static int CONFDATA = 3;
//        public final static int TERMDATA = 4;
//        public final static int USERLIST = 5;
//    }

//            //Added by jinyuhe，2012.8.9
//            //定义发给终端的主席功能申请应答结果字
//            #define CHAIR_RES_IS_CHAIRMAN           0x5000  //该终端已经是主席
//            #define CHAIR_RES_REJECT_APPLYCHAIR     0x5001  //拒绝主席申请
//            #define CHAIR_RES_ACK_APPLYCHAIR        0x5002  //同意主席申请
//            #define CHAIR_RES_ISNOT_CHAIRMAN        0x5010  //该终端不是主席，不用释放
//            #define CHAIR_RES_REJECT_FREECHAIR      0x5011  //拒绝释放主席
//            #define CHAIR_RES_ACK_FREECHAIR         0x5012  //同意释放主席
//            #define CHAIR_RES_IS_SPEECHER           0x5020  //该终端正在发言
//            #define CHAIR_RES_REJECT_APPLYSPEECH    0x5021  //拒绝申请发言
//            #define CHAIR_RES_ACK_APPLYSPEECH       0x5022  //同意申请发言
//            #define CHAIR_RES_ISNOT_SPEECHER        0x5030  //该终端没有发言
//            #define CHAIR_RES_REJECT_CANCELSPEECH   0x5031  //拒绝取消发言
//            #define CHAIR_RES_ACK_CANCELSPEECH      0x5032  //同意取消发言
//            //Ended by jinyuhe，2012.8.9
}
