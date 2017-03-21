package cn.diaovision.omnicontrol.core.model.device.matrix.io;

import cn.diaovision.omnicontrol.core.model.device.State;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class Channel {
    public static final int CHN_VIDEO = 0;

    public int inputMatrix = -1;
    public int inputIdx = -1;

    public int outputMatrix = -1;
    public int outputIdx = -1;

    public int type = 0;
    public int state = State.ON;

    public String alias = "";

    public Channel(int inputMatrix, int inputIdx, int outputMatrix, int outputIdx) {
        this.inputMatrix = inputMatrix;
        this.inputIdx = inputIdx;
        this.outputMatrix = outputMatrix;
        this.outputIdx = outputIdx;
    }

    @Override
    public int hashCode() {
        int val[] = {inputMatrix, inputIdx, outputMatrix, outputIdx};
        return val.hashCode();
    }
}
