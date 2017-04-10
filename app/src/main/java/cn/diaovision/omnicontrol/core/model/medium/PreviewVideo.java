package cn.diaovision.omnicontrol.core.model.medium;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.core.model.conference.Chairman;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by liulingfeng on 2017/3/21.
 */

public class PreviewVideo {
    public String ip;
    public int port;

    public Port sourcePort; //output port connected to the matrix

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
