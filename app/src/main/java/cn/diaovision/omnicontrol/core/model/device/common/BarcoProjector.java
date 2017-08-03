package cn.diaovision.omnicontrol.core.model.device.common;

/**
 * Created by TaoYimin on 2017/8/1.
 * 巴可投影机
 */

public class BarcoProjector extends CommonDevice{

    public BarcoProjector(String name, String ip, int port) {
        super(name, ip, port);
        this.setType(TYPE.BARCO_PROJECTOR);
    }

    /*生成打开电源消息*/
    @Override
    public byte[] buildPowerOnMessage() {
        //:(space)POWR1(space)(carriage return)
        byte[] bytes=new byte[9];
        bytes[0]=58;
        bytes[1]=32;
        bytes[2]=80;
        bytes[3]=79;
        bytes[4]=87;
        bytes[5]=82;
        bytes[6]=49;
        bytes[7]=32;
        bytes[8]=13;
        return bytes;
    }

    /*生成关闭电源消息*/
    @Override
    public byte[] buildPowerOffMessage() {
        //:(space)POWR0(space)(carriage return)
        byte[] bytes=new byte[9];
        bytes[0]=58;
        bytes[1]=32;
        bytes[2]=80;
        bytes[3]=79;
        bytes[4]=87;
        bytes[5]=82;
        bytes[6]=48;
        bytes[7]=32;
        bytes[8]=13;
        return bytes;
    }

    /*生成获取运行状态的消息*/
    @Override
    public byte[] buildStateMessage() {
        //:(space)POST?(space)(carriage return)
        byte[] bytes=new byte[9];
        bytes[0]=58;
        bytes[1]=32;
        bytes[2]=80;
        bytes[3]=79;
        bytes[4]=87;
        bytes[5]=82;
        bytes[6]=63;
        bytes[7]=32;
        bytes[8]=13;
        return bytes;
    }
}
