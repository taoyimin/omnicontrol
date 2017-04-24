package cn.diaovision.omnicontrol.core.message.conference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.diaovision.omnicontrol.util.ByteUtils;

/******************************************
 * TermConfigmessage
 * Created by liulingfeng on 2017/4/13.
 ******************************************/
public class TermConfigMessage implements BaseMessage, Serializable {
    String name; //32 bytes
    String gbkAlias; //32 bytes alias in GBK coding
    long id; //term id
    long addr; //4 bytes, ip addr
    int port; //2 bytes
    byte isChair; //是否是主席（或广播端）
    byte isSelectView; //是否是选看端
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
    byte[] reserved;     //保留, 4 bytes
    byte termType;       //0--呼入  1--呼出  2--呼入呼出
    int bandwidth;	        //终端带宽, 2 bytes


    public TermConfigMessage(){
        reserved = new byte[4];
    }
    /*deep copy of template to termconfig*/
    static public TermConfigMessage copyFrom(TermConfigMessage t){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (TermConfigMessage) ois.readObject();
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
        byte[] bytes = new byte[calcMessageLength()];

        System.arraycopy(name.getBytes(), 0, bytes, 0, name.getBytes().length > 32 ? 32 : name.getBytes().length);
        System.arraycopy(gbkAlias.getBytes(), 0, bytes, 32, gbkAlias.getBytes().length > 32 ? 32 : gbkAlias.getBytes().length);

        System.arraycopy(ByteUtils.int2bytes((int) id, 4), 0, bytes, 64, 4);
        System.arraycopy(ByteUtils.int2bytes((int) addr, 4), 0, bytes, 68, 4);
        System.arraycopy(ByteUtils.int2bytes(port, 2), 0, bytes, 72, 2);
        bytes[74] = isChair;
        bytes[75] = isSelectView;
        bytes[76] = status;
        bytes[77] = mix;
        bytes[78] = viaGateway;
        System.arraycopy(telNumGateway.getBytes(), 0, bytes, 79, telNumGateway.getBytes().length > 20 ? 20 : telNumGateway.getBytes().length);
        bytes[99] = videoFlag;
        bytes[100] = audioFlag;
        bytes[101] = dataFlag;
        bytes[102] = pingOnline;
        bytes[103] = chair;
        bytes[104] = floor;
        bytes[105] = reserved[0];
        bytes[106] = reserved[1];
        bytes[107] = reserved[2];
        bytes[108] = reserved[3];
        bytes[109] = termType;
        System.arraycopy(ByteUtils.int2bytes(bandwidth, 2), 0, bytes, 110, 2);

        return bytes;
    }

     /*dump byte arrays into the data data, (memcpy)*/
    public int dumpBytes(byte[] bytes, int offset){
        if (calcMessageLength() > bytes.length - offset) {
            return -1;
        }

        byte[] nameBytes = new byte[32];
        System.arraycopy(bytes, offset, nameBytes, 0, 32);
        name = String.valueOf(nameBytes);

        byte[] gbkAliasBytes = new byte[32];
        System.arraycopy(bytes, offset+32, gbkAliasBytes, 0, 32);
        name = String.valueOf(nameBytes);

        id = ByteUtils.bytes2int(bytes, offset+64, 4);
        addr = ByteUtils.bytes2int(bytes, offset+68, 4);
        port = ByteUtils.bytes2int(bytes, offset+72, 2);
        isChair = bytes[offset+74];
        isSelectView = bytes[offset+75];
        status = bytes[offset+76];
        mix = bytes[offset+77];
        viaGateway = bytes[offset+78];

        byte[] telNumGatewayBytes = new byte[20];
        System.arraycopy(bytes, offset+79, telNumGatewayBytes, 0, 20);
        telNumGateway = String.valueOf(telNumGatewayBytes);

        videoFlag = bytes[offset+99];
        audioFlag = bytes[offset+100];
        dataFlag = bytes[offset+101];
        pingOnline = bytes[offset+102];
        chair = bytes[offset+103];
        floor = bytes[offset+104];
        reserved[0] = bytes[offset+105];
        reserved[1] = bytes[offset+106];
        reserved[2] = bytes[offset+107];
        reserved[3] = bytes[offset+108];
        termType = bytes[offset+109];
        bandwidth = ByteUtils.bytes2int(bytes, offset+110, 2);

        return calcMessageLength();
    }

    @Override
    public int calcMessageLength() {
        return 112;
    }
}
