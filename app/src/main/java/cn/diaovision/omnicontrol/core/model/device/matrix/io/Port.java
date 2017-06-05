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

    public static final int CATEGORY_CAMERA=0;
    public static final int CATEGORY_DESKTOP=1;
    public static final int CATEGORY_VIDEO=2;
    public static final int CATEGORY_OUTPUT_RETURN=3;
    public static final int CATEGORY_PROJECTOR=4;
    public static final int CATEGORY_DISPLAY=5;
    public static final int CATEGORY_IP=6;
    public static final int CATEGORY_COMPUTER=7;
    public static final int CATEGORY_TV=8;
    public static final int CATEGORY_CONFERENCE=9;
    public static final int CATEGORY_SPLIT_SCREEN=10;

    public int parentIdx; //matrix's idx
    public int idx;
    public int type;
    public int dir; //direction: in/out/bidirection
    public int state;
    public int category;
    public String alias;

    public Port(int parentIdx, int idx, int type, int dir, int category) {
        this.parentIdx = parentIdx;
        this.idx = idx;
        this.type = type;
        this.dir = dir;
        this.category=category;
    }

    @Override
    public int hashCode() {
        int[] val = {parentIdx, idx, type, dir};
        return val.hashCode();
    }
}
