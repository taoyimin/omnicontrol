package cn.diaovision.omnicontrol.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.common.Device;
import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * Created by liulingfeng on 2017/4/24.
 */

public interface Config {
    String getMainName();
    String getMainPasswd();
    void setMainName(String name);
    void setMainPasswd(String password);

    String getConfName();
    String getConfPasswd();

    String getMcuIp();
    int getMcuPort();


    //Matrix attributes
    int getMatrixId();
    void setMatrixId(String id);
    String getMatrixIp();
    void setMatrixIp(String ip);
    int getMatrixUdpIpPort();
    void setMatrixUdpIpPort(String port);
    String getMatrixPreviewIp();

    //get the port where the preview channel is plugged on to the matrix
    int getMatrixPreviewPort();

    Map<Integer,HiCamera> getMatrixCameras();
    Set<Channel> getMatrixChannels();

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

    List<Device> getDeviceList();
    void setDeviceList(List<Device> devices);
}
