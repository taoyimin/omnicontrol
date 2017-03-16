package cn.diaovision.omnicontrol.core.model.device.endpoint;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;
import cn.diaovision.omnicontrol.core.protocol.CameraProtocol;

/**
 * Created by liulingfeng on 2017/3/2.
 */

public class IpCamera {

    CameraProtocol proto;
    int state;
    Port port;

    boolean inPreset;
    Preset preset;

    static public class Preset{
        private int angle;
        private String name;

        public Preset(int angle, String name) {
            this.angle = angle;
            this.name = name;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
