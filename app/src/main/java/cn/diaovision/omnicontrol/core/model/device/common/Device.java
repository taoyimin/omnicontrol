package cn.diaovision.omnicontrol.core.model.device.common;

import java.util.ArrayList;
import java.util.List;

import cn.diaovision.omnicontrol.conn.UdpClient;

/**
 * Created by TaoYimin on 2017/8/2.
 */

public class Device {
    private int id;
    private String name;
    private String ip;
    private int port;
    private List<Command> cmds;
    private UdpClient controller;

    public Device() {
    }

    public Device(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        cmds=new ArrayList<>();
    }

    public Device(int id, String name, String ip, int port) {
        this.id=id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        cmds=new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Command> getCmds() {
        return cmds;
    }

    public void setCmds(List<Command> cmds) {
        this.cmds = cmds;
    }

    public UdpClient getController() {
        if (controller == null) {
            controller = new UdpClient(ip, port);
        }
        return controller;
    }

    public static class Command{
        private int id;
        private String name;
        private String stringCmd;
        private byte[] byteCmd;

        public Command() {
        }

        public Command(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStringCmd() {
            return stringCmd;
        }

        public void setStringCmd(String stringCmd) {
            this.stringCmd = stringCmd;
        }

        public byte[] getByteCmd() {
            return byteCmd;
        }

        public void setByteCmd(byte[] byteCmd) {
            this.byteCmd = byteCmd;
        }
    }
}
