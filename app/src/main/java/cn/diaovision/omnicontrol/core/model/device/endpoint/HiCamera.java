package cn.diaovision.omnicontrol.core.model.device.endpoint;

import cn.diaovision.omnicontrol.core.model.device.matrix.io.Port;

/**
 * Hi-definition cameras connected to the matrix, for controlling
 * Created by liulingfeng on 2017/3/2.
 */

public class HiCamera {
    public static int PROTO_FELICA_D = 0;
    public static int PROTO_FELICA_A = 1;
    public static int PROTO_PILSA = 2;

    int state;
    int proto; //camera protocols used to control the device
    int baudrate; //baudrate for controlling
    String address; //camera address
    Port port; //which the port is plugged

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
