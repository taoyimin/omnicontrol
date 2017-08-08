package cn.diaovision.omnicontrol.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.common.CommonDevice;
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

    void setPort(Port port);
    void setChannelSet(Set<Channel> channelSet);

    void setPreviewVideoPort(int portIdx);
    void setCamera(HiCamera camera);
    void deleteCamera(HiCamera camera);
    void setCameraPreset(int cameraIdx,HiCamera.Preset preset);
    void addCameraPreset(int cameraIdx,HiCamera.Preset preset);
    void deleteCameraPreset(int cameraIdx,HiCamera.Preset preset);

    List<CommonDevice> getDeviceList();
    void setDeviceList(List<CommonDevice> devices);
}
