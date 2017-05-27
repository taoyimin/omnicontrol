package cn.diaovision.omnicontrol.core.message.conference;

/** 终端配置类
 * Created by liulingfeng on 2017/4/14.
 */

public class TermInfoMessage implements BaseMessage{
    byte termNum;
    TermConfigMessage[] termConfig; //size = 256

    public TermInfoMessage() {
        termConfig = new TermConfigMessage[256];
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[calcMessageLength()];
        bytes[0] = termNum;
        for (int m = 0; m < 32; m ++){
            byte[] bs = termConfig[m].toBytes();
            System.arraycopy(bs, 0, bytes, bs.length*m+1, bs.length);
        }
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 1 + 32*termConfig[0].calcMessageLength();
    }
}
