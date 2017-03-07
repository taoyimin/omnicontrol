package cn.diaovision.omnicontrol.core.protocol;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public interface CameraProtocol {
    static final int CMD_GO_UP = 0;
    static final int CMD_GO_DOWN = 1;
    static final int CMD_GO_LEFT = 2;
    static final int CMD_GO_RIGHT = 3;
    static final int CMD_ZOOM_IN = 4;
    static final int CMD_ZOOM_OUT = 5;

    byte[] createCmd(int cmd);
}
