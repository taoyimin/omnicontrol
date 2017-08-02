package cn.diaovision.omnicontrol.core.model.device.common;

import cn.diaovision.omnicontrol.conn.UdpClient;

/**
 * Created by TaoYimin on 2017/8/2.
 */

public abstract class CommonDevice {
    private String name;
    private String ip;
    private int port;
    private int state;
    private int type;
    private UdpClient controller;

    public CommonDevice(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UdpClient getController() {
        if (controller == null) {
            controller = new UdpClient(ip, port);
        }
        return controller;
    }

    public abstract byte[] buildPowerOnMessage();

    public abstract byte[] buildPowerOffMessage();

    public interface TYPE{
        int BARCO_PROJECTOR=0;
    }
}
