package cn.diaovision.omnicontrol.core.model.device.matrix.io;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class Channel {
    public static final int CHN_VIDEO = 0;
    public static final int CHN_AUDIO = 1;

    public int inputIdx;
    public int outputIdx;
    public Controller controller;
    public int type;
    public int state;
}
