package cn.diaovision.omnicontrol.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public interface Config {
    String getMainName();
    String getMainPasswd();

    String getConfName();
    String getConfPasswd();

    String getMcuIp();
    int getMcuPort();


    //Matrix attributes
    int getMatrixId();
    String getMatrixIp();
    int getMatrixUdpIpPort();
    String getMatrixPreviewIp();
    int getMatrixPreviewIpPort();

    //get the port where the preview channel is plugged on to the matrix
    int getMatrixPreviewPort();

    int getMatrixInputVideoNum();
    int getMatrixOutputVideoNum();

    byte getSubtitleFontSize();
    byte getSubtitleFontColor();

    Map<Integer,HiCamera> getMatrixCameras();
    Set<Channel> getMatrixChannels();

    Date getConfStartDate();
    Date getConfEndDate();

    List<Port> getInputPortList();
    List<Port> getOutputPortList();

    void modifyPort(Port port);
    void modifyChannel(Set<Channel> channelSet);
}
