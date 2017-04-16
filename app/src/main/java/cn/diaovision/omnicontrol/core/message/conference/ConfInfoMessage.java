package cn.diaovision.omnicontrol.core.message.conference;

import cn.diaovision.omnicontrol.BaseFragment;

/**
 * conference 集合
 * Created by liulingfeng on 2017/4/14.
 */

public class ConfInfoMessage implements BaseMessage{
    byte confNum;
    ConfConfigMessage[] confConfig; //size = 32

    public ConfInfoMessage() {
        confConfig = new ConfConfigMessage[32];
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[calcMessageLength()];
        bytes[0] = confNum;
        for (int m = 0; m < 32; m ++){
            byte[] bs = confConfig[m].toBytes();
            System.arraycopy(bs, 0, bytes, bs.length*m+1, bs.length);
        }
        return bytes;
    }

    @Override
    public int calcMessageLength() {
        return 1 + 32*confConfig[0].calcMessageLength();
    }
}
