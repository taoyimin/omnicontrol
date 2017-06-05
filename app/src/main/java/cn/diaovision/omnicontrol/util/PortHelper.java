package cn.diaovision.omnicontrol.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * Created by TaoYimin on 2017/6/5.
 */

public class PortHelper {
    Set<Channel> channelSet;
    List<Port> inputList;
    List<Port> outputList;

    static private PortHelper instance;

    public static PortHelper getIntance() {
        if (instance == null) {
            synchronized (PortHelper.class) {
                if (instance == null) {
                    instance = new PortHelper();
                }
            }
        }
        return instance;
    }

    public void init(List<Port> inputList, List<Port> outputList, Set<Channel> channelSet) {
        this.inputList = inputList;
        this.outputList = outputList;
        this.channelSet = channelSet;
    }

    public boolean inputPortIsUsed(int inputIdx) {
        if (getOutputIdx(inputIdx) == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean outputPortIsUsed(int outputIdx) {
        if (getInputIdx(outputIdx) == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Port getInputPort(int portIdx) {
        return inputList.get(portIdx);
    }

    public Port getOutputPort(int portIdx) {
        return outputList.get(portIdx);
    }

    public int[] getOutputIdx(int inputIdx) {
        Iterator<Channel> iterator = channelSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.getInputIdx() == inputIdx) {
                return channel.getOutputIdx();
            }
        }
        return null;
    }

    public int getInputIdx(int outputIdx) {
        Iterator<Channel> iterator = channelSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.containOutputIdx(outputIdx)) {
                return channel.getInputIdx();
            }
        }
        return -1;
    }

    public int getInputPortBadge(int inputIdx) {
        int badge = 1;
        int category = inputList.get(inputIdx).category;
        for (int i = 0; i <inputIdx; i++) {
            if(inputList.get(i).category==category){
                badge++;
            }
        }
        return badge;
    }

    public int getOutPortBadge(int outPutIdx){
        int inputIdx=getInputIdx(outPutIdx);
        if(inputIdx!=-1){
            return getInputPortBadge(inputIdx);
        }
        return -1;
    }

    public int getInputPortCategory(int outputIdx){
        int inputIdx=getInputIdx(outputIdx);
        if(inputIdx!=-1){
            return inputList.get(inputIdx).category;
        }
        return -1;
    }
}
