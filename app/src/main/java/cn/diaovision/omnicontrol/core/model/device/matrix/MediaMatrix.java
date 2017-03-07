package cn.diaovision.omnicontrol.core.model.device.matrix;

import java.util.List;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Controller;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.core.protocol.CameraProtocol;
import io.realm.RealmObject;

/**
 * Created by liulingfeng on 2017/2/21.
 */

public class MediaMatrix {

    //Ports
    public List<Port> videoInPort;
    public List<Port> videoOutPort;
    public List<Port> audioInPort;
    public List<Port> audioOutPort;
    public List<Port> serialPort;

    //Channels
    public List<Channel> videoChn;
    public List<Channel> audioChn;

    //Controller
    public List<Controller> cameraController;

    //Protocols
    public CameraProtocol cameraProtocol;

    public void bindVideoChn(int in, int out){
        for (Channel chn : videoChn){
            if (chn.outputIdx == out){
                unbindVideoOutPort(out);
                break;
            }
        }
    }

    public void unbindVideoOutPort(int out){
    }
}
