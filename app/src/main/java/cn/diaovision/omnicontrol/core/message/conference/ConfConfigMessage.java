package cn.diaovision.omnicontrol.core.message.conference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.diaovision.omnicontrol.core.model.conference.Term;
import cn.diaovision.omnicontrol.util.ByteUtils;

/**
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
        System.arraycopy(ByteUtils.int2bytes((int) streamAddr, 4), 0, bytes, 155, 4);



        System.arraycopy(ByteUtils.int2bytes(videoMode, 2), 0, bytes, 101, 2);
        return bytes;
    }



    @Override
    public int calcMessageLength() {
        return 2785;
//            return 32+64+2+1+2*2+1*4+2+1*4+2+1*4+32+2+1+1+4+2*2+4+1*4+4*2+1*2+x*256+1*4+2*3
    }

    static public class TermAttr{
        int id; //2 bytes
        int type;  //2 bytes
        long addr; //4 bytes
        int bandwidth; //2 bytes
    }
}

