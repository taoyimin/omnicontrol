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

    public Device(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        cmds=new ArrayList<>();
        cmds.add(new Command(1,"指令1",""));
        cmds.add(new Command(2,"指令2",""));
        cmds.add(new Command(3,"指令3",""));
        cmds.add(new Command(4,"指令4",""));
        cmds.add(new Command(5,"指令5","765"));
        cmds.add(new Command(6,"指令6",""));
        cmds.add(new Command(7,"指令7",""));
        cmds.add(new Command(8,"指令8",""));
        cmds.add(new Command(9,"指令9","345"));
        cmds.add(new Command(10,"指令10",""));
        cmds.add(new Command(11,"指令11",""));
        cmds.add(new Command(12,"指令12",""));
    }

    public Device(int id, String name, String ip, int port) {
        this.id=id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        cmds=new ArrayList<>();
        cmds.add(new Command(1,"指令1",""));
        cmds.add(new Command(2,"指令2",""));
        cmds.add(new Command(3,"指令3",""));
        cmds.add(new Command(4,"指令4",""));
        cmds.add(new Command(5,"指令5","765"));
        cmds.add(new Command(6,"指令6",""));
        cmds.add(new Command(7,"指令7",""));
        cmds.add(new Command(8,"指令8",""));
        cmds.add(new Command(9,"指令9","345"));
        cmds.add(new Command(10,"指令10",""));
        cmds.add(new Command(11,"指令11",""));
        cmds.add(new Command(12,"指令12",""));
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

    public class Command{
        private int id;
        private String name;
        private String cmd;
        private String byteCmd;

        public Command(int id, String name, String cmd) {
            this.id = id;
            this.name = name;
            this.cmd = cmd;
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

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getByteCmd() {
            return byteCmd;
        }

        public void setByteCmd(String byteCmd) {
            this.byteCmd = byteCmd;
        }
    }
}
