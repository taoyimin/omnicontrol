package cn.diaovision.omnicontrol.core.model.device.fusion;

import cn.diaovision.omnicontrol.conn.UdpClient;

/**
 * Created by TaoYimin on 2017/7/25.
 * 融合处理器
 */

public class MediaFusion {
    //融合器ip
    private String ip="192.168.10.109";
    //融合器通讯端口
    private int port=5000;
    //UPD客户端
    UdpClient controller;

    public UdpClient getController() {
        if (controller == null) {
            controller = new UdpClient(ip, port);
        }
        return controller;
    }
}
