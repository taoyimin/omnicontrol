package cn.diaovision.omnicontrol.core.model.device.matrix.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.diaovision.omnicontrol.core.model.device.State;

/**
 * Channel on solo matrix
 * Created by liulingfeng on 2017/3/2.
 */

//TODO: port, channel, check
public class Channel {
    public static final int CHN_VIDEO = 0;

    public static final int MOD_NORMAL = 0; //1对1通道
    public static final int MOD_STITCH = 1; //拼接通道

    public int inputIdx = -1;

    public int[] outputIdx;

    public Map<String, Object> properties; //通道属性: 分辨率，明暗, 音量，色饱和度

    public int type = 0;
    public int mode = MOD_NORMAL;
    public int state = State.ON; //状态

    String alias = ""; //别名

    public Channel(int type, int inputIdx, int[] outputIdx) {
        this.type = type;
        this.inputIdx = inputIdx;
        this.outputIdx = outputIdx;
        this.properties = new HashMap<>();
    }

    @Override
    public int hashCode() {
        if (outputIdx != null) {
            int val[] = new int[1 + outputIdx.length];
            val[0] = inputIdx;
            System.arraycopy(outputIdx, 0, val, 1, outputIdx.length);
            return val.hashCode();
        }
        else {
            return inputIdx;
        }
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setProperty(String name, Object val){
        properties.put(name, val);
    }

    public Object getProperty(String name){
        Object val = properties.get(name);
        if (val == null){
            return new Object();
        }
        else {
            return val;
        }
    }

    public int getInputIdx() {
        return inputIdx;
    }

    public void setInputIdx(int inputIdx) {
        this.inputIdx = inputIdx;
    }

    public int[] getOutputIdx() {
        return outputIdx;
    }

    public void setOutputIdx(int[] outputIdx) {
        this.outputIdx = outputIdx;
    }

    public boolean containOutputIdx(int idx){
        boolean containIdx = false;
        for (int i : outputIdx){
            if (idx == i) {
                containIdx = true;
                break;
            }
        }
        return containIdx;
    }
}

