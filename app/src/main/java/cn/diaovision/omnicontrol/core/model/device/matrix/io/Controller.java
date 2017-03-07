package cn.diaovision.omnicontrol.core.model.device.matrix.io;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class Controller {
    public static final int CTRL_POWER = 0;
    public static final int CTRL_LIGHT = 1;
    public static final int CTRL_CAMERA = 2;

    public int idx;
    public int type;
    public int state;
    public int protocol;
}
