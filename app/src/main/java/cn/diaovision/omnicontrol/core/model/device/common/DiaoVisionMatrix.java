package cn.diaovision.omnicontrol.core.model.device.common;

/**
 * Created by TaoYimin on 2017/8/4.
 */

public class DiaoVisionMatrix extends CommonDevice{
    public DiaoVisionMatrix(String name, String ip, int port) {
        super(name, ip, port);
        setType(TYPE.DIAOVISION_MATRIX);
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
        byte[] bytes=new byte[10];
        bytes[0]=0x01;
        bytes[1]='F';
        bytes[2]='S';
        bytes[3]='I';
        bytes[4]='D';
        bytes[5]='F';
        bytes[6]='S';
        bytes[7]=48;
        bytes[8]='D';
        bytes[9]=0x04;
        return bytes;
    }
}
