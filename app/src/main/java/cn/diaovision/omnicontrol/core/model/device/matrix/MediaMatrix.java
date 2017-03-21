package cn.diaovision.omnicontrol.core.model.device.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.diaovision.omnicontrol.core.model.device.endpoint.HiCamera;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Channel;
import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.core.model.medium.PreviewVideoChannel;
//import io.realm.RealmObject;

/**
 * Created by liulingfeng on 2017/2/21.
 */

public class MediaMatrix {

    //Ports
    public List<Port> videoInPort = new ArrayList<>();
    public List<Port> videoOutPort = new ArrayList<>();

    //Channels
    public Set<Channel> videoChn = new HashSet<>();

    //Cameras
    public List<HiCamera> cameras = new ArrayList<>();

    //preview channel
    PreviewVideoChannel previewVideoChn = new PreviewVideoChannel();
    //meeting channel
    PreviewVideoChannel meetingViedoChn = previewVideoChn;


    public void configPrevewVideoChannel(String ip, int port, Port outputIdx){
        previewVideoChn.ip = ip;
        previewVideoChn.port = port;
        previewVideoChn.outputPort = outputIdx;
    }

    public int bindVideoChannel(int mIn, int pIn, int mOut, int pOut) {
        Channel chn = new Channel(mIn, pIn, mOut, pOut);
        if (videoChn.contains(chn)){
            return 0;
        }
        else {
            videoChn.add(chn);
            return 1;
        }
    }

    public void unbindVideoChannel(int mIn, int pIn, int mOut, int pOut){
        Channel chn = new Channel(mIn, pIn, mOut, pOut);
        videoChn.remove(chn);
    }
}
