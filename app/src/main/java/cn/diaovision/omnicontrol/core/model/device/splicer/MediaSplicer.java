package cn.diaovision.omnicontrol.core.model.device.splicer;

import java.util.List;

import cn.diaovision.omnicontrol.conn.UdpClient;

/**
 * Created by TaoYimin on 2017/7/25.
 * 拼接控制器
 */

public class MediaSplicer {
    //融合器ip
    private String ip="192.168.10.109";
    //融合器通讯端口
    private int port=5000;
    //UPD客户端
    UdpClient controller;
    //场景集合
    List<Scene> scenes;

    public UdpClient getController() {
        if (controller == null) {
            controller = new UdpClient(ip, port);
        }
        return controller;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }
}
