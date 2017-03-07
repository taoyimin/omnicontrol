package cn.diaovision.omnicontrol.core.model.device.matrix.io;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class Port {
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_SERIAL = 2;
    public static final int TYPE_SWITCH = 3;

    public static final int DIR_IN = 0; //input
    public static final int DIR_OUT = 1; //output
    public static final int DIR_BI = 2; //bidirection

    public static final int STATE_ON = 0;
    public static final int STATE_OFF = 1;
    public static final int STATE_ERR = 2;
    public static final int STATE_NA = 3;

    public int idx;
    public int type;
    public int dir; //direction: in/out/bidirection
    public int state;
    public String alias;
}
