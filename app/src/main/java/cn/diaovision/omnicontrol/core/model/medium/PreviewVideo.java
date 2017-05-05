package cn.diaovision.omnicontrol.core.model.medium;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * 这里是预览视频的类，目前包括数据：ip/port
 * Created by liulingfeng on 2017/3/21.
 */

public class PreviewVideo {
    public String ip;
    public int port;

    public Port sourcePort; //如果是来自矩阵的preview，则用来表明当前的视频源所属的端口

    public PreviewVideo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public PreviewVideo(String ip, int port, Port sourcePort) {
        this.ip = ip;
        this.port = port;
        this.sourcePort = sourcePort;
    }

    public Port getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Port sourcePort) {
        this.sourcePort = sourcePort;
    }
}
