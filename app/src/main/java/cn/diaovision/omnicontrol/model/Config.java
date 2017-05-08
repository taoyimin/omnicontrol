package cn.diaovision.omnicontrol.model;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;

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
    int getMtatrixPreviewIpPort();

    //get the port where the preview channel is plugged on to the matrix
    int getMatrixPreviewPort();

    List<HiCamera> getHiCameraInfo();

    int getMatrixInputVideoNum();
    int getMatrixOutputVideoNum();
}
