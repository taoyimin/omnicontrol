package cn.diaovision.omnicontrol.core.model.device.common;

/**
 * Created by TaoYimin on 2017/8/8.
 */

public class BigBirdSplicer extends CommonDevice{

    public BigBirdSplicer(String name, String ip, int port) {
        super(name, ip, port);
        setType(TYPE.BIGBIRD_SPLICER);
    }

    @Override
    public byte[] buildPowerOnMessage() {
        return new byte[0];
    }

    @Override
    public byte[] buildPowerOffMessage() {
        return new byte[0];
    }

    @Override
    public byte[] buildStateMessage() {
        byte[] bytes=new byte[6];
        bytes[0]='<';
        bytes[1]='R';
        bytes[2]='E';
        bytes[3]='I';
        bytes[4]='D';
        bytes[5]='>';
        return bytes;
    }
}
