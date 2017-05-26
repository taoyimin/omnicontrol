package cn.diaovision.omnicontrol.core.message.conference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.util.ByteUtils;
import cn.diaovision.omnicontrol.util.DateHelper;

/**会议配置类
 * Created by liulingfeng on 2017/4/13.
 */

public class ConfConfigMessage implements BaseMessage, Serializable {
    String name; //32 bytes
    String descrip; //64 bytes
    int id; //2 bytes
    byte type; //1 byte 会议类型: 0=H323; 1= H323&T120; 2=T120; 3=混合会议
    int audioMode; //2 bytes
    int videoMode; //2 bytes
    byte mode; //1 byte 会议组织模式: 0,主席模式1,导演模式 2,语音模式3,自动浏览方式4自动选看方式
    byte maxTermNum; //1 byte
    byte termNum; //1 byte
    byte status; //1 byte 会议状态: 5=正在召开 其它= 准备召开

    int startYear; //2 bytes
    byte startMonth; //1 byte
    byte startDay; //1 byte
    byte startHour; //1 byte
    byte startMin; //1 byte

    int endYear; //2 bytes
    byte endMonth; //1 byte
    byte endDay; //1 byte
    byte endHour; //1 byte
    byte endMin; //1 byte

    String creatorName; //32 bytes

    int bandwidth; //2 bytes 会议带宽 100bps为单位
    byte fps; //1 byte 每秒帧率

    byte mixMode; //1 byte 媒体混合 0--不混; 1--混音; 2--混图像; 3--混音+混图像
    long streamAddr; // 4 bytes to ip address 流媒体服务器地址
    int streamAudioPort; //2 bytes, 流媒体音频端口
    int streamVideoPort; //2 bytes, 流媒体视频端口

    long autoTime; //4 bytes 自动浏览时间
    byte termNameFlag; //1 byte 终端名字显示方式 0:使用终端传送的名字 1:使用地址簿的名字

    byte autoInvite; //1 byte 自动重邀请

    byte selectViewSync; //1 byte 选看同步标志:1:选看端同步于广播端标志 0:选看端不同步于广播端

    byte autoOperator; //1 byte, 0:操作员不自动选看 1:操作员自动选看
    long autoOperatorTime; //4 bytes, 操作员自动选看时间长

    long calloverTermId; //4 bytes, 点名发言终端id（ip地址）
    byte operatorMode; //1 byte 0:正常 1:前台操作员 2：后台操作员

    byte continuousMode; //1 byte, 四画面模式0:普通四画面1:主席四画面

    int termAttrNum; //2 bytes, termattr数量
    TermAttr[] termAttrs; //maximal 256

    //dual stream attrs
    byte dsVideoMode; //1 byte, 双流媒体格式：0:动态双流;1:高清双流;other:DisableDuoVideo
    byte dsImageMode; //1 byte, 双流图像格式，同前
    int dsBandwidth; //2 bytes, same as previous bandwidth
    byte dsStatus; //1 byte, 双流状态，1：已启动，0：停止，
    byte dsReserved; //1 byte, 保留字节

    int[] bandwithMulti;	//会议的三种带宽


    public ConfConfigMessage() {
        bandwithMulti = new int[3];
        termAttrs = new TermAttr[256];
    }

    public void setDate(Date startTime, Date endTime){
        startYear = DateHelper.getInstance().getYear(startTime);
        startMonth = (byte) (DateHelper.getInstance().getMonth(startTime) & 0xff);
        startDay = (byte) (DateHelper.getInstance().getMonth(startTime) & 0xff);
        startHour = (byte) (DateHelper.getInstance().getHour(startTime) & 0xff);
        startMin = (byte) (DateHelper.getInstance().getMin(startTime) & 0xff);

        endYear = DateHelper.getInstance().getYear(endTime);
        endMonth = (byte) (DateHelper.getInstance().getMonth(endTime) & 0xff);
        endDay = (byte) (DateHelper.getInstance().getMonth(endTime) & 0xff);
        endHour = (byte) (DateHelper.getInstance().getHour(endTime) & 0xff);
        endMin = (byte) (DateHelper.getInstance().getMin(endTime) & 0xff);
    }

    /*deep copy of template to confConfig*/
    static public ConfConfigMessage copyFrom(ConfConfigMessage t){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (ConfConfigMessage) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[this.calcMessageLength()];
        System.arraycopy(name.getBytes(), 0, bytes, 0, name.getBytes().length > 32 ? 32 : name.getBytes().length);
        System.arraycopy(descrip.getBytes(), 0, bytes, 32, descrip.getBytes().length > 64 ? 64 : descrip.getBytes().length);
        System.arraycopy(ByteUtils.int2bytes(id, 2), 0, bytes, 96, 2);
        bytes[98] = type;
        System.arraycopy(ByteUtils.int2bytes(audioMode, 2), 0, bytes, 99, 2);
        System.arraycopy(ByteUtils.int2bytes(videoMode, 2), 0, bytes, 101, 2);
        bytes[103] = mode;
        bytes[104] = maxTermNum;
        bytes[105] = termNum;
        bytes[106] = status;
        System.arraycopy(ByteUtils.int2bytes(startYear, 2), 0, bytes, 107, 2);
        bytes[109] = startMonth;
        bytes[110] = startDay;
        bytes[111] = startHour;
        bytes[112] = startMin;
        System.arraycopy(ByteUtils.int2bytes(endYear, 2), 0, bytes, 113, 2);
        bytes[115] = endMonth;
        bytes[116] = endDay;
        bytes[117] = endHour;
        bytes[118] = endMin;
        System.arraycopy(creatorName.getBytes(), 0, bytes, 119, creatorName.getBytes().length > 32 ? 32 : creatorName.getBytes().length);
        System.arraycopy(ByteUtils.int2bytes(bandwidth, 2), 0, bytes, 151, 2);
        bytes[153] = fps;
        bytes[154] = mixMode;
        System.arraycopy(ByteUtils.int2bytes((int) streamAddr, 4), 0, bytes, 155, 4);
        System.arraycopy(ByteUtils.int2bytes(streamAudioPort, 2), 0, bytes, 159, 2);
        System.arraycopy(ByteUtils.int2bytes(streamVideoPort, 2), 0, bytes, 161, 2);
        System.arraycopy(ByteUtils.int2bytes((int) autoTime, 4), 0, bytes, 163, 4);
        bytes[167] = termNameFlag;
        bytes[168] = autoInvite;
        bytes[169] = selectViewSync;
        bytes[170] = autoOperator;
        System.arraycopy(ByteUtils.int2bytes((int) autoOperatorTime, 4), 0, bytes, 171, 4);
        System.arraycopy(ByteUtils.int2bytes((int) calloverTermId, 4), 0, bytes, 175, 4);
        bytes[179] = operatorMode;
        bytes[180] = continuousMode;
        System.arraycopy(ByteUtils.int2bytes(termAttrNum, 2), 0, bytes, 181, 2);

        for (int m = 0; m < termAttrNum; m ++){
            int offset = 183+ 10*m;
            System.arraycopy(ByteUtils.int2bytes(termAttrs[m].id, 2), 0, bytes, offset, 2);
            System.arraycopy(ByteUtils.int2bytes(termAttrs[m].type, 2), 0, bytes, offset+2, 2);
            System.arraycopy(ByteUtils.int2bytes((int) termAttrs[m].addr, 4), 0, bytes, offset+4, 4);
            System.arraycopy(ByteUtils.int2bytes(termAttrs[m].bandwidth, 2), 0, bytes, offset+8, 2);
        }

        int offset = 183+256*10;

        bytes[offset] = dsVideoMode;
        bytes[offset+1] = dsImageMode;
        System.arraycopy(ByteUtils.int2bytes(dsBandwidth, 2), 0, bytes, offset+2, 2);
        bytes[offset+4] = dsStatus;
        bytes[offset+5] = dsReserved;
        System.arraycopy(ByteUtils.int2bytes(bandwithMulti[0], 2), 0, bytes, offset+6, 2);
        System.arraycopy(ByteUtils.int2bytes(bandwithMulti[1], 2), 0, bytes, offset+8, 2);
        System.arraycopy(ByteUtils.int2bytes(bandwithMulti[2], 2), 0, bytes, offset+10, 2);

        return bytes;
    }

    /*dump byte arrays into the data data, (memcpy)*/
    public int dumpBytes(byte[] bytes, int offset){
        if (this.calcMessageLength() > bytes.length - offset){
            return -1;
        }

        byte[] nameBytes = new byte[32];
        System.arraycopy(bytes, offset, nameBytes, 0, 32);
        name = String.valueOf(nameBytes);

        byte[] descripBytes = new byte[32];
        System.arraycopy(bytes, offset+32, descripBytes, 0, 64);
        descrip = String.valueOf(descripBytes);

        id = ByteUtils.bytes2int(bytes, offset+96, 2);
        type = bytes[98];
        audioMode = ByteUtils.bytes2int(bytes, offset+99, 2);
        videoMode = ByteUtils.bytes2int(bytes, offset+101, 2);
        mode = bytes[offset+103];
        maxTermNum = bytes[offset+104];
        termNum = bytes[offset+105];
        status = bytes[offset+106];
        startYear = ByteUtils.bytes2int(bytes, offset+107, 2);
        startMonth = bytes[offset+109];
        startDay = bytes[offset+110];
        startHour = bytes[offset+111];
        startMin = bytes[offset+112];
        endYear = ByteUtils.bytes2int(bytes, offset+113, 2);
        endMonth = bytes[offset+115];
        endDay = bytes[offset+116];
        endHour = bytes[offset+117];
        endMin = bytes[offset+118];

        byte[] creatorNameBytes =  new byte[32];
        System.arraycopy(bytes, offset+119, creatorNameBytes, 0, 32);
        creatorName = String.valueOf(creatorNameBytes);

        bandwidth = ByteUtils.bytes2int(bytes, offset+151, 2);
        fps = bytes[offset+153];
        mixMode = bytes[offset+154];
        streamAddr = ByteUtils.bytes2int(bytes, offset+155, 4);
        streamAudioPort = ByteUtils.bytes2int(bytes, offset+159, 2);
        streamVideoPort =  ByteUtils.bytes2int(bytes, offset+161, 2);
        autoTime =  ByteUtils.bytes2int(bytes, offset+163, 4);
        termNameFlag = bytes[offset+167];
        autoInvite = bytes[offset+168];
        selectViewSync = bytes[offset+169];
        autoOperator = bytes[offset+170];
        autoOperatorTime = ByteUtils.bytes2int(bytes, offset+171, 4);
        calloverTermId = ByteUtils.bytes2int(bytes, offset+175, 4);
        operatorMode = bytes[offset+179];
        continuousMode = bytes[offset+180];
        termAttrNum = ByteUtils.bytes2int(bytes, offset+181, 2);

        for (int m = 0; m < termAttrNum; m ++){
            int d = offset+183 + 10*m;
            termAttrs[m].id = ByteUtils.bytes2int(bytes, d, 2);
            termAttrs[m].type = ByteUtils.bytes2int(bytes, d+2, 2);
            termAttrs[m].addr = ByteUtils.bytes2int(bytes, d+4, 4);
            termAttrs[m].bandwidth = ByteUtils.bytes2int(bytes, d+8, 2);
        }

        int dNew = offset+183+256*10;

        dsVideoMode = bytes[dNew];
        dsImageMode = bytes[dNew+1];
        dsBandwidth = ByteUtils.bytes2int(bytes, dNew+2, 2);
        dsStatus = bytes[dNew+4];
        dsReserved = bytes[dNew+5];
        bandwithMulti[0] = ByteUtils.bytes2int(bytes, dNew+6, 2);
        bandwithMulti[1] = ByteUtils.bytes2int(bytes, dNew+8, 2);
        bandwithMulti[2] = ByteUtils.bytes2int(bytes, dNew+10, 2);

        return calcMessageLength();
    }



    @Override
    public int calcMessageLength() {
        return 2755;
//            return 32+64+2+1+2*2+1*4+2+1*4+2+1*4+32+2+1+1+4+2*2+4+1*4+4*2+1*2+10*256+1*4+2*3
    }

    static public class TermAttr{
        public int id; //2 bytes
        public  int type;  //2 bytes
        public long addr; //4 bytes
        public int bandwidth; //2 bytes
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTermAttrNum() {
        return termAttrNum;
    }

    public void setTermAttrNum(int termAttrNum) {
        this.termAttrNum = termAttrNum;
    }

    public TermAttr[] getTermAttrs() {
        return termAttrs;
    }

    public void setTermAttrs(TermAttr[] termAttrs) {
        this.termAttrs = termAttrs;
    }
}

