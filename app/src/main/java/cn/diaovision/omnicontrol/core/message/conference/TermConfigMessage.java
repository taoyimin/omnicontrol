package cn.diaovision.omnicontrol.core.message.conference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
    byte termType;       //0--呼入  1--呼出  2--呼入呼出
    int bandwidth;	        //终端带宽


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
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 32+32+4+4+2+1*5+32+20+1*6+4+1+2;
    }
}
