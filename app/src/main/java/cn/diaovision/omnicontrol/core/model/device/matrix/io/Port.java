package cn.diaovision.omnicontrol.core.model.device.matrix.io;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class Port {
    public static final int TYPE_VIDEO = 0; //video port
    public static final int TYPE_AUDIO = 1; //audio port
    public static final int TYPE_SERIAL = 2; //serial port
    public static final int TYPE_SWITCH = 3; //GPIO

    public static final int DIR_IN = 0; //input
    public static final int DIR_OUT = 1; //output
    public static final int DIR_BI = 2; //bidirection

    public int parentIdx; //matrix's idx
    public int idx;
    public int type;
    public int dir; //direction: in/out/bidirection
    public int state;
    public String alias;

    public Port(int parentIdx, int idx, int type, int dir) {
        this.parentIdx = parentIdx;
        this.idx = idx;
        this.type = type;
        this.dir = dir;
    }

    @Override
    public int hashCode() {
        int[] val = {parentIdx, idx, type, dir};
        return val.hashCode();
    }
}
