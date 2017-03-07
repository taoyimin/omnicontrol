package cn.diaovision.omnicontrol.core.protocol;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public interface MatrixProtocol {
    static final int CMD_CHN_BIND = 0;
    static final int CMD_CHN_UNBIND = 1;
    static final int CMD_SERIAL = 2;

    byte[] createBindCmd(int channelType, int in, int out);
    byte[] createSerialCmd(int ctrl, byte[] cmd);
}
