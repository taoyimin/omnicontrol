package cn.diaovision.omnicontrol.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.MainControlActivity;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * Created by TaoYimin on 2017/6/5.
 */

public class PortHelper {
    Set<Channel> channelSet;
    List<Port> inputList;
    List<Port> outputList;
    Channel channel;

    static private PortHelper instance;

    public static PortHelper getInstance() {
        if (instance == null) {
            synchronized (PortHelper.class) {
                if (instance == null) {
                    instance = new PortHelper();
                    init();
                }
            }
        }
        return instance;
    }

    public static void init() {
        instance.inputList = MainControlActivity.matrix.getVideoInPort();
        instance.outputList = MainControlActivity.matrix.getVideoOutPort();
        instance.channelSet = MainControlActivity.matrix.getVideoChnSet();
    }

    /*输入端口是否有对应的输出端口*/
    public boolean inputPortIsUsed(int inputIdx) {
        int[] outs = getOutputIdx(inputIdx);
        if(outs!=null){
            if(outs.length==0){
                return true;
            }else{
                if(outs[0]==-1){
                    return false;
                }else {
                    return true;
                }
            }
        }else{
            return false;
        }
    }

    /*输出端口是否有对应的输入端口*/
    public boolean outputPortIsUsed(int outputIdx) {
        if (getInputIdx(outputIdx) == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*通过index获取输入端口的对象*/
    public Port getInputPort(int portIdx) {
        return inputList.get(portIdx);
    }

    /*通过index获取输出端口的对象*/
    public Port getOutputPort(int portIdx) {
        return outputList.get(portIdx);
    }

    /*获取输入端口对应的输出端口index数组*/
    public int[] getOutputIdx(int inputIdx) {
        Iterator<Channel> iterator = channelSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.getInputIdx() == inputIdx) {
                return channel.getOutputIdx();
            }
        }
        return new int[]{-1};
    }

    /*获取输出端口对应的输入端口*/
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

    /*获取输入端口的角标*/
    public int getInputPortBadge(int inputIdx) {
        int badge = 1;
        int category = inputList.get(inputIdx).category;
        for (int i = 0; i < inputIdx; i++) {
            if (inputList.get(i).category == category) {
                badge++;
            }
        }
        return badge;
    }

    /*获取输出端口的角标*/
    public int getOutPortBadge(int outPutIdx) {
        int inputIdx = getInputIdx(outPutIdx);
        if (inputIdx != -1) {
            return getInputPortBadge(inputIdx);
        }
        return -1;
    }

    /*获取输出端口对应的输入端口的种类*/
    public int getInputPortCategory(int outputIdx) {
        int inputIdx = getInputIdx(outputIdx);
        if (inputIdx != -1) {
            return inputList.get(inputIdx).category;
        }
        return -1;
    }

    public void removeChannel(Port port) {
        Iterator<Channel> iterator = channelSet.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.getInputIdx() == port.idx) {
                this.channel = channel;
                iterator.remove();
            }
        }
    }

    public Set<Channel> getChannelSet() {
        return channelSet;
    }

    public List<Port> getInputList() {
        return inputList;
    }

    public List<Port> getOutputList() {
        return outputList;
    }
}
